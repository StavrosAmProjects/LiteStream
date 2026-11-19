package com.ipvio.stream

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.TimeZone

class Sport24LogicTest {
    private fun ch(id: String, t: String) = Item(id, Kind.LIVE, t, null, "http://x/$id", null, "Sports")
    private val live = listOf(ch("1", "GR| COSMOTE SPORT 1 HD"), ch("2", "GR| COSMOTE SPORT 2 HD"), ch("3", "NOVA SPORTS PRIME FHD"), ch("4", "EUROSPORT 1"))
    private val pool = Sport24Logic.Pool(live)
    private val tz = TimeZone.getTimeZone("Europe/Athens")

    @Test fun numbersMustMatch() {
        assertEquals("1", Sport24Logic.channelsFor("Cosmote Sport 1", pool, null).first().id)
        assertEquals("2", Sport24Logic.channelsFor("Cosmote Sport 2", pool, null).first().id)
        assertTrue(Sport24Logic.channelsFor("Cosmote Sport 7", pool, null).isEmpty())
    }

    @Test fun spacingAndBrandsDontMatter() {
        assertEquals("3", Sport24Logic.channelsFor("Novasports Prime", pool, null).first().id)
        assertEquals("3", Sport24Logic.channelsFor("Nova Sport Prime", pool, null).first().id)
        val l2 = listOf(ch("9", "NOVASPORTS PRIME"), ch("8", "Magenta Sport 1 FHD"), ch("7", "Cosmote Sports 2"))
        val p2 = Sport24Logic.Pool(l2)
        assertEquals("9", Sport24Logic.channelsFor("Nova Sports Prime", p2, null).first().id)
        assertEquals("8", Sport24Logic.channelsFor("Cosmote Sport 1", p2, null).first().id)
        assertEquals("7", Sport24Logic.channelsFor("Magenta Sport 2", p2, null).first().id)
        assertTrue(Sport24Logic.channelsFor("Cosmote Sport 1", Sport24Logic.Pool(listOf(ch("5", "Cosmote Sport 12"))), null).isEmpty())
    }

    @Test fun handChoiceWins() {
        assertEquals("4", Sport24Logic.channelsFor("Cosmote Sport 1", pool, "4").first().id)
    }

    @Test fun parsesHtmlLines() {
        val html = "<div><span>20:45</span><span>Cosmote Sport 1</span><span>Ολυμπιακός - ΠΑΟΚ</span><span>Ποδόσφαιρο</span></div>" +
            "<div><span>21:00</span><span>Nova Sports Prime</span><span>Λίβερπουλ - Άρσεναλ</span></div>"
        val rows = Sport24Logic.parse(html, { Sport24Logic.isChannelText(it, pool) }, Sport24Logic.dayStart(0L, tz), tz)
        assertEquals(2, rows.size)
        assertEquals("Ολυμπιακός - ΠΑΟΚ", rows[0].title)
        assertEquals("Cosmote Sport 1", rows[0].channelName)
    }

    @Test fun parsesJson() {
        val json = """{"items":[{"time":"18:30","channel":"Eurosport 1","title":"ATP Paris"}]}"""
        val rows = Sport24Logic.parse(json, { true }, Sport24Logic.dayStart(0L, tz), tz)
        assertEquals("Eurosport 1", rows.single().channelName)
    }
