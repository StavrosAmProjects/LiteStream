package com.ipvio.stream

import org.junit.Assert.assertEquals
import org.junit.Test

class CountryBrowseOrderLogicTest {
    @Test fun savedOrderSurvivesRefreshAndMovesOneCountryAtATime() {
        val base = listOf("CY", "DE", "GB", "GR")
        assertEquals(listOf("GR", "CY", "DE", "GB"), CountryBrowseOrderLogic.apply(base, listOf("GR", "missing")))
        assertEquals(listOf("CY", "GB", "DE", "GR"), CountryBrowseOrderLogic.move(base, listOf("CY", "DE", "GB", "GR"), "GB", -1))
    }

    @Test fun firstCountryCannotMoveFurtherUp() {
        assertEquals(listOf("CY", "DE"), CountryBrowseOrderLogic.move(listOf("CY", "DE"), emptyList(), "CY", -1))
    }

    @Test fun movingVisibleCountriesKeepsOrderForCountriesTemporarilyMissing() {
        assertEquals(listOf("DE", "CY", "GR"), CountryBrowseOrderLogic.move(listOf("CY", "DE"), listOf("CY", "GR", "DE"), "DE", -1))
    }
}
