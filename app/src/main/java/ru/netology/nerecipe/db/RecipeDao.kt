package ru.netology.nerecipe.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nerecipe.db.entities.RecipeEntity
import ru.netology.nerecipe.dto.Categories

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAll(): LiveData<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getById(recipeId: Long): RecipeEntity

    @Insert
    fun insert(recipe: RecipeEntity)

    @Query(
        "UPDATE recipes SET " +
                "title = :title, " +
                "recipeImgPath = :recipeImgPath, " +
                "steps = :steps, " +
                "tags = :tags  " +
                "WHERE id = :id"
    )
    fun update(
        id: Long,
        title: String,
        recipeImgPath: String?,
        steps: MutableMap<String, String>,
        tags: MutableSet<Categories>
    )

    @Query(
        """
        UPDATE recipes SET
        isFavourite = CASE WHEN isFavourite THEN 0 ELSE 1 END
        WHERE id = :id
    """
    )
    fun favourite(id: Long)

    @Query("DELETE FROM recipes WHERE id = :id")
    fun delete(id: Long)
}