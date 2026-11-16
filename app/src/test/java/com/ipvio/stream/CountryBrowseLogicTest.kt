package com.ipvio.stream

import org.junit.Assert.*
import org.junit.Test

class CountryBrowseLogicTest {
    private fun item(id: String, title: String, group: String) = Item(id, Kind.LIVE, title, null, "http://example.test/$id", null, group)

    @Test fun recognizesCountryAliasesFlagsAndPrefixSuffixMarkers() {
        assertEquals("CY", CountryBrowseLogic.classify("🇨🇾 | Sports"))
        assertEquals("GR", CountryBrowseLogic.classify("gr: Greek Premium"))
        assertEquals("GR", CountryBrowseLogic.classify("Movies - Ellada"))
        assertEquals("GB", CountryBrowseLogic.classify("UK - News"))
        assertEquals("GB", CountryBrowseLogic.classify("Drama | GB"))
        assertEquals("GB", CountryBrowseLogic.classify("🇬🇧 Great Britain | Sports"))
        assertEquals("US", CountryBrowseLogic.classify("United States - Entertainment"))
    }

    @Test fun doesNotGuessFromOrdinaryWordsAndPreservesUnknownEntries() {
        assertNull(CountryBrowseLogic.classify("International Movies"))
        assertNull(CountryBrowseLogic.classify("Universal News"))
        val unknown = item("u", "World News", "International")
        val source = listOf(unknown)
        val projection = CountryBrowseLogic.project(source)
        assertEquals("Unknown", projection.single().name)
        assertSame(unknown, projection.single().groups.single().channels.single())
        assertEquals(1, source.size)
    }

    @Test fun groupsChannelsByCountryWithoutChangingProviderGroupNames() {
        val cyOne = item("1", "Channel 1", "CY | News")
        val cyTwo = item("2", "Channel 2", "CY | News")
        val gr = item("3", "Channel 3", "Greek | Movies")
        val result = CountryBrowseLogic.project(listOf(cyOne, gr, cyTwo))
        assertEquals(listOf("CY", "GR"), result.map { it.code })
        assertEquals("CY | News", result.first().groups.single().name)
        assertEquals(listOf(cyOne, cyTwo), result.first().groups.single().channels)
        assertEquals("Greek | Movies", result.last().groups.single().name)
    }

    @Test fun usesCountryPrefixInChannelNameWhenProviderGroupHasNoCountry() {
        val channel = item("1", "UK | BBC One", "General")
        assertEquals("GB", CountryBrowseLogic.project(listOf(channel)).single().code)
    }
}
