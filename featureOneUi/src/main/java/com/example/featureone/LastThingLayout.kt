package com.example.featureone

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.featureone.databinding.LastThingLayoutBinding
import com.example.thingdata.GetLastGeneratedThingUsecase
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Named

class LastThingLayout (context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    @Inject @Named("lastGeneratedThing") lateinit var text : String
    private val binding by lazy { LastThingLayoutBinding.bind(this) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) return
        context.featureOneComponent().inject(this)
        binding.textView.text = text
    }

    class LayoutFragment : Fragment(R.layout.last_thing_layout)
}

fun Context.featureOneComponent() = (applicationContext as FeatureOneModule.Component.Provider).featureOneComponent()



@Module
class FeatureOneModule {
    @Provides
    @Named("lastGeneratedThing")
    fun provideLastGeneratedThing(lastThing: GetLastGeneratedThingUsecase)
            = lastThing.getLastGeneratedThing()?.toString() ?: ""

    @dagger.Component(modules = [FeatureOneModule::class])
    interface Component {
        fun inject(lastThingLayout: LastThingLayout)

        @dagger.Component.Factory
        interface Factory {
            fun create(@BindsInstance getLastGeneratedThingUsecase: GetLastGeneratedThingUsecase) : Component
        }

        interface Provider {
            fun featureOneComponent() : Component
        }
    }
}
