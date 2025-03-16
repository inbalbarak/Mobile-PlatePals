package com.example.platepals.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.platepals.model.Post
import androidx.room.Delete
import androidx.room.OnConflictStrategy

@Dao
interface PostDao {
    @Insert
    fun create(vararg post: Post)

    @Query("SELECT * FROM Post WHERE id=:id")
    fun getById(id:String):Post

    @Query("SELECT * FROM Post")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM Post WHERE id =:id")
    fun getPostById(id: String): Post

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg post: Post)

    @Delete
    fun delete(post: Post)
}