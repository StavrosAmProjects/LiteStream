package com.ipvio.stream

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeLayoutDefaultsTest {
    @Test fun defaultHomeHidesOptionalRowsAndKeepsTvEssentials() {
        assertEquals(
            setOf("trend", "because", "wl", "hist", "newm", "topm", "news", "tops", "sg", "mg"),
            HomeLayoutDefaults.DEFAULT_HIDDEN
        )
        assertEquals(
            emptySet<String>(),
            setOf("hero", "tiles", "resume", "favch", "chnow").intersect(HomeLayoutDefaults.DEFAULT_HIDDEN)
        )
    }
}
