package com.example.platepals.model

import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import com.example.platepals.base.BooleanCallback
import com.example.platepals.base.PostCallback
import com.example.platepals.base.TagsByIdsCallback
import com.example.platepals.base.TagsCallback
import com.example.platepals.base.UserCallback
import com.example.platepals.base.UsersCallback
import com.example.platepals.model.dao.AppLocalDb
import com.example.platepals.model.dao.AppLocalDbRepository
import com.example.platepals.networking.ChatGptRequest
import com.example.platepals.networking.ChatgptClient
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.Executors

class Model private constructor() {
    private var executor = Executors.newSingleThreadExecutor()
    private val firebaseModel = FirebaseModel()
    private val cloudinaryModel = CloudinaryModel()

    private val database: AppLocalDbRepository = AppLocalDb.database
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    val posts: LiveData<List<Post>> = database.PostDao().getAllPosts()

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

    fun getAllUsers(callback: UsersCallback){
        firebaseModel.getAllUsers(callback)
    }

    fun addPost(post: Post, image: Bitmap?, update: Boolean, callback: BooleanCallback) {
                executor.execute {
                    val existingPost = if(update) database.PostDao().getPostById(post.id) else null

                    val finalPost = if (image == null) {
                        post.copy(imageUrl = existingPost?.imageUrl ?: post.imageUrl)
                    } else {
                        post
                    }

                    val ratingCount = if(post.ratingCount == 0)  existingPost?.ratingCount?.toInt() ?: 0 else post?.ratingCount?.toInt() ?:0
                    val ratingSum =  if(post.ratingSum == 0)  existingPost?.ratingSum?.toInt() ?: 0 else post?.ratingSum?.toInt() ?:0
                    val rating = if (ratingCount > 0) {
                        BigDecimal(ratingSum.toDouble() / ratingCount.toDouble()).setScale(2, RoundingMode.HALF_UP).toDouble()
                    } else {
                        0.0
                    }

                    val postToSave = finalPost.copy(
                        ratingCount = ratingCount,
                        ratingSum = ratingSum,
                        rating = rating
                    )

                    database.PostDao().insertAll(postToSave)

                    mainHandler.post {
                        firebaseModel.addPost(finalPost, update) { success ->
                            if (success && image != null) {
                                uploadImageToCloudinary(
                                    bitmap = image,
                                    name = post.id,
                                    onSuccess = { uri ->
                                        if (!uri.isNullOrBlank()) {
                                            val ratingCount = if(post.ratingCount == 0)  existingPost?.ratingCount?.toInt() ?: 0 else post?.ratingCount?.toInt() ?:0
                                            val ratingSum =  if(post.ratingSum == 0)  existingPost?.ratingSum?.toInt() ?: 0 else post?.ratingSum?.toInt() ?:0
                                            val rating = if (ratingCount > 0) {
                                                BigDecimal(ratingSum.toDouble() / ratingCount.toDouble()).setScale(2, RoundingMode.HALF_UP).toDouble()
                                            } else {
                                                0.0
                                            }

                                            val postToSave = finalPost.copy(
                                                ratingCount = ratingCount,
                                                ratingSum = ratingSum,
                                                rating = rating
                                            )

                                            val updatedPost = finalPost.copy(imageUrl = uri)
                                            executor.execute {
                                                database.PostDao().insertAll(postToSave.copy(imageUrl = uri))
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
        }
    }

    fun getAllTags(getAll: Boolean, callback: TagsCallback) {
        val lastUpdated: Long = Tag.lastUpdated

        val since = if (getAll) 0 else lastUpdated

        firebaseModel.getAllTags(since) { tags ->
            executor.execute {
                var currentTime = lastUpdated
                for (tag in tags) {
                    database.TagDao().insertAll(tag)
                    tag.lastUpdated?.let {
                        if (currentTime < it) {
                            currentTime = it
                        }
                    }
                }

                Tag.lastUpdated = currentTime
                val savedTags = database.TagDao().getAllTags()

                mainHandler.post {
                    callback(savedTags)
                }
            }
        }
    }

    fun getTagsByIds(ids: List<String>, callback: TagsByIdsCallback) {
        executor.execute {
            val tagsByIds = database.TagDao().getTagsByIds(ids)

            mainHandler.post {
                callback(tagsByIds)
            }
        }
    }

    fun refreshPosts(getAll: Boolean, callback: BooleanCallback) {
        val lastUpdated: Long = Post.lastUpdated

        val since = if (getAll) 0 else lastUpdated

        firebaseModel.getAllPosts(since) { posts ->
            val authorEmails = posts.map { it.author }.distinct()


            firebaseModel.getUsersByEmails(authorEmails) { emailToUsername ->
                executor.execute {
                    var latestTime = lastUpdated

                    database.PostDao().deleteAll()

                    for (post in posts) {
                        database.PostDao().insertAll(post)

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
            val post = database.PostDao().getPostById(id)
            mainHandler.post {
                callback(post)
            }
        }
    }

    fun getUserByEmail(email: String, callback: UserCallback) {
        firebaseModel.getUserById(email, callback)
    }

    fun getUserByUsername(username: String, callback: UserCallback) {
        firebaseModel.getUserByUsername(username, callback)
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
            database.PostDao().deleteById(postId)
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