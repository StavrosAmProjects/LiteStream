# LiteStream TV

LiteStream TV is a TV-first fork of IPVIO Stream for **Android TV, Google TV and Amazon Fire TV**. It keeps IPVIO's Kotlin, Compose, Media3 player and provider infrastructure, with a simpler television flow.

![LiteStream TV home screen](docs/screenshots/homescreen.jpg)

> **LiteStream TV does not provide any content.** It contains no channels, playlists, films or series. It is only a player for playlists that **you** add yourself. Only use sources that you are allowed to access.

This fork is based on [IPVIO Stream](https://github.com/Emrodev/IPVIO-Stream). It preserves the upstream GPL-3.0 notices and third-party notices.

### Upstream status

Checked **19 November 2026**: the official [IPVIO releases](https://github.com/Emrodev/IPVIO-Stream/releases) page lists **1.4.3** (`e55395a`, 16 November 2026) as its latest stable release. LiteStream's recorded upstream baseline is that same commit, so no newer stable release was available to bring in at this check. This confirms release-level alignment only; a live comparison with upstream's current `main` branch was unavailable, so unreleased commits are not confirmed.

The LiteStream workflow is configured to check IPVIO's official `main` branch daily and propose source changes as a review pull request into `codex/litestream-tv-fork`. It becomes active after the workflow reaches the repository's default branch. Upstream changes are reviewed before integration.

## Project authorship and upstream thanks

LiteStream TV's product direction, branding, TV-first experience, and fork-specific improvements are by **[@StavrosAmProjects](https://github.com/StavrosAmProjects)**. This work builds on the mature IPTV and playback foundation created by **[Emrodev](https://github.com/Emrodev) and the IPVIO contributors**; I am deeply grateful for their original work. The inherited code remains under GPL-3.0, with upstream copyright and license notices preserved.

## Install

**LiteStream TV APK release is not configured yet.** The IPVIO release APK is a different app and must not be installed as this fork.

## Features

**Playlists**
- Xtream Codes, M3U URL/file, and Stalker/Ministra providers
- Add providers with a phone-assisted local setup, provider TXT/URL import, or manual entry
- Several playlists can be active together; switch between one playlist, merged playlists, or all playlists in Live TV
- Choose countries and categories per provider; refresh or edit a provider later and restore previously excluded categories
- Xtream accounts load through `player_api.php` automatically when `get.php` is blocked
- Handles large playlists with language selection, resumable downloads, and compact local storage
- Scheduled refresh uses the saved playlist between updates and waits during playback

**Home screen**
- Home, Movies and Series with a large rotating header image
- The background follows the selected title and shows its title, ratings and description
- Rows can be rearranged and hidden; Favourite Categories provides direct access to saved Live TV categories
- Rows include Continue watching, Live TV now, Top 10 today, My list, recently watched, and optional genre rows
- A Sports on TV tile opens the sports hub, including the Sport24.gr daily TV schedule and playlist-channel matching

**Browse and details**
- Grid or cinema view, filters by sort order, rating, year, genre and country
- "Top rated" and sorting by rating use IMDb first, then Rotten Tomatoes, TMDB and finally the provider's rating
- Provider categories by default; optional TMDB automatic genres are off by default
- Detail page with resume, trailer, my list, favourites, choice of source/quality
- Series: seasons on the left, episodes on the right with picture, rating and description; sections and "go to episode" for very long series

**Live TV**
- Country-first browsing with provider categories, favourites and your own channel lists
- Compact channel list or cards, playlist switching, channel ordering and remote channel surfing
- TV guide, catch-up/archive ("from the beginning"), reminders with notification
- Rewind live TV with an on-device buffer, record the current show (internal storage or USB)
- Sports section: live sports, fixtures matched to your playlist and EPG, plus the Sport24.gr TV schedule when available; channel matches can be corrected manually

**Player** (Media3 / ExoPlayer)
- Audio and subtitle selection (remembered per title), preferred languages, subtitle style
- FFmpeg audio decoder for AC3/E-AC3/DTS (also over Bluetooth headphones)
- Skip intro and credits (TheIntroDB), next episode automatically
- Quality selection with 4K check, fallback to another source on errors or stuttering
- Buffer levels, connection watchdog, endless reconnect for live TV, reconnect after network change and standby
- HLS, MPEG-DASH and MPEG-TS
- Aspect ratio/zoom, frame-rate matching, technical info, clock overlay, sleep timer

**Personal**
- Profiles ("Who's watching?"), parental controls with PIN
- History, my list, favourites, backup and restore of settings
- Transfer to another device over Wi-Fi, optional Google Drive backup
- In-app update support for LiteStream GitHub Releases (the update feed is not configured yet)

**Look and feel**
- Navy-based interface, 12 colour themes, 5 backgrounds, custom colours
- Fonts Outfit, Inter or system; adjustable text and display size; animations off/subtle/normal
- Sidebar left, right or top, icons only or with text; phones: landscape, tap and double-tap controls
- App menus and controls are in English; provider names, programme titles and descriptions stay as supplied

## Screenshots

| | |
|---|---|
| ![Home screen](docs/screenshots/homescreen.jpg)<br>**Home** – rotating header, quick tiles, live TV now | ![Series details](docs/screenshots/details_series.jpg)<br>**Series details** – seasons, episodes with ratings, director |
| ![Categories](docs/screenshots/categories_list.jpg)<br>**Categories** – provider groups and optional genres | ![Director page](docs/screenshots/producer.jpg)<br>**Cast and crew pages** – with the titles from your playlist |
| ![Player](docs/screenshots/player.jpg)<br>**Player** – skip intro, audio/subtitles, quality | ![TV guide](docs/screenshots/tvguide.jpg)<br>**TV guide** – timeline with preview |
| ![Live TV](docs/screenshots/channel_list.jpg)<br>**Live TV** – now and next at a glance | ![Settings](docs/screenshots/settings.jpg)<br>**Settings** – grouped, remote-friendly |

<sub>Screenshots taken on a TV. Film artwork, channel logos and programme data belong to their respective owners; provider categories are blurred.</sub>

## Your own API keys (optional)

The app ships **without any API keys**. You can enter your own free keys in the app under **Settings > API keys**:

| Service | Used for | Key |
|---|---|---|
| TMDB | posters, descriptions, cast, trailers, optional automatic genres, episode info, similar titles | free, see below |
| OMDb | IMDb and Rotten Tomatoes ratings | free, see below |
| TheIntroDB | intro/recap/credits times | no key needed |

**Without keys** the app still works. With an Xtream Codes playlist it takes description, cover, rating, genres, running time and trailer **directly from your provider** (`player_api.php`). With a TMDB key it uses TMDB first and falls back to the provider when a title is not found. Keys are stored only on the device.

### Get a free TMDB key
1. Create an account at https://www.themoviedb.org/signup and confirm the e-mail address.
2. **Log in.** The API page shows "restricted" as long as you are not logged in.
3. Open https://www.themoviedb.org/settings/api (or: profile picture > Settings > API) and click **Request an API key**.
4. Choose **Developer**, accept the terms and fill in the form (application name e.g. "LiteStream TV", type "Personal use", a short description; the URL can be your GitHub page).
5. Copy the **API Key** (the 32-character one, not the "API Read Access Token").

### Get a free OMDb key
1. Open https://www.omdbapi.com/apikey.aspx, choose **FREE** (1,000 requests per day), enter your e-mail address and a short reason (e.g. "personal media player").
2. You receive an e-mail with an 8-character key and an **activation link**. Click the link, otherwise the key does not work.

### Enter the keys in the app
Settings > **API keys**: paste the key into the field, press **Save and check** (a green "Key valid" confirms it) and restart the app once. Tip: type long keys with the phone import or an USB keyboard instead of the remote.

*This product uses the TMDB API but is not endorsed or certified by TMDB.*

## Building

Requirements: Android Studio (current version) with the Android SDK; the Gradle wrapper downloads everything else.

```
git clone <this repository>
cd LITESTREAM
./gradlew assembleDebug        # Windows: gradlew.bat assembleDebug
```

The APK is then in `app/build/outputs/apk/debug/`. Release signing and the in-app update source are not configured yet; see [docs/RELEASE.md](docs/RELEASE.md). Every LiteStream TV release must use the same signing key.

Package ID: `com.litestream.tv`. It installs alongside IPVIO Stream, whose app data remains separate. Export an IPVIO backup and import it in LiteStream TV to move settings and playlists. Keep IPVIO installed until the restore is confirmed.

Optional Google Drive backup: [docs/GOOGLE_DRIVE.md](docs/GOOGLE_DRIVE.md).

Developers can optionally copy `secrets.properties.example` to `secrets.properties` (ignored by Git) and put their keys there; they are only used when no key was entered in the app.

Unit tests for the pure logic: `./gradlew test`

## Project layout

```
app/src/main/java/com/ipvio/stream/   all Kotlin sources (one package)
  *Logic.kt                           pure, Android-free logic (covered by unit tests)
  Dict*.kt                            translations (one row per text: de¦en¦tr¦bs¦hr¦sr¦es¦fr¦it)
app/src/test/java/com/ipvio/stream/   JUnit tests
app/src/main/res/font/                Outfit and Inter (SIL Open Font License)
licenses/                             font licences
```

## Project updates and contributions

LiteStream TV is maintained under [@StavrosAmProjects](https://github.com/StavrosAmProjects). Please use the [GitHub repository](https://github.com/StavrosAmProjects/LiteStream) for source, issues, and project updates. The project remains free and open source.

## License

Copyright (C) 2026 IPVIO Stream contributors
Copyright (C) 2026 @StavrosAmProjects (LiteStream-specific branding, product direction, and modifications)

LiteStream TV is distributed under the **GNU General Public License version 3** (see [LICENSE](LICENSE)), preserving IPVIO's license and notices. The declared Jellyfin Media3 FFmpeg decoder dependency is GPL-3.0; see [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) for the direct dependency and font inventory. Transitive dependency licenses still need verification from the resolved release build.

The name "IPVIO" and the IPVIO logo identify the original project. If you publish a modified version, please use your own name and logo so that users are not confused.

## Disclaimer

This software is provided "as is", without warranty of any kind. The authors do not provide, host, sell or link to any streams or playlists and are not responsible for how the app is used. Using playlists without the rights holders' permission may be illegal in your country.
