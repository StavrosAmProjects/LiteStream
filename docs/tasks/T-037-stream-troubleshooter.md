# T-037 — Stream connection troubleshooter

## Goal

Let a TV user diagnose a selected channel or playlist URL and tell apart connection, TLS, HTTP, playlist-format/HLS, and playback failures.

## Source audit

Settings > Diagnostics (`SettingsHub.kt`, `DiagPage`) reports app status, recent app errors, EPG status and reload controls. It does not make a per-stream request or identify TLS/HLS failures. `PlaylistStore` already uses the app's OkHttp client with TLS fallback for playlist loading; player failures are handled through the existing Media3 path.

## Plan

- Add a TV-accessible “Diagnose channel” action from the selected channel/player error context, not a general all-channel scan.
- Reuse the existing network/player paths and dependencies. Do not add a player engine or weaken TLS.
- Show a small result: DNS/connect, TLS, timeout, HTTP status, recognized HLS manifest, or Media3 playback error. Redact credentials and query values; do not persist raw URLs or response bodies.
- Keep diagnostics opt-in and bounded (timeouts and small response read) to avoid downloading a full stream or probing a large provider catalogue.
- Add JVM tests for error classification and provide device checks for a working stream and representative failure cases.

## Affected areas

Likely `PlayerScreen.kt` or its existing error surface, `PlaylistStore.kt`/shared HTTP helpers only if a safe reusable probe is absent, a pure diagnostic classifier and JVM test, English translations, `CHANGELOG.md`, and `WORK_STATE.md`. Confirm the actual error callback and URL ownership before implementation.

## Risks

A connection probe may not reproduce Media3 behavior, and TLS fallback can mask an initial handshake issue. HTTP responses may contain provider credentials or tokens. Never show or log raw request/response values. Network/player changes require a separate source review and device calibration.

## Acceptance criteria

- [ ] Probe starts only for a user-selected stream and uses bounded requests/timeouts.
- [ ] Results distinguish network, TLS, HTTP, HLS recognition, and player failures without claiming certainty beyond observed evidence.
- [ ] Credentials and query tokens are redacted from UI, logs and stored diagnostics.
- [ ] Unit classification coverage and manual Android TV steps are documented.

## Status

Audit and implementation plan only. No channel-level troubleshooter is implemented yet.
