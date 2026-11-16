# T-030 — Playback overlay timeout

## Goal

Use a three-second default for playback-control auto-hide, matching the TV-first interaction target while preserving an existing user's saved choice.

## Analysis and change

`PlayerScreen.kt` already reveals controls on remote interaction, hides them after `AppSettings.hideSec`, and leaves the player instance in place. Only the new-install fallback in `Store.kt` was four seconds. Set the in-memory default and preference fallback to three seconds. Existing stored `hide_sec` values are read unchanged.

## Validation

- `git diff --check` passed; no Gradle or device checks were run.
- Device-only check: on a fresh install, start live and VOD playback, press OK to reveal controls, and confirm they hide after about three seconds without playback interruption. Change the timeout in Settings, restart, and confirm the saved value remains.

## Status

Implemented in the working tree on `codex/litestream-tv-fork`. No test was added because the changed behavior is the player UI timer, which requires an Android device; manual steps above cover it.
