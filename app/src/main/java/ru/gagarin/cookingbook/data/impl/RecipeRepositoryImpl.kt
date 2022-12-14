package ru.gagarin.cookingbook.data.impl

import androidx.lifecycle.map
import ru.gagarin.cookingbook.data.RecipeRepository
import ru.gagarin.cookingbook.db.RecipeDao
import ru.gagarin.cookingbook.db.toRecipe
import ru.gagarin.cookingbook.db.toRecipeEntity
import ru.gagarin.cookingbook.dto.Recipe

class RecipeRepositoryImpl(
    private val dao: RecipeDao
) : RecipeRepository {

    override val data = dao.getAll().map { listOfEntities ->
        listOfEntities.map { recipeEntity ->
            recipeEntity.toRecipe()
        }
    }

    override fun favourite(recipeId: Long) {
        dao.favourite(recipeId)
    }

    override fun delete(recipeId: Long) {
        dao.delete(recipeId)
    }

    override fun create(recipe: Recipe) {
        dao.insert(recipe.toRecipeEntity())
    }

    override fun update(recipe: Recipe) {
        dao.update(
            id = recipe.id,
            title = recipe.title,
            recipeImgPath = recipe.recipeImgPath,
            steps = recipe.steps,
            tags = recipe.tags
        )
    }

    override fun getById(recipeId: Long): Recipe {
        return dao.getById(recipeId).toRecipe()
    }
}