# T-059 — Playlist management and selective Live TV

## Goal

Make multiple playlists easier to use on TV and keep unselected live channels out of the active catalogue.

## Changes

- Add Live TV playlist choices for the current merged set, all saved playlists, and an individual playlist.
- Add direct Edit on phone and Update actions to Live TV; hide Edit for Stalker portals because the current edit flow does not support them.
- Reshape each Settings > Playlists entry into a labelled card with main/merged state, update, rename, include/exclude, mode, customization, edit, backup-server, and delete actions.
- Add per-provider Country → Category → Content selection from Settings and import. Country order uses the existing device-wide `CountryBrowseOrder`.
- Scan M3U files for preview categories before parsing channels. Xtream and Stalker previews use their category endpoints. After confirmation, the active live catalogue contains only selected groups; the original compressed M3U remains local so selections can be changed later.
- Xtream and Stalker requests load selected live categories. VOD and series retain their existing loading paths.
- Use XMLTV `<channel><icon src="…">` logos as a fallback when a provider or M3U does not provide a channel logo.
- Add tests for country/group selection and M3U live filtering.

## Files

`AddPlaylistScreen.kt`, `CountryFilterLogic.kt`, `CountryFilterScreen.kt`, `Epg.kt`, `HomeScreen.kt`, `LiveTab.kt`, `Models.kt`, `PlaylistStore.kt`, `SettingsHub.kt`, `StalkerApi.kt`, `XtreamApi.kt`, `LoadLogicTest.kt`, `CountryFilterLogicTest.kt`, `CHANGELOG.md`, `docs/TESTING.md`, `docs/tasks/WORK_STATE.md`.

## Validation

- [x] `git diff --check` passes.
- [ ] Run the JVM tests and Android debug build on the maintainer's machine.
- [ ] On Android TV, verify D-pad focus and all playlist choices with two providers, including one hidden from the merged set.
- [ ] Verify Edit on phone, Update, rename, include/exclude, and per-provider selection from Settings.
- [ ] Verify M3U import scanning and cancel behavior; confirm only selected live channels appear, then change the selection and confirm previously unselected channels return.
- [ ] Verify Xtream/Stalker category requests with real providers; verify XMLTV icon fallback on a channel without a provider logo.

## Risks and status

M3U requires downloading and scanning the complete text once because it has no category API. Its full source remains in the app's local compressed cache to allow later selection changes; the active parsed live catalogue is filtered. Xtream/Stalker category labels can be inconsistent, so country/group matching remains best-effort and unknown content stays available through the unknown group. No static Greek/Cypriot logo pack was added because no redistributable source/license was established; the app now uses logos supplied by the playlist or XMLTV. Implemented in the working tree; tests, build, provider behavior, and TV UI remain unverified.
