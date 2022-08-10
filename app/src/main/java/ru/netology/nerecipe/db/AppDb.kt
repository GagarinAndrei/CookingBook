package ru.netology.nerecipe.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nerecipe.db.converters.ListConverter
import ru.netology.nerecipe.db.converters.MapConverter
import ru.netology.nerecipe.db.converters.SetConverter
import ru.netology.nerecipe.db.entities.RecipeEntity


@Database(
    entities = [RecipeEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListConverter::class, MapConverter::class, SetConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract val recipeDao: RecipeDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDb {
            return Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .allowMainThreadQueries()
                .build()
        }
    }
}