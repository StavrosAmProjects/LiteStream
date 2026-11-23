package com.ipvio.stream

import org.json.JSONArray
import org.json.JSONObject

/** Credential-free interchange for the selected country/category subset of one playlist. */
object PlaylistSelectionTransfer {
    private const val FORMAT = "litestream-playlist-selection"
    private const val MAX_GROUPS = 20_000
    const val MAX_BYTES = 1_048_576

    fun encode(selection: Set<String>?): String = JSONObject().apply {
        put("format", FORMAT)
        put("version", 1)
        if (selection == null) put("all", true) else {
            put("all", false)
            put("groups", JSONArray().apply { selection.sorted().forEach { put(it) } })
        }
    }.toString(2)

    /** Returns null for a valid ALL selection; malformed input is rejected with [IllegalArgumentException]. */
    fun decode(text: String): Set<String>? {
        require(text.toByteArray(Charsets.UTF_8).size <= MAX_BYTES) { "File is too large" }
        val root = JSONObject(text)
        require(root.optString("format") == FORMAT && root.optInt("version", -1) == 1) { "Unsupported selection file" }
        if (root.optBoolean("all", false)) {
            require(!root.has("groups") || root.optJSONArray("groups")?.length() == 0) { "Invalid ALL selection" }
            return null
        }
        val groups = root.optJSONArray("groups") ?: throw IllegalArgumentException("Missing groups")
        require(groups.length() <= MAX_GROUPS) { "Too many categories" }
        val out = LinkedHashSet<String>(groups.length())
        for (i in 0 until groups.length()) {
            val value = groups.optString(i, "")
            require(value.length in 3..300 && value.contains('|') && value.none { it == '\n' || it == '\r' }) { "Invalid category" }
            out.add(value)
        }
        return out
    }
}
