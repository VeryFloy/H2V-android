# H2V Messenger — Android

Native Android client for the H2V Messenger platform, built with Kotlin and Jetpack Compose.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin 2.0 |
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | Clean Architecture (UI → Domain → Data) |
| **DI** | Hilt |
| **Network** | Retrofit 2 + OkHttp 4 |
| **WebSocket** | OkHttp WebSocket |
| **Database** | Room |
| **Storage** | DataStore Preferences |
| **Images** | Coil |
| **Navigation** | Navigation Compose |
| **Design** | Liquid Glass (custom dark theme) |

## Architecture

```
app/src/main/java/com/h2v/messenger/
├── core/           # DI modules, network, database, utilities
│   ├── di/         # Hilt modules (Network, Database, Repository)
│   ├── network/    # ApiService, WebSocketManager, TokenManager, AuthInterceptor
│   ├── database/   # Room DB, DAOs, Entities
│   └── util/       # Resource sealed class
├── domain/         # Pure Kotlin — no Android dependencies
│   ├── model/      # User, Chat, Message
│   ├── repository/ # Repository interfaces
│   └── usecase/    # Use cases (auth, chat, message, user)
├── data/           # Implementation layer
│   ├── remote/     # DTOs, mappers (API → Domain)
│   ├── local/      # Mappers (Entity ↔ Domain)
│   └── repository/ # Repository implementations
└── ui/             # Compose screens
    ├── theme/      # Liquid Glass design system
    ├── components/ # GlassCard, GlassButton, GlassTextField, GlassBottomBar, Avatar
    ├── navigation/ # AppNavigation, Routes
    ├── auth/       # OTP login (email → code → nickname)
    ├── chatlist/   # Chat list + user search
    ├── chat/       # Messaging (bubbles, input, typing)
    └── profile/    # Profile view/edit + logout
```

## Setup

1. Open the `android/` folder in Android Studio
2. Sync Gradle
3. Run on device/emulator (min API 26)

Backend URL is configured in `app/build.gradle.kts` → `BuildConfig.BASE_URL`.

## Features (MVP)

- [x] Email OTP authentication (3-step flow)
- [x] Chat list with unread badges and online indicators
- [x] Real-time messaging via WebSocket
- [x] Message status (sent/delivered/read)
- [x] Typing indicators
- [x] User search and direct chat creation
- [x] Profile view and edit
- [x] Liquid Glass UI design system
- [x] Room cache (offline-first architecture)
- [x] Auto-reconnect WebSocket

## License

GNU General Public License v3.0 — see [LICENSE](../LICENSE).
