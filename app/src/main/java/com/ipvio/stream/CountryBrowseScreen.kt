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
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

@Composable
fun CountryBrowseScreen(items: List<Item>, onExit: () -> Unit, onCustomize: (() -> Unit)? = null, initialCountryCode: String? = null, initialGroupName: String? = null, onPin: ((String, String?) -> Unit)? = null, onOpen: (Item, List<Item>, String) -> Unit) {
    val countriesRaw = remember(items) { CountryBrowseLogic.project(items) }
    val countries = remember(countriesRaw, CountryBrowseOrder.version) { CountryBrowseOrder.sorted(countriesRaw) }
    val context = LocalContext.current
    var countryCode by remember(initialCountryCode) { mutableStateOf(initialCountryCode) }
    var groupName by remember(initialCountryCode, initialGroupName) { mutableStateOf(initialGroupName) }
    val country = countries.firstOrNull { it.code == countryCode }
    val groups = country?.groups.orEmpty()
    val group = groups.firstOrNull { it.name == groupName }
    val channels = group?.channels.orEmpty()
    val countryFocus = remember { FocusRequester() }
    val groupFocus = remember { FocusRequester() }
    val channelFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(60)
        runCatching { (if (initialCountryCode == null) countryFocus else if (initialGroupName == null) groupFocus else channelFocus).requestFocus() }
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
                if (countries.isEmpty()) BrowseHint("Keine Inhalte") else LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    itemsIndexed(countries, key = { _, item -> item.code }) { index, item ->
                        val modifier = (if (item.code == countryCode || (countryCode == null && index == 0)) Modifier.focusRequester(countryFocus) else Modifier).onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight) {
                                runCatching { groupFocus.requestFocus() }.getOrDefault(false)
                            } else false
                        }
                        BrowseRow(countryLabel(item), item.groups.sumOf { it.channels.size }, item.code == countryCode, modifier,
                            onFocus = { if (countryCode != item.code) { countryCode = item.code; groupName = null } }) {
                                countryCode = item.code
                                groupName = null
                            }
                    }
                }
                if (country != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FocusButton("Move up", compact = true) { CountryBrowseOrder.move(context, countries.map { it.code }, country.code, -1) }
                        FocusButton("Move down", compact = true) { CountryBrowseOrder.move(context, countries.map { it.code }, country.code, 1) }
                    }
                }
                if (country != null && onPin != null) FocusButton("Pin ${countryLabel(country)} to Home", compact = true) { onPin(country.code, null) }
            }
        }
        CountryPane("Kategorien", groupName, Modifier.weight(1.15f)) {
            if (country == null) BrowseHint("Land auswählen") else LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(groups, key = { _, item -> item.name }) { index, item ->
                    val modifier = (if (item.name == groupName || (groupName == null && index == 0)) Modifier.focusRequester(groupFocus) else Modifier).onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) {
                            runCatching { countryFocus.requestFocus() }.getOrDefault(false)
                        } else if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight) {
                            runCatching { channelFocus.requestFocus() }.getOrDefault(false)
                        } else false
                    }
                    BrowseRow(item.name, item.channels.size, item.name == groupName, modifier,
                        onFocus = { groupName = item.name }) { groupName = item.name }
                }
            }
            if (country != null && group != null && onPin != null) FocusButton("Pin ${countryLabel(country)} · ${group.name} to Home", compact = true) { onPin(country.code, group.name) }
        }
        CountryPane("Sender", null, Modifier.weight(1.5f)) {
            if (group == null) BrowseHint("Kategorie auswählen") else LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
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

private fun countryLabel(country: BrowseCountry): String = country.name

@Composable
private fun CountryPane(title: String, selected: String?, modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxHeight().clip(RoundedCornerShape(DS.RadiusL)).background(Surf).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(Lang.tr(title), color = Accent2, fontSize = 14.sp)
        Text(selected ?: " ", color = Color.White, fontSize = 18.sp, maxLines = 1, raw = true)
        content()
    }
}

@Composable
private fun BrowseRow(label: String, count: Int?, selected: Boolean, modifier: Modifier = Modifier, onFocus: () -> Unit = {}, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(DS.RadiusS))
            .background(if (focused) DS.FocusFill else if (selected) Accent1.copy(alpha = 0.24f) else Color(0x0AFFFFFF))
            .onFocusChanged { focused = it.isFocused; if (it.isFocused) onFocus() }.clickable(onClick = onClick)
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
