package com.ipvio.stream

import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Locale

data class HomeCountryShortcut(val code: String, val group: String? = null) {
    val label get() = Locale.forLanguageTag("und-$code").getDisplayCountry(Locale.ENGLISH).ifBlank { code } + (group?.let { " · $it" } ?: "")
}

object HomeCountryShortcutLogic {
    fun favoriteGroups(favorites: List<String>, groups: Map<String, List<Item>>): List<HomeCountryShortcut> =
        favorites.distinct().mapNotNull { group ->
            val code = CountryBrowseLogic.classify(group)
                ?: groups[group]?.firstOrNull()?.let(CountryBrowseLogic::countryCode)
            if (code.isNullOrBlank() || code == "?") null else HomeCountryShortcut(code, group)
        }

    fun decode(value: String): List<HomeCountryShortcut> = value.lineSequence().mapNotNull { line ->
        val pair = line.split('|', limit = 2)
        if (pair.size != 2 || !pair[0].matches(Regex("[A-Z]{2}"))) null
        else runCatching { HomeCountryShortcut(pair[0], URLDecoder.decode(pair[1], "UTF-8").ifEmpty { null }) }.getOrNull()
    }.distinct().take(12).toList()

    fun encode(items: List<HomeCountryShortcut>): String = items.distinct().takeLast(12).joinToString("\n") {
        it.code + "|" + URLEncoder.encode(it.group.orEmpty(), "UTF-8")
    }
}
