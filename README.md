The following is a contrived example application that incorporates 
Kotlin, Dagger, Coroutines, Flow & Channel, ViewModel, and LiveData.  
It has two primary features:
1. generate Things by clicking a button
2. observe Things as they are created. 

This suggests two use cases, GenerateThingUsecase and ObserveThingsUsecase, 
that act on a Thing entity.

```kotlin
data class Thing(val id : Int)

interface GenerateThingUsecase {
    suspend fun generateThing()
}

interface ObserveThingsUsecase {
    fun observeThings(): Flow<Thing>
}
```

We can implement both of these use cases with a single class, ThingsLocalDatasource.
In reality there could be one or more remote services responsible for this, and there might be one
or more datasources acting as adapters between the remote services and the use cases.

```kotlin
class ThingsLocalDatasource(var currentId : Int, val fakeDelay: Long)
    : ObserveThingsUsecase, GenerateThingUsecase {
    private val thingChannel = Channel<Thing>()

    override suspend fun generateThing() {
        withContext(Dispatchers.IO) { thingChannel.send(Thing(currentId++)) }
    }

    override fun observeThings() : Flow<Thing> {
        return thingChannel.receiveAsFlow().onEach { delay(fakeDelay) }.flowOn(Dispatchers.IO)
    }
}
```
Now in the Application class we can initialize an AppComponent (Dagger) that
binds ThingsLocalDatasource as the implementation for both ObserveThingUsecase and GenerateThingUsecase.

```kotlin
class App : Application(), FeatureOneComponent.Provider {
    val component by lazy { DaggerAppComponent.factory().create(this) }
}

@Component(modules = [AppModule::class, ThingModule::class])
@Singleton
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance appContext: Context) : AppComponent
    }

    fun mainFactory() : MainComponent.Factory
}
```

Note: references here to MainComponent will become clear in future steps as we define dependencies for the MainActivity.

Next we define a ViewModel to:
1) mediate ui events from our main view (MainActivity) and
2) broadcast data events from elsewhere (our use cases) that the view should respond to.

To accomplish this, our ViewModel constructor requires instances of both use cases (described above).
```kotlin
class MainViewModel(
    private val observeThings : ObserveThingsUsecase,
    private val generateThing : GenerateThingUsecase
) : ViewModel() {
    fun generateThingButtonClicked() { viewModelScope.launch { generateThing.generateThing() } }
    val thingLiveData = observeThings.observeThings().asLiveData()
}
```

The MainActivity can now focus on:
1) delegating button clicks to the MainViewModel (using ViewBinding)
2) changing text when LiveData changes occur

Initializing ViewBinding and ViewModel instances requires boilerplate that we can delegate
our dependency provider (Dagger) to inject when `inject(this)` is called.


```kotlin
class MainActivity : AppCompatActivity() {
    @Inject lateinit var viewBinding : ActivityMainBinding
    @Inject lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).component.mainFactory().create(this)
            .inject(this)

        viewBinding.generateThingButton.setOnClickListener {
            viewModel.generateThingButtonClicked()
        }

        viewModel.thingLiveData.observe(this, Observer {
            viewBinding.textView.text = "$it"
        })
    }
}

```

Our MainComponent will be a Subcomponent of AppComponent so it can inherit dependencies from the
parent's modules (ThingModule) as well as create some of its own (MainModule) below.  Exposing it's
factory thru the AppComponent interface (see mainFactory() on the AppComponent above) makes this possible.

```kotlin
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
    fun ActivityMainBinding(activity: MainActivity): ActivityMainBinding {
        return ActivityMainBinding.inflate(activity.layoutInflater)
            .apply { activity.setContentView(root) }
    }
    @Provides
    fun MainViewModel(
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
```

<img src="docs/com.example.myapplication.MainComponent.svg" alt="MainComponent.svg"/>

Note: Jetpack ViewModels are lifecycle-aware and thus have special conventions for their initialization.
We must use a ViewModelProvider.Factory.   
