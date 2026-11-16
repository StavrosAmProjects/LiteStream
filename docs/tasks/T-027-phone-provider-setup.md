# T-027 — Phone provider setup and credential import

## Goal

Extend IPVIO's existing phone setup and file import paths. Keep credentials local, stage phone submissions for TV confirmation, parse text/JSON and common provider URLs, and preserve the current provider loader/persistence.

## Plan

- Reuse `PhoneImport` and `AddPlaylistScreen`; do not add a server library or cloud service.
- Add a setup route bound to a cryptographically random, short-lived token. Keep legacy phone routes and the existing 4-digit gate unchanged for their current features, but reject all non-setup routes during the setup-only session.
- Add a pure parser for key/value provider text, JSON, complete Xtream URLs, and M3U URLs. Reuse `PlaylistStore.load` only after the TV confirms the staged setup.
- Add a file picker action and a redacted TV preview. Do not persist selection state or alter provider storage formats in this task.
- Add pure logic and local-server tests. Gradle/device checks remain maintainer steps per project limits.

## Affected files

`PhoneImport.kt`, `AddPlaylistScreen.kt`, `ImportFiles.kt`, `DictA.kt`, new `ProviderImportLogic.kt`, `PhoneImportTest.kt`, `ProviderImportLogicTest.kt`, `docs/tasks/WORK_STATE.md`, `CHANGELOG.md`.

## Required and preserved behaviour

- Phone session exists only while Add Playlist is open, expires automatically, accepts one credential submission, and requires explicit TV confirmation before loading/saving.
- QR encodes the token URL; show the address if QR/local IP fails. Reject malformed, expired, replayed, and oversized submissions.
- Existing M3U/Xtream/Stalker entry, M3U upload, backup, remote search, and other phone routes continue working outside the setup-only session.
- Never display a password in the TV preview or include credentials in logs/errors.
- Provider files/URLs parse locally and flow through the existing `PlaylistStore` validation and persistence.
- No app ID, secrets, manifest, provider schema, player, or dependency changes.

## Acceptance criteria

- [x] Valid text/JSON/URL input normalizes into provider name and M3U or Xtream fields; malformed input is rejected without persistence.
- [x] Setup QR uses a random token; expired and replayed submissions fail; setup-only session rejects legacy credential submission.
- [x] A valid phone submission only stages data; TV cancel discards staged phone M3U and closes the session; TV confirm starts the existing loader.
- [x] Password is absent from the TV preview.
- [x] Existing phone routes remain available outside setup; setup-only rejects the root and every non-token route.
- [x] Unit tests cover parser and session edge cases, including staged file upload.

## Risks / validation

- Existing provider loader remembers an add attempt before network completion to support crash recovery. This task starts that loader only after confirmation and does not change its recovery contract.
- Local HTTP still does not encrypt traffic on the LAN; the random expiring token limits access to this temporary setup session but does not provide transport encryption.
- Gradle is unavailable here; do not report build/tests passed without maintainer or CI results.

## Status

Implemented on `codex/litestream-tv-fork`. `git diff --check` passed. Gradle/JUnit and Android TV checks were not run in this environment.

Manual checks for the maintainer:
1. Open Add Playlist on Android TV; verify Set up from phone is immediately visible, opens a QR/address, and the phone page accepts either fields or a full Xtream/M3U URL.
   The card should not show the legacy four-digit code or claim the connection is encrypted.
2. Send Xtream details; verify the TV shows provider/server and no password, Cancel does not add it, and Confirm loads it.
3. Repeat with a phone M3U file; Cancel removes the staged import and Confirm loads it.
4. Import TXT credentials, JSON credentials, a complete Xtream URL, and an M3U URL from USB/Downloads; each should show the redacted preview before loading.
5. Verify expiry/replay rejection and confirm existing backup/EPG/remote phone tools still work while Add Playlist is open.
6. During setup, open the phone server root and `/crash`; both must return not found. After cancel/confirm/expiry, the token route must reject requests.

Known risk: pairing remains plain HTTP on the local network, consistent with the existing server. The expiring random token gates the temporary setup route but does not encrypt traffic.
