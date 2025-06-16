## Holy Music - MVVM - Clean Architecture   

## Branches

- **main:** Jetpack Compose using Dagger Hilt

## Migrate XML Project to Jetpack Compose

- **Update Android Gradle Plugin and Kotlin Plugin:** Using the latest versions of the Android Gradle Plugin and Kotlin Plugin.
- **Add Compose Dependencies:** build.gradle (module-level)
- **Set Up Compose Application:** Create a new @Composable function
- **Replace XML Layouts with Compose Code:** For example, if you had an XML layout with a TextView, replace it with a Compose Text element.
- **Adopt Compose Components:** Replace XML-based UI components with their Compose equivalents.
- **Integrate Compose Navigation:** Migrate from XML-based navigation to Compose Navigation.
- **Migrate UI Logic:** Update UI logic to use Compose's state management.
- **Migrate Resources:** Migrate string resources, colors, drawable.
- **Update Gradle Plugin Versions:** Update your Gradle dependencies accordingly.
- **Testing:** Write tests for your Compose UI using the Compose testing library.
- **Documentation and Learning:** - Refer to the official Jetpack Compose documentation and samples. Learn about Compose concepts like Composables, state management, and navigation.

## Migrate Dagger2 Project to Dagger-Hilt

- **Add Hilt Dependencies:** build.gradle (module-level)
- **Apply Hilt Gradle Plugin:** build.gradle (apply plugin: 'dagger.hilt.android.plugin').
- **Update Dagger Annotations:** Replace Dagger annotations with Hilt annotations where needed, For example, replace @Component with @HiltComponent.
- **Add Hilt Android Application:** Replace Dagger's DaggerAppComponent with Hilt's Hilt_AppComponent.
- **Annotate Android Application Class:** Annotate your Application class with @HiltAndroidApp.
- **Replace Dagger Android Modules:** Replace Dagger Android modules with Hilt Android entry points.
- **Update Injection in Activities/Fragments:** Replace Dagger with Hilt for activity or fragment injection.
- **Update ViewModel Injection:** Replace Dagger ViewModelFactory and ViewModelKey with Hilt's HiltViewModel and @ViewModelInject.
- **Update Dagger Android Testing Components:** Replace DaggerMyTestComponent with HiltTestApplicationComponent.

  ## Major Highlights

- **Jetpack Compose** for modern UI
- **Offline caching** with a **single source of truth**
- **MVVM architecture** for a clean and scalable codebase
- **Kotlin** and **Kotlin DSL**
- **Dagger Hilt** for efficient dependency injection.
- **Retrofit** for seamless networking
- **Room DB** for local storage of articles
- **Coroutines** and **Flow** for asynchronous programming
- **StatFlow** for streamlined state management
- **Pagination** to efficiently load and display articles
- **Navigation** for smooth transitions between screens
- **Coil** for efficient image loading

<p align="center">
<img alt="mvvm-architecture"  src="https://github.com/nitinlondhe2113/NewsApp-MVVM-Architecture/assets/53563712/bcf9f205-5508-48ac-922e-6932415311b3">
</p>

- ## Feature implemented:
- NewsApp
- Instant search using Flow operators
    - Debounce
    - Filter
    - DistinctUntilChanged
    - FlatMapLatest
- Offline data
- Pagination

## Dependency Use

- Jetpack Compose for UI: Modern UI toolkit for building native Android UIs
- Coil for Image Loading: Efficiently loads and caches images
- Retrofit for Networking: A type-safe HTTP client for smooth network requests
- Dagger Hilt for Dependency Injection: Simplifies dependency injection
- Room for Database: A SQLite object mapping library for local data storage

##  Dependency Used:

- UI
```
implementation ("androidx.compose.ui:ui")
implementation ("androidx.compose.ui:ui-graphics")
implementation ("androidx.compose.ui:ui-tooling-preview")
implementation ("androidx.compose.foundation:foundation")
implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
implementation ("androidx.activity:activity-compose:1.8.2")
implementation(platform("androidx.compose:compose-bom:2023.08.00"))
```

- Material3
```
implementation ("androidx.compose.material3:material3:1.1.2")
```

- Navigation
```
implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")
implementation ("androidx.navigation:navigation-compose:2.7.6")
```

- Coil for image loading
```
implementation ("io.coil-kt:coil-compose:2.4.0")
```

- Retrofit for networking
```
 implementation("com.squareup.retrofit2:retrofit:2.9.0")
 implementation("com.squareup.retrofit2:converter-gson:2.9.0")
 implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
```

- Dagger hilt for dependency injection
```
 implementation("com.google.dagger:hilt-android:2.44")
 kapt("com.google.dagger:hilt-android-compiler:2.44")
```

- Room database
```
implementation ("androidx.room:room-runtime:2.5.0")
kapt ("androidx.room:room-compiler:2.6.1")
// optional - Kotlin Extensions and Coroutines support for Room
implementation ("androidx.room:room-ktx:2.5.0")
```
