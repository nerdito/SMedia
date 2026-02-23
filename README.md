# Media Viewer

A simple Android application for viewing video files from a user-selected folder, with PIN-protected settings.

## Features

- **Video Library**: Browse and play video files from any folder on your device
- **PIN Security**: Protect access to settings with a 4-digit PIN
- **Folder Selection**: Use Android's Storage Access Framework to select any folder
- **Full-Screen Playback**: Tap on any video to play it in full-screen mode
- **Modern UI**: Built with Jetpack Compose and Material Design 3

## Architecture

- **Pattern**: MVVM (Model-View-ViewModel)
- **UI Framework**: Jetpack Compose
- **Navigation**: Navigation Compose
- **State Management**: Compose State with ViewModel
- **Dependency Injection**: Manual DI (simple app, no framework needed)
- **Data Storage**: DataStore Preferences + EncryptedSharedPreferences
- **Media Playback**: Media3 ExoPlayer

## Project Structure

```
app/
├── src/main/
│   ├── java/com/sebsmedia/mediaviewer/
│   │   ├── MainActivity.kt          # Entry point
│   │   ├── App.kt                   # Navigation setup
│   │   ├── data/
│   │   │   ├── model/
│   │   │   │   └── VideoFile.kt      # Video data model
│   │   │   └── repository/
│   │   │       └── MediaRepository.kt # MediaStore query
│   │   ├── ui/
│   │   │   ├── screens/
│   │   │   │   ├── MainScreen.kt     # Video list screen
│   │   │   │   ├── PinScreen.kt      # PIN entry screen
│   │   │   │   ├── SettingsScreen.kt # Settings screen
│   │   │   │   └── VideoPlayerScreen.kt # Video player
│   │   │   ├── components/
│   │   │   │   └── VideoListItem.kt  # List item component
│   │   │   └── theme/
│   │   │       └── Theme.kt          # Material 3 theme
│   │   ├── viewmodel/
│   │   │   ├── MediaViewModel.kt     # Video list state
│   │   │   └── SettingsViewModel.kt  # Settings state
│   │   └── util/
│   │       ├── SecurityManager.kt    # PIN management
│   │       └── SettingsManager.kt   # Folder preferences
│   └── res/                          # Android resources
└── build.gradle.kts                  # App build config
```

## Requirements

- Android API 26+ (Android 8.0 Oreo)
- Android SDK 35
- Java 17

## Building

```bash
# Set JDK 17 path
export JAVA_HOME=/path/to/jdk-17

# Build debug APK
./gradlew assembleDebug

# APK location
# app/build/outputs/apk/debug/app-debug.apk
```

## Usage

1. **First Launch**: Set a 4-digit PIN to protect settings
2. **Main Screen**: View videos from selected folder
3. **Settings**: Tap the gear icon to access settings (requires PIN)
4. **Select Folder**: Choose a folder containing videos
5. **Play Video**: Tap any video to play in full-screen

## Permissions

- `READ_MEDIA_VIDEO` (Android 13+) - Read video files
- `READ_EXTERNAL_STORAGE` (Android 12 and below) - Read video files

## Tech Stack

| Category | Library |
|----------|---------|
| UI | Jetpack Compose BOM 2024.06.00 |
| Navigation | Navigation Compose 2.7.7 |
| Video Playback | Media3 ExoPlayer 1.3.1 |
| Security | Security Crypto 1.1.0-alpha06 |
| Preferences | DataStore Preferences 1.1.1 |
| Image Loading | Coil 2.6.0 |
| Lifecycle | Lifecycle 2.8.3 |

## License

Private - All rights reserved
