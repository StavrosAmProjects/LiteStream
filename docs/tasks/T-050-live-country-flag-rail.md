# T-050: Live TV country flag rail

## Goal

Make country selection visible and direct in the compact Live TV navigation, and make Home pinning visibly confirm completion.

## Affected files

- `app/src/main/java/com/ipvio/stream/LiveTab.kt` — country flag rail and country-filtered merged channel list.
- `app/src/main/java/com/ipvio/stream/CountryBrowseScreen.kt` — pin confirmation feedback.
- `app/src/main/java/com/ipvio/stream/CountryBrowseLogic.kt` — recognize regional-indicator country flags used in provider labels.
- `CHANGELOG.md` — user-visible change.

## Acceptance criteria

- Country flags appear in the Live TV rail, in the existing saved country order.
- Selecting a flag shows that country's channels from the current merged provider list.
- The globe continues to open country/category browsing; All restores the full list.
- Long-pressing a flag opens the existing global channel reorder actions.
- Pinning a country or group shows an explicit success message and keeps the existing per-profile shortcut storage.
- Provider catalogue and persisted filtering data are not modified.

## Risks

- Flag emoji rendering varies by device/font; country names are exposed as accessibility descriptions.
- Device-specific focus/navigation still requires phone and Android TV checks.

## Validation

- `git diff --check`: passed.
- `:app:testDebugUnitTest`: passed as part of the 432-test suite.
- Debug build: `:app:assembleDebug` passed.
- Install: succeeded on USB-connected device `0fe02816`; app launched.
- Device checks: visual feature check remains pending because ADB lost the phone after launch and the app resumed a live stream. On phone, verify the scrollable flag rail, country channel filtering, All reset, globe browser, long-press reorder, and Home pin confirmation/shortcut. On Android TV, verify existing category navigation and D-pad behavior remain unchanged.

## Status

Implemented; phone and Android TV interaction checks remain open.
