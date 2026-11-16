# T-028 — Country-first Live TV browsing

## Goal

Add a country → provider group → channel browsing path over the active IPVIO Live TV list. Keep original provider groups and channel records untouched; include an Unknown country bucket.

## Plan

- Add a pure, deterministic country classifier with common aliases and flag support; test false-positive boundaries and provider separators.
- Derive country/group/channel lists from the already-loaded live items. Do not change playlist parsing, storage, refresh, or provider filters.
- Add a focused three-pane Live TV view and a clear entry from the existing category list. Reuse existing channel playback and player flow.
- Keep this task to browsing. Persistent country/group selection and ALL/NONE/CUSTOMIZE onboarding are separate follow-up work after this view is stable.

## Affected files

New `CountryBrowseLogic.kt`, `CountryBrowseScreen.kt`, `CountryBrowseLogicTest.kt`; `LiveTab.kt`, `DictA.kt`; `WORK_STATE.md`; `CHANGELOG.md`.

## Risks and validation

- Country inference can misclassify provider names; unknown content must remain reachable, and classification is presentation-only.
- The custom screen must retain D-pad focus, logical Back, and normal channel playback.
- Run `git diff --check`; Gradle/device checks are maintainer steps in this environment.

## Status

Implemented on `codex/litestream-tv-fork`. Added tested pure classification/projection source and a three-pane D-pad browse dialog launched from Live TV > Countries. Provider items and their groups remain untouched; unknown items stay visible. `git diff --check` passed. Gradle/JUnit and device checks were not run.

Manual checks for the maintainer:
1. Open Live TV > Countries and use only D-pad/OK to select a country, then provider group, then channel.
2. Press Back from channels, groups, and countries; each step returns one level.
3. Play a channel and use normal channel switching; Back from playback returns to the country view.
4. Try provider labels with `CY`, `Cyprus`, `GR`, `Greek`, `UK`, `GB`, and country flags; verify Unknown still shows unclassified channels.
5. Verify the normal Live TV category list and provider catalogue are unchanged.

Known limit: country display names come from Java's English locale data, with common provider aliases explicitly defined in the classifier. No persistent country/group filters or ALL/NONE/CUSTOMIZE setup is included in this task.
