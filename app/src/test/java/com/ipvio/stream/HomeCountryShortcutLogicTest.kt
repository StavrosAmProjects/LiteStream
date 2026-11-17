package com.ipvio.stream

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeCountryShortcutLogicTest {
    @Test fun shortcutsRoundTripAndIgnoreMalformedRows() {
        val shortcuts = listOf(HomeCountryShortcut("DE"), HomeCountryShortcut("DE", "Sports | Live"))
        assertEquals(shortcuts, HomeCountryShortcutLogic.decode(HomeCountryShortcutLogic.encode(shortcuts)))
        assertEquals(listOf(HomeCountryShortcut("CY")), HomeCountryShortcutLogic.decode("CY|\ninvalid"))
    }
}
