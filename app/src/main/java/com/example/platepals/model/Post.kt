package com.example.platepals.model

import android.content.Context
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.platepals.base.MyApplication
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import com.google.firebase.firestore.FieldValue

@Parcelize
@Entity
data class Post(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String,
    val imageUrl: String ?= "",
    val tags: List<String> ?= emptyList(),
    val rating: Double? = null,
    val ratingSum:Number?= 0,
    val ratingCount:Number?=0,
    val ingredients: String,
    val instructions: String,
    val createdAt: Date ?= Date(),
    val lastUpdated: Long? = null
): Parcelable {
    companion object {

        var lastUpdated: Long
            get() = MyApplication.Globals.context?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                ?.getLong(LOCAL_LAST_UPDATED, 0) ?: 0

            set(value) {
                MyApplication.Globals.context
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.apply {
                        edit().putLong(LOCAL_LAST_UPDATED, value).apply()
                    }
            }

        private const val ID_KEY = "id"
        private const val TITLE_KEY = "title"
        private const val AUTHOR_KEY = "author"
        private const val IMAGE_URL_KEY = "imageUrl"
        private const val TAGS_KEY = "tags"
        private const val RATING_SUM_KEY = "ratingSum"
        private const val RATING_COUNT_KEY = "ratingCount"
        private const val RATING_KEY = "rating"
        private const val INGREDIENTS_KEY = "ingredients"
        private const val INSTRUCTIONS_KEY = "instructions"
        private const val CREATED_AT_KEY = "createdAt"
        const val LAST_UPDATED = "lastUpdated"
        const val LOCAL_LAST_UPDATED = "locaStudentLastUpdated"

        fun fromJSON(json: Map<String, Any>): Post {
            val id = json[ID_KEY] as? String ?: UUID.randomUUID().toString()
            val title = json[TITLE_KEY] as? String ?: ""
            val author = json[AUTHOR_KEY] as? String ?: ""
            val imageUrl = json[IMAGE_URL_KEY] as? String ?: ""
            val tags = (json[TAGS_KEY] as? List<*>)?.filterIsInstance<String>() ?: emptyList()  // Change Array to List
            val ratingSum = (json[RATING_SUM_KEY] as? Number)?.toInt() ?: 0
            val ratingCount = (json[RATING_COUNT_KEY] as? Number)?.toInt() ?: 0
            val ingredients = json[INGREDIENTS_KEY] as? String ?: ""
            val instructions = json[INSTRUCTIONS_KEY] as? String ?: ""
            val createdAt = (json[CREATED_AT_KEY] as? Timestamp)?.toDate() ?: Date()
            val timeStamp = json[LAST_UPDATED] as? Timestamp
            val lastUpdatedLongTimestamp = timeStamp?.toDate()?.time

            var tempRating = 0.0

            if(ratingCount != 0) {
                tempRating = BigDecimal(ratingSum.toDouble() / ratingCount.toDouble()).setScale(2, RoundingMode.HALF_UP).toDouble()
            }


            return Post(
                id = id,
                title = title,
                author = author,
                imageUrl = imageUrl,
                tags = tags,
                rating = tempRating,
                ratingCount = ratingCount,
                ingredients = ingredients,
                instructions = instructions,
                createdAt = createdAt,
                lastUpdated = lastUpdatedLongTimestamp
            )
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                TITLE_KEY to title,
                AUTHOR_KEY to author,
                IMAGE_URL_KEY to (imageUrl ?: ""),
                TAGS_KEY to (tags ?: emptyList()),
                RATING_SUM_KEY to (ratingSum ?: 0),
                RATING_COUNT_KEY to (ratingCount ?: 0),
                RATING_KEY to (rating ?: 0),
                INGREDIENTS_KEY to ingredients,
                INSTRUCTIONS_KEY to instructions,
                CREATED_AT_KEY to (createdAt ?: Date()),
                LAST_UPDATED to FieldValue.serverTimestamp()
            )
        }

    val updateObject: Map<String, Any?>
        get() {
            return hashMapOf<String, Any?>().apply {
                put(ID_KEY, id)
                put(TITLE_KEY, title)
                put(AUTHOR_KEY, author)
                if (!imageUrl.isNullOrEmpty()) {
                    put(IMAGE_URL_KEY, imageUrl)
                }
                put(TAGS_KEY, tags)
                put(INGREDIENTS_KEY, ingredients)
                put(INSTRUCTIONS_KEY, instructions)
            }
        }
}
