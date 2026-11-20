# LiteStream TV releases

## Current state

LiteStream TV uses package ID `com.litestream.tv` and starts at version `0.1.0` / version code `1`. It installs beside IPVIO Stream, so its app data is separate. Once merged into the repository's default branch, the `Sync IPVIO main` workflow checks the official upstream `main` branch daily and on demand, then opens or updates a review pull request into `codex/litestream-tv-fork`. It never merges upstream changes automatically. This is source synchronization for maintainers; it does not install upstream IPVIO APKs on LiteStream TVs.

In-app updates must use signed LiteStream TV releases from this repository. Do not use the upstream IPVIO APK or release URL: its package ID and signing identity do not match LiteStream TV. The app's update URL remains disabled until the LiteStream release feed is configured and the first signed release is available.

## Move data from IPVIO Stream

1. In IPVIO Stream, create a backup. Include provider credentials only if you want them moved; protect the backup file because it can contain logins.
2. Keep IPVIO installed. Install LiteStream TV as a separate app.
3. In LiteStream TV, open Settings > Storage > Backup > Load backup from file. Review the restore summary before confirming.
4. Check playlists, favorites, history and settings on the TV before deciding whether to remove IPVIO.

The old installation remains unchanged. LiteStream has its own Android app data and signing identity.

## Signing key

Create a dedicated release key for LiteStream TV and keep it backed up outside the repository. Every later LiteStream TV APK must use the same key or Android will refuse an in-place update. Never commit `keystore.properties` or a key file.

For a local signed build, create `keystore.properties` in the project root:

```properties
storeFile=/secure/path/litestream-release.jks
storePassword=...
keyAlias=...
keyPassword=...
```

The release workflow expects signing values in repository secrets. The workflow must run only in the new fork repository and publish `LiteStream-TV.apk` with `update.json`.

## Configure updates after the first LiteStream release

1. Build a signed release with a version code higher than the previous LiteStream TV release.
2. Publish `LiteStream-TV.apk` and `update.json` in the same GitHub release.
3. `update.json` must include `versionCode`, `versionName`, `apk`, and the APK's SHA-256. `apk` may be the release asset name or a full HTTPS URL.
4. In the app, enter the direct `update.json` URL under Settings > About > App update. Enable automatic checks if wanted.

Example:

```json
{
  "versionCode": 2,
  "versionName": "0.1.1",
  "apk": "LiteStream-TV.apk",
  "sha256": "64-character-lowercase-sha256",
  "minSupportedCode": 0,
  "mandatory": false,
  "notes": "Short release notes"
}
```

The app verifies the downloaded package name, version and signer before opening Android's installer. Keep the update address blank until the fork has its own release; a blank address disables update checks. The currently installed debug build uses package ID `com.litestream.tv.test`, so Android cannot update it in place with the production package `com.litestream.tv`; install the first production release separately, then use in-app updates for later releases.

## Upstream source synchronization

The scheduled workflow fetches the current `main` commit from `Emrodev/IPVIO-Stream`, merges it into a bot branch based on `codex/litestream-tv-fork`, and opens or updates a pull request to that branch. A merge conflict stops the workflow before the branch is pushed. Review every upstream change for LiteStream-specific behavior, run the normal PR checks, and merge manually. The workflow preserves upstream Git history and does not copy or publish the upstream APK. LiteStream's `main` remains release-only.

## Google Drive

Google Drive sign-in uses the client configured in `secrets.properties` and `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` repository secrets. Configure a LiteStream TV OAuth consent screen before enabling it in distributed APKs. The existing Drive folder and file prefixes keep their IPVIO names for compatibility with existing backups; do not rename them without a migration plan.
