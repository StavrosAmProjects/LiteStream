# T-026 — LiteStream TV fork architecture audit

## Goal

Complete a source-based architecture and gap audit for a TV-first LiteStream-derived IPVIO fork. No application code changes are part of this task.

## Source snapshots

- IPVIO checkout: `main`, commit `e55395a73ee8ee7b21e67a00a677581d90fa9529` (merge PR #98, release 1.4.3 in source, 16 Nov 2026).
- Latest published upstream release checked: `V1.4.2`, commit `abc5a21` (14 Nov 2026). The supplied latest-APK URL therefore points to 1.4.2, while this checkout contains the later 1.4.3 source.
- LiteStream comparison checkout: `StavrosAmProjects/LIGHTSTREAM`, tag `v0.3.1-working`, commit `28a55a9a08125e049bd5af6094fdcd237a484ce3` (4 Oct 2026); Android implementation under `android/app/src/main/java/com/litestream/android/`.
- Local working branch is `main`, tracking `origin/main`; working tree was clean except for the user-provided untracked `AGENTS.md`. No fork branch was created because this was audit-only.

## Current architecture (confirmed in source)

- Single Android `:app` module, Kotlin and Jetpack Compose. Navigation is app-owned state and focus handling in `MainActivity.kt`, `Nav.kt`, and screen composables; no navigation framework or DI container was found.
- Provider data/loading and persistence are owned by `PlaylistStore.kt`, using SharedPreferences and app files. No Room/SQLite database was found. Existing language/category filters and large-playlist parsing are already established and should not be repurposed as destructive country filtering.
- Xtream (`XtreamApi.kt`, `XtreamLogic.kt`), M3U (`Models.kt`, `PlaylistStore.kt`), and Stalker/Ministra (`StalkerApi.kt`, `StalkerLogic.kt`) are present. Xtream and Stalker support lazy/large-catalogue paths; multi-provider support is in `PlaylistStore.kt` and related UI.
- Playback is Media3/ExoPlayer in `PlayerScreen.kt`, with existing playback, EPG, catch-up, live timeshift, recording, multi-view and recovery code. Keep this engine and lifecycle.
- Live TV, home and TV interaction are in `LiveTab.kt`, `HomeScreen.kt`, `Boomer.kt`, `Nav.kt`, and `PlayerScreen.kt`. Remote key/focus handling already exists. `Boomer.kt` is a simple mode candidate to assess before a second TV home is considered.
- The phone server is `PhoneImport.kt`, started and stopped by `AddPlaylistScreen.kt` while that screen is composed. It already provides a phone page, QR/address flow, M3U/Xtream credential entry, file upload and other transfer features.
- `PhoneImport` uses plain HTTP on ports 8765–8775 and a 4-digit code, with a lock after five failures. It has no random URL token or automatic session expiry; the Add screen lifetime is the current stop boundary. Submission immediately calls the existing playlist load path. There is no phone setup preview/TV confirmation step. These are material gaps against the requested pairing security and save flow.
- Existing tests are pure Kotlin/JUnit under `app/src/test/java/com/ipvio/stream/`; `PhoneImportTest.kt`, `QrCodeTest.kt`, provider and playlist tests are relevant patterns.

## LiteStream comparison (confirmed in source)

- LiteStream Android is an Xtream-focused account/catalogue client. `CategoryClassifier.kt` classifies leading country tokens and adult labels; it includes UK/GB, `GREEK`/`ELLADA`/`GRC`, and Cyprus aliases, but no explicit `GREECE` alias. The current classifier is deliberately narrow (leading token, ISO two-letter result, ambiguity guard), so it does not satisfy the requested general flag/prefix/suffix normalization by itself.
- `AppRepository.kt` persists accounts, category catalogues and preferences in one SharedPreferences JSON document (`litestream_state`). `LiteStreamViewModel.kt` handles category import/selection and grouping; `Dialogs.kt` has country/category import UI; `RootScreen.kt` has a two-pane TV layout and separate phone layout.
- Android account setup asks for name, server, username and password. LiteStream also has category import / Do Not Load concepts, but no source evidence was found for IPVIO-required M3U/Stalker parity, local QR setup, TXT/JSON provider import, or a three-pane country/group/channel editor.
- LiteStream Android declares Media3 and libVLC fallback; its player/repository architecture is not a candidate to transplant into IPVIO.

## Requirement gap analysis

| Requirement | IPVIO behaviour | LiteStream behaviour | Recommended action | Likely IPVIO files/classes | Risk |
|---|---|---|---|---|---|
| Preserve mature provider/player capabilities | Xtream, M3U, Stalker, large-list loading, multiple providers, Media3, EPG, catch-up, timeshift, recording and recovery exist | Android source is Xtream-focused and narrower | Keep IPVIO implementations; use LiteStream only as presentation/classification reference | `PlaylistStore.kt`, `XtreamApi.kt`, `StalkerApi.kt`, `PlayerScreen.kt`, `Epg.kt`, `Catchup.kt`, `LiveTimeshift.kt`, `Recorder.kt` | High if provider or player internals are replaced |
| Country-first live browsing | Current category/language-based browsing; no country-first projection found | Country classification and import/grouping are present | Add a deterministic, pure presentation classifier and derived projection over provider categories; preserve unknown/unclassified bucket and catalogue | new pure logic; `LiveTab.kt`, `PlaylistStore.kt` | False positives and provider naming variance |
| Country alias normalization | No equivalent country classifier found; language tags are not countries | Leading country token aliases and ambiguity protection | Reuse tested ideas only after extending tests for CY, GR/Greek, UK/GB, flags, suffixes, separators, case, and false positives | new pure logic and unit tests | False classification can hide content if filtering is destructive |
| Retain full provider data and persist filters | Full playlist/cache remains in existing provider store; hidden language parsing can omit entries from active memory and reparse from saved source | Selective category import/Do Not Load is part of LiteStream's source model | Store country/group presentation choices separately, keyed by stable provider/category identity; do not delete underlying catalogue; explicitly preserve across refresh/profile changes | `PlaylistStore.kt`, `Profiles.kt`, `Store.kt`, `Backup.kt` only if backup inclusion is decided | Persistence key collisions, refresh identity changes, backup/profile behaviour |
| Phone-first provider setup | Phone page already exposed on Add Playlist with QR and local HTTP server; credentials currently submit directly into provider load | No local phone pairing in LiteStream Android found | Extend the existing server/page. Add an explicit setup-only session, random short-lived token, expiry, TV confirmation and secret-safe error handling; reuse save/load infrastructure | `PhoneImport.kt`, `AddPlaylistScreen.kt`, `QrCode.kt`, `PlaylistStore.kt` | High: LAN credential exposure, server lifecycle, credentials currently enter save/load immediately |
| TXT / JSON / complete URL import | M3U file/URL import exists; credential text-file parser was not found | Xtream server normalization exists, but TXT setup import was not found | Add a small pure parser for key/value text, complete Xtream URLs and M3U URLs; preview redacted provider/server and use existing persistence | `ImportFiles.kt`, `XtreamLogic.kt`, `PlaylistLogic.kt`, `AddPlaylistScreen.kt`, tests | Parsing ambiguity and secret leakage in diagnostics |
| ALL / NONE / CUSTOMIZE setup | Existing loading and filters; no equivalent onboarding choice confirmed | Categories can be selectively imported and excluded from loading | First define semantics for NONE and later re-enable; then reuse existing loading/filter facilities. Do not use current memory-saving language filter as a destructive selection store | `AddPlaylistScreen.kt`, `PlaylistStore.kt`, filter UI | NONE must not make provider inaccessible; large catalogue cost |
| Country → group → channel customize panes | Existing category/channel screens and D-pad focus; no draft country/group/channel editor found | Country import management and a two-pane TV library, not equivalent three-pane draft editor | Implement only after the classifier/filter contract; draft state, cancel discard and Done commit; reuse Compose focus patterns | likely new focused Compose screen, `LiveTab.kt`, existing pure selection tests | D-pad focus and partial selection complexity |
| Simple primary TV home | Existing broad Home and Live TV; Boomer simple mode exists | Focused account/library presentation | Evaluate Boomer against actual TV test feedback; prefer adapting existing entry/navigation state over a second home stack | `Boomer.kt`, `HomeScreen.kt`, `MainActivity.kt`, `Nav.kt` | Broad visible behaviour changes and focus regressions |
| Fullscreen controls | Player already toggles controls, auto-hides using `hideSec` setting, handles Back and live up/down zapping | Fullscreen player surface exists in its own app | Audit exact device behaviour before changes; adjust overlay state only, preserve the existing ExoPlayer instance | `PlayerScreen.kt`, `PlayerControls.kt` | High: accidental playback restart or key mapping change |
| Licensing / independent fork identity | IPVIO is GPL-3.0; source requires preserving notices and independent branding | LiteStream `LICENSE` grants SA private, personal, non-commercial use and grants no other person/entity distribution/commercial rights | Initial recommendation was superseded by the owner's confirmation; use is authorized for this fork. Preserve GPL notices and update the LiteStream license before any public distribution | `LICENSE`, notices, app ID/name/icons/release metadata (future implementation) | High; preserve source and dependency notices |

## Initial implementation recommendation (audit-time status)

This section records the source-based recommendation as it stood during the audit. The maintainer later confirmed ownership of LiteStream code, assets and branding and authorized their use. The implementation branch `codex/litestream-tv-fork` starts from the audited IPVIO commit.

## Current status after implementation

| Requirement | Status | Evidence / remaining check |
|---|---|---|
| Keep IPVIO provider, player, EPG, recording and recovery infrastructure | ALREADY BETTER IN IPVIO; preserved | Existing source paths retained; Android build/playback still need verification. |
| Country recognition and country-first live browse | SMALL ADAPTATION; implemented in T-028 | `CountryBrowseLogic.kt`, `CountryBrowseScreen.kt`; run classifier tests and D-pad device check. |
| Non-destructive, profile-scoped country/group filters | SMALL ADAPTATION; implemented in T-029 | `CountryFilterStore.kt` uses existing `ipvio_live` prefs; verify refresh, profile and backup behavior on device. |
| Phone pairing and provider-file/URL import | SUBSTANTIAL ADAPTATION; implemented in T-027 | Token, expiry, one-use stage, TV confirmation and setup-only route isolation; plain HTTP remains visible to LAN observers. |
| ALL / NONE / CUSTOMIZE and three-pane draft editing | SMALL ADAPTATION; implemented in T-029 | Xtream setup choice and draft country/group/content editor; verify TV focus and cancellation. |
| Simple TV home | ALREADY EQUIVALENT FEATURE; default status unresolved | Existing Boomer mode now respects filters. Its opt-in/PIN behavior remains; target-TV feedback decides whether it should become the default. |
| Fullscreen and live zapping | ALREADY BETTER IN IPVIO with timeout adaptation | Existing Media3 controls and live Up/Down zapping remain; default overlay timeout changed to 3 seconds. Verify key mappings and live/timeshift seek on device. |
| Independent LiteStream identity | SMALL ADAPTATION; implemented in T-032 | App ID `com.litestream.tv`, owner-provided assets, and no upstream updater default. New release repository/OAuth configuration and install test remain. |
| Licensing and notices | NOT READY FOR DISTRIBUTION | IPVIO GPL notices retained; review the combined source/dependency notices before any public distribution. |

The supplied IPVIO 1.4.2 APK has not yet been tested on the target TV. Device feedback remains useful for later home, focus and playback-overlay changes.

## Risks and open decisions

- **License/branding:** The maintainer owns LiteStream code, assets and branding and has authorized their use in this fork. Before distribution, preserve IPVIO's GPL-3.0 notices and review obligations for the combined work and exact third-party dependency notices/licences. Update LiteStream's current personal-use license for any public fork distribution so the published terms match the owner's intent.
- **Phone security:** the fork's temporary setup route now uses a random short-lived token, one-use staging and TV confirmation. Local HTTP is still unencrypted, so another device on the same network may observe credentials.
- **Data model:** provider category identifiers and refresh stability need inspection before designing persisted country/group filters. No migrations or key names should be chosen in this audit.
- **Device behaviour:** no APK was installed or tested in this audit. Focus, QR reliability, player overlay timing and key mappings remain unverified on the user's TV.
- **Test/build limitation:** Gradle/Android SDK are unavailable per project rules; no build, unit tests or device checks were run.

## Acceptance criteria / result

- [x] Confirm exact local source commits and published IPVIO release.
- [x] Map target app entry, provider, playback, storage, phone setup, TV UI/focus and tests from source.
- [x] Compare relevant LiteStream Android concepts and identify what should not be transplanted.
- [x] Provide requirement-by-requirement gaps, likely integration surfaces and risks.
- [x] Recommend a bounded implementation sequence without changing application code.
- [x] Record manual test/build limitations and material licensing/security considerations.

## Files and test results

- Documentation: this audit and `docs/tasks/WORK_STATE.md` only.
- Application code/build configuration: unchanged.
- Checks performed: source inspection, Git history/status, public upstream release/commit verification.
- Not run: Gradle, JUnit, APK build, Android TV/Fire TV device test, third-party licence review or legal opinion.
- Status: **audit complete; T-027 through T-032 implemented in the working tree; build/device validation pending**.
