package com.example.platepals.model
import com.example.platepals.base.Constants
import com.example.platepals.base.CreatePostCallback
import com.example.platepals.base.EmptyCallback
import com.example.platepals.base.PostCallback
import com.example.platepals.base.TagsCallback
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings

class FirebaseModel {
    private val database = Firebase.firestore
    init {
        val setting = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }
        database.firestoreSettings = setting
    }

    fun getAllTags(callback: TagsCallback) {
        database.collection(Constants.COLLECTIONS.TAGS).get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val tags: MutableList<Tag> = mutableListOf()
                        for (json in it.result) {
                            tags.add(Tag.fromJSON(json.data))
                        }
                        callback(tags)
                    }
                    false -> callback(listOf())
                }
            }
    }

    fun addPost(post: Post, update: Boolean, callback: CreatePostCallback) {
        if(update){
            database.collection(Constants.COLLECTIONS.POSTS).whereEqualTo("id",post.id).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.update(post.json).addOnSuccessListener{
                            callback(true)
                        }
                    }
                }
        }else{
            database.collection(Constants.COLLECTIONS.POSTS).document()
                .set(post.json)
                .addOnCompleteListener {
                    callback(true)
                }
        }
    }

    fun getPostById(postId: String, callback: PostCallback) {
        database.collection(Constants.COLLECTIONS.POSTS).whereEqualTo("id",postId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val post :Post = Post.fromJSON(document.data ?: mapOf())
                    callback(post)

                }
            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents: ", exception)
//            }
    }
}