package com.example.platepals.model

import com.example.platepals.base.BooleanCallback
import com.example.platepals.base.PostCallback
import com.example.platepals.base.PostsCallback
import com.example.platepals.base.TagsByIdsCallback
import com.example.platepals.base.TagsCallback
import com.example.platepals.base.UserCallback

class Model private constructor() {
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

}