# T-068 – Approved IPVIO updates adapted for LiteStream

**Status:** implementation complete; validation pending maintainer build and device checks. No Gradle build, unit tests, or device install was run in this environment.

## Goal

Port only the upstream changes the maintainer approved, adapting them to LiteStream's existing provider, playback, settings, and playlist-filter code. Keep LiteStream-specific local edits intact.

## Included

- Treat non-file `/live/` Xtream paths as live streams even when their category label says Movies or VOD.
- Reject obviously cut, unsized M3U downloads and compare an unsized Xtream `get.php` list against live-category names when the provider API is available. If most categories are missing, try the existing API-generated playlist path.
- Retry the existing backup-server chain for HTTP 401, 403, and 404 responses as well as its existing connection/5xx failures.
- Replace silent HTTPS-to-HTTP fallback after a certificate-hostname mismatch with per-host consent. Other TLS failures remain failures. This uses the existing provider clients and timeshift data source.
- Add per-profile private categories for provider Live TV, movie, and series groups. Keep their content reachable from that category; exclude it from ordinary home/search/all-channel/favourite/genre/remote views and stop history/resume/progress recording. Existing parental flagged categories can opt into the same behavior.
- Add optional title-page stream-header details through Media3's existing family: resolution, HDR, video/audio formats, audio tracks, and subtitles. The probe is delayed, cached per session, and can be switched off.
- Add credential-free JSON export/import for each playlist's saved country/category selection in Settings > Playlists.

## Already present; not duplicated

- Resumable playlist downloads, existing Media3 playback, provider filters, merged playlist view, global refresh schedule, cached startup behavior, and disabled-by-default automatic genre scans were already in LiteStream. No replacement infrastructure was added for them.

## Excluded

No VPN/DNS changes, sports competition additions, new playback engine, or unrelated upstream UI/architecture changes were ported.

## Files

- Playlist correctness: `Models.kt`, `LoadLogic.kt`, `PlaylistStore.kt`, `XtreamApi.kt`.
- Existing failover/security path: `HostFailover.kt`, `TlsFallback.kt`, `TlsFallbackLogic.kt`, `TlsConsentDialog.kt`, `LiveTimeshift.kt`, `MainActivity.kt`.
- Private categories and backup: `PrivateCats.kt`, `PrivateLogic.kt`, `Store.kt`, `Profiles.kt`, `Backup.kt`, `LiveTab.kt`, `CategoriesScreen.kt`, `HomeScreen.kt`, `SearchScreen.kt`, `PlayerLive.kt`, `Boomer.kt`, `RemoteHub.kt`, `MainActivity.kt`, `SettingsHub.kt`. `LiveTab` passes the filtered channel list to the existing multi-view UI.
- Stream details and selection transfer: `MediaProbe.kt`, `MediaProbeLogic.kt`, `PlaylistSelectionTransfer.kt`, `Playback.kt`, `SettingsExtra.kt`, `SettingsReset.kt`, `DetailScreen.kt`, `app/build.gradle.kts`, `SettingsHub.kt`.
- Product/security notes: `FEATURES.md`, `SECURITY.md`, `CHANGELOG.md`, `WORK_STATE.md`.

## Risks and limits

- A certificate mismatch now requires the user to retry after consent; HTTP is used only for hosts explicitly approved by the user. This intentionally changes the former silent downgrade.
- The Xtream completeness comparison is best-effort: unavailable category APIs are treated as unknown, and lists with fewer than five provider categories are not judged by that check.
- Stream probing briefly opens another provider connection while a title detail page stays open; it starts after a delay and has a setting to disable it.
- Selection JSON contains category identifiers only. Import replaces the selected playlist's current country/category filter; it does not include playlist URLs, credentials, or provider catalogue data.
- Private-category marks use provider group names, so a provider renaming a group requires the user to mark the renamed group again.

## Validation

- `git diff --check`: passed.
- Gradle build / JVM suite: not run; the repository instructions say this environment cannot run Gradle, and no tests were requested for this task.
- Device checks pending: existing and backup server failover; interrupted/unsized playlist behavior; TLS prompt/cancel/consent; private category visibility and history cleanup; detail-page probe on providers with one-connection limits; selection export/import and malformed-file rejection.

## Maintainer test steps

1. Build and install the debug APK on the Android TV and phone.
2. Open a private Live TV category, play a channel, and verify it remains reachable there but absent from All, search, home rows, history, player recent channels, and the phone remote. Repeat for one movie/series provider category; back up and restore the category marks.
3. On a provider with a hostname-mismatch certificate, verify no HTTP request occurs before consent; cancel once, then retry and approve. Verify subsequent requests use the saved per-host choice.
4. Open a movie detail page, wait for stream details, then disable the setting and confirm no probe appears.
5. Export a playlist selection, inspect that its JSON contains only selected category keys, then import it. Try a malformed/oversized file and verify the saved selection is unchanged.
6. If possible, exercise an unsized/truncated playlist and a backup server returning 401/403/404 before a working server.
