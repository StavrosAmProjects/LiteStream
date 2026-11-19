# T-057 — Sport24 Home shortcut and project credits

## Goal

Open Sport24's daily sports TV schedule directly from Home and show clear credit for LiteStream's fork-specific work and the upstream IPVIO foundation.

## Changes

- Add a Sport24 TV page to the existing Sports hub and route the Home tile directly to it.
- Reuse current playlist channels and allow manual broadcaster-to-channel mappings per profile.
- Keep mappings inside the existing profile preference pattern and clear them when that profile is deleted.
- Add README and changelog credit for `@StavrosAmProjects`, with thanks to Emrodev and IPVIO contributors; retain GPL and copyright notices.

## Affected files

`app/src/main/java/com/ipvio/stream/Sport24Guide.kt`, `Sport24Logic.kt`, `SportHub.kt`, `HomeScreen.kt`, `Profiles.kt`, `app/src/test/java/com/ipvio/stream/Sport24LogicTest.kt`, `README.md`, `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`.

## Acceptance and validation

- [x] Home opens the Sport24 schedule tab directly.
- [x] Broadcast rows can play the best channel match or open manual channel assignment.
- [x] Manual channel assignments are profile-scoped.
- [x] The README credits the LiteStream owner and the upstream creator/contributors.
- [ ] Build, run `Sport24LogicTest`, and check playback mapping and D-pad focus on Android TV.
- [ ] Confirm the live page response and date behavior on device.

## Risks and limits

The code reads the public Sport24 TV-program page; it does not stream content. The page markup or access rules can change, and a blocked/changed response may leave the schedule empty. The supplied implementation covers today's listings only; a date selector is not included because the provided code does not establish a supported date-specific endpoint.

GitHub Actions excludes ordinary pushes to `codex/litestream-tv-fork`. Build/PR checks run when a PR is opened, reopened, or marked ready (not on each later push); a manual build remains available. The release workflow runs on `main`, and upstream sync runs on schedule or manual dispatch.

## Status

Implemented and pushed on `codex/litestream-tv-fork` in commit `95e2cb8`. YAML and shell syntax checks passed; no Gradle build, test run, or device install was done. Date selection remains open because no supported date-specific endpoint was established.
