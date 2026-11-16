package com.ipvio.stream

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun CountryBrowseScreen(items: List<Item>, onExit: () -> Unit, onCustomize: (() -> Unit)? = null, onOpen: (Item, List<Item>, String) -> Unit) {
    val countries = remember(items) { CountryBrowseLogic.project(items) }
    var countryCode by remember { mutableStateOf<String?>(null) }
    var groupName by remember { mutableStateOf<String?>(null) }
    val country = countries.firstOrNull { it.code == countryCode }
    val groups = country?.groups.orEmpty()
    val group = groups.firstOrNull { it.name == groupName }
    val channels = group?.channels.orEmpty()
    val countryFocus = remember { FocusRequester() }
    val groupFocus = remember { FocusRequester() }
    val channelFocus = remember { FocusRequester() }
    LaunchedEffect(countryCode) {
        delay(60)
        runCatching { (if (countryCode == null) countryFocus else groupFocus).requestFocus() }
    }
    LaunchedEffect(groupName, countryCode) {
        if (countryCode != null) {
            delay(60)
            runCatching { (if (groupName == null) groupFocus else channelFocus).requestFocus() }
        }
    }
    BackHandler {
        when {
            groupName != null -> groupName = null
            countryCode != null -> countryCode = null
            else -> onExit()
        }
    }
    Row(Modifier.fillMaxSize().background(Bg).padding(20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        CountryPane("Länder", country?.let(::countryLabel), Modifier.weight(0.85f)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                onCustomize?.let { FocusButton(Lang.tr("Auswahl anpassen"), if (countries.isEmpty()) Modifier.focusRequester(countryFocus) else Modifier, compact = true, onClick = it) }
                if (countries.isEmpty()) BrowseHint("Keine Inhalte") else LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    itemsIndexed(countries, key = { _, item -> item.code }) { index, item ->
                        val modifier = (if (item.code == countryCode || (countryCode == null && index == 0)) Modifier.focusRequester(countryFocus) else Modifier).onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight) {
                                runCatching { groupFocus.requestFocus() }.getOrDefault(false)
                            } else false
                        }
                        BrowseRow(countryLabel(item), item.groups.sumOf { it.channels.size }, item.code == countryCode, modifier) {
                            countryCode = item.code
                            groupName = null
                        }
                    }
                }
            }
        }
        CountryPane("Kategorien", groupName, Modifier.weight(1.15f)) {
            if (country == null) BrowseHint("Land auswählen") else LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(groups, key = { _, item -> item.name }) { index, item ->
                    val modifier = (if (item.name == groupName || (groupName == null && index == 0)) Modifier.focusRequester(groupFocus) else Modifier).onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) {
                            runCatching { countryFocus.requestFocus() }.getOrDefault(false)
                        } else if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight) {
                            runCatching { channelFocus.requestFocus() }.getOrDefault(false)
                        } else false
                    }
                    BrowseRow(item.name, item.channels.size, item.name == groupName, modifier) { groupName = item.name }
                }
            }
        }
        CountryPane("Sender", null, Modifier.weight(1.5f)) {
            if (group == null) BrowseHint("Kategorie auswählen") else LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(channels) { index, item ->
                    val modifier = (if (index == 0) Modifier.focusRequester(channelFocus) else Modifier).onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) {
                            runCatching { groupFocus.requestFocus() }.getOrDefault(false)
                        } else false
                    }
                    BrowseRow(item.title, null, false, modifier) { onOpen(item, channels, group.name) }
                }
            }
        }
    }
}

private fun countryLabel(country: BrowseCountry): String = if (country.code == "?") Lang.tr("Sonstige")
else Locale("", country.code).getDisplayCountry(Lang.locale()).ifBlank { country.name }

@Composable
private fun CountryPane(title: String, selected: String?, modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxHeight().clip(RoundedCornerShape(DS.RadiusL)).background(Surf).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(Lang.tr(title), color = Accent2, fontSize = 14.sp)
        Text(selected ?: " ", color = Color.White, fontSize = 18.sp, maxLines = 1, raw = true)
        content()
    }
}

@Composable
private fun BrowseRow(label: String, count: Int?, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(DS.RadiusS))
            .background(if (focused) DS.FocusFill else if (selected) Accent1.copy(alpha = 0.24f) else Color(0x0AFFFFFF))
            .onFocusChanged { focused = it.isFocused }.clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = if (focused) DS.FocusInk else Color.White, fontSize = 16.sp, maxLines = 1, raw = true)
        count?.let { Text(it.toString(), color = if (focused) DS.FocusInk else Muted, fontSize = 13.sp, raw = true) }
    }
}

@Composable
private fun BrowseHint(text: String) {
    Text(Lang.tr(text), color = Muted, fontSize = 15.sp, modifier = Modifier.padding(12.dp))
}
