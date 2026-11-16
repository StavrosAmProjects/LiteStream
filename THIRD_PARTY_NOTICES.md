# Third-party notices

LiteStream TV is derived from IPVIO Stream. The retained IPVIO source and this fork are distributed under GNU GPL v3; see `LICENSE` and the copyright notices in the source. The following inventory reflects direct declarations in `app/build.gradle.kts` and local font assets.

## Libraries

| Component | Declared version | Scope | License |
|---|---:|---|---|
| AndroidX Activity Compose | 1.13.0 | App | Apache License 2.0 |
| Jetpack Compose UI/Foundation | Compose BOM 2026.09.00 | App | Apache License 2.0 |
| AndroidX Media3 ExoPlayer, HLS, DASH, UI | 1.11.1 | App | Apache License 2.0 |
| Jellyfin Media3 FFmpeg decoder (`org.jellyfin.media3:media3-ffmpeg-decoder`) | 1.9.0+1 | App | GNU GPL v3.0 (Maven metadata) |
| OkHttp (`com.squareup.okhttp3:okhttp`) | 4.12.0 | App | Apache License 2.0 |
| Coil Compose (`io.coil-kt:coil-compose`) | 2.7.0 | App | Apache License 2.0 |
| AndroidX Work Runtime KTX | 2.10.0 | App | Apache License 2.0 |
| JUnit 4 (`junit:junit`) | 4.13.2 | Tests only | Eclipse Public License 1.0 |
| JSON-java (`org.json:json`) | 20250517 | Tests only | Public domain |

Kotlin standard library and coroutine artifacts are used by the application, but their resolved versions and the full transitive runtime dependency set have not been verified in this environment. They must be included in the release review from the resolved Gradle dependency graph. Dependencies are downloaded at build time; their license texts and required notices must be preserved from the respective upstream distributions.

License references checked for this direct inventory:

- [AndroidX license](https://github.com/androidx/androidx/blob/androidx-main/LICENSE)
- [Media3 license](https://github.com/androidx/media/blob/release/LICENSE)
- [Jellyfin decoder Maven metadata](https://central.sonatype.com/artifact/org.jellyfin.media3/media3-ffmpeg-decoder/1.9.0%2B1) and [source license](https://github.com/jellyfin/jellyfin-androidx-media/blob/master/LICENSE)
- [OkHttp 4.12.0 license](https://github.com/square/okhttp/blob/parent-4.12.0/LICENSE.txt)
- [Coil 2.7.0 license](https://github.com/coil-kt/coil/blob/2.7.0/LICENSE.txt)
- [JUnit 4 license](https://github.com/junit-team/junit4/blob/main/LICENSE-junit.txt)
- [JSON-java license](https://github.com/stleary/JSON-java/blob/master/LICENSE)

## Fonts

| Font | License | File |
|---|---|---|
| Outfit (The Outfit Project Authors) | SIL Open Font License 1.1 | `licenses/OFL_Outfit.txt` |
| Inter (The Inter Project Authors) | SIL Open Font License 1.1 | `licenses/OFL_Inter.txt` |

The font files in `app/src/main/res/font/` are subsets/copies of the original fonts and remain under the OFL.

## Online services (optional, user-supplied keys)

| Service | Purpose | Notes |
|---|---|---|
| The Movie Database (TMDB) | film and series information, images | *This product uses the TMDB API but is not endorsed or certified by TMDB.* Users must provide their own API key and follow the TMDB terms of use. |
| OMDb API | IMDb and Rotten Tomatoes ratings | Users must provide their own API key. |
| TheIntroDB | intro, recap and credits times | Read access without a key. |

IMDb, Rotten Tomatoes, TMDB, Android TV, Google TV and Fire TV are trademarks of their respective owners. LiteStream TV is not affiliated with or endorsed by them.
