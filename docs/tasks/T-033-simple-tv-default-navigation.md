# T-033 — Simple default TV navigation

## Goal

Make first-run LiteStream TV navigation show only Home, Live TV, Movies, Series, and Settings. Keep advanced sections available through Settings > Menu, and preserve every saved navigation choice.

## Plan

- Reuse `MenuLogic.DEFAULT_HIDDEN`; do not add another navigation mode or persistence key.
- Hide Search, EPG, Favorites, My List, History, and Sports only when no saved menu preference exists.
- Leave `AppSettings.init` and saved `menu_hidden` values unchanged so existing users and imported backups keep their navigation.
- Update pure logic tests for the visible default and the current test that asserts History is not hidden.

## Affected files

`MenuLogic.kt`, `CoreLogicTest.kt`, `Round17Test.kt`, `docs/tasks/WORK_STATE.md`, `CHANGELOG.md`.

## Risks and validation

- Advanced sidebar sections will be less prominent on fresh installs; users can restore them under Settings > Menu.
- Saved settings and imported backups must retain their existing menu choices.
- Unit tests cover the default hidden and visible lists. Android TV validation remains needed for focus and discoverability.

## Status

Planned as part of the approved LiteStream objective. No saved preferences will be rewritten. Manual TV check: fresh install shows Home, Live TV, Movies, Series, Settings; Settings > Menu can reveal each advanced section; an imported backup with a custom menu keeps that menu.
