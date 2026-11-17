# T-047: Live TV category tap selection

- **Goal:** Make touch taps on Live TV category rows select the tapped list before opening its channels.
- **Affected files:** `LiveTab.kt`, `CountryBrowseScreen.kt`, `CHANGELOG.md`.
- **Acceptance criteria:** Tapping Favorites or a provider category opens that tapped category; tapping Countries opens its browser; remote focus/OK behavior remains intact. Country reorder and pin buttons fit the country pane on a phone in landscape.
- **Risk:** The first tap on a different category now selects and opens it immediately; this matches expected phone touch behavior.
- **Documentation:** `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`.
- **Tests:** Debug build passed. On USB-connected phone, tapping Favorites selected the Favorites list; tapping Greece showed its group; tapping `EU | GREECE VIP` showed its channels. No JVM logic changed. TV remote behavior not checked.
- **Manual checks:** On the phone, tap Favorites, a provider group and Countries; confirm each opens the matching content. On TV, verify D-pad then OK still works.
- **Status:** Implemented; Android TV remote check pending.
