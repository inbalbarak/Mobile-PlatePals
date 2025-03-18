package com.example.platepals.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.platepals.model.Tag

@Dao
interface TagDao {
    @Query("SELECT * FROM Tags")
    fun getAllTags(): List<Tag>
}

