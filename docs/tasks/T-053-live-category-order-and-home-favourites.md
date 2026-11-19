# T-053 — Live category order and Home favourites

- **Goal:** Keep the provider playlist selector at the top of Live TV, show provider channel groups before automatically detected smart playlists, and make Home favourites easy to find.
- **Affected files:** `app/src/main/java/com/ipvio/stream/LiveTab.kt`, `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`.
- **Acceptance criteria:** Provider groups and saved group favourites appear before automatic genre playlists. Home's existing live-favourites row and Favorites shortcut remain available.
- **Risks:** Category position changes; no provider data or playback behavior changes.
- **Documentation:** This task and the Unreleased changelog.
- **Test results:** `git diff --check` passed. JVM tests and Android build not run in this turn.
- **Status:** Source change complete. The TV currently has zero favorites and its saved Home layout hides both the Favorites row (`favch`) and quick tiles (`tiles`); enable those in Home customization after playback ends. Build/install/device verification pending.
