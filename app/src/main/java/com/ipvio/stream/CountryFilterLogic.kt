package com.ipvio.stream

/** Pure provider visibility rules. A null selection means ALL; an empty set means NONE. */
object CountryFilterLogic {
    fun groupKey(country: String, group: String): String = "$country|$group"

    fun includesLive(selection: Set<String>?, group: String, title: String): Boolean {
        if (selection == null) return true
        val country = CountryBrowseLogic.classify(group) ?: CountryBrowseLogic.classify(title) ?: "?"
        return groupKey(country, group.ifBlank { "Other" }) in selection
    }

    /** Select whole provider categories before requesting their channel rows. */
    fun includesCategory(selection: Set<String>?, category: String): Boolean =
        selection == null || selection.any { it.substringAfter('|', "").equals(category.ifBlank { "Other" }, ignoreCase = true) }
            || includesLive(selection, category, category)

    fun all(countries: List<BrowseCountry>): Set<String> = countries.flatMap { country ->
        country.groups.map { groupKey(country.code, it.name) }
    }.toSet()

    fun isGroupSelected(selection: Set<String>?, country: String, group: String): Boolean =
        selection == null || groupKey(country, group) in selection

    fun isCountrySelected(selection: Set<String>?, country: BrowseCountry): Boolean =
        country.groups.isNotEmpty() && country.groups.all { isGroupSelected(selection, country.code, it.name) }

    fun isCountryPartial(selection: Set<String>?, country: BrowseCountry): Boolean =
        country.groups.any { isGroupSelected(selection, country.code, it.name) } && !isCountrySelected(selection, country)

    fun toggleCountry(selection: Set<String>?, country: BrowseCountry, countries: List<BrowseCountry>): Set<String>? {
        val next = (selection ?: all(countries)).toMutableSet()
        val keys = country.groups.map { groupKey(country.code, it.name) }
        if (keys.all { it in next }) next.removeAll(keys.toSet()) else next.addAll(keys)
        val all = all(countries)
        return next.takeIf { !it.containsAll(all) }
    }

    fun toggleGroup(selection: Set<String>?, country: String, group: String, countries: List<BrowseCountry>): Set<String>? {
        val next = (selection ?: all(countries)).toMutableSet()
        val key = groupKey(country, group)
        if (!next.add(key)) next.remove(key)
        val all = all(countries)
        return next.takeIf { !it.containsAll(all) }
    }

    /** Item source indices match the active provider followed by the additional shown providers. */
    fun filterSources(items: List<Item>, selections: List<Set<String>?>): List<Item> {
        if (selections.all { it == null }) return items
        val groupCountries = HashMap<String, String?>()
        return items.filter { item ->
            val selection = selections.getOrNull(item.src) ?: return@filter true
            val groupCountry = if (groupCountries.containsKey(item.group)) groupCountries[item.group]
                else CountryBrowseLogic.classify(item.group).also { groupCountries[item.group] = it }
            groupKey(groupCountry ?: CountryBrowseLogic.classify(item.title) ?: "?", CountryBrowseLogic.groupName(item)) in selection
        }
    }
}
