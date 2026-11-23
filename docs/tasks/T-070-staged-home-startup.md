# T-070 – Stage Home data for faster Live TV access

**Status:** implemented; build and device validation pending.

## Goal

Make saved Live TV category favourites usable before the full playlist-derived Home indexes finish building, while keeping the complete local catalogue and existing category behavior.

## Changes

- Partition the already-loaded catalogue once into Live TV, movies, and series.
- Publish Live TV channels, channel lookup, and only saved provider-category favourites first.
- Build all provider categories and movie/series indexes afterward on the existing background dispatcher.
- Keep VOD, favorites, history, and other catalogue-dependent tabs on their loading state until full data is ready, avoiding false empty states.
- Do not alter provider storage, playlist contents, or refresh behavior.

## Validation

- `git diff --check`: passed.
- Gradle build and automated tests: not run.
- Android TV timing and focus check: pending.

## Manual checks

1. Cold-start with a large playlist and confirm Home tiles/favourite-category shortcuts become usable before the rest of the catalogue finishes preparing.
2. Open Live TV during preparation; confirm saved categories and All channels work, then confirm remaining provider categories appear when preparation completes.
3. Verify Movies, Series, Favorites, and History do not briefly show empty-state messages while their full indexes are still being built.
4. Reopen the app with a warm cache and confirm the existing fast-start path remains unchanged.
