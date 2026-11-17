# T-045: Country and group Home shortcuts

- **Goal:** Pin country or country/group entry points to the TV Home screen.
- **Affected files:** `CountryBrowseScreen.kt`, `LiveTab.kt`, `HomeLayout.kt`, `HomeCountryShortcutLogic.kt`, `HomeScreen.kt`, `MainActivity.kt`, `HomeCountryShortcutLogicTest.kt`, `CHANGELOG.md`.
- **Acceptance criteria:** Country and group can be pinned from the country-first browser; Home shortcut opens that country/group directly; shortcuts are profile-scoped via the existing Home preference file; maximum 12; malformed saved rows are ignored.
- **Risk:** A renamed/removed provider group can leave a shortcut pointing to an empty group; users can recreate it from Live TV. Existing preference keys are unchanged.
- **Documentation:** `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`.
- **Tests:** Debug build passed. Added JVM round-trip/malformed-row test; tests not run.
- **Manual checks:** With D-pad, pin a country and a group, open each from Home, play a channel, restart, switch profile, and refresh provider.
- **Status:** Implemented; device verification pending.
