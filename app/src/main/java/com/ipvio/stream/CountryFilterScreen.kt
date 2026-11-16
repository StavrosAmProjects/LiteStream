package com.ipvio.stream

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun CountryFilterScreen(items: List<Item>, initial: Set<String>?, onCancel: () -> Unit, onDone: (Set<String>?) -> Unit) {
    val countries = remember(items) { CountryBrowseLogic.project(items) }
    var draft by remember { mutableStateOf(initial) }
    var countryCode by remember { mutableStateOf(countries.firstOrNull()?.code) }
    var groupName by remember { mutableStateOf(countries.firstOrNull()?.groups?.firstOrNull()?.name) }
    val country = countries.firstOrNull { it.code == countryCode }
    val groups = country?.groups.orEmpty()
    val group = groups.firstOrNull { it.name == groupName }
    val countryFocus = remember { FocusRequester() }
    val groupFocus = remember { FocusRequester() }
    val channelFocus = remember { FocusRequester() }
    var activePane by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { delay(60); runCatching { countryFocus.requestFocus() } }
    BackHandler {
        when (activePane) {
            2 -> { activePane = 1; runCatching { groupFocus.requestFocus() } }
            1 -> { activePane = 0; runCatching { countryFocus.requestFocus() } }
            else -> onCancel()
        }
    }
    Column(Modifier.fillMaxSize().background(Bg).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            FilterPane("Länder", country?.let(::filterCountryLabel), Modifier.weight(0.85f)) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    itemsIndexed(countries, key = { _, value -> value.code }) { index, value ->
                        val modifier = (if (value.code == countryCode || (countryCode == null && index == 0)) Modifier.focusRequester(countryFocus) else Modifier).onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight) runCatching { groupFocus.requestFocus() }.getOrDefault(false) else false
                        }
                        FilterRow(filterCountryLabel(value), value.groups.sumOf { it.channels.size },
                            CountryFilterLogic.isCountrySelected(draft, value), CountryFilterLogic.isCountryPartial(draft, value), modifier, onFocus = { activePane = 0 }) {
                            draft = CountryFilterLogic.toggleCountry(draft, value, countries)
                            countryCode = value.code
                            groupName = value.groups.firstOrNull()?.name
                        }
                    }
                }
            }
            FilterPane("Kategorien", groupName, Modifier.weight(1.15f)) {
                if (country == null) FilterHint("Land auswählen") else LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    itemsIndexed(groups, key = { _, value -> value.name }) { index, value ->
                        val modifier = (if (value.name == groupName || (groupName == null && index == 0)) Modifier.focusRequester(groupFocus) else Modifier).onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) runCatching { countryFocus.requestFocus() }.getOrDefault(false)
                            else if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight) runCatching { channelFocus.requestFocus() }.getOrDefault(false)
                            else false
                        }
                        FilterRow(value.name, value.channels.size,
                            CountryFilterLogic.isGroupSelected(draft, country.code, value.name), false, modifier, onFocus = { activePane = 1 }) {
                            draft = CountryFilterLogic.toggleGroup(draft, country.code, value.name, countries)
                            groupName = value.name
                        }
                    }
                }
            }
            FilterPane("Inhalt", null, Modifier.weight(1.5f)) {
                if (group == null) FilterHint("Kategorie auswählen") else LazyColumn(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    itemsIndexed(group.channels) { index, channel ->
                        FilterPreviewRow(channel.title, (if (index == 0) Modifier.focusRequester(channelFocus) else Modifier).onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) runCatching { groupFocus.requestFocus() }.getOrDefault(false) else false
                        }) { activePane = 2 }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FocusButton(Lang.tr("Alles auswählen"), compact = true) { draft = null }
            FocusButton(Lang.tr("Nichts auswählen"), compact = true) { draft = emptySet() }
            Spacer(Modifier.weight(1f))
            FocusButton(Lang.tr("Abbrechen"), compact = true, onClick = onCancel)
            FocusButton(Lang.tr("Fertig"), primary = true, compact = true) { onDone(draft) }
        }
    }
}

private fun filterCountryLabel(country: BrowseCountry): String = if (country.code == "?") Lang.tr("Sonstige")
else Locale("", country.code).getDisplayCountry(Lang.locale()).ifBlank { country.name }

@Composable
private fun FilterPane(title: String, selected: String?, modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxHeight().clip(RoundedCornerShape(DS.RadiusL)).background(Surf).padding(11.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(Lang.tr(title), color = Accent2, fontSize = 14.sp)
        Text(selected ?: " ", color = Color.White, fontSize = 17.sp, maxLines = 1, raw = true)
        content()
    }
}

@Composable
private fun FilterRow(label: String, count: Int, selected: Boolean, partial: Boolean, modifier: Modifier = Modifier, onFocus: () -> Unit, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val mark = when { selected -> "✓"; partial -> "◩"; else -> "□" }
    Row(modifier.fillMaxWidth().clip(RoundedCornerShape(DS.RadiusS))
        .background(if (focused) DS.FocusFill else if (selected || partial) Accent1.copy(alpha = 0.22f) else Color(0x0AFFFFFF))
        .onFocusChanged { focused = it.isFocused; if (it.isFocused) onFocus() }.clickable(onClick = onClick).padding(horizontal = 10.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text("$mark  $label", color = if (focused) DS.FocusInk else Color.White, fontSize = 15.sp, maxLines = 1, raw = true)
        Text(count.toString(), color = if (focused) DS.FocusInk else Muted, fontSize = 12.sp, raw = true)
    }
}

@Composable
private fun FilterPreviewRow(label: String, modifier: Modifier, onFocus: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Row(modifier.fillMaxWidth().clip(RoundedCornerShape(DS.RadiusS))
        .background(if (focused) DS.FocusFill else Color(0x0AFFFFFF))
        .onFocusChanged { focused = it.isFocused; if (it.isFocused) onFocus() }.focusable()
        .padding(horizontal = 10.dp, vertical = 11.dp)) {
        Text(label, color = if (focused) DS.FocusInk else Color.White, fontSize = 15.sp, maxLines = 1, raw = true)
    }
}

@Composable
private fun FilterHint(text: String) { Text(Lang.tr(text), color = Muted, fontSize = 15.sp, modifier = Modifier.padding(12.dp)) }
