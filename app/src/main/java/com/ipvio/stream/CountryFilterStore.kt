package com.ipvio.stream

import android.content.Context
import java.security.MessageDigest

/** Profile-scoped saved country/group visibility, stored alongside Live TV navigation state. */
object CountryFilterStore {
    private const val PREFS = "ipvio_live"
    private const val PREFIX = "country_filter_v1_"

    fun read(c: Context, providerUrl: String): Set<String>? =
        Profiles.sp(c, PREFS).getStringSet(key(providerUrl), null)?.toSet()

    fun write(c: Context, providerUrl: String, selection: Set<String>?) {
        val edit = Profiles.sp(c, PREFS).edit()
        if (selection == null) edit.remove(key(providerUrl)) else edit.putStringSet(key(providerUrl), selection.toSet())
        edit.apply()
    }

    fun key(providerUrl: String): String = PREFIX + MessageDigest.getInstance("SHA-256")
        .digest(providerUrl.trim().toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }
}
