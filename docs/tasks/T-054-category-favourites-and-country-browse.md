# T-054 — Category favourites and country browsing

- **Goal:** Put saved category favourites under the playlist selector, show those favourites on Home as flag shortcuts, and make the country/group/channel browser clearer and quicker for large country catalogues.
- **Requested behavior:** New category favourites appear at the start of the favourites block; Home shortcuts retain that order. Selecting a country shows its flag, then its provider categories, then the channels. Category rows are denser and have page-scroll controls.
- **Affected files:** `LiveTab.kt`, `CountryBrowseScreen.kt`, `HomeScreen.kt`, `HomeCountryShortcutLogic.kt`, `HomeCountryShortcutLogicTest.kt`, `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`.
- **Preserved behavior:** Provider catalogue, category favourites storage, country classification, playback, and playlist setup remain unchanged. The existing local `secrets.properties` is consumed by Gradle; it is not copied to Git or directly to the TV.
- **Risks:** Page controls add two focusable targets in the category column. Home shortcut opens the existing country/group/channel browser.
- **Tests:** `:app:testDebugUnitTest` passed (433 tests); `:app:assembleDebug` passed; `git diff --check` passed.
- **Manual TV checks:** Installed and launched on the Android TV over wireless ADB. Home showed GR|Sports and GR|General with Greek flags. Live TV showed both immediately below the playlist selector. Country browser showed flag-labelled countries and the Countries → Categories → Channels panes. Page-scroll controls compile; interaction and exact D-pad focus behavior were not manually exercised.
- **Secrets:** Local `secrets.properties` is Git-ignored. The build included the configured TMDB API key and OMDB key; no secret values were printed or committed. TMDB API read access token is not currently consumed by this app build.
- **Status:** Implemented, built, and installed; primary ordering and Home shortcuts verified on TV.
