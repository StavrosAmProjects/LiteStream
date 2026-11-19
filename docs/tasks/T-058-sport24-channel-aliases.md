# T-058 — Sport24 broadcaster matching and attribution

## Goal

Improve Sport24-to-playlist channel matching when broadcaster names differ by spacing, provider branding, or quality tags; show the project owner using the GitHub alias only.

## Changes

- Canonicalize common Nova Sports, Magenta/Cosmote, and Sports/Sport spellings before matching.
- Keep numeric channel identifiers exact and reject partial numeric matches such as Sport 1 inside Sport 12.
- Add matching tests for spacing, aliases, and exact channel numbers.
- Credit the project through `@StavrosAmProjects`; retain thanks to Emrodev/IPVIO.

## Files

`README.md`, `CHANGELOG.md`, `Sport24Logic.kt`, `Sport24LogicTest.kt`, `docs/tasks/WORK_STATE.md`, `docs/tasks/T-057-sport24-home-and-attribution.md`.

## Validation

- [x] `git diff --check` passes.
- [ ] Run `Sport24LogicTest` and build the Android app.
- [ ] Verify channel assignment on the Android TV with the user's playlist.

## Risks and status

Alias matching is a best-effort name match; incorrect provider naming can still require manual channel assignment. Implemented; tests were added but not run. Gradle build and Android TV checks remain open.
