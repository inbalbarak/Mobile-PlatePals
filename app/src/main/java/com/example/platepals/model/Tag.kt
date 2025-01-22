package com.example.platepals.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tag(
    @PrimaryKey val id: String,
    val name:String
    ) {
    companion object {
        private const val ID_KEY = "id"
        private const val NAME_KEY = "name"

        fun fromJSON(json: Map<String, Any>): Tag {
            val id = json[ID_KEY] as? String ?: ""
            val name = json[NAME_KEY] as? String ?: ""

            return Tag(
                id = id,
                name = name
            )
        }
    }
    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                NAME_KEY to name
            )
        }

}