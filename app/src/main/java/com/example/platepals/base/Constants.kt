package com.example.platepals.base

import com.example.platepals.model.Post
import com.example.platepals.model.Tag
import com.example.platepals.model.User

typealias TagsCallback = (List<Tag>) -> Unit
typealias TagsByIdsCallback = (List<Tag>) -> Unit
typealias EmptyCallback = () -> Unit
typealias PostsCallback = (List<Post>) -> Unit
typealias PostCallback = (Post?) -> Unit
typealias BooleanCallback = (Boolean) -> Unit
typealias UsersByEmailsCallback = (Map<String, String>) -> Unit
typealias UserCallback = (User?)-> Unit

object Constants {
    object COLLECTIONS {
        const val USERS = "users"
        const val TAGS = "tags"
        const val POSTS = "posts"
    }
}