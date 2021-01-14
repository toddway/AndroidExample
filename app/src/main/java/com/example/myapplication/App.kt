package com.example.myapplication

import android.app.Application
import android.content.Context
import com.example.featureone.DaggerFeatureOneComponent
import com.example.featureone.FeatureOneComponent
import com.example.thingdata.GenerateThingUsecase
import com.example.thingdata.GetLastGeneratedThingUsecase
import com.example.thingdata.ObserveThingsUsecase
import com.example.thingdata.ThingsLocalDatasource
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

class App : Application(), FeatureOneComponent.Provider {
    val component by lazy { DaggerAppComponent.factory().create(this) }
    override fun featureOneComponent(): FeatureOneComponent = component.featureOneComponent()
}

@Component(modules = [AppModule::class, ThingModule::class])
@Singleton
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance appContext: Context) : AppComponent
    }

    fun mainFactory() : MainComponent.Factory
    fun featureOneComponent() : FeatureOneComponent
}

@Module(subcomponents = [MainComponent::class])
class AppModule {
    @Provides
    @Named("nextThingId") fun provideThingId() : Int = 2

    @Provides
    @Named("screenOneText") fun provideScreenOneText() : String = "This text provided by AppModule!!!"

    @Provides
    fun UiFeatureOneComponent(lastGeneratedThingUsecase: GetLastGeneratedThingUsecase)
            = DaggerFeatureOneComponent.factory().create(lastGeneratedThingUsecase)
}

@Module
class ThingModule {
    @Provides
    @Singleton
    fun ThingsLocalDatasource(
        @Named("fakeDelay") fakeDelay : Long,
        @Named("nextThingId") thingId : Int
    ) : ThingsLocalDatasource = ThingsLocalDatasource(thingId, fakeDelay)

    @Provides
    @Named("fakeDelay") fun providesFakeDelay() : Long = 1000L

    @Provides
    fun ObserveThingsUsecase(datasource: ThingsLocalDatasource) : ObserveThingsUsecase = datasource

    @Provides
    fun AddThingUsecase(datasource: ThingsLocalDatasource) : GenerateThingUsecase = datasource

    @Provides
    fun GetLastGeneratedThingUsecase(datasource: ThingsLocalDatasource) : GetLastGeneratedThingUsecase = datasource
}