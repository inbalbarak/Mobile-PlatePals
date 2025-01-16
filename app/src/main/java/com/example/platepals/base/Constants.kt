package com.example.platepals.base

import com.example.platepals.model.Post
import com.example.platepals.model.Tag

typealias TagsCallback = (List<Tag>) -> Unit
typealias EmptyCallback = () -> Unit
typealias PostCallback = (Post?) -> Unit
typealias CreatePostCallback = (Boolean) -> Unit

object Constants {
    object COLLECTIONS {
        const val USERS = "users"
        const val TAGS = "tags"
        const val POSTS = "posts"
    }
}