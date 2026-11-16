# T-039 — Hide system bars during playback

## Goal

Make fullscreen playback use the full display without Android status/navigation bars, while restoring their prior visibility when playback closes.

## Plan and implementation

Reuse AndroidX Core's `WindowInsetsControllerCompat` already available through the existing Activity Compose dependency on API 26 and newer. AndroidX documents that `hide()` has no effect below API 26, so API 24–25 use the platform's immersive sticky flags and restore the original view flags on exit. `PlayerScreen` captures which system bars were visible and the prior transient-bar behavior on newer versions, then restores them when the screen leaves. No player instance or playback state is changed.

## Acceptance criteria

- [x] Status, navigation, and caption bars are hidden while the player screen is active.
- [x] The previously visible system bars and bar behavior are restored when leaving playback.
- [x] API 24–25 use a legacy fullscreen fallback compatible with the app's minimum SDK.
- [x] No dependency or player lifecycle changes are introduced.
- [x] Manual Android TV checks are documented; this behavior cannot be meaningfully verified by a JVM test.

## Validation and risks

`git diff --check` is the available check here. No Gradle build or device run was performed. Android TV implementations vary in how they expose system bars, so confirm entry, control overlay, Back, and return-to-app behavior on the target TV. If insets are unavailable at player entry, the implementation assumes system bars were visible and restores them visible.

## Manual Android TV checks

1. Start a live stream and a VOD item. Confirm video fills the screen and app/system chrome is absent.
2. Press OK and use the D-pad to reveal and operate controls; confirm hiding system bars does not affect playback or focus.
3. Press Back to leave playback. Confirm the prior app/system bar state returns and the app remains navigable.
4. Repeat after showing transient system bars, where the TV exposes that behavior.
5. If available, check a device on API 24 or 25 to verify the legacy fullscreen flags and restoration.

## Status

Implemented in `ed968ff` with an API 24–25 compatibility follow-up; not built or device-tested.
