package com.example.thingdata

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext

class ThingsLocalDatasource(var currentId : Int, val fakeDelay: Long)
    : ObserveThingsUsecase, GenerateThingUsecase, GetLastGeneratedThingUsecase {
    private val thingChannel = Channel<Thing>()

    override suspend fun generateThing() {
        withContext(Dispatchers.IO) { thingChannel.send(Thing(currentId++)) }
    }

    override fun observeThings() : Flow<Thing> {
        return thingChannel.receiveAsFlow().onEach { delay(fakeDelay) }.flowOn(Dispatchers.IO)
    }

    override fun getLastGeneratedThing(): Thing {
        return Thing(currentId)
    }
}
