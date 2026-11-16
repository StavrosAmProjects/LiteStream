# T-035 — Edit a provider on phone

## Goal

Add an **Edit on phone** action to saved Xtream and M3U providers. Reuse the temporary local setup session and redacted TV confirmation. Replace the saved provider only after the new credentials load successfully.

## Analysis

- Provider management lives in `SettingsHub.kt` (`PlaylistPage`); it currently supports switching, show/hide, mode, rename, backups, and delete, but no credential edit.
- The setup token, one-use receive, expiry, and redacted confirmation live in `PhoneImport.kt` and `AddPlaylistScreen.kt`.
- Saved providers are `PL(name, url)` entries in `PlaylistStore`. The loader already validates and saves a new URL only after a successful load.
- Provider-related state is keyed by URL: `CountryFilterStore` visibility, `PlaylistMode`, backup server lists, multi-provider visibility, and cache files. `ipvio_lang` is profile-wide, not URL-keyed.

## Plan

- Add the edit action to each supported saved Xtream/M3U provider. Launch the existing Add Playlist surface with the target index and start a fresh temporary phone session.
- Do not read or send saved passwords to the phone. The phone submits replacement fields; the TV previews a redacted summary and requires confirmation.
- Load the submitted URL through the existing loader. On failure or Cancel, keep the old provider record and its cached data.
- On success, replace the target provider in place. Preserve its display name when the phone leaves the name blank, its multi-provider visibility, and its country/group selection and content mode. Keep backup servers only when the Xtream base server is unchanged. Then remove the old provider cache and obsolete backup entry.
- Reject a URL already assigned to another saved provider instead of merging provider state.
- Add a pure replacement guard test. Device-only session, validation, cancel, and successful replacement checks remain manual.

## Affected files

`MainActivity.kt`, `HomeScreen.kt`, `SettingsHub.kt`, `AddPlaylistScreen.kt`, `PlaylistStore.kt`, `DictA.kt`, `DictI.kt`, new `ProviderEditLogic.kt`, `CountryFilterStore.kt`, `PlaylistMode.kt`, new `ProviderEditLogicTest.kt`, `docs/tasks/WORK_STATE.md`, `CHANGELOG.md`.

## Acceptance criteria

- [x] Saved Xtream/M3U provider rows expose **Edit on phone**; Stalker remains available through its existing path.
- [x] Every edit uses a fresh random, expiring, one-use local session; no permanent setup server is added.
- [x] TV preview redacts the password and no provider value changes before confirmation and successful load.
- [x] Failure, duplicate URL, Cancel, and expiry leave the original provider and its data usable.
- [x] Successful edit replaces the same provider slot and preserves display name, show state, filter, and content mode; compatible backup hosts remain attached.
- [x] Provider replacement guard has JVM test coverage; the manual device steps cover server lifecycle and data preservation.

## Risks / validation

- A new provider URL has a different local cache identity. The old provider must remain until the replacement loader succeeds; cleanup happens only after the new provider is installed.
- Filter group keys may no longer match if the provider changes catalogue naming. Preserving the prior selection can therefore hide new groups; the user can adjust it in provider customization.
- Gradle/JUnit and Android TV checks are not available in this workspace. The maintainer must validate phone submission, failure/cancel safety, replacement, multi-provider state, and backup behavior before release.

## Manual Android TV checks

1. Add an Xtream and an M3U provider, then open Settings > Playlists and choose **Edit on phone**. Scan the fresh QR and submit changed credentials.
2. Confirm the TV preview hides the password. Cancel; verify the original provider still plays and its filter/mode remain.
3. Repeat and submit a duplicate URL. Verify the old provider remains usable and the duplicate is rejected.
4. Submit invalid credentials and let a session expire. Verify the original provider remains saved and the expired phone page cannot submit.
5. Submit working replacement credentials and confirm. Verify the provider stays in the same slot, the prior active provider is restored if it was another one, and filters/mode/multi-provider visibility remain.
6. For Xtream, verify configured backup hosts remain only when the replacement uses the same Xtream base server.
7. Restart the app and verify provider state. Confirm Stalker rows still use their existing management path.

## Status

Implemented in the working tree. `ProviderEditLogicTest` was added but not run; Gradle and Android TV checks are unavailable here. The maintainer must run the manual checks above before release.
