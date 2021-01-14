package com.example.featureone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.thingdata.GetLastGeneratedThingUsecase
import com.example.featureone.databinding.ActivityScreenOneBinding
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Named

class ScreenOneActivity : AppCompatActivity() {
    @Inject @Named("lastGeneratedThing") lateinit var text : String

    private val viewBinding by lazy {
        ActivityScreenOneBinding.inflate(layoutInflater).also { setContentView(it.root) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as FeatureOneComponent.Provider).featureOneComponent().inject(this)
        viewBinding.textView.text = text
    }
}

@Component(modules = [FeatureOneModule::class])
interface FeatureOneComponent {
    fun inject(screenOneActivity: ScreenOneActivity)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance getLastGeneratedThingUsecase: GetLastGeneratedThingUsecase
        ) : FeatureOneComponent
    }

    interface Provider {
        fun featureOneComponent() : FeatureOneComponent
    }
}

@Module
class FeatureOneModule {
    @Provides @Named("lastGeneratedThing") fun provideLastGeneratedThing(
        lastGeneratedThingUsecase: GetLastGeneratedThingUsecase
    ) = lastGeneratedThingUsecase.getLastGeneratedThing().toString()
}
