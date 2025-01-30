package com.example.platepals.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey
    val email: String,
    val password: String,
    val username: String? = generateDefaultUsername(),
    val ratingSum: Int? = 0,
    val ratingCount: Int? = 0,
    val avatarUrl: String? = null,
) {
    companion object {
        private const val EMAIL_KEY = "email"
        private const val PASSWORD_KEY = "password"
        private const val USERNAME_KEY = "username"
        private const val RATING_SUM_KEY = "ratingSum"
        private const val RATING_COUNT_KEY = "ratingCount"
        private const val AVATAR_URL_KEY = "avatarUrl"

        private fun generateDefaultUsername(): String {
            val randomDigits = (10000..99999).random()
            return "User$randomDigits"
        }

        fun fromJSON(json: Map<String, Any>): User {
            val email = json[EMAIL_KEY] as? String ?: ""
            val password = json[PASSWORD_KEY] as? String ?: ""
            val username = json[USERNAME_KEY] as? String ?: generateDefaultUsername()
            val ratingSum = json[RATING_SUM_KEY] as? Int ?: 0
            val ratingCount = json[RATING_COUNT_KEY] as? Int ?: 0
            val avatarUrl = json[AVATAR_URL_KEY] as? String ?: ""

            return User(
                email = email,
                password = password,
                username = username,
                ratingSum = ratingSum,
                ratingCount = ratingCount,
                avatarUrl = avatarUrl
            )
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                EMAIL_KEY to email,
                PASSWORD_KEY to password,
                USERNAME_KEY to (username ?: generateDefaultUsername()),
                RATING_SUM_KEY to (ratingSum ?: 0),
                RATING_COUNT_KEY to (ratingCount ?: 0),
                AVATAR_URL_KEY to (avatarUrl ?: "")
            )
        }
}
