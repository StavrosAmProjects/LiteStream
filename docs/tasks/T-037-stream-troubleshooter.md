# T-037 — Stream connection troubleshooter

## Goal

Let a TV user diagnose a selected channel or playlist URL and tell apart connection, TLS, HTTP, playlist-format/HLS, and playback failures.

## Source audit

Settings > Diagnostics (`SettingsHub.kt`, `DiagPage`) reports app status, recent app errors, EPG status and reload controls. It does not make a per-stream request or identify TLS/HLS failures. `PlaylistStore` already uses the app's OkHttp client with TLS fallback for playlist loading; player failures are handled through the existing Media3 path.

## Plan

- Add a TV-accessible “Diagnose channel” action from the selected channel/player error context, not a general all-channel scan.
- Reuse the existing network/player paths and dependencies. Do not add a player engine or weaken TLS.
- Show a small result: DNS/connect, TLS, timeout, HTTP status, or recognized HLS manifest. Keep IPVIO's existing Media3 error message visible; do not claim the probe proves playback compatibility. Redact credentials and query values; do not persist raw URLs or response bodies.
- Keep diagnostics opt-in and bounded (timeouts and small response read) to avoid downloading a full stream or probing a large provider catalogue.
- Add JVM tests for error classification and provide device checks for a working stream and representative failure cases.

## Affected areas

`PlayerScreen.kt` (existing playback error surface), new `StreamProbe.kt` (bounded network request), new `StreamProbeLogic.kt` (pure result classification), new `StreamProbeLogicTest.kt`, `DictA.kt`/`DictI.kt`, `CHANGELOG.md`, and `WORK_STATE.md`. Uses the shared OkHttp client; no player or dependency replacement.

## Risks

A short OkHttp range request may negotiate differently from Media3 and can only test the HTTP endpoint, not segment loading or decoding. HTTP responses may contain provider credentials or tokens. Never show or log raw request/response values. Network/player changes require a separate source review and device calibration.

## Acceptance criteria

- [x] Probe starts only for a user-selected stream and uses bounded requests/timeouts.
- [x] Results distinguish network, TLS, HTTP and HLS recognition without claiming certainty beyond observed evidence.
- [x] Credentials and query tokens are redacted from UI, logs and stored diagnostics.
- [x] Unit classification coverage and manual Android TV steps are documented.

## Manual Android TV checks

1. Start a working HLS channel, then use the connection check after a simulated/player failure; result should report HLS without claiming decoding success.
2. Check a working direct stream; result should report an HTTP response and say the format is unconfirmed.
3. Check a non-existent host, an unreachable host, a server returning 401/403, and (where available) a broken TLS endpoint; confirm each class is clear.
4. Start a check, then zap to another channel; the old result must not appear on the new channel.
5. Confirm the error panel, diagnostics log and clipboard report contain no stream URL, credentials or response body.

## Status

Implemented in the working tree. `StreamProbeLogicTest` was added but not run. The check makes one bounded HTTP range request (512-byte prefix, 4-second connect/read timeouts, 8-second overall timeout) only after a playback error and user action. It reports the observed network/HTTP/HLS response; it cannot prove Media3 can decode the stream. Android TV validation remains pending.
