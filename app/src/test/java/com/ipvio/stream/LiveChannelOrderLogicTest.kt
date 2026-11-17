package com.ipvio.stream

import org.junit.Assert.assertEquals
import org.junit.Test

class LiveChannelOrderLogicTest {
    private fun channel(id: String, source: Int = 0) = Item(id, Kind.LIVE, id, null, "", null, "", src = source)

    @Test fun movedMergedRowsKeepOrderAcrossGroupsAndSources() {
        val rows = listOf(channel("a"), channel("b", 1), channel("c"))
        val moved = LiveChannelOrderLogic.move(rows, emptyList(), LiveChannelOrderLogic.key(rows[2]), -1)
        assertEquals(listOf("a", "c", "b"), LiveChannelOrderLogic.apply(rows, moved).map { it.id })
        val movedAgain = LiveChannelOrderLogic.move(rows, moved, LiveChannelOrderLogic.key(rows[1]), -1)
        assertEquals(listOf("a", "b", "c"), LiveChannelOrderLogic.apply(rows, movedAgain).map { it.id })
    }

    @Test fun greekChannelNamesEnterSmartPlaylists() {
        val greekBasketball = channel("1").copy(title = "Μπάσκετ Ολυμπιακός", group = "Ελλάδα")
        val greekMovies = channel("2").copy(title = "Ταινίες", group = "Ελληνικά")
        val greekSportsChannel = channel("3").copy(title = "ΕΡΤ2 ΣΠΟΡ", group = "EU | Greece")
        assertEquals(true, LiveGenre.matches(LiveGenre.byKey("GEN:sport")!!, greekBasketball))
        assertEquals(true, LiveGenre.matches(LiveGenre.byKey("GEN:movies")!!, greekMovies))
        assertEquals(true, SportLogic.isSportChannel(greekSportsChannel))
        assertEquals(SportLogic.catIndex("Basketball"), SportLogic.categoryOf("", "Ολυμπιακός Μπάσκετ"))
        assertEquals("🇬🇷", CountryBrowseLogic.flagEmoji("GR"))
    }
}
