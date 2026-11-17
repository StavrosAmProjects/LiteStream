# T-042 — Terminal debug build

## Goal

Produce a debug APK from the pushed fork branch so the maintainer can install it on Android TV later.

## Build environment

- OpenJDK 17 installed from Homebrew.
- Android SDK command-line tools already present; Android 37.0 platform and SDK Build-Tools 37.0.0 installed. Gradle also installed Build-Tools 36.0.0 as requested by the Android Gradle Plugin.
- SDK package licenses accepted to install the requested build components.
- The repository's `gradlew` is not executable in this checkout; invoked with `bash ./gradlew`.

## Build result

- First `:app:assembleDebug` attempt failed Kotlin compilation. Fixed the constructor calls to `PhoneImport`, added the missing `kotlinx.coroutines.launch` import, corrected the SSL exception package, and used stable local state for nullable remote-server access.
- Command: `ANDROID_HOME="$HOME/Library/Android/sdk" ANDROID_SDK_ROOT="$HOME/Library/Android/sdk" JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/opt/openjdk@17/bin:$PATH bash ./gradlew :app:assembleDebug`
- Result: **BUILD SUCCESSFUL**.
- APK: `app/build/outputs/apk/debug/app-debug.apk` (24 MB).
- Package: `com.litestream.tv.test`; version `0.1.0`; min API 24; target API 37.
- `apksigner verify --verbose`: passed; one signer, APK Signature Scheme v2.
- SHA-256: `72067be8d3869a6d57173e27781bcce3eb125ba545da2dff9a6cc1a89a019790`.
- `git diff --check`: passed before build.

## Not run

- Unit tests, lint, signed release build, and device install/playback checks.
- `adb devices` showed no attached or paired device. The user plans to install on the TV later; do not attempt installation until the TV is available and the user is ready.

## Status

Debug APK built and signature-verified. TV validation remains in `T-041-android-tv-smoke-check.md`.
