package com.example.platepals.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.platepals.model.Tag

@Dao
interface TagDao {
    @Query("SELECT * FROM Tags")
    fun getAllTags(): List<Tag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg tag: Tag)

    @Query("SELECT * FROM Tags WHERE id IN (:ids)")
    fun getTagsByIds(ids: List<String>): List<Tag>
}

