# T-044: Phone provider TXT template

- **Goal:** Help users create the provider file accepted by TV import.
- **Affected files:** `PhoneImport.kt`, `PhoneImportTest.kt`, `CHANGELOG.md`.
- **Acceptance criteria:** Phone setup shows copyable `name/server/username/password` example and downloads a blank `.txt` template without exposing real credentials.
- **Risk:** Clipboard access depends on browser permission; the download remains available as fallback.
- **Documentation:** `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`.
- **Tests:** Debug build passed. Added JVM assertions; tests not run.
- **Manual checks:** Open phone setup, copy example, download template, import edited `.txt` on TV and verify redacted confirmation.
- **Status:** Implemented; device verification pending.
