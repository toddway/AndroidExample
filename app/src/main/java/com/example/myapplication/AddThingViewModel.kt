package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.thingdata.GenerateThingUsecase
import com.example.thingdata.ObserveThingsUsecase
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddThingViewModel @Inject constructor(
    private val observeThings : ObserveThingsUsecase,
    private val generateThing : GenerateThingUsecase
) : ViewModel() {
    fun generateThingButtonClicked() { viewModelScope.launch { generateThing.generateThing() } }
    val thingLiveData = observeThings.observeThings().asLiveData()
}