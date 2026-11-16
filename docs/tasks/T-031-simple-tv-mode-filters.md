# T-031 — Apply provider filters in the simple TV mode

## Goal

Keep IPVIO's existing Boomer mode as the simple TV home candidate and make its visible catalogue respect saved provider country/group choices.

## Analysis

`BoomerHome` already provides large D-pad tiles for live channels, resume, favorites, selected VOD, and new movies/series. A second home stack would duplicate existing TV navigation. Its `all` input came directly from the complete provider catalogue and bypassed the new presentation filter.

## Change

Filter the input only at the `BoomerHome` call site in `MainActivity.kt`, using the same source mapping and `CountryFilterLogic` as Home/Search/Live TV. Playlist data and persistence remain untouched.

## Acceptance criteria

- [x] Reuse existing Boomer mode; add no separate home/navigation stack.
- [x] Apply provider visibility filters to Boomer's derived rows.
- [x] Preserve underlying playlist data and existing playback callbacks.
- [ ] Confirm D-pad behavior and row contents on Android TV/Fire TV.

## Risks and validation

Favorites or selected Boomer tiles hidden by a provider filter may not appear in the filtered simple-mode presentation; clearing the filter restores them. `git diff --check` passed. Gradle/JUnit and device checks were not run.

Manual check: enable Boomer mode, select a provider country/group filter, verify Live TV and VOD rows show only selected content, then switch to ALL and confirm hidden items return and playback/channel switching still work.

## Status

Implemented and committed as `594d312` on `codex/litestream-tv-fork`.
