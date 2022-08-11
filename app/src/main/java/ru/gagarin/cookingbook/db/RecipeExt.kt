package ru.gagarin.cookingbook.db

import ru.gagarin.cookingbook.db.entities.RecipeEntity
import ru.gagarin.cookingbook.dto.Recipe

internal fun RecipeEntity.toRecipe(): Recipe {
    return Recipe(
        id = id,
        title = title,
        recipeImgPath = recipeImgPath,
               steps = steps,
        tags = tags,
        isFavourite = isFavourite
    )
}

internal fun Recipe.toRecipeEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        title = title,
        recipeImgPath = recipeImgPath,
               steps = steps,
        tags = tags,
        isFavourite = isFavourite
    )
}