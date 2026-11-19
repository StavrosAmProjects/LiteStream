# T-060: Sport24 schedule and Live TV actions/mobile browsing

**Status:** Implemented; Sport24 schedule behavior approved by the maintainer on 19 Nov 2026. Other manual screen checks pending.

## Goal

Fix Sport24's empty schedule/reload behavior and improve visibility of common playlist actions and country/category browsing on phones.

## Changes

- `Sport24Guide.kt`: fetch Sport24's public dated events JSON feed instead of its client-rendered page shell; reload refetches; add previous/today/next date controls.
- `Sport24Logic.kt`: parse the feed's dated schedule rows, broadcaster, title, and tags.
- `LiveTab.kt`: put Update, Switch, and Edit playlist actions beside Cards/List. Switching offers individual, merged, and all playlists.
- `CountryBrowseScreen.kt`: use a compact one-column country → category → channel flow on narrow screens; keep the three-pane TV layout and make channel rows denser.
- `SettingsHub.kt`: explain that Customize live TV can restore previously excluded categories.
- Favourite categories on Home were already implemented and tested in T-054; no duplicate implementation was added.

## Data and user impact

No playlist/provider storage format changed. The schedule is fetched from Sport24's public `tv.24media.gr/service/events` feed. Existing per-provider selection and retained local playlist sources are reused, so the settings picker can reselect omitted categories.

## Validation

- Confirmed the public endpoint returns dated JSON event rows for 2026-11-19.
- `:app:assembleDebug`: passed.
- `git diff --check`: passed.
- Installed and launched package `com.litestream.tv.test` on `192.168.1.58:39119` (OnePlus AC2003); `pm path` confirms installed APK.
- Unit tests and manual Sport24/mobile/TV interaction checks were not run.

## Maintainer checks

1. Open Sports on TV and confirm today's rows load; press Reload and confirm a fresh request succeeds.
2. Use Previous day / Today / Next day, including a day with no broadcasts.
3. On Live TV Home, use Cards/List, Update playlist, Switch playlist (single/merged/all), and Edit playlist.
4. On the phone, open country browsing; select a country, a category, and browse the compact channel list; verify Back returns one level.
5. In Settings > Playlists > Customize live TV, select a previously excluded category and confirm its channels return after applying.
6. Confirm existing Home favourite category shortcuts still open their category.

## Risks and limits

Sport24 controls its undocumented public feed and may change its response or access policy. No schedule cache is kept for an empty response. The UI mapping to an IPTV stream still depends on matching broadcaster names or the saved manual mapping.
