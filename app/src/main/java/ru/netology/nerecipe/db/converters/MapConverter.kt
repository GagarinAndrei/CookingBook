package ru.netology.nerecipe.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapConverter {

    @TypeConverter
    fun mapToString(value: Map<String, String>): String {
        val gson = Gson()
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun stringToMap(value: String): Map<String, String> {
        val gson = Gson()
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, type)

    }
}