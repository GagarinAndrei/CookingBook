package ru.gagarin.cookingbook.adapters.steps

import ru.gagarin.cookingbook.dto.Recipe

interface StepInteractionListener {

    fun onDeleteStepButtonClicked(recipe: Recipe, stepKey: String, caller: String)

}