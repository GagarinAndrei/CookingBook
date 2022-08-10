package ru.netology.nerecipe.adapters.steps

import ru.netology.nerecipe.dto.Recipe

interface StepInteractionListener {

    fun onDeleteStepButtonClicked(recipe: Recipe, stepKey: String, caller: String)

}