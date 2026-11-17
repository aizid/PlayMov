# PlayMov - TMDB Kotlin Multiplatform Companion App

PlayMov is a modern, high-performance Kotlin Multiplatform (KMP) movie companion application targeting Android and iOS.

---

## 🌟 Architectural Overview

PlayMov strictly segregates core responsibilities across cleanly decoupled layers:

- **Domain Layer (`domain/`)**: Pure Kotlin business logic holding model entities, contracts, and repository interfaces (`MonoRepo`).
- **Data Layer (`data/`)**: Data persistence and networking implementations (`MonoRepoImpl`, `AppDatabase`, `FavoriteMovie.sq`).
- **Remote Data Source Layer (`remote/`)**: Ktor HTTP client configured for TMDB API REST calls (`MonoDataSourceImpl`).
- **UI Layer (`ui/`)**: Stateful Compose wrappers, stateless UI content, ViewModels, and state representations colocated per feature:
  - `ui/genre/`: Interactive Genre Explorer with movie & TV genre chips.
  - `ui/discover/`: Genre-filtered movie discover list featuring infinite scroll.
  - `ui/search/`: Instant multi-search across movies, actors, and TV series.
  - `ui/movies/`: Rich Movie Details with backdrop previews, trailers, and local SQLDelight favorite toggles.
  - `ui/movie_list/`: Embedded YouTube Trailer player via `WebView` & Community Reviews screen with empty states and pagination.
  - `ui/splash/`: Dedicated PlayMov Cinema splash entry point.

---

## 🚀 Key Features & Tech Stack

- **UI Framework:** Compose Multiplatform (Android & iOS).
- **Navigation:** Navigation3 (`rememberNavBackStack`, `NavDisplay`, `NavEntry`) with polymorphic serializable routes.
- **Dependency Injection:** Koin (`koin-core`, `koin-compose`, `koin-compose-viewmodel`).
- **Networking:** Ktor Client with `ContentNegotiation`, `kotlinx.serialization`, logging, and automatic TMDB `api_key` injection.
- **Local Database:** SQLDelight (`AppDatabase` with `FavoriteMovie.sq` table, expect/actual driver factory for Android & iOS).
- **Image Loading:** Coil 3 Multiplatform (`coil-compose`, `coil-network-ktor3`).
- **Embedded Web Streaming:** `compose-webview-multiplatform` for embedded YouTube trailer playback.
- **Pagination:** `PagingManager<T>` utility for infinite scrolling across discover lists, search, and user reviews.
- **Secure Preferences:** `KSafe` multiplatform key-value encrypted data storage.

---

## 🛠️ Project Structure

```text
PlayMov/
├── androidApp/          # Android entry point Activity
├── iosApp/              # Xcode iOS application entry point
├── shared/
│   └── src/
│       ├── commonMain/
│       │   ├── kotlin/com/aizid/playmov/
│       │   │   ├── data/             # Repository impls & SQLDelight DB
│       │   │   ├── di/               # Koin modules (Network, Database, Repo, ViewModel)
│       │   │   ├── domain/           # Domain interfaces & models
│       │   │   ├── remote/           # Ktor DataSources & TMDB DTOs
│       │   │   ├── routes/           # Navigation3 Route definitions
│       │   │   ├── ui/               # Colocated Feature Screens (Screen, Content, ViewModel, State)
│       │   │   ├── utils/            # PagingManager, NetworkConfig, Toast, Locale
│       │   │   └── App.kt            # App Root Composable & NavDisplay setup
│       │   └── sqldelight/           # SQLDelight database schemas (.sq)
│       ├── androidMain/              # Android platform expect/actuals
│       └── iosMain/                  # iOS platform expect/actuals
```

---

## ⚙️ Building & Running

### Prerequisites
- Android Studio / IntelliJ IDEA with KMP plugin
- Xcode 15+ (for running iOS target on macOS)
- JDK 17+

### Commands
- **Compile Android Shared Module:**
  ```bash
  ./gradlew :shared:compileAndroidMain
  ```
- **Assemble Android Debug APK:**
  ```bash
  ./gradlew :androidApp:assembleDebug
  ```
- **Run iOS App:**
  Open `iosApp/iosApp.xcodeproj` in Xcode and launch on Simulator or Device.
