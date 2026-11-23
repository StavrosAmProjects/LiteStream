# T-069 – Make Live TV smart genres opt-in

**Status:** implemented; build and device validation pending.

## Goal

Skip Live TV smart-genre preparation during normal startup unless the existing Automatic genres setting is enabled. Preserve provider categories and the current setting default.

## Changes

- Gate the Live TV genre split with `AppSettings.ipvioFilter` in `LiveTab`.
- Gate the delayed Home-screen genre prewarm with the same setting.
- Explain in the setting description that enabling Automatic genres also enables Live TV smart playlists.
- Leave provider category grouping and ordering untouched.

## Validation

- `git diff --check`: passed.
- Gradle build and automated tests: not run.
- Device validation: pending.

## Manual checks

1. With Automatic genres off, open Live TV and confirm provider categories appear and smart-genre categories do not.
2. Enable Automatic genres and confirm Live TV smart playlists appear.
3. Disable it again and confirm provider categories remain available without smart-genre preparation on the next normal startup.
