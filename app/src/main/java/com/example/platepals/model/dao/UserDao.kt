package com.example.platepals.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.platepals.model.User

@Dao
interface UserDao {
    @Insert
    fun create(vararg user: User)

    @Query("SELECT * FROM User WHERE email=:email")
    fun getByUsername(email:String): User
}