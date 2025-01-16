package com.example.platepals.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Post(
    @PrimaryKey val id: String,
    val title:String,
    val author: String,
    val tags: Array<String>,
    val rating: Number,
    val ingredients:String,
    val instructions:String,
    val createdAt: Date
) {
    companion object {
        private const val ID_KEY = "id"
        private const val TITLE_KEY = "title"
        private const val AUTHOR_KEY = "author"
        private const val TAGS_KEY = "tags"
        private const val RATING_KEY = "rating"
        private const val INGREDIENTS_KEY = "ingredients"
        private const val INSTRUCTIONS_KEY = "instructions"
        private const val CREATED_AT_KEY = "createdAt"

        fun fromJSON(json: Map<String, Any>): Post {
            val id = json[ID_KEY] as? String ?: ""
            val title = json[TITLE_KEY] as? String ?: ""
            val author = json[AUTHOR_KEY] as? String ?: ""
            val tags = (json[TAGS_KEY] as? List<*>)?.filterIsInstance<String>()?.toTypedArray() ?: emptyArray()
            val rating = json[RATING_KEY] as? Number ?: 0
            val ingredients = json[INGREDIENTS_KEY] as? String ?: ""
            val instructions = json[INSTRUCTIONS_KEY] as? String ?: ""
            val createdAt = json[CREATED_AT_KEY] as? Date ?: Date()

            return Post(
                id = id,
                title = title,
                author=author,
                tags=tags,
                rating=rating,
                ingredients=ingredients,
                instructions=instructions,
                createdAt=createdAt
            )
        }
    }
    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                TITLE_KEY to title,
                AUTHOR_KEY to author,
                TAGS_KEY to tags,
                RATING_KEY to rating,
                INGREDIENTS_KEY to ingredients,
                INSTRUCTIONS_KEY to instructions,
                CREATED_AT_KEY to createdAt
            )

        }
}