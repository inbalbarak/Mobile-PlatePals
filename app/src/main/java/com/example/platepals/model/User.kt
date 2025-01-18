package com.example.platepals.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey
    val email: String,
    val password: String,
    val ratingSum: Int? =0,
    val ratingCount : Int? =0,
    val avatarUrl: String? = null,
) {
    companion object {
        private const val EMAIL_KEY = "email"
        private const val PASSWORD_KEY = "password"
        private const val RATING_SUM_KEY = "ratingSum"
        private const val RATING_COUNT_KEY = "ratingCount"
        private const val AVATAR_URL_KEY = "avatarUrl"

        fun fromJSON(json: Map<String, Any>): User {
            val email = json[EMAIL_KEY] as? String ?: ""
            val password = json[PASSWORD_KEY] as? String ?: ""
            val ratingSum = json[RATING_SUM_KEY] as? Int ?:0
            val ratingCount = json[RATING_COUNT_KEY] as? Int ?: 0
            val avatarUrl = json[AVATAR_URL_KEY] as? String ?: ""

            return User(
                email = email,
                password = password,
                ratingSum=ratingSum,
                ratingCount=ratingCount,
                avatarUrl = avatarUrl
            )
        }
    }
    val json: Map<String, Any>
        get() {
            return hashMapOf(
                EMAIL_KEY to email,
                PASSWORD_KEY to password,
                RATING_SUM_KEY to (ratingSum ?: 0),
                RATING_COUNT_KEY to (ratingCount ?: 0),
                AVATAR_URL_KEY to (avatarUrl ?:"")
            )
        }
}