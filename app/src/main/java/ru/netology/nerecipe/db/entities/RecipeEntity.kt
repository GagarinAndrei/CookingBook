package ru.netology.nerecipe.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nerecipe.dto.Categories

@Entity(tableName = "recipes")
class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "recipeImgPath")
    val recipeImgPath: String,

    @ColumnInfo(name = "steps")
    val steps: MutableMap<String, String>,

    @ColumnInfo(name = "tags")
    val tags: MutableSet<Categories>,

    @ColumnInfo(name = "isFavourite")
    val isFavourite: Boolean = false
)