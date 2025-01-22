package com.example.platepals.model.dao

import androidx.room.Insert
import androidx.room.Query
import com.example.platepals.model.Post

interface PostDao {
    @Insert
    fun create(vararg post: Post)

    @Query("SELECT * FROM Post WHERE id=:id")
    fun getById(id:String):Post
}