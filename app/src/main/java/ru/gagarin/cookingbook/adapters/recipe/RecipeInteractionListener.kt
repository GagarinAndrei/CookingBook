package ru.gagarin.cookingbook.adapters.recipe

import ru.gagarin.cookingbook.dto.Recipe

interface RecipeInteractionListener {

    fun onFavouriteButtonClicked(recipe: Recipe)

    fun onDeleteMenuOptionClicked(recipeId: Long)

    fun onEditMenuOptionClicked(recipeId: Long)

    fun onRecipeClicked(recipe: Recipe)
}