package com.ipvio.stream

import org.junit.Assert.*
import org.junit.Test

class CountryFilterLogicTest {
    private fun item(id: String, group: String, title: String = id, src: Int = 0) =
        Item(id, Kind.LIVE, title, null, "http://example.test/$id", null, group, src = src)

    @Test fun missingSelectionMeansAllAndEmptySelectionMeansNone() {
        val items = listOf(item("cy", "CY | News"), item("gr", "GR | Sport"), item("unknown", "Other"))
        assertSame(items, CountryFilterLogic.filterSources(items, listOf(null)))
        assertTrue(CountryFilterLogic.filterSources(items, listOf(emptySet())).isEmpty())
    }

    @Test fun countryAndGroupTogglesKeepPartialDraftAndRestoreAll() {
        val countries = CountryBrowseLogic.project(listOf(item("a", "CY | News"), item("b", "CY | Sport"), item("c", "GR | News")))
        val cyprus = countries.first { it.code == "CY" }
        var draft: Set<String>? = null
        draft = CountryFilterLogic.toggleGroup(draft, "CY", "CY | News", countries)
        assertTrue(CountryFilterLogic.isCountryPartial(draft, cyprus))
        assertFalse(CountryFilterLogic.isGroupSelected(draft, "CY", "CY | News"))
        assertTrue(CountryFilterLogic.isGroupSelected(draft, "CY", "CY | Sport"))
        draft = CountryFilterLogic.toggleCountry(draft, cyprus, countries)
        assertTrue(CountryFilterLogic.isCountrySelected(draft, cyprus))
        draft = CountryFilterLogic.toggleCountry(draft, cyprus, countries)
        assertFalse(CountryFilterLogic.isCountrySelected(draft, cyprus))
        draft = CountryFilterLogic.toggleGroup(draft, "CY", "CY | News", countries)
        draft = CountryFilterLogic.toggleGroup(draft, "CY", "CY | Sport", countries)
        assertNull(draft)
    }

    @Test fun filterPreservesUnknownAndSeparatesProviderSources() {
        val items = listOf(item("cy", "CY | News", src = 0), item("gr", "GR | News", src = 1), item("unknown", "Other", src = 1))
        val projection = CountryBrowseLogic.project(items)
        val cyprus = projection.first { it.code == "CY" }
        val none = CountryFilterLogic.toggleCountry(null, cyprus, projection)
        val visible = CountryFilterLogic.filterSources(items, listOf(none, null))
        assertEquals(listOf("gr", "unknown"), visible.map { it.id })
    }

    @Test fun providerPreferenceKeyDoesNotContainTheProviderUrl() {
        val url = "https://tv.example/get.php?username=private&password=secret"
        val key = CountryFilterStore.key(url)
        assertTrue(key.startsWith("country_filter_v1_"))
        assertFalse(key.contains("private"))
        assertFalse(key.contains("secret"))
        assertEquals(key, CountryFilterStore.key(url))
    }

    @Test fun selectedGroupsMatchLiveItemsAndProviderCategories() {
        val selected = setOf(CountryFilterLogic.groupKey("GR", "GR | Sports"))
        assertTrue(CountryFilterLogic.includesLive(selected, "GR | Sports", "Alpha"))
        assertFalse(CountryFilterLogic.includesLive(selected, "GR | Movies", "Film"))
        assertTrue(CountryFilterLogic.includesCategory(selected, "GR | Sports"))
        assertFalse(CountryFilterLogic.includesCategory(selected, "GR | Movies"))
        assertFalse(CountryFilterLogic.includesLive(emptySet(), "GR | Sports", "Alpha"))
        assertTrue(CountryFilterLogic.includesLive(null, "GR | Sports", "Alpha"))
    }
}
