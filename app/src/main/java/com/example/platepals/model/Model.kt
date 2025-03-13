package com.example.platepals.model

import android.util.Log
import com.example.platepals.base.BooleanCallback
import com.example.platepals.base.PostCallback
import com.example.platepals.base.PostsCallback
import com.example.platepals.base.TagsByIdsCallback
import com.example.platepals.base.TagsCallback
import com.example.platepals.base.UserCallback
import com.example.platepals.networking.ChatGptRequest
import com.example.platepals.networking.ChatgptClient
import java.util.concurrent.Executors

class Model private constructor() {
    private var executor = Executors.newSingleThreadExecutor()
    private val firebaseModel = FirebaseModel()

    companion object {
        val shared = Model()
    }

    fun addPost(post: Post, update: Boolean, callback: BooleanCallback) {
        firebaseModel.addPost(post,update,callback)
    }

    fun getAllTags(callback: TagsCallback) {
        firebaseModel.getAllTags(callback)
    }

    fun getTagsByIds(ids: List<String>, callback: TagsByIdsCallback) {
        firebaseModel.getTagsByIds(ids, callback)
    }

    fun getAllPosts(callback: PostsCallback) {
        firebaseModel.getAllPosts(callback)
    }

    fun getPostById(id: String, callback: PostCallback) {
        firebaseModel.getPostById(id,callback)
    }

    fun getUserByEmail(email: String, callback: UserCallback) {
        firebaseModel.getUserById(email,callback)
    }

    fun upsertUser(user: User, callback: BooleanCallback) {
        firebaseModel.upsertUser(user, callback)
    }

    fun deletePostById(postId:String, callback: BooleanCallback){
        firebaseModel.deletePostById(postId,callback)
    }

    fun fetchChatGptResponse(body: ChatGptRequest, callback: (String?) -> Unit) {
        executor.execute {
            try {
                val request = ChatgptClient.chatgptApiClient.getChatResponse(body)
                val response = request.execute()

                if (response.isSuccessful) {
                    val chatResponse = response.body()?.choices?.firstOrNull()?.message?.content ?: "Sorry, I didn't get that."
                    callback(chatResponse)
                    Log.e("chatgpt", "Fetched ChatGPT response: $chatResponse")
                } else {
                    callback("Oops! Something went wrong.")
                    Log.e("chatgpt", "Failed to fetch ChatGPT response: ${response.code()} - ${response.message()}\")")
                }
            } catch (e: Exception) {
                callback("An error has occurred.")
                Log.e("chatgpt", "Failed to fetch ChatGPT response with exception: $e")
            }
        }
    }
}