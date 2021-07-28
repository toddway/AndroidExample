The following is a contrived example application that incorporates 
Dagger, Kotlin, Coroutines, Flow & Channel, ViewModel, Compose, and Navigation.
 
It has two primary features:
1. generate Things by clicking a button
2. observe Things that are created. 

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

Next we define an AddThingViewModel to:
1) mediate ui events (e.g. "Add Thing" button clicked) from a view, and
2) broadcast data events (new Thing was generated) that a view can respond to.

To accomplish this, our ViewModel constructor requires instances of both use cases (described above).
```kotlin
class AddThingViewModel @Inject constructor(
    private val observeThings : ObserveThingsUsecase,
    private val generateThing : GenerateThingUsecase
) : ViewModel() {
    fun generateThingButtonClicked() { viewModelScope.launch { generateThing.generateThing() } }
    val thingFlow = observeThings.observeThings()
}
```

The AddThing UI Composable will then:
1) send button click events to the ViewModel and
2) set display text when the ViewModel data changes are observed

```kotlin
@Composable
fun AddThing(viewModel: AddThingViewModel) {
    val lastThingText = rememberSaveable { mutableStateOf("") }
    val thingState = viewModel.thingFlow.collectAsState(null)
    thingState.value?.let { lastThingText.value = "$it" }
    AddThing(viewModel::generateThingButtonClicked, lastThingText.value)
}

@Preview
@Composable
fun AddThing(onClick : () -> Unit = {}, text : String = "") {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = text, Modifier.padding(8.dp), fontSize = 20.sp)
        Button(onClick = onClick) {
            Text(text = "Add a thing")
        }
    }
}
```
Now we can use Dagger to generate the graph of dependencies needed to provide the AddThingViewModel.
In the Application class we initialize a Dagger AppComponent that
binds a @Singleton instance of ThingsLocalDatasource as the implementation for both ObserveThingUsecase and GenerateThingUsecase.
Notice both the AppModule and ThingModule are required to fully build the component.  
If either one were removed we would get a "missing dependency" compiler error.

```kotlin
class App : Application(), FeatureOneComponent.Provider {
    val component by lazy { DaggerAppComponent.factory().create() }
}

@Module
class AppModule {
    @Provides
    @Named("firstThingId") fun provideThingId() : Int = 1

    @dagger.Component(modules = [AppModule::class, ThingModule::class])
    @Singleton
    interface Component {
        @dagger.Component.Factory
        interface Factory {
            fun create(@BindsInstance @Named("appContext") appContext: Context) : Component
        }

        fun addThingViewModel() : Provider<AddThingViewModel>
    }
}

@Module
class ThingModule {
    @Provides
    @Singleton
    fun ThingsLocalDatasource(@Named("firstThingId") thingId : Int) : ThingsLocalDatasource = ThingsLocalDatasource(thingId, 1000L)

    @Provides
    fun ObserveThingsUsecase(datasource: ThingsLocalDatasource) : ObserveThingsUsecase = datasource

    @Provides
    fun AddThingUsecase(datasource: ThingsLocalDatasource) : GenerateThingUsecase = datasource
}
```
### [Code Level Diagram](https://c4model.com/#CodeDiagram) of Injected Object Dependencies for AppComponent
A slanted box indicates a binding instance ([@BindsInstance](https://dagger.dev/api/2.28/dagger/BindsInstance.html)) passed into the component through the factory or builder. 

<img src="docs/com.example.myapplication.AppModule.Component.svg" alt="AppComponent"/>

The component above will likely be useful to several views in our application, but for now we will
use it only to set up our first view.

```kotlin
class TabFragment1 : Fragment() {
    private val viewModel : AddThingViewModel by viewModelStore {
        requireContext().appComponent().addThingViewModel().get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply { setContent { AddThing(viewModel) } }
    }
}
``` 

Note: Jetpack ViewModels are lifecycle-aware and thus have special conventions for their initialization.
We must use a `androidx.lifecycle.ViewModelProvider` so that the view model can be paused, reused, and 
only recreated when required by the lifecycle.  The `viewModelStore` extension method encapsulates the
typical approach we want to use for this.
We use a Dagger `Provider` as an object that knows *how* to create new view model and the `ViewModelStoreOwner`
will decide *when* to create it.   

### [Code Level Diagram](https://c4model.com/#CodeDiagram) of Injected Object Dependencies for AddThingModule.Component
<img src="docs/com.example.myapplication.AddThingModule.Component.svg" alt="AddThingComponent"/>

### [Component Level Diagram](https://c4model.com/#ComponentDiagram) of Local Module Dependencies
The components in this diagram are Gradle modules. Green boxes are Android Modules, red boxes are JVM modules, and orange boxes are Kotlin Multiplatform modules.  Arrows point to the dependency. 

<img src="docs/project.dot.png" alt="Local Dependency Graph"/>

### [Component Level Diagram](https://c4model.com/#ComponentDiagram) of Remote Module Dependencies 
The components in this diagram are Gradle modules.  White boxes are local modules, orange boxes are remote modules. Transitive dependencies are excluded. Arrows point to the dependency.

<img src="docs/dependency-graph-my-generator.png" alt="Remote Dependency Graph"/>

Note: The graph generator doesn't currently work with the updated Gradle version that was required to support Compose.
So for now, the generated graphs are out of date.