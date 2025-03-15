package com.example.platepals.model
import com.example.platepals.base.BooleanCallback
import com.example.platepals.base.Constants
import com.example.platepals.base.PostCallback
import com.example.platepals.base.PostsCallback
import com.example.platepals.base.TagsByIdsCallback
import com.example.platepals.base.TagsCallback
import com.example.platepals.base.UserCallback
import com.example.platepals.base.UsersByEmailsCallback
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

    fun getTagsByIds(ids: List<String>, callback: TagsByIdsCallback) {
        database.collection(Constants.COLLECTIONS.TAGS).whereIn("id", ids).get()
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


    fun addPost(post: Post, update: Boolean, callback: BooleanCallback) {
        if(update){
            database.collection(Constants.COLLECTIONS.POSTS).whereEqualTo("id",post.id).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.update(post.updateObject).addOnSuccessListener{
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

    private fun getUsersByEmails(emails: List<String>, callback: UsersByEmailsCallback) {
        if (emails.isEmpty()) {
            callback(mapOf())
            return
        }

        database.collection(Constants.COLLECTIONS.USERS)
            .whereIn("email", emails)
            .get()
            .addOnSuccessListener { usersSnapshot ->
                val emailToUsername = usersSnapshot.associate { doc ->
                    val user = User.fromJSON(doc.data)
                    user.email to (user.username ?: user.email)
                }
                callback(emailToUsername)
            }
            .addOnFailureListener { callback(mapOf()) }
    }

    fun getAllPosts(callback: PostsCallback) {
        database.collection(Constants.COLLECTIONS.POSTS).get()
            .addOnSuccessListener { postsSnapshot ->
                if (postsSnapshot.isEmpty) {
                    callback(listOf())
                } else {
                    val authorEmails = postsSnapshot.mapNotNull {
                        it.data["author"] as? String
                    }.distinct()

                    getUsersByEmails(authorEmails) { emailToUsername ->
                        val posts = postsSnapshot.map { json ->
                            Post.fromJSON(json.data).let { post ->
                                post.copy(author = emailToUsername[post.author] ?: post.author)
                            }
                        }
                        callback(posts)
                    }
                }
            }
            .addOnFailureListener { callback(listOf()) }
    }


    fun getPostById(postId: String, callback: PostCallback) {
        database.collection(Constants.COLLECTIONS.POSTS).whereEqualTo("id",postId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val post :Post = Post.fromJSON(document.data ?: mapOf())
                    callback(post)

                }
            }
    }

    fun getUserById(email: String, callback: UserCallback) {
        database.collection(Constants.COLLECTIONS.USERS).whereEqualTo("email",email).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user : User = User.fromJSON(document.data ?: mapOf())
                    callback(user)
                }
            }
    }

    fun upsertUser(user: User, callback: BooleanCallback) {
        database.collection(Constants.COLLECTIONS.USERS).whereEqualTo("email",user.email).get()
            .addOnSuccessListener { documents ->
                if(documents.size() == 0){
                    database.collection(Constants.COLLECTIONS.USERS).document()
                        .set(user.json).addOnSuccessListener{
                            callback(true)
                        }
                }else{
                    for (document in documents) {
                        document.reference.update(user.json).addOnSuccessListener{
                            callback(true)
                        }
                    }
                }

        }
    }

    fun deletePostById(postId: String, callback: BooleanCallback) {
        database.collection(Constants.COLLECTIONS.POSTS)
            .whereEqualTo("id", postId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(false) // Post not found
                } else {
                    for (document in documents) {
                        document.reference.delete()
                            .addOnSuccessListener { callback(true) }
                            .addOnFailureListener { callback(false) }
                    }
                }
            }
            .addOnFailureListener { callback(false) }
    }
}