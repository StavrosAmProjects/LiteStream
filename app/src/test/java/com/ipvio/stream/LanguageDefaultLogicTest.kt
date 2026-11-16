package com.ipvio.stream

import org.junit.Assert.assertEquals
import org.junit.Test

class LanguageDefaultLogicTest {
    @Test fun englishIsOnlyTheMissingPreferenceDefault() {
        assertEquals("en", LanguageDefaultLogic.resolve(null))
        assertEquals("", LanguageDefaultLogic.resolve(""))
        assertEquals("de", LanguageDefaultLogic.resolve("de"))
    }
}
