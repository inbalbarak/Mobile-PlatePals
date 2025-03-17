package com.example.platepals.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.platepals.model.Post
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import com.example.platepals.model.User

@Dao
interface PostDao {
    @Insert
    fun create(vararg post: Post)

    @Query("SELECT * FROM Post WHERE id=:id")
    fun getById(id: String): Post

    @Query("SELECT * FROM Post")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM Post WHERE id =:id")
    fun getPostById(id: String): Post

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg post: Post)

    @Query("DELETE FROM Post WHERE id = :postId")
    fun deleteById(postId: String)

//    @Query("SELECT p.*, u.username AS author FROM Post p JOIN Users u ON p.author = u.email")
//    fun getAllPostsWithUsernames(): LiveData<List<Post>>
}