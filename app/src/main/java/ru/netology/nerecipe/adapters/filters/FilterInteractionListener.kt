package ru.netology.nerecipe.adapters.filters

import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.dto.Categories

interface FilterInteractionListener {

    fun onCheckClicked(category: Categories)

    fun onUncheckClicked(category: Categories)

    val filterCheckboxUpdate:MutableLiveData<Boolean>

}