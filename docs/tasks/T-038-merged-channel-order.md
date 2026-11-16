# T-038 — Custom order for channels across merged providers

## Goal

Let users decide the order of channels from multiple installed playlists while retaining country/group filtering and provider data.

## Source audit

`PlaylistStore` already supports multiple saved playlists and an opt-in merged view (`multi`, `merged`, `MultiLogic`). T-029 stores country/group visibility per provider. Existing “own order” controls reorder categories. `FavOrderLogic` supports manual order for favorites only; it does not reorder every channel in a merged catalogue.

The LiteStream Android source audit described country/category import and grouping, but did not establish a persisted arbitrary channel order across fused providers. The combined selected-country and cross-provider order is therefore an extension request, not a feature already proven in OG LiteStream.

## Plan

- Define the smallest TV interaction: reorder a selected country/group/category, or reorder individual channels; do not implement both until the user need is demonstrated.
- Preserve provider data and current source order. Store only the user's presentation order, scoped to profile and stable channel identity.
- Reuse multi-provider source identity and existing favorite/category order patterns; preserve choices across refresh where identity remains stable.
- Add pure ordering tests and manual D-pad checks before any storage format change.

## Affected areas

Likely `MultiLogic.kt`, `PlaylistStore.kt`, Live TV/category presentation and existing order persistence; any new preference key requires review of profile scoping, backup inclusion, collision handling and refresh behavior before implementation.

## Risks

Merged channel identity can change across provider refreshes, and providers may contain duplicate channels. Persisting an order against unstable URLs or provider list indices can lose or misapply user choices. No storage key or schema has been added.

## Acceptance criteria

- [ ] TV users can set the selected scope's order with D-pad controls.
- [ ] Filtering and refresh do not delete provider entries or silently reorder unrelated scopes.
- [ ] Order survives restart and refresh; profile and backup behavior is defined.
- [ ] JVM ordering coverage and Android TV manual steps are documented.

## Status

Audit and implementation plan only. Existing multi-provider merge, per-provider filters, category ordering, and favorite ordering remain available.
