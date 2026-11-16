package com.ipvio.stream

import java.util.Locale

data class BrowseGroup(val name: String, val channels: List<Item>)
data class BrowseCountry(val code: String, val name: String, val groups: List<BrowseGroup>)

/** Country-first view of live entries; this never changes the provider catalogue. */
object CountryBrowseLogic {
    private val separators = Regex("[|/:;,_\\-]+")
    private val words = Regex("[^\\p{L}\\p{N}]+")
    private val flags = Regex("(?:\\uD83C[\\uDDE6-\\uDDFF]){2}")
    private val shortCodes = setOf("CY", "GR", "UK", "GB")
    private val aliases: Map<String, String> by lazy {
        val result = HashMap<String, String>()
        Locale.getISOCountries().forEach { code ->
            result[code] = code
            val name = Locale("", code).getDisplayCountry(Locale.ENGLISH)
            if (name.isNotBlank()) result[normalize(name)] = code
        }
        fun add(code: String, vararg names: String) { names.forEach { result[normalize(it)] = code } }
        add("CY", "Cyprus", "Κύπρος")
        add("GR", "Greece", "Greek", "Grecia", "Ellada", "Hellas", "Ελλάδα", "Ελληνικά")
        add("GB", "UK", "United Kingdom", "Great Britain", "Britain", "England", "Scotland", "Wales", "Northern Ireland")
        add("US", "USA", "United States", "United States of America")
        add("AE", "UAE", "United Arab Emirates")
        add("KR", "South Korea", "Korea Republic")
        add("CZ", "Czech Republic")
        add("RU", "Russia", "Russian Federation")
        result
    }
    private val aliasMaxWords by lazy { aliases.keys.maxOf { it.split(' ').size } }

    /** ISO country code, or null when the provider label has no confident country marker. */
    fun classify(label: String): String? {
        val marked = flags.replace(label) { match -> flagCode(match.value)?.let { " $it " } ?: " " }
        for (part in separators.split(marked)) {
            val normalized = normalize(part)
            if (normalized.isEmpty()) continue
            aliases[normalized]?.let { return it }
            val tokens = normalized.split(' ')
            for (start in tokens.indices) {
                for (size in 1..minOf(aliasMaxWords, tokens.size - start)) {
                    val candidate = tokens.subList(start, start + size).joinToString(" ")
                    if (candidate.length > 2 || candidate in shortCodes) aliases[candidate]?.let { return it }
                }
            }
        }
        return null
    }

    /** Groups retain provider order; countries sort by display name and unclassified stays last. */
    fun project(items: List<Item>): List<BrowseCountry> {
        val countries = LinkedHashMap<String, LinkedHashMap<String, MutableList<Item>>>()
        val groupCountries = HashMap<String, String?>()
        for (item in items) {
            val groupCountry = if (groupCountries.containsKey(item.group)) groupCountries[item.group]
                else classify(item.group).also { groupCountries[item.group] = it }
            val country = groupCountry ?: classify(item.title) ?: "?"
            countries.getOrPut(country) { LinkedHashMap() }
                .getOrPut(item.group.ifBlank { "Other" }) { ArrayList() }
                .add(item)
        }
        return countries.map { (code, groups) ->
            BrowseCountry(code, if (code == "?") "Unknown" else countryName(code), groups.map { (name, channels) -> BrowseGroup(name, channels) })
        }.sortedWith(compareBy<BrowseCountry>({ it.code == "?" }, { it.name.lowercase(Locale.ROOT) }))
    }

    fun countryCode(item: Item): String = classify(item.group) ?: classify(item.title) ?: "?"

    fun groupName(item: Item): String = item.group.ifBlank { "Other" }

    private fun normalize(text: String): String = words.replace(text.uppercase(Locale.ROOT), " ").trim().replace(Regex("\\s+"), " ")

    private fun countryName(code: String): String = when (code) {
        "GB" -> Locale("", code).getDisplayCountry(Locale.ENGLISH).ifBlank { "United Kingdom" }
        "US" -> Locale("", code).getDisplayCountry(Locale.ENGLISH).ifBlank { "United States" }
        else -> Locale("", code).getDisplayCountry(Locale.ENGLISH).ifBlank { code }
    }

    private fun flagCode(flag: String): String? {
        if (flag.length != 4) return null
        val a = Character.codePointAt(flag, 0)
        val b = Character.codePointAt(flag, Character.charCount(a))
        if (a !in 0x1F1E6..0x1F1FF || b !in 0x1F1E6..0x1F1FF) return null
        return "${('A'.code + a - 0x1F1E6).toChar()}${('A'.code + b - 0x1F1E6).toChar()}"
    }
}
