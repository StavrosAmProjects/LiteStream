package com.ipvio.stream

/** Uses English only when no language preference has ever been saved. */
object LanguageDefaultLogic {
    fun resolve(saved: String?): String = saved ?: "en"
}
