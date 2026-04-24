# Gardien Android App

This directory contains the Phase 1 Android application scaffold.

## Structure

```
app/
├── build.gradle.kts
└── src/
    └── main/
        ├── AndroidManifest.xml
        ├── java/dev/gardien/app/
        │   ├── GardienApp.kt
        │   ├── MainActivity.kt
        │   ├── capture/
        │   ├── dataset/
        │   └── storage/
        └── res/
```

## Build

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug --no-daemon
```

Phase 1 capture services are disabled by default and gated behind the research capture toggle in the debug app. The synthetic harness logs metadata only: source, capture type, content length, timestamp, and synthetic flag.
