package ru.netology.nerecipe.adapters.recipe

import ru.netology.nerecipe.dto.Recipe

interface RecipeInteractionListener {

    fun onFaveButtonClicked(recipe: Recipe)

    fun onDeleteMenuOptionClicked(recipeId: Long)

    fun onEditMenuOptionClicked(recipeId: Long)

    fun onRecipeClicked(recipe: Recipe)
}