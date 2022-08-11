package ru.gagarin.cookingbook.adapters.filters

import androidx.lifecycle.MutableLiveData
import ru.gagarin.cookingbook.dto.Categories

interface FilterInteractionListener {

    fun onCheckClicked(category: Categories)

    fun onUncheckClicked(category: Categories)

    val filterCheckboxUpdate:MutableLiveData<Boolean>

}