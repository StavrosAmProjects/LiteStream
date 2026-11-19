# T-051: Live TV and Home density

## Goal

Make Live TV channel discovery denser and give users a familiar list option, while surfacing more Home content without removing the featured title.

## Affected files

- `app/src/main/java/com/ipvio/stream/LiveTab.kt` — saved Cards/List choice and list rendering from the existing channel row component.
- `app/src/main/java/com/ipvio/stream/Components.kt` — optional compact presentation for Live TV cards; other callers keep the current presentation.
- `app/src/main/java/com/ipvio/stream/HomeScreen.kt` — reduce featured area height on Home and title tabs.
- `CHANGELOG.md` — user-visible change.

## Acceptance criteria

- Live TV start view offers clear Cards and List controls.
- Card mode uses smaller cards with less artwork height.
- List mode shows all currently visible merged channels using the existing channel row, EPG and remote-focus behavior.
- The chosen Live TV home mode persists in the existing profile-scoped `ipvio_live` preferences.
- Home and title tabs retain the featured title while showing more content sooner.
- Playback, playlist records and existing card sizing preferences remain unchanged.

## Risks

- List mode displays the merged visible channel catalogue as one continuous list; country/category navigation remains available in the existing left navigation.
- TV remote focus and the new density need device checks.

## Validation

- `git diff --check`: passed.
- `:app:testDebugUnitTest`: passed as part of the 432-test suite.
- Debug build: `:app:assembleDebug` passed.
- Device-only manual checks: switch Cards/List, confirm the choice survives leaving/reopening Live TV and app restart, select/play a channel from either mode, test OK/Back/D-pad and long-OK favorites, and check Home shortcuts/rows are visible earlier on Android TV.

## Status

Implemented; Android TV focus/layout checks remain open.
