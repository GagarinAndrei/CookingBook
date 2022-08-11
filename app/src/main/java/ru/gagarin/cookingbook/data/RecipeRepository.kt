package ru.gagarin.cookingbook.data

import androidx.lifecycle.LiveData
import ru.gagarin.cookingbook.dto.Recipe

interface RecipeRepository {

    val data: LiveData<List<Recipe>>

    fun create(recipe: Recipe)

    fun getById(recipeId: Long): Recipe

    fun update(recipe: Recipe)

    fun delete(recipeId: Long)

    fun favourite(recipeId: Long)

}