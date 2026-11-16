# T-029 — Provider country and group filters

## Goal

Let the user choose ALL, NONE, or CUSTOMIZE after Xtream setup; retain the complete imported playlist while saving a provider-scoped country/group visibility filter. Apply it to normal Live TV, movie, series, and search browsing while preserving favourites and the complete catalogue underneath.

## Plan

- Store a `StringSet` in the existing profile-scoped `ipvio_live` preferences file, keyed by SHA-256 of the provider URL. That file already participates in profile backup; no provider schema or file names change.
- Treat a missing filter key as ALL and a present empty set as NONE. Custom sets identify country and original group pairs.
- Build a pure filter helper over country projections and test all/none, partial selection, unknown items, and source isolation.
- Add an editable three-pane country/group view with draft selection, Cancel, and Done. Never mutate `PlaylistStore` data.
- After Xtream setup, show ALL / NONE / CUSTOMIZE. Load through the existing loader; apply visibility only after load succeeds.
- Collapse optional Xtream backup-server fields by default, reopening them when a pending provider already has saved entries.
- Apply saved filters to normal Live TV, movie, series, and search presentation; keep favourites and all provider data available.

## Affected files

New `CountryFilterLogic.kt`, `CountryFilterStore.kt`, `CountryFilterScreen.kt`, `CountryFilterLogicTest.kt`; `AddPlaylistScreen.kt`, `LiveTab.kt`, `CountryBrowseScreen.kt`, `Profiles.kt`, `DictA.kt`, `WORK_STATE.md`, `CHANGELOG.md`.

## Risks and validation

- A changed provider URL creates a new filter key; refresh of the same provider retains its selection. Hashing prevents credentials from appearing in preference key names.
- NONE hides matching provider content from normal browsing while retaining the complete underlying catalogue; the provider remains editable in settings.
- The existing profile deletion flow must clear `ipvio_live` data with the other profile-scoped preferences.
- Check profile isolation, backup inclusion through existing `ipvio_live`, refresh retention, D-pad draft/cancel/done, and multi-provider isolation on device.
- Add pure tests. Gradle/device checks remain maintainer steps.

## Status

Implemented in `594d312`; profile cleanup follow-up committed as `1ca26e9` on `codex/litestream-tv-fork`. Xtream setup now offers ALL / NONE / CUSTOMIZE; filters live in profile-scoped `ipvio_live` preferences under a hashed provider URL key. The existing profile deletion flow now clears this preferences file with the others. The full provider catalogue stays intact. Normal Live TV, Home, and Search presentation apply the saved filter; favorites and underlying provider data remain available. Customization uses a draft and preserves an existing selection on Cancel. Pure logic tests were added but not run. Profile deletion uses Android SharedPreferences and has a manual device check instead of a JVM test. `git diff --check` passed.

Manual checks for the maintainer:
1. Add an Xtream provider with each choice: ALL shows all content; NONE hides normal provider content; CUSTOMIZE saves the selected countries/groups.
2. On a new Xtream provider, choose CUSTOMIZE, change a selection, and press Cancel; verify the uncommitted filter is discarded and default ALL visibility remains. Repeat and press Done to commit the selected subset.
3. Reopen CUSTOMIZE for an existing provider, change a selection, press Cancel, and verify its previous selection remains. Press Done and verify it updates.
4. Verify filter behavior in Live TV, Movies, Series, and Search; favorites and the source catalogue remain intact.
5. Add a second provider and verify its filter is independent. Switch profile and verify filters are isolated.
   Add it while the existing provider has a nonzero saved-list index, then choose CUSTOMIZE and confirm the new provider's groups appear.
6. Refresh the provider and verify the selection remains; use backup export/import to verify the existing `ipvio_live` backup coverage.
7. With NONE selected, open Live TV > Countries and confirm there is a path to customize again.
8. On a new Xtream provider, confirm backup-server fields are hidden until requested; resume a provider with saved backup entries and confirm they are visible.
9. Create a secondary profile, save a non-default provider filter, delete that profile, and confirm its scoped preferences are cleared with the rest of the profile data.

Not checked here: Gradle/JUnit, Android TV focus, profile deletion, profile/backup round trip, or refresh behavior on a device. Risk: country inference and URL identity depend on provider group labels and stable provider URLs.
