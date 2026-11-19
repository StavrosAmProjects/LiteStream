# T-062: Country and category row actions

- **Goal:** Make country and category reordering and Home pinning available directly from their rows.
- **Affected files:** `CountryBrowseScreen.kt`, `docs/tasks/WORK_STATE.md`, `CHANGELOG.md`.
- **Acceptance criteria:** Long-pressing a country or category highlights it and opens Move Up, Move Down, and Pin/Unpin to Home actions. TV Channel Up/Down reorder the focused country, category, or channel. Existing saved country/category order, merged channel order, and Home shortcut storage are reused.
- **Risk:** Remote manufacturers may map Channel Up/Down differently; touch long-press timing depends on Android's normal gesture handling. No provider catalogue or playback data is changed.
- **Documentation:** Changelog and work state updated.
- **Tests:** Manual Android TV steps: long-press/select each row, move up/down, pin/unpin, confirm Home shortcut order and persistence after relaunch; test Channel Up/Down on a country, category, and channel.
- **Status:** Implemented; build/device verification pending.
