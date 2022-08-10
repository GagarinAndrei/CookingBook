package ru.netology.nerecipe.db

import ru.netology.nerecipe.db.entities.RecipeEntity
import ru.netology.nerecipe.dto.Recipe

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