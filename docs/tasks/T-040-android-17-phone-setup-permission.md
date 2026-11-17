# T-040 — Android 17 local network permission for phone setup

## Goal

Keep the temporary phone provider setup working when the app targets Android 17 (API 37), where direct local network access requires the runtime `ACCESS_LOCAL_NETWORK` permission.

## Affected files

- `app/src/main/AndroidManifest.xml` — declare the permission.
- `app/src/main/java/com/ipvio/stream/AddPlaylistScreen.kt` — request it before starting the temporary provider setup listener.
- `CHANGELOG.md`, `docs/tasks/WORK_STATE.md` — record the change and validation.

## Acceptance criteria

- On API 37+, tapping **Set up from phone** requests permission before opening the local listener.
- Editing a provider on phone follows the same permission path.
- Denial leaves the listener stopped and reports why setup could not start.
- On API 24–36, the existing flow starts without a permission prompt.
- Existing credential parsing, one-use session token and TV confirmation remain unchanged.

## Validation

- `git diff --check`: passed.
- Not run: Gradle build or tests (per repository environment limits).
- Manual TV checks: on API 37+, grant and deny the permission; verify grant starts setup and denial shows no QR/listener. On API 24–36, verify no prompt and normal setup.

## Status

Implemented in source; build and device validation remain open.
