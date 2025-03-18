package com.example.platepals.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return Gson().toJson(value ?: emptyList<String>())
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            Gson().fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Number converters
    @TypeConverter
    fun fromNumber(value: Number?): Int {
        return value?.toInt() ?: 0
    }

    @TypeConverter
    fun toNumber(value: Int): Number {
        return value
    }

    // Date converters
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp != null) Date(timestamp) else null
    }
}