package com.ipvio.stream

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object CountryBrowseOrder {
    private var codes by mutableStateOf<List<String>>(emptyList())
    var version by mutableStateOf(0)
        private set

    fun init(context: Context) {
        codes = Profiles.sp(context, "ipvio_live").getString("country_order_v1", "").orEmpty().lines().filter(String::isNotBlank)
        version++
    }

    fun sorted(countries: List<BrowseCountry>): List<BrowseCountry> {
        val byCode = countries.associateBy { it.code }
        return CountryBrowseOrderLogic.apply(countries.map { it.code }, codes).mapNotNull(byCode::get)
    }

    fun move(context: Context, available: List<String>, code: String, delta: Int) {
        codes = CountryBrowseOrderLogic.move(available, codes, code, delta)
        Profiles.sp(context, "ipvio_live").edit().putString("country_order_v1", codes.joinToString("\n")).apply()
        version++
    }
}
