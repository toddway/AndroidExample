package com.example.myapplication

import android.view.View
import androidx.fragment.app.findFragment
import androidx.lifecycle.LifecycleOwner
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Provider

@Module
class AddThingModule {
    @Provides
    fun LifecycleOwner(view: View) : LifecycleOwner = view.findFragment()

    @Provides
    @FromStore
    fun ViewModel(view: View, provider : Provider<AddThingViewModel>) = provider.fromStore(view.findFragment())

    @Subcomponent(modules = [AddThingModule::class])
    interface Component {
        fun inject(addThingLayout: AddThingLayout)

        @Subcomponent.Factory
        interface Factory {
            fun create(@BindsInstance view: View) : Component
        }
    }
}
