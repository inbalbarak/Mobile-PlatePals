package com.example.platepals

import androidx.lifecycle.ViewModel
import com.example.platepals.model.Post

class PostsListViewModel : ViewModel() {
    private var _posts: List<Post>? = null
    var posts: List<Post>?
        get() = _posts
        private set(value) {
            _posts = value
        }

    fun set(posts: List<Post>?) {
        this.posts = posts
    }
}