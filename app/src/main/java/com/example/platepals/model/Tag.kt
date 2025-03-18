package com.example.platepals.model

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.platepals.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity(tableName = "Tags")
data class Tag(
    @PrimaryKey val id: String,
    val name:String,
    val lastUpdated: Long? = null
    ) {
    companion object {
        var lastUpdated: Long
            get() = MyApplication.Globals.context?.getSharedPreferences(
                "TAG",
                Context.MODE_PRIVATE
            )
                ?.getLong(LOCAL_LAST_UPDATED, 0) ?: 0

            set(value) {
                MyApplication.Globals.context
                    ?.getSharedPreferences(
                        "TAG",
                        Context.MODE_PRIVATE
                    )?.apply {
                        edit().putLong(LOCAL_LAST_UPDATED, value).apply()
                    }
            }

        private const val ID_KEY = "id"
        private const val NAME_KEY = "name"
        const val LAST_UPDATED = "lastUpdated"
        private const val LOCAL_LAST_UPDATED = "localTagLastUpdated"

        fun fromJSON(json: Map<String, Any>): Tag {
            val id = json[ID_KEY] as? String ?: ""
            val name = json[NAME_KEY] as? String ?: ""
            val timeStamp = json[LAST_UPDATED] as? Timestamp
            val lastUpdatedLongTimestamp = timeStamp?.toDate()?.time

            return Tag(
                id = id,
                name = name,
                lastUpdated = lastUpdatedLongTimestamp
            )
        }
    }
    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                NAME_KEY to name,
                LAST_UPDATED to FieldValue.serverTimestamp()
            )
        }

}