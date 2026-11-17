# T-032 — LiteStream TV fork identity and install package

## Goal

Make the derived app install as LiteStream TV, keep it alongside IPVIO Stream, and prevent it from querying the upstream IPVIO release feed.

## Analysis

The audited Android source used application ID `com.ipvio.stream`, the IPVIO launcher assets, and a hard-coded updater feed from `Emrodev/IPVIO-Stream`. Reusing that ID would collide with the source app; replacing it would fail without the upstream signing key. IPVIO's backup/restore flow can transfer selected user data into a separately installed fork. The LiteStream brand assets are present in owner-controlled repository `StavrosAmProjects/LIGHTSTREAM` at commit `28a55a9a08125e049bd5af6094fdcd237a484ce3`.

## Change

- Set application ID to `com.litestream.tv`; keep Kotlin namespace and component class names `com.ipvio.stream` to avoid an unnecessary package-wide refactor.
- Start the distinct app at version code `1`, version name `0.1.0`.
- Use the LiteStream launcher icon, adaptive mark, TV banner, app label, setup header, and About identity.
- Keep IPVIO GPL and copyright notices; note the fork origin in About and README. The donation section identifies its existing links as support for the original IPVIO project.
- Clear the default upstream update URLs, default auto-check off, hide the first-start update card until an address is configured, and rename the release artifact to `LiteStream-TV.apk`.
- Document side-by-side install, backup transfer, release signing, custom update URL, and Google OAuth setup.
- Use LiteStream TV as the example TMDB API app name in the setup guide.
- Update the README and direct dependency license inventory for the fork identity; leave transitive and bundled native license review open until the exact release dependency graph and artifacts can be inspected.

## Affected files

`app/build.gradle.kts`, `app/src/main/res/`, `Brand.kt`, `SettingsHub.kt`, `AddPlaylistScreen.kt`, `AppUpdate.kt`, `UpdateCard.kt`, `RemoteLogic.kt`, `ImportFiles.kt`, `Boomer.kt`, `Look.kt`, translation dictionaries, `.github/workflows/release.yml`, `README.md`, `THIRD_PARTY_NOTICES.md`, `docs/RELEASE.md`, `docs/GOOGLE_DRIVE.md`, `CHANGELOG.md`, `WORK_STATE.md`.

## Acceptance criteria

- [x] LiteStream TV has its own install identity and does not overwrite IPVIO Stream.
- [x] The upstream release feed is not queried by default.
- [x] Launcher/app branding uses owner-provided LiteStream assets.
- [x] Backup transfer and separate app data are documented.
- [x] README and direct third-party dependency inventory use the LiteStream TV identity and declared versions.
- [x] About page version label uses the LiteStream TV translation key in all supported languages.
- [x] TMDB API setup instructions use the fork's app name.
- [ ] Resolve the complete release dependency graph and inspect the packaged native binaries for required notices/licenses.
- [ ] Before distribution, check the modified-version/date and user-interface notices and provide corresponding source with the release under GPL-3.0 sections 5–6.
- [ ] Verify package, adaptive icon, TV banner, OAuth, signed update, and backup migration in an Android build/device run.

## Risks and validation

`git diff --check` was run during implementation. T-042 later resolved Gradle dependencies and built/verified a debug APK, but no release dependency/native-license audit, unit tests, or device validation has been completed. The direct dependency inventory is documented. Maven Central metadata for the Jellyfin FFmpeg decoder declares GPL-3.0; this does not establish every license for native libraries inside the resolved artifact. Transitive dependency and bundled native notices still need review. The new application ID means data is separate; users must export/import a backup. Google Drive needs a LiteStream OAuth configuration; the code keeps the legacy Drive folder name and file prefixes for compatibility. The release repository is not configured, so update checking remains off until its URL is entered.

## Status

Implemented and committed as `594d312` on `codex/litestream-tv-fork`. Direct Jellyfin license metadata and the remaining artifact-level review limit were recorded on 2026-11-17. Before release, verify the exact resolved dependency graph and native binaries; add a relevant modified-version date to the release notices; preserve appropriate in-app legal notices; and make the matching corresponding source available with the binary. See [GPL-3.0 sections 5–6](https://www.gnu.org/licenses/gpl-3.0.html#section5).
