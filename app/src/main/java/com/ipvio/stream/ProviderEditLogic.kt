package com.ipvio.stream

/** Guards in-place provider replacement against missing targets and URL-hash collisions in existing storage keys. */
object ProviderEditLogic {
    fun canReplace(savedUrls: List<String>, targetIndex: Int, replacementUrl: String): Boolean {
        val replacement = PlaylistLogic.normalizeUrl(replacementUrl) ?: return false
        if (targetIndex !in savedUrls.indices) return false
        val current = PlaylistLogic.normalizeUrl(savedUrls[targetIndex]) ?: return false
        if (current != replacement && current.hashCode() == replacement.hashCode()) return false
        return savedUrls.withIndex().none { (index, value) ->
            if (index == targetIndex) false
            else PlaylistLogic.normalizeUrl(value)?.let { it == replacement || it.hashCode() == replacement.hashCode() } == true
        }
    }
}
