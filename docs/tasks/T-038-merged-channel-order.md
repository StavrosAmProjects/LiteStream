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

## Manual Android TV check

1. Add two providers, enable both in Settings > Playlists, and save different country/group selections for each.
2. In Live TV, choose **All playlists** and open Countries. Confirm the country view contains the combined channels that pass each provider's own filter.
3. From the merged **All channels** list, create an own channel list. Add channels from both providers; verify search while adding searches the currently filtered merged catalogue.
4. Open the own list, use the channel menu to move entries up/down, then play a channel and verify channel surfing stays in that custom order.
5. Restart the app and verify the order remains. If checking profile backup, export and restore the profile and confirm the list and order return.

This checks custom ordering of the user's curated cross-provider list. The underlying merged provider catalogue remains in provider order; it does not expose a global manual reorder mode.
