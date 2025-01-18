package com.example.platepals.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey
    val email: String,
    val password: String,
    val avatarUrl: String? = null,
) {
    companion object {
        private const val EMAIL_KEY = "email"
        private const val PASSWORD_KEY = "password"
        private const val AVATAR_URL_KEY = "avatarUrl"

        fun fromJSON(json: Map<String, Any>): User {
            val email = json[EMAIL_KEY] as? String ?: ""
            val password = json[PASSWORD_KEY] as? String ?: ""
            val avatarUrl = json[AVATAR_URL_KEY] as? String ?: ""

            return User(
                email = email,
                password = password,
                avatarUrl = avatarUrl
            )
        }
    }
    val json: Map<String, Any>
        get() {
            return hashMapOf(
                EMAIL_KEY to email,
                PASSWORD_KEY to password,
                AVATAR_URL_KEY to AVATAR_URL_KEY
            )
        }
}