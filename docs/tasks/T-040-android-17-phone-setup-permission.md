# T-040 — Android 17 local network permission for phone services

## Goal

Keep phone provider setup and the existing phone remote, backup transfer, list import, and API-key entry working when the app targets Android 17 (API 37), where local network access requires the runtime `ACCESS_LOCAL_NETWORK` permission.

## Affected files

- `app/src/main/AndroidManifest.xml` — declare the permission.
- `app/src/main/java/com/ipvio/stream/LocalNetworkPermission.kt` — shared permission check and request.
- `app/src/main/java/com/ipvio/stream/AddPlaylistScreen.kt` — provider setup listener.
- `RemoteUi.kt`, `TransferUi.kt`, `ListImportUi.kt`, `SettingsHub.kt` — existing phone services.
- `CHANGELOG.md`, `docs/tasks/WORK_STATE.md` — record the change and validation.

## Acceptance criteria

- On API 37+, starting any phone service requests permission before opening the local listener.
- Editing a provider on phone follows the same permission path.
- Denial leaves the listener stopped and reports why setup could not start.
- On API 24–36, the existing flow starts without a permission prompt.
- Existing credential parsing, one-use session token and TV confirmation remain unchanged.

## Validation

- `git diff --check`: passed.
- Not run: Gradle build or tests (per repository environment limits).
- Manual TV checks: on API 37+, grant and deny permission from provider setup; verify grant starts the server and denial shows no QR/listener. Also open phone remote, backup transfer, list import, and API-key entry first on a fresh install; each should request permission. On API 24–36, verify no prompt and normal behavior.

## Status

Implemented in source; build and device validation remain open.
