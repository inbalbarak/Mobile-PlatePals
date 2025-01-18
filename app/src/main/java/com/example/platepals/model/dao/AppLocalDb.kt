package com.example.platepals.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.platepals.base.MyApplication
import com.example.platepals.model.User

@Database(entities = [User::class], version = 1)
abstract class AppLocalDbRepository: RoomDatabase(){
    abstract fun UserDao(): UserDao
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