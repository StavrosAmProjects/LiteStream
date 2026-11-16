# T-034 — Simplify the first-run TV home

## Goal

Make LiteStream TV's default Home easier to scan from a couch while retaining the existing screen and all optional content.

## Plan

- Keep the fixed hero, quick navigation tiles, continue-watching row, live favorites, and live-now row.
- Hide recommendation, history, watchlist, and extra movie/series category rows only when no saved Home layout exists.
- Reuse `HomeLayout` preferences and its existing Home customization screen; do not add another navigation mode or persistence key.
- Test the pure default row set and provide a TV manual check for focus and reveal behavior.

## Affected files

`HomeLayout.kt`, new `HomeLayoutDefaults.kt`, new `HomeLayoutDefaultsTest.kt`, `docs/tasks/WORK_STATE.md`, `CHANGELOG.md`.

## Acceptance criteria

- [x] New installs show only the existing hero, quick tiles, resume, favorites, and live-now Home blocks when data is available.
- [x] Users can still reveal and reorder optional Home blocks in the existing customization page.
- [x] Existing saved Home layouts load unchanged.
- [x] A pure JVM test checks the default hidden/visible block set.

## Risks and validation

- Optional VOD recommendations are less prominent on a fresh install; users can enable them in Settings > Home customization.
- Android TV/Fire TV D-pad focus and the customization path still need device validation.
- Gradle/JUnit and device checks were not run in this environment.

## Status

Implemented on `codex/litestream-tv-fork`; the commit reference is recorded in `WORK_STATE.md`. `git diff --check` passed. Manual validation: on a fresh install, confirm the Home has the five retained blocks, open Settings > Customize home, enable a hidden row and confirm it appears, then import a backup with saved Home layout preferences, restart the app, and verify its choices remain unchanged.
