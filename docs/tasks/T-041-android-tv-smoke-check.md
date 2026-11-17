# T-041 — Android TV smoke check

Use this focused run to verify the fork on the target Android TV/Google TV after installing a build from `codex/litestream-tv-fork`. Detailed edge cases remain in T-027 through T-040.

## Checks

1. **Fresh start and language:** confirm the menu is English and shows Home, Live TV, Movies, Series, and Settings. Use only D-pad/OK/Back. Under Settings > Menu, reveal an advanced section; return Home.
2. **Provider setup:** on API 37+, choose Set up from phone and grant local-network access. Scan the QR, send a test provider, check the redacted TV preview, then cancel. Repeat and confirm; verify the provider loads. Also import a TXT file or complete provider URL. Confirm permission denial starts no listener and shows no QR.
3. **Filtering:** for an Xtream provider, try ALL, NONE, and CUSTOMIZE. In Customize, change a country/group and Cancel; confirm nothing changes. Repeat with Done; confirm the subset appears in Live TV, Movies, Series, and Search. Unknown channels must remain reachable when included.
4. **Multiple providers and order:** enable two providers, set a filter on each, open the merged country view, and create an own channel list using channels from both. Move entries up/down, restart, and confirm the curated order remains. The general merged catalogue keeps provider order.
5. **Playback and recovery:** play a live channel, use Up/Down to surf, reveal controls and wait about three seconds for them to hide. Press Back. Confirm live playback has no misleading VOD seek bar. On a failed stream, run the connection check and confirm its result does not show the URL or credentials.
6. **Edit and other phone tools:** edit a saved provider on phone, verify the old provider remains if canceled, and confirm the replacement only after TV approval. If used, open phone remote, backup transfer, list import, and API-key entry once to verify the Android 17 permission is requested before their local server starts.

## Report

Record TV model/Android version and pass/fail for each numbered check. For failures, note the step and visible result; do not include provider credentials or full URLs in screenshots or logs.

The debug APK is built under T-042. No unit test or device result is recorded by this checklist. It is a manual validation plan.
