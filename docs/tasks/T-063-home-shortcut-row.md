# T-063: Remove the extra pinned shortcut row from Home

- **Goal:** Show Favourite Categories without a second row of pinned country/group shortcuts above the Home tiles.
- **Affected files:** `HomeScreen.kt`, `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`.
- **Acceptance criteria:** Home no longer renders the separate pinned country/group shortcut row. Favourite Categories remains where the user placed it and continues opening the selected category. Home's initial focus no longer targets the removed row.
- **Risk:** Previously saved shortcut values remain stored for compatibility, but are no longer rendered on Home.
- **Tests:** Verify Home shows no extra country/group shortcut row, Favourite Categories remains visible and opens its selected category, and initial focus lands on Favourite Categories or the tiles.
- **Status:** Implemented; build/device verification pending.
