package com.example.platepals.model

import com.example.platepals.base.EmptyCallback
import com.example.platepals.base.PostCallback
import com.example.platepals.base.TagsCallback

class Model private constructor() {
    private val firebaseModel = FirebaseModel()

    companion object {
        val shared = Model()
    }

    fun addPost(post: Post, callback: EmptyCallback) {
        firebaseModel.addPost(post,callback)
    }

    fun getAllTags(callback: TagsCallback) {
        firebaseModel.getAllTags(callback)
    }


    fun getPostById(id: String, callback: PostCallback) {
        firebaseModel.getPostById(id,callback)
    }

}