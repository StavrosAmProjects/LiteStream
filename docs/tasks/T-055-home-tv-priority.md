# T-055 — Home TV priority

- **Goal:** Make Home feel like a clear TV starting point rather than a VOD feature page.
- **Changes:** Home now places the header and personal country/category shortcuts before its primary navigation row. The default Home block order puts quick access, favourite channels, and current Live TV ahead of the featured VOD hero. The hero is shorter, movable, and hideable through existing Home customization. Entry focus targets the first favourite category, then a pinned country, then Live TV.
- **Preserved:** Provider data, playback, favourites, profile storage, saved custom Home order, and all destinations remain intact. Existing saved row order is not migrated or overwritten.
- **Files:** `HomeScreen.kt`, `HomeParts.kt`, `HomeLayout.kt`, `Nav.kt`, `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`.
- **Validation:** `git diff --check` passed. No build, automated tests, or device validation was run in this turn.
- **Manual TV checks:** Confirm first focus lands on GR|Sports (when it is the first favourite), otherwise the first pinned country, otherwise the Live TV tile. Confirm primary TV shortcuts appear before the featured VOD hero. In Home customization, move or hide the hero and confirm its changes persist. Verify a previously saved Home order remains unchanged.
- **Status:** Implemented; device validation pending.
