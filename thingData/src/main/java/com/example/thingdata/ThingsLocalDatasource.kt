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
    private var lastThing : Thing? = null

    override suspend fun generateThing() {
        withContext(Dispatchers.IO) {
            lastThing = Thing(currentId++).apply { thingChannel.send(this) }
        }
    }

    override fun observeThings() : Flow<Thing> {
        return thingChannel.receiveAsFlow().onEach {
            delay(fakeDelay)
        }.flowOn(Dispatchers.IO)
    }

    override fun getLastGeneratedThing(): Thing? {
        return lastThing
    }
}
