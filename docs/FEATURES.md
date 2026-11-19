# LiteStream TV features

This is the current product summary for the LiteStream TV fork. The code is derived from [IPVIO Stream](https://github.com/Emrodev/IPVIO-Stream); see the repository README for authorship and upstream credit. Source references use `app/src/main/java/com/ipvio/stream/`.

Reviewed against the feature branch on 19 November 2026. A listed feature means it exists in source; it does not imply every provider or device combination has been tested.

## TV experience

- Home, Live TV, Movies, Series, and Settings work with a TV remote.
- Home rows can be arranged and hidden. Favourite Categories gives direct access to the user's saved Live TV categories.
- Live TV supports country-first browsing, provider categories, favourites, custom channel lists, a compact list view, and cards.
- Channel order and category/country order can be changed; live playback supports remote channel surfing.
- TV guide, catch-up, reminders, recording, timeshift, and multiple-view playback remain available where the provider/device supports them.

## Providers and playlists

- Xtream, M3U URL/file, and Stalker/Ministra providers.
- Phone-assisted local setup, TXT/provider URL import, and manual setup.
- Several playlists can be shown together, switched individually, or viewed as a merged/all list.
- Country/category selection is saved per provider. Excluded categories can be restored later; provider data is retained for supported import paths.
- Large playlists use resumable downloads, local compressed storage, and language selection when needed.
- Refresh can be scheduled; the app uses its saved playlist between refreshes.

## Movies and series

- Search, provider categories, favourites, history, custom lists, and resume playback.
- Grid or cinema-view browsing with filters and title details.
- TMDB/OMDb metadata requires user-supplied keys. Automatic genre scans are off by default; provider categories remain available.
- HLS, DASH, and common direct stream playback use the existing Media3 player, with audio/subtitle selection, reconnect handling, quality variants, and optional live timeshift.

## Sports and app setup

- Sports hub with fixtures matched against the user's channel list and EPG.
- Sport24.gr daily sports TV schedule with available channel matching and manual correction.
- Optional Google Drive sync and local backup/restore.
- App chrome is English. Provider names and programme information remain in their original form.

## Distribution status

The source is maintained on the `codex/litestream-tv-fork` feature branch. The fork's first public signed release and in-app update feed are not configured yet. Build and data-transfer notes are in [RELEASE.md](RELEASE.md).
