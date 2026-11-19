# T-061: Live TV controls, Home favourites, and category restoration

**Status:** Implemented; debug build installed on the maintainer's phone. Manual screen checks pending.

## Goal

Keep playlist actions available throughout Live TV, make phone browsing visibly country/category based, allow Home category shortcuts to be reordered like other Home rows, and restore categories excluded from a provider's active catalogue.

## Changes

- `LiveTab.kt`: put Update playlist, Switch playlist, and Edit playlist in a persistent toolbar above every channel list, including country-filtered phone lists. Switch offers an individual playlist, merged playlists, or all playlists. The phone flag rail now exposes a category picker when the selected country has multiple groups; phone channel rows use the compact row size. Live TV customization now uses a complete provider category preview and reapplies the saved selection.
- `HomeLayout.kt`: add Favourite Categories as a normal movable/visibility-controlled Home block; insert it after Live TV when loading older saved row orders.
- `HomeScreen.kt`: render favourite category shortcuts in that block and keep initial focus on a visible shortcut.
- `PlaylistStore.kt`: use Stalker category API previews, keep a cached preview as an offline fallback, scan M3U sources if the full compressed source is missing, and fetch the M3U again when an older active import cannot restore locally.
- `SettingsHub.kt`: report whether applying the custom category selection succeeded.

## Data and user impact

No existing preference names or provider storage formats changed. Home row ordering continues in the existing `ipvio_homelayout` preference; the new `favcats` ID is inserted after `chnow` only when absent from a saved order. Provider filter selections remain in the existing profile-scoped preference. Rebuilding an old M3U selection can use network data if the complete local source is unavailable.

## Validation

- `:app:assembleDebug`: passed.
- `git diff --check`: passed.
- Debug APK installed to the connected phone `192.168.1.58:39119`.
- The phone display was asleep during the final screenshot attempt, so visual interaction and post-install screen confirmation are still pending.
- Unit tests were not run.

## Maintainer checks

1. Wake the phone, open Live TV, tap a country flag with multiple groups, and confirm the category picker appears below the playlist actions.
2. Select a category and confirm the list shows compact rows; choose All categories to restore the country's full list.
3. Open a non-Home Live TV category and confirm Update, Switch, and Edit remain visible.
4. In Settings > Customize Home, show Favourite Categories and move it below Live TV. Confirm category shortcuts open the saved country/category.
5. In Settings > Playlists > Customize live TV, reselect a previously excluded category. Confirm the success/failure message and that the category's channels return. Try a provider with no retained local M3U copy if available to verify network fallback.

## Risks and limits

Restoring a missing M3U source requires a provider network request. A Stalker provider that is offline can only show cached categories; omitted categories cannot be restored until its API responds. Phone UI interaction and Android TV focus behavior remain unverified on hardware.
