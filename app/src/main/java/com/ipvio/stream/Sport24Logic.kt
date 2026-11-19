package com.ipvio.stream

import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import java.util.TimeZone
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * sport24.gr TV program: parsing and mapping to the channels of your own playlist (pure logic, no Android).
 * Nothing is streamed from sport24 – it only tells which broadcaster shows what; the channel comes from your playlist.
 */
object Sport24Logic {
    /** One broadcast. [channelName] = broadcaster as written by sport24, [start] = epoch seconds. */
    class Row(val start: Long, val channelName: String, val title: String, val sport: String)

    private val TIME = Regex("""^(\d{1,2})[:.](\d{2})$""")
    private val TAGS = Regex("""<[^>]+>""")
    private val SCRIPTS = Regex("""<(script|style)[^>]*>.*?</\1>""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val BLOCKS = Regex("""</?(div|li|tr|td|th|p|br|span|a|h\d|section|article|time|strong|b)[^>]*>""", RegexOption.IGNORE_CASE)
    private val QUALITY = Regex("""\b(hd|fhd|uhd|4k|sd|hevc|h265|raw|vip|gr|el)\b""")
    private val SPORTS = Regex("""^(ποδοσφαιρο|μπασκετ|τενις|βολεϊ|βολει|χαντμπολ|πολο|μοτοσπορ|στιβος|ράγκμπι|rugby|golf|γκολφ|πυγμαχια|mma|ποδηλασια|φορμουλα|formula|futsal|αλλα|λοιπα)$""")

    fun norm(s: String): String = ChannelSearch.norm(s).replace(QUALITY, " ").replace(Regex("\\s+"), " ").trim()

    /** Entry point: JSON (array/object with time/channel/title fields) or HTML (text lines: time, broadcaster, title). */
    fun parse(body: String, known: (String) -> Boolean, dayStartSec: Long, tz: TimeZone): List<Row> {
        val t = body.trimStart()
        val rows = if (t.startsWith("{") || t.startsWith("[")) runCatching { parseJson(t, dayStartSec, tz) }.getOrDefault(emptyList())
        else emptyList()
        return rows.ifEmpty { parseHtml(body, known, dayStartSec, tz) }
    }

    /** Parses Sport24's public events feed (scheduleDate, channel.name, title and tags). */
    fun parseFeed(body: String, tz: TimeZone): List<Row> = runCatching {
        val rows = JSONArray(body)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).also { it.timeZone = tz }
        buildList {
            for (i in 0 until rows.length()) {
                val item = rows.optJSONObject(i) ?: continue
                val start = format.parse(item.optString("scheduleDate"))?.time?.div(1000) ?: continue
                val channel = item.optJSONObject("channel")?.optString("name").orEmpty()
                val title = item.optString("title")
                if (channel.isBlank() || title.isBlank()) continue
                val tags = item.optJSONArray("tags")
                val sport = (0 until (tags?.length() ?: 0)).asSequence()
                    .mapNotNull { tags?.optJSONObject(it)?.optString("displayName")?.ifBlank { null } }
                    .firstOrNull().orEmpty()
                add(Row(start, channel, title, sport))
            }
        }
    }.getOrDefault(emptyList())

    private fun parseHtml(html: String, known: (String) -> Boolean, dayStartSec: Long, tz: TimeZone): List<Row> {
        val text = html.replace(SCRIPTS, " ").replace(BLOCKS, "\n").replace(TAGS, " ")
            .replace("&nbsp;", " ").replace("&amp;", "&").replace("&#8211;", "–")
        val lines = text.split('\n').map { it.trim().replace(Regex("\\s+"), " ") }.filter { it.isNotEmpty() }
        val out = ArrayList<Row>()
        var i = 0
        while (i < lines.size) {
            val m = TIME.matchEntire(lines[i])
            if (m == null) { i++; continue }
            val start = atTime(dayStartSec, m.groupValues[1].toInt(), m.groupValues[2].toInt(), tz)
            val block = ArrayList<String>()
            var j = i + 1
            while (j < lines.size && block.size < 5 && TIME.matchEntire(lines[j]) == null) { block.add(lines[j]); j++ }
            // The broadcaster is the line that is a known channel; the sport is a known sport word; the title is the longest rest.
            val ch = block.firstOrNull { known(it) } ?: block.firstOrNull { it.length in 2..28 && !SPORTS.matches(norm(it)) } ?: ""
            val sport = block.firstOrNull { SPORTS.matches(norm(it)) } ?: ""
            val title = block.filter { it != ch && it != sport }.maxByOrNull { it.length } ?: ""
            if (ch.isNotEmpty() && title.isNotEmpty()) out.add(Row(start, ch, title, sport))
            i = j
        }
        return out
    }

    private val K_TIME = listOf("time", "start", "hour", "ora", "datetime", "date")
    private val K_CH = listOf("channel", "station", "network", "broadcaster", "tv")
    private val K_TITLE = listOf("title", "event", "match", "name", "description")
    private val K_SPORT = listOf("sport", "category", "league")

    private fun pick(o: JSONObject, keys: List<String>): String {
        for (k in keys) for (n in o.keys()) if (n.lowercase().contains(k)) {
            val v = o.opt(n)
            when (v) {
                is String -> if (v.isNotBlank()) return v
                is JSONObject -> v.optString("name").ifBlank { v.optString("title") }.let { if (it.isNotBlank()) return it }
            }
        }
        return ""
    }

    private fun parseJson(s: String, dayStartSec: Long, tz: TimeZone): List<Row> {
        val out = ArrayList<Row>()
        fun walk(v: Any?) {
            when (v) {
                is JSONArray -> for (i in 0 until v.length()) walk(v.opt(i))
                is JSONObject -> {
                    val time = pick(v, K_TIME); val ch = pick(v, K_CH); val title = pick(v, K_TITLE)
                    val hm = Regex("""(\d{1,2}):(\d{2})""").find(time)
                    if (hm != null && ch.isNotBlank() && title.isNotBlank()) {
                        out.add(Row(atTime(dayStartSec, hm.groupValues[1].toInt(), hm.groupValues[2].toInt(), tz), ch, title, pick(v, K_SPORT)))
                    } else for (k in v.keys()) walk(v.opt(k))
                }
            }
        }
        walk(if (s.startsWith("[")) JSONArray(s) else JSONObject(s))
        return out
    }

    fun dayStart(nowMs: Long, tz: TimeZone): Long {
        val c = Calendar.getInstance(tz); c.timeInMillis = nowMs
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis / 1000
    }

    /** Times after midnight (00:30 on a list that began the evening before) belong to the next day: callers sort by start. */
    private fun atTime(dayStartSec: Long, h: Int, m: Int, tz: TimeZone): Long {
        val c = Calendar.getInstance(tz); c.timeInMillis = dayStartSec * 1000
        c.set(Calendar.HOUR_OF_DAY, h); c.set(Calendar.MINUTE, m)
        return c.timeInMillis / 1000
    }

    // Same channel, different spelling/brand: applied on the compact form (no spaces), so "Nova Sports" = "NovaSports".
    private val ALIASES = listOf(
        "magentatv" to "cosmote", "magenta" to "cosmote", "cosmotetv" to "cosmote", "sports" to "sport"
    )
    private val RUNS = Regex("""\d+""")

    /** "GR| NovaSports Prime FHD" -> "novasportprime"; "Magenta Sport 1" -> "cosmotesport1". */
    fun canon(s: String): String {
        var w = norm(s).split(' ').filter { it.isNotEmpty() }
        if (w.size > 1 && w[0].length <= 2 && w[0].all(Char::isLetter)) w = w.drop(1)
        var c = w.joinToString("")
        for ((from, to) in ALIASES) if (from != to) c = c.replace(from, to)
        return c
    }

    /** Playlist channels in compact form, computed once per playlist (not per lookup). */
    class Pool(live: List<Item>) {
        val all: List<Pair<String, Item>> = live.map { canon(it.title) to it }.filter { it.first.length >= 3 }
        private val prefix = HashSet<String>().also { s -> all.forEach { s.add(it.first.take(4)) } }
        fun mayMatch(text: String): Boolean = canon(text).let { it.length >= 3 && it.take(4) in prefix }
    }

    /**
     * Match broadcaster and playlist names despite spaces, HD tags and known branding variants.
     * Numbers must match exactly ("Sport 1" never matches "Sport 2"); a manual choice always wins.
     */
    fun channelsFor(name: String, pool: Pool, fixed: String?): List<Item> {
        val out = ArrayList<Item>()
        if (fixed != null) pool.all.firstOrNull { it.second.id == fixed }?.let { out.add(it.second) }
        val n = canon(name)
        if (n.length < 3) return out
        val nums = RUNS.findAll(n).map { it.value }.toList()
        val scored = ArrayList<Pair<Int, Item>>()
        for ((cn, c) in pool.all) {
            val score = when {
                cn == n -> 0
                cn.contains(n) && boundaryOk(cn, n) && RUNS.findAll(cn).map { it.value }.toList() == nums -> 1
                n.contains(cn) && boundaryOk(n, cn) && RUNS.findAll(cn).map { it.value }.toList() == nums && cn.length >= 6 -> 2
                else -> continue
            }
            scored.add(score to c)
        }
        scored.sortedWith(compareBy({ it.first }, { it.second.title.length })).forEach { out.add(it.second) }
        return out.distinctBy { it.id }
    }

    /** [inner] inside [outer] must not cut a number ("sport1" must not match inside "sport12"). */
    private fun boundaryOk(outer: String, inner: String): Boolean {
        var i = outer.indexOf(inner)
        while (i >= 0) {
            val after = outer.getOrNull(i + inner.length)
            if (after == null || !after.isDigit() || !inner.last().isDigit()) return true
            i = outer.indexOf(inner, i + 1)
        }
        return false
    }

    /** Is this text one of the playlist's channel names (used to find the broadcaster line in HTML)? */
    fun isChannelText(text: String, pool: Pool): Boolean = text.length < 40 && pool.mayMatch(text) && channelsFor(text, pool, null).isNotEmpty()
}
