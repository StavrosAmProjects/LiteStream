# T-043: English-only app interface

- **Goal:** Show app UI in English regardless of saved or device language; preserve provider-supplied text.
- **Affected files:** `Lang.kt`, `SettingsHub.kt`, `PhoneImport.kt`, `RemoteLogic.kt`, `RemoteMore.kt`, `RemoteHub.kt`, `CHANGELOG.md`.
- **Acceptance criteria:** English dictionary index is always used; TV and phone-remote language selectors are absent; local phone pages use English; raw provider/channel names stay unchanged; existing language preference is not deleted.
- **Risk:** English dictionary rows that are missing still fall back to their German source text.
- **Documentation:** `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`.
- **Tests:** Debug build passed. Unit tests and device/phone checks not run.
- **Manual checks:** Launch with German saved preference; inspect start, settings, add-provider and phone setup screens; confirm provider/channel labels are unchanged.
- **Status:** Implemented; device verification pending.
