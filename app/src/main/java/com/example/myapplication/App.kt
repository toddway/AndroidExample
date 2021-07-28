package com.example.myapplication

import android.app.Application
import android.content.Context
import com.example.featureone.FeatureOneModule

class App : Application(), FeatureOneModule.Component.Provider {
    val component by lazy { DaggerAppModule_Component.factory().create(this) }
    override fun featureOneComponent(): FeatureOneModule.Component = component.featureOneComponent()
}

fun Context.appComponent() = (applicationContext as App).component