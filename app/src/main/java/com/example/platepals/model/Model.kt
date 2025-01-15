package com.idz.colman24class2.model

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.platepals.model.User
import com.example.platepals.model.dao.AppLocalDb
import com.example.platepals.model.dao.AppLocalDbRepository
import java.util.concurrent.Executors

typealias EmptyCallback = () -> Unit

class Model private constructor() {
    private val database: AppLocalDbRepository = AppLocalDb.database
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val shared = Model()
    }

    fun add(user: User, callback: EmptyCallback) {
        executor.execute {
            database.UserDao().create(user)
            mainHandler.post {
                callback()
            }
        }
    }

}