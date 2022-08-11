package ru.gagarin.cookingbook.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.gagarin.cookingbook.adapters.filters.FilterInteractionListener
import ru.gagarin.cookingbook.adapters.steps.StepInteractionListener
import ru.gagarin.cookingbook.adapters.recipe.RecipeInteractionListener
import ru.gagarin.cookingbook.data.impl.RecipeRepositoryImpl
import ru.gagarin.cookingbook.db.AppDb
import ru.gagarin.cookingbook.dto.Categories
import ru.gagarin.cookingbook.dto.Recipe
import ru.gagarin.cookingbook.ui.EditRecipeFragment
import ru.gagarin.cookingbook.ui.NewRecipeFragment
import ru.gagarin.cookingbook.utils.SingleLiveEvent

class RecipeViewModel(application: Application) :
    RecipeInteractionListener,
    StepInteractionListener,
    FilterInteractionListener,
    AndroidViewModel(application) {

    private val repository = RecipeRepositoryImpl(
        dao = AppDb.getInstance(
            context = application
        ).recipeDao
    )

    val data by repository::data

    val navigateToNewRecipeScreenEvent = SingleLiveEvent<Recipe>()
    val navigateToRecipeScreenEvent = SingleLiveEvent<Recipe>()
    val navigateToEditRecipeScreenEvent = SingleLiveEvent<Boolean>()
    val navigateToFaveRecipesScreenEvent = SingleLiveEvent<Boolean>()
    val navigateToAllRecipesScreenEvent = SingleLiveEvent<Boolean>()

    val newRecipe = MutableLiveData<Recipe>()
    val newRecipeImg = MutableLiveData<String>()
    val newStepImg = MutableLiveData<String>()

    val editingRecipe = MutableLiveData<Recipe>()
    val editRecipeImg = MutableLiveData<String>()
    val editStepImg = MutableLiveData<String>()

    val listOfFilters = MutableLiveData<MutableList<Categories>>(mutableListOf())
    val filteredRecipes = MutableLiveData<List<Recipe>>(mutableListOf())
    val filterRecipes = SingleLiveEvent<Boolean>()
    override val filterCheckboxUpdate = MutableLiveData(false)


    fun onCreateRecipeSaveButtonClicked(recipe: Recipe) {
        repository.create(recipe)
    }

    fun onEditRecipeSaveButtonClicked(recipe: Recipe) {
        repository.update(recipe)
    }

    fun onAddButtonClicked() {
        newRecipe.value = Recipe(
            id = 0,
            title = "",
            recipeImgPath = "",
            steps = mutableMapOf(),
            tags = mutableSetOf()
        )
        newRecipeImg.value = ""
        newStepImg.value = ""

        navigateToNewRecipeScreenEvent.call()
    }

    fun onApplyFiltersButtonClicked() {
        filteredRecipes.value = data.value
        val recipes = if (!listOfFilters.value.isNullOrEmpty()) {
            filteredRecipes.value?.filter { recipe ->
                recipe.tags.any { category ->
                    category in (listOfFilters.value?.toList() ?: mutableListOf())
                }
            }
        } else {
            data.value
        }

        filteredRecipes.value = recipes ?: data.value
        filterRecipes.call()
    }

    fun onResetFiltersButtonClicked() {
        filteredRecipes.value = data.value
        listOfFilters.value = mutableListOf()
        filterRecipes.call()
    }


    override fun onFavouriteButtonClicked(recipe: Recipe) {
        repository.favourite(recipe.id)
    }

    override fun onDeleteMenuOptionClicked(recipeId: Long) {
        repository.delete(recipeId)
    }

    override fun onEditMenuOptionClicked(recipeId: Long) {
        editingRecipe.value = repository.getById(recipeId)
        navigateToEditRecipeScreenEvent.value = true
    }

    override fun onRecipeClicked(recipe: Recipe) {
        navigateToRecipeScreenEvent.value = recipe
    }

    override fun onDeleteStepButtonClicked(recipe: Recipe, stepKey: String, caller: String) {
        val newRecipeSteps = recipe.steps
            .filterNot { step ->
                step.key == stepKey
            }
            .toMutableMap()

        if (caller == NewRecipeFragment.CALLER_NEW_RECIPE) {
            newRecipe.value = recipe.copy(steps = newRecipeSteps)
        } else if (caller == EditRecipeFragment.CALLER_EDIT_RECIPE) {
            editingRecipe.value = recipe.copy(steps = newRecipeSteps)
        }
    }

    override fun onCheckClicked(category: Categories) {
        val filters = listOfFilters.value
        filters?.add(category)
        listOfFilters.value = filters ?: mutableListOf()
    }

    override fun onUncheckClicked(category: Categories) {
        val filters = listOfFilters.value
        filters?.remove(category)
        listOfFilters.value = filters ?: mutableListOf()
    }
}
