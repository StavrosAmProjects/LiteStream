# T-048: Phone Live TV browsing and Greek smart-playlist matching

- **Goal:** Make phone Live TV browsing use a thin current-country flag rail, a full-width channel list, and a full-page channel detail with Back/Watch. Support merged channel reordering from the flag menu, shared across profiles. Recognize Greek channel labels in existing smart genres.
- **Affected files:** `LiveTab.kt`, `LiveChannelOrderLogic.kt`, `LiveGenre.kt`, `SportLogic.kt`, `CountryBrowseLogic.kt`, `LiveChannelOrderLogicTest.kt`.
- **Acceptance:** Phone hides the TV detail pane while browsing; tapping a channel opens details before playback; the flag opens category/country navigation and long press offers Move Up/Down; moving affects the merged catalogue and group projections; saved order is device-wide; Greek sports/movie labels classify into smart playlists.
- **Risks:** The compact move history uses stable `(source index, item id)` keys; changing playlist source numbering can make an old move unavailable. Moves replay over refreshed provider order, so new channels retain their provider position unless moved. The history is held in a device-wide preference file and does not change provider records or profile data.
- **Documentation:** `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`.
- **Tests:** JVM test added for order and Greek classification; not run here. Manual: open Live TV on phone; confirm thin flag rail and full-width rows; tap channel, Back, then Watch; long-press flag and move channel across groups; switch profile and confirm order remains; inspect Greek Sports/Movies categories. TV D-pad regression check remains required.
- **Status:** Implemented; debug build succeeded and APK installed/launched on the phone. Feature interaction checks and Android TV checks remain pending.
