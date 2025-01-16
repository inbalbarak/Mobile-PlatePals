package com.example.platepals.model

import com.example.platepals.base.CreatePostCallback
import com.example.platepals.base.PostCallback
import com.example.platepals.base.TagsCallback

class Model private constructor() {
    private val firebaseModel = FirebaseModel()

    companion object {
        val shared = Model()
    }

    fun addPost(post: Post, update: Boolean, callback: CreatePostCallback) {
        firebaseModel.addPost(post,update,callback)
    }

    fun getAllTags(callback: TagsCallback) {
        firebaseModel.getAllTags(callback)
    }


    fun getPostById(id: String, callback: PostCallback) {
        firebaseModel.getPostById(id,callback)
    }

}