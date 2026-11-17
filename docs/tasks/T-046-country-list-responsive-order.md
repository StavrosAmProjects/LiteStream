# T-046: Responsive country browser and saved country order

- **Goal:** Make country/group browsing respond while navigating and allow country order customization.
- **Affected files:** `CountryBrowseScreen.kt`, `CountryFilterScreen.kt`, `CountryBrowseOrder.kt`, `CountryBrowseOrderLogic.kt`, `MainActivity.kt`, `CountryBrowseOrderLogicTest.kt`, `CHANGELOG.md`.
- **Acceptance criteria:** Focusing a country previews its groups; focusing a group previews its channels; OK/tap still selects/plays as appropriate. Move Up/Down reorders countries, saved per profile in the existing `ipvio_live` preferences, and new provider countries append without discarding prior order.
- **Risk:** A country can disappear from the current provider filter/catalogue and will return to its saved position if it reappears.
- **Documentation:** `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`.
- **Tests:** Added pure order tests; not run. Debug build passed; TV interaction test pending.
- **Manual checks:** In Live TV > Countries, move focus across countries and groups and confirm adjacent panes update. Pin a country to top/second using Move Up/Down; reopen, restart, switch profile, and verify ordering is retained per profile.
- **Status:** Implemented; validation pending.
