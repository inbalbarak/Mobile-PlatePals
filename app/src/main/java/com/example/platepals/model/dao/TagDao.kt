package com.example.platepals.model.dao

import androidx.room.Query
import com.example.platepals.model.Tag

interface TagDao {
    @Query("SELECT * FROM Tag")
    fun getAllTags(): Tag
}

