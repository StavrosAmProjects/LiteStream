# T-052: TV install readiness check

## Goal

Verify the current feature branch builds, passes its JVM suite, and produces a signed debug APK with an Android TV launcher entry.

## Affected files

- `app/src/main/java/com/ipvio/stream/CountryBrowseLogic.kt` — fix flag-only country recognition found by the suite.
- `app/src/main/java/com/ipvio/stream/PhoneImport.kt` — discard unread request input before replying on rejected one-request HTTP calls, avoiding TCP resets.
- `app/src/test/java/com/ipvio/stream/PhoneImportTest.kt`, `ProviderImportLogicTest.kt`, `RemoteLogicTest.kt`, `Round20Test.kt`, `Round22Test.kt` — align constructor calls and assertions with current APIs/English labels.
- `CHANGELOG.md` and task state — record fixes and validation.

## Acceptance criteria

- All JVM tests pass.
- Debug APK assembles and verifies its signature.
- APK manifest reports the LiteStream test package and Android TV launcher category.
- App installs and starts on the available Android device.

## Validation

- `git diff --check`: passed.
- `:app:testDebugUnitTest`: 432 tests passed, no failures or skips.
- `:app:assembleDebug`: passed.
- `apksigner verify`: passed with APK Signature Scheme v2.
- `aapt dump badging`: package `com.litestream.tv.test`, version `0.1.0`, min API 24, target API 37; manifest declares `LEANBACK_LAUNCHER` and optional `android.software.leanback`.
- Installed and launched on USB phone `0fe02816`; process remained running.
- No Android TV was connected; remote focus and playback on TV remain unverified.

## Status

Build and automated checks complete; TV device check remains open.
