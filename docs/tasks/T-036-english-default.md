# T-036 — English default for fresh installs

## Goal

Start LiteStream TV with an English menu when no language preference exists, while preserving any explicit user choice. Android TV is the primary target.

## Affected files

- `app/src/main/java/com/ipvio/stream/General.kt` — default only when the language preference key is absent.
- `app/src/main/java/com/ipvio/stream/LanguageDefaultLogic.kt` and its JVM test — lock the missing-only default behavior.
- `CHANGELOG.md` — record the behavior.
- `docs/tasks/WORK_STATE.md` — handoff state.

## Acceptance criteria

- [x] A fresh install with no saved language starts in English.
- [x] Saved language settings are not overwritten.
- [x] Change is English source text only; no dictionary keys or stored values are renamed.

## Risks and validation

No migration: existing stored values remain authoritative. Confirm on Android TV: fresh install shows English; change to another language, restart, and confirm it remains selected; choose Automatic/device and confirm that also remains selected. No build/device test was available in this session.

## Status

Implemented in the working tree; `LanguageDefaultLogicTest` was added but not run. Build and Android TV checks are pending.
