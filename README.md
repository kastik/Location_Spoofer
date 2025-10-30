# 🛰️ Location Spoofer

Location Spoofer is an advanced **Android application** for simulating GPS locations, routes, and motion patterns.  
It’s designed for **developers**, **testers**, and **researchers** who need to emulate realistic movement without physically traveling.

Built from scratch using **Clean Architecture** and **Jetpack Compose** it separates logic into clear layers — `domain`, `data`, and `presentation` — with fully testable, modular components.

---

## ✨ Features

- 📍 **Live GPS Spoofing**
    - Instantly spoof to any coordinate on the map.
    - Seamless integration with Google's Maps APIs (Route computation, Place searching).

- 🗺️ **Route Simulation**
    - Emulate routes using Google Maps **Routes API**.

- 💾 **Data Persistence**
    - Saves routes, places, and preferences with **Jetpack DataStore (Proto)**.

- 🧩 **Modular Architecture**
    - `:locationSpoofer` — Main Android app
    - `:proto` — Protobuf definitions for Google Maps APIs and DataStore
    - `:tests` — Unit test module
    - `:androidTests` — Instrumentation test module
    - `:baselineprofile` — Baseline profile & macrobenchmark performance tests

- 🧠 **Clean Architecture**
    - Domain layer is **platform-agnostic**.
    - Data layer handles **gRPC**, **DataStore**, and **Android services**.
    - Presentation layer uses **Compose**, **Hilt**, and **StateFlows**.

- 🌙 **Dynamic Theming**
    - Full **Material 3** support with dynamic color.
    - Light / dark / system themes.

---

## 🧱 Architecture Overview

```mermaid
flowchart TD
    A[UI Layer\n(Compose Screens)] -->|StateFlow, ViewModel| B[Presentation Layer]
    B --> C[Domain Layer\n(UseCases, Models)]
    C --> D[Data Layer\n(Repositories, DataSources)]
    D --> E[gRPC APIs\n(Google Maps Routes, Places, Geocoding)]
    D --> F[DataStore Proto\n(Saved Places, Routes, Preferences)]
    E -->|Network| G[Google Maps Platform]
```

---

## 🧰 Tech Stack

| Category | Technologies |
|-----------|---------------|
| **Language** | Kotlin 2.2, Coroutines, Flow |
| **UI** | Jetpack Compose, Material 3, Navigation, Maps Compose |
| **Architecture** | Clean Architecture, Hilt DI, MVVM |
| **Persistence** | DataStore (Proto Serialization) |
| **Networking** | gRPC, Google Maps Routes / Places / Geocoding APIs |
| **Testing** | JUnit 5, MockK, Turbine, Hilt Testing, Baseline Profiles |
| **Build Tools** | Gradle 8.13, KSP, Protobuf Plugin |
| **Other** | Dynamic Themes, Compose Preview System, Kotlin Serialization |

---

## 🧩 Module Structure

```
locationSpoofer/
├── locationSpoofer/ (root module)
│   ├── ui/ – Compose screens, state, navigation
│   ├── domain/ – Models, use cases, interfaces
│   ├── data/ – Repositories & DataSources (local + remote)
│   ├── service/ – Location spoofing & route emulation services
│   └── di/ – Hilt modules
│
├── proto/
│   ├── google/ – gRPC-based Google Maps API protos
│   ├── kastik/ – Proto models for SavedPlaces, SavedRoutes, UserPreferences (DataStore Models)
│   └── build.gradle.kts
│
├── tests/
│   ├── data/ – Unit tests for data layer
│   ├── domain/ – Unit tests for domain layer
│   └── build.gradle.kts
│
├── androidTests/
│   ├── src/main/java/.../InstrumantationTest.kt – Instrumentation tests
│   └── build.gradle.kts
│
└── baselineprofile/
    ├── src/main/java/.../BaselineProfileGenerator.kt
    └── build.gradle.kts
```

---

## ⚙️ Setup

### Prerequisites
- Android Studio **Ladybug | 2025.1.4+**
- JDK **17+**
- Android SDK **36**
- A valid **Google Maps API key**
- A properly configured **`google-services.json`** file matching the package name

### Steps
1. Clone the repository:

2. Add your Maps API keys:
   ```
   locationSpoofer/secrets.properties
   MAPS_API_KEY=your_api_key_here
   PLACES_API_KEY=your_api_key_here
   ROUTES_API_KEY=your_api_key_here
   ```
---

## 📱 Usage

1. **Search a location** or select one on the map.
2. **Tap "Spoof"** to start spoofing your position.
3. **Load or record a route**, then **press play** to emulate movement.
4. Adjust **speed**, **loop**, and **reset behavior** from settings.
5. Watch your device location update in real time.

---

## 🧑‍💻 Developer Notes

- `EmulateRouteUseCase` handles polyline decoding, speed interpolation, and looping logic.
- `MockLocationService` interacts with `LocationManager` to emit fake GPS updates.
- `DataStore` serializes models via `kotlinx.serialization.protobuf`.
- `SavedPlaces`, `SavedRoutes`, and `UserPreferences` are strongly typed via generated `.proto` models.

---

## 📄 License

```
Copyright (c) 2025 Kostas Papastathopoulos

Licensed under the MIT License.  
You may use, copy, modify, and distribute this software provided that
the above copyright notice and this permission notice appear in all copies.
```

---

## 🧭 Author

**Kostas Papastathopoulos**  
📧 [kostas21422112@gmail.com](mailto:kostas21422112@gmail.com)  


---

> _“Simulate smart. Move anywhere.”_
