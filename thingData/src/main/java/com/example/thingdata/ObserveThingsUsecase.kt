package com.example.thingdata

import kotlinx.coroutines.flow.Flow

interface ObserveThingsUsecase {
    fun observeThings(): Flow<Thing>
}