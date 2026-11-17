# T-049: Sports on TV Home shortcut

- **Goal:** Put a small sports schedule shortcut on Home and route it to the existing Sports area, where fixtures and the user's own channel/EPG matches are already joined.
- **Affected files:** `HomeScreen.kt`, `HomeParts.kt`, `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`.
- **Acceptance:** Home shows a Sports on TV quick tile; selecting it opens the existing Sports hub. Existing SportWatch matching can map a fixture to channels in the installed playlist and open playback.
- **Source limitation:** Sport24 publishes today's sports broadcast schedule, but a direct request to its page returns HTTP 403 in this environment and the page does not expose a documented public feed/API in the inspected source. Do not bypass that restriction or embed a brittle scraper. This first version uses the existing fixture feed plus the user's IPTV EPG and channel matching; it does not claim Sport24 as its source.
- **Tests:** No new logic; manual: select the tile, inspect a known fixture with EPG available, confirm the matched channel is shown and opens from the Sports hub. Availability depends on the provider's EPG and channel naming.
- **Status:** Implemented; debug build succeeded and APK installed/launched on the phone. Feature interaction checks and Android TV checks remain pending.
