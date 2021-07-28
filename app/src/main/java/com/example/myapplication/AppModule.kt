package com.example.myapplication

import android.content.Context
import com.example.featureone.DaggerFeatureOneModule_Component
import com.example.featureone.FeatureOneModule
import com.example.thingdata.GetLastGeneratedThingUsecase
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Named("firstThingId") fun provideThingId() : Int = 1

    @Provides
    @Named("screenOneText") fun provideText() : String = "This text provided by AppModule!!!"

    @Provides
    fun FeatureOneComponent(lastGeneratedThingUsecase: GetLastGeneratedThingUsecase)
            = DaggerFeatureOneModule_Component.factory().create(lastGeneratedThingUsecase)

    @dagger.Component(modules = [AppModule::class, ThingModule::class])
    @Singleton
    interface Component {
        @dagger.Component.Factory
        interface Factory {
            fun create(@BindsInstance @Named("appContext") appContext: Context) : Component
        }

        fun featureOneComponent() : FeatureOneModule.Component
        fun addThingViewModel() : Provider<AddThingViewModel>
    }
}

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class FromStore