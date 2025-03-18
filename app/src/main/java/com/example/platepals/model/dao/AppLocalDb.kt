package com.example.platepals.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.platepals.base.MyApplication
import com.example.platepals.model.Converters
import com.example.platepals.model.Post
import com.example.platepals.model.Tag
import com.example.platepals.model.User

@Database(entities = [User::class, Tag:: class, Post::class ], version = 4)
@TypeConverters(Converters::class)
abstract class AppLocalDbRepository: RoomDatabase(){
    abstract fun UserDao(): UserDao
    abstract fun TagDao(): TagDao
    abstract fun PostDao(): PostDao

}

object AppLocalDb{
    val database: AppLocalDbRepository by lazy {
        val context = MyApplication.Globals.context?:throw IllegalArgumentException("Application context is missing")

        Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "dbFileName.db"
        ).fallbackToDestructiveMigration().build()
    }
}