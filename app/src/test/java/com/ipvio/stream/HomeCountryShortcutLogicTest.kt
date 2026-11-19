package com.ipvio.stream

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeCountryShortcutLogicTest {
    private fun item(id: String, group: String) = Item(id, Kind.LIVE, "Channel $id", null, "http://example.test/$id", null, group)

    @Test fun categoryFavoritesBecomeFlagShortcutsInSavedOrder() {
        val shortcuts = HomeCountryShortcutLogic.favoriteGroups(
            listOf("GR | Sports", "GR | General", "Unknown"),
            mapOf("GR | Sports" to listOf(item("1", "GR | Sports")), "GR | General" to listOf(item("2", "GR | General")))
        )
        assertEquals(listOf(HomeCountryShortcut("GR", "GR | Sports"), HomeCountryShortcut("GR", "GR | General")), shortcuts)
    }

    @Test fun shortcutsRoundTripAndIgnoreMalformedRows() {
        val shortcuts = listOf(HomeCountryShortcut("DE"), HomeCountryShortcut("DE", "Sports | Live"))
        assertEquals(shortcuts, HomeCountryShortcutLogic.decode(HomeCountryShortcutLogic.encode(shortcuts)))
        assertEquals(listOf(HomeCountryShortcut("CY")), HomeCountryShortcutLogic.decode("CY|\ninvalid"))
    }
}
