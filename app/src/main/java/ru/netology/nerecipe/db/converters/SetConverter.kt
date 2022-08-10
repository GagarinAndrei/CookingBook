package ru.netology.nerecipe.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nerecipe.dto.Categories

class SetConverter {

    @TypeConverter
    fun setToString(value: Set<Categories>): String {
        val gson = Gson()
        val type = object : TypeToken<Set<Categories>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun stringToSet(value: String): Set<Categories> {
        val gson = Gson()
        val type = object : TypeToken<Set<Categories>>() {}.type
        return gson.fromJson(value, type)

    }
}