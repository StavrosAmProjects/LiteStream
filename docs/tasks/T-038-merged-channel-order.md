# T-038 — Merged playlist selection and custom channel order

## Goal

Check whether the existing multi-provider and channel-list features already let users combine selected provider content and arrange a useful TV subset.

## Source audit result

This is already supported without another storage system:

1. Add/import multiple playlists using the existing provider flow.
2. In Settings > Playlists, enable additional playlists for simultaneous display.
3. Save country/group filters separately for each provider; the filtered source lists are merged for Live TV.
4. In the merged Live TV channel view, create an own channel list and add the desired channels. Own lists accept channels from the current visible catalogue and already support moving entries up/down.
5. `ChannelLists` is profile-scoped; `ipvio_chlists` is included in profile backup and restore.

The arbitrary cross-provider order is therefore available for a curated custom channel list, not as a reorder of the underlying merged provider catalogue. Category ordering is separate and already supports provider, A–Z, and own order. Favorites also have their own manual ordering.

The LiteStream Android audit confirmed country/category selection and grouping, but did not establish a global custom order across merged providers. No new code or data format is needed for the requested curated-list workflow.

## Status

Source audit complete; existing features satisfy the requested workflow. No parallel ordering implementation added. Android TV discoverability and D-pad behavior should be checked by the maintainer.
