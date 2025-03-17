package com.example.platepals.model

import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import com.example.platepals.base.BooleanCallback
import com.example.platepals.base.PostCallback
import com.example.platepals.base.PostsCallback
import com.example.platepals.base.TagsByIdsCallback
import com.example.platepals.base.TagsCallback
import com.example.platepals.base.UserCallback
import com.example.platepals.model.dao.AppLocalDb
import com.example.platepals.model.dao.AppLocalDbRepository
import com.example.platepals.networking.ChatGptRequest
import com.example.platepals.networking.ChatgptClient
import java.util.concurrent.Executors

class Model private constructor() {
    private var executor = Executors.newSingleThreadExecutor()
    private val firebaseModel = FirebaseModel()
    private val cloudinaryModel = CloudinaryModel()

    private val database: AppLocalDbRepository = AppLocalDb.database
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    val posts: LiveData<List<Post>> = database.PostDau().getAllPosts()
//    val posts: LiveData<List<Post>> = database.PostDau().getAllPostsWithUsernames()

    companion object {
        val shared = Model()
    }

    private fun uploadImageToCloudinary(
        bitmap: Bitmap,
        name: String,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        cloudinaryModel.uploadImage(
            bitmap = bitmap,
            name = name,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun addPost(post: Post, image: Bitmap?, update: Boolean, callback: BooleanCallback) {
//        firebaseModel.getUserById(post.author) { user ->
//            if (user != null) {
                executor.execute {
                    val existingPost = database.PostDau().getPostById(post.id)

                    val finalPost = if (image == null) {
                        post.copy(imageUrl = existingPost?.imageUrl ?: post.imageUrl)
                    } else {
                        post
                    }

                    database.PostDau().insertAll(finalPost) // Save post with email

                    mainHandler.post {
                        firebaseModel.addPost(finalPost, update) { success ->
                            if (success && image != null) {
                                uploadImageToCloudinary(
                                    bitmap = image,
                                    name = post.id,
                                    onSuccess = { uri ->
                                        if (!uri.isNullOrBlank()) {
                                            val updatedPost = finalPost.copy(imageUrl = uri)
                                            executor.execute {
                                                database.PostDau().insertAll(updatedPost)
                                                mainHandler.post {
                                                    firebaseModel.addPost(updatedPost, true) { innerSuccess ->
                                                        callback(innerSuccess)
                                                    }
                                                }
                                            }
                                        } else {
                                            callback(true)
                                        }
                                    },
                                    onError = { callback(true) }
                                )
                            } else {
                                callback(success)
                            }
                        }
                    }
//                }
//            } else {
//                callback(false) // User not found
//            }
        }
    }





    fun getAllTags(callback: TagsCallback) {
        firebaseModel.getAllTags(callback)
    }

    fun getTagsByIds(ids: List<String>, callback: TagsByIdsCallback) {
        firebaseModel.getTagsByIds(ids, callback)
    }

    fun refreshPosts(callback: BooleanCallback) {
        val lastUpdated: Long = Post.lastUpdated

        firebaseModel.getAllPosts(lastUpdated) { posts ->
            val authorEmails = posts.map { it.author }.distinct()

            firebaseModel.getUsersByEmails(authorEmails) { emailToUsername ->
                executor.execute {
                    var latestTime = lastUpdated

                    for (post in posts) {
                        val postWithUsername = post.copy(author = emailToUsername[post.author] ?: post.author)
                        database.PostDau().insertAll(postWithUsername)

                        post.lastUpdated?.let {
                            if (latestTime < it) {
                                latestTime = it
                            }
                        }
                    }

                    Post.lastUpdated = latestTime
                    mainHandler.post {
                        callback(true)
                    }
                }
            }
        }
    }


    fun getPostById(id: String, callback: PostCallback) {
        executor.execute {
            val post = database.PostDau().getPostById(id)
            mainHandler.post {
                callback(post)
            }
        }
    }

    fun getUserByEmail(email: String, callback: UserCallback) {
        firebaseModel.getUserById(email, callback)
    }

    fun upsertUser(user: User, image: Bitmap?, callback: BooleanCallback) {
        val customCallback = { uri: String? ->
            if (!uri.isNullOrBlank()) {
                val updatedUser = user.copy(avatarUrl = uri)
                firebaseModel.upsertUser(updatedUser) { success ->
                    callback(success)
                }
            } else {
                callback(false)
            }
        }

        firebaseModel.upsertUser(user) { success ->
            if (success) {
                image?.let {
                    uploadImageToCloudinary(
                        bitmap = image,
                        name = user.email,
                        onSuccess = customCallback,
                        onError = { customCallback(null) }
                    )
                } ?: callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun deletePostById(postId: String, callback: BooleanCallback) {
        executor.execute {
            database.PostDau().deleteById(postId)
            mainHandler.post {
                firebaseModel.deletePostById(postId, callback)
            }
        }
    }

    fun fetchChatGptResponse(body: ChatGptRequest, callback: (String?) -> Unit) {
        executor.execute {
            try {
                val request = ChatgptClient.chatgptApiClient.getChatResponse(body)
                val response = request.execute()

                if (response.isSuccessful) {
                    val chatResponse = response.body()?.choices?.firstOrNull()?.message?.content
                        ?: "Sorry, I didn't get that."
                    callback(chatResponse)
                    Log.e("chatgpt", "Fetched ChatGPT response: $chatResponse")
                } else {
                    callback("Oops! Something went wrong.")
                    Log.e(
                        "chatgpt",
                        "Failed to fetch ChatGPT response: ${response.code()} - ${response.message()}\")"
                    )
                }
            } catch (e: Exception) {
                callback("An error has occurred.")
                Log.e("chatgpt", "Failed to fetch ChatGPT response with exception: $e")
            }
        }
    }
}