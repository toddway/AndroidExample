package com.example.myapplication

import com.example.thingdata.GenerateThingUsecase
import com.example.thingdata.GetLastGeneratedThingUsecase
import com.example.thingdata.ObserveThingsUsecase
import com.example.thingdata.ThingsLocalDatasource
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class ThingModule {
    @Provides
    @Singleton
    fun ThingsLocalDatasource(@Named("firstThingId") thingId : Int) : ThingsLocalDatasource =
        ThingsLocalDatasource(thingId, 1000L)

    @Provides
    fun ObserveThingsUsecase(datasource: ThingsLocalDatasource) : ObserveThingsUsecase = datasource

    @Provides
    fun AddThingUsecase(datasource: ThingsLocalDatasource) : GenerateThingUsecase = datasource

    @Provides
    fun GetLastGeneratedThingUsecase(datasource: ThingsLocalDatasource) : GetLastGeneratedThingUsecase = datasource
}