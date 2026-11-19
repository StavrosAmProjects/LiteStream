package com.ipvio.stream

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Locale

/** Fetches the sport24.gr TV program and remembers the broadcaster -> channel choices you made by hand. */
object Sport24Guide {
    const val URL = "https://www.sport24.gr/tvprogram/"

    class State(val rows: List<Sport24Logic.Row>, val error: String?)

    @Volatile private var cached: Pair<Long, List<Sport24Logic.Row>>? = null

    suspend fun load(live: List<Item>, force: Boolean = false): State = withContext(Dispatchers.IO) {
        val c = cached
        val now = System.currentTimeMillis()
        if (!force && c != null && now - c.first < 5 * 60_000) return@withContext State(c.second, null)
        try {
            val req = Request.Builder().url(URL).header("User-Agent", "Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36").header("Accept-Language", "el,en;q=0.8").build()
            val body = Net.base.newCall(req).execute().use { if (!it.isSuccessful) return@withContext State(emptyList(), "HTTP ${it.code}"); it.body?.string().orEmpty() }
            val tz = General.zone()
            val pool = Sport24Logic.Pool(live)
            val rows = Sport24Logic.parse(body, { Sport24Logic.isChannelText(it, pool) }, Sport24Logic.dayStart(now, tz), tz).sortedBy { it.start }
            if (rows.isNotEmpty()) cached = now to rows
            State(rows, if (rows.isEmpty()) "empty" else null)
        } catch (e: Exception) { State(c?.second ?: emptyList(), e.message ?: "error") }
    }

    // Hand-made choices: sport24 broadcaster name (normalized) -> channel ID, per profile
    val fixed = androidx.compose.runtime.mutableStateMapOf<String, String>()
    private var loadedProfile = -1
    private fun prefs(c: Context) = Profiles.sp(c, "ipvio_sport24fix")

    fun init(c: Context) {
        if (loadedProfile == Profiles.active) return
        fixed.clear()
        loadedProfile = Profiles.active
        (prefs(c).getString("m", "") ?: "").split("\n").forEach { l ->
            val i = l.indexOf('\t'); if (i > 0) fixed[l.substring(0, i)] = l.substring(i + 1)
        }
    }

    fun set(c: Context, name: String, chId: String?) {
        init(c)
        val k = Sport24Logic.norm(name)
        if (chId == null) fixed.remove(k) else fixed[k] = chId
        prefs(c).edit().putString("m", fixed.entries.joinToString("\n") { it.key + "\t" + it.value }).apply()
    }
}

/** Hub tab: what is on TV today (sport24.gr), each row jumps into the matching channel of your playlist. */
@Composable
internal fun Sport24Tab(hub: HubCtx) {
    val ctx = LocalContext.current
    Sport24Guide.init(ctx)
    var reload by remember { mutableIntStateOf(0) }
    var assign by remember { mutableStateOf<Sport24Logic.Row?>(null) }
    val pool = remember(hub.live) { Sport24Logic.Pool(hub.live) }
    val state by produceState<Sport24Guide.State?>(null, hub.live, reload) { value = Sport24Guide.load(hub.live, reload > 0) }
    val fmt = remember { SimpleDateFormat("HH:mm", Locale.ROOT).also { it.timeZone = General.zone() } }
    val nowSec = System.currentTimeMillis() / 1000

    val s = state
    when {
        s == null -> HubEmpty(AIcon.Info, "Loading TV schedule…", "sport24.gr")
        s.rows.isEmpty() -> HubEmpty(AIcon.Info, "No TV schedule found", "sport24.gr returned no listings (${s.error ?: "unknown error"}).", "Reload") { reload++ }
        else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = DS.PageX, vertical = dpx(8)), verticalArrangement = Arrangement.spacedBy(dpx(4))) {
            item(key = "head") { HubSectionTitle("Today's sports TV schedule · sport24.gr", action = "Reload") { reload++ } }
            itemsIndexed(s.rows, key = { i, r -> i.toString() + r.start + r.channelName }) { _, r ->
                val chans = Sport24Logic.channelsFor(r.channelName, pool, Sport24Guide.fixed[Sport24Logic.norm(r.channelName)])
                val live = r.start <= nowSec && nowSec - r.start < 3 * 3600
                FRow(
                    onClick = { if (chans.isNotEmpty()) hub.play(chans[0], chans) else assign = r },
                    onLong = { assign = r }
                ) {
                    Text(raw = true, text = fmt.format(r.start * 1000), color = if (live) Accent2 else Muted, fontSize = spx(18), fontWeight = FontWeight.Bold, modifier = Modifier.width(dpx(70)))
                    Column(Modifier.weight(1f)) {
                        Text(raw = true, text = r.title, color = Tok.TextHi, fontSize = spx(18), fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(raw = true, text = r.channelName + (if (r.sport.isNotBlank()) " · " + r.sport else ""), color = Muted, fontSize = spx(14), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    if (chans.isNotEmpty()) Text(raw = true, text = chans[0].title, color = Accent2, fontSize = spx(14), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(dpx(220)))
                    else Text("Choose channel", color = DS.Gold, fontSize = spx(14))
                }
            }
        }
    }
    assign?.let { r ->
        Sport24Picker(r, hub.live, { assign = null }) { id -> Sport24Guide.set(ctx, r.channelName, id); assign = null }
    }
}

/** Choose the channel for a broadcaster by hand (sports channels first; null = back to automatic). */
@Composable
private fun Sport24Picker(r: Sport24Logic.Row, live: List<Item>, onClose: () -> Unit, onPick: (String?) -> Unit) {
    val pool = remember(live) { live.filter { SportLogic.isSportChannel(it) }.ifEmpty { live } }
    Dialog(onDismissRequest = onClose) {
        Column(
            Modifier.clip(RoundedCornerShape(DS.RadiusM)).background(Bg).border(1.dp, Tok.Line, RoundedCornerShape(DS.RadiusM)).padding(dpx(16)),
            verticalArrangement = Arrangement.spacedBy(dpx(8))
        ) {
            Text("Choose channel", color = Tok.TextHi, fontSize = spx(20), fontWeight = FontWeight.Bold)
            Text(raw = true, text = r.channelName, color = Muted, fontSize = spx(14), maxLines = 1, overflow = TextOverflow.Ellipsis)
            LazyColumn(Modifier.height(dpx(420)).width(dpx(520)), verticalArrangement = Arrangement.spacedBy(dpx(4))) {
                item(key = "auto") { FRow({ onPick(null) }) { Text("Use automatic match", color = Color.White, fontSize = spx(16)) } }
                items(pool, key = { it.id }) { c ->
                    FRow({ onPick(c.id) }) {
                        TeamLogo(c.logo, c.title.take(2), dpx(30))
                        Text(raw = true, text = c.title, color = Color.White, fontSize = spx(16), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}
