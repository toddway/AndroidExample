package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.thingdata.GenerateThingUsecase
import com.example.thingdata.ObserveThingsUsecase
import com.example.thingdata.Thing
import com.example.featureone.ScreenOneActivity
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject lateinit var viewBinding : ActivityMainBinding
    @Inject lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).component.mainFactory().create(this).inject(this)

        viewBinding.generateThingButton.setOnClickListener {
            viewModel.generateThingButtonClicked()
        }

        viewModel.thingLiveData.observe(this, Observer {
            viewBinding.textView.text = "$it"
        })

        viewBinding.mainActivity.setOnClickListener {
            startActivity(Intent(this, ScreenOneActivity::class.java))
        }
    }
}

class MainViewModel(
    private val observeThings : ObserveThingsUsecase,
    private val generateThing : GenerateThingUsecase
) : ViewModel() {
    fun generateThingButtonClicked() { viewModelScope.launch { generateThing.generateThing() } }
    val thingLiveData = observeThings.observeThings().asLiveData()
}

@Subcomponent(modules = [MainModule::class])
interface MainComponent {
    fun inject(mainActivity: MainActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance activity: MainActivity) : MainComponent
    }
}

@Module
class MainModule {
    @Provides
    fun provideActivityMainBinding(activity: MainActivity): ActivityMainBinding {
        return ActivityMainBinding.inflate(activity.layoutInflater)
            .apply { activity.setContentView(root) }
    }
    @Provides
    fun provideMainViewModel(
        activity : MainActivity,
        observeThings: ObserveThingsUsecase,
        generateThing: GenerateThingUsecase
    ): MainViewModel {
        return ViewModelProvider(
            activity,
            object: ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                        MainViewModel(observeThings, generateThing) as T
                    } else {
                        throw IllegalArgumentException("Unknown ViewModel Class")
                    }
                }
            }
        ).get(MainViewModel::class.java)
    }
}
