package com.ipvio.stream

import org.json.JSONObject
import java.net.URI
import java.io.ByteArrayOutputStream
import java.io.InputStream

data class ProviderImport(val name: String, val input: PhoneInput, val discardOnCancel: Boolean = false) {
    /** A credential-safe server label for previews. */
    fun serverLabel(): String = if (input.isXtream) input.host else if (input.url.startsWith("file:")) "Local M3U file" else runCatching {
        val uri = URI(input.url)
        buildString {
            append(uri.host ?: "Playlist")
            if (uri.port >= 0) append(':').append(uri.port)
        }
    }.getOrDefault("Playlist")
}

/** Parses provider files and pasted URLs without Android dependencies. */
object ProviderImportLogic {
    const val MAX_BYTES = 16 * 1024

    fun read(input: InputStream): String? {
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(2048)
        while (true) {
            val n = input.read(buffer)
            if (n < 0) break
            if (out.size() + n > MAX_BYTES) return null
            out.write(buffer, 0, n)
        }
        return out.toString(Charsets.UTF_8.name())
    }

    fun parse(text: String): ProviderImport? {
        if (text.toByteArray(Charsets.UTF_8).size > MAX_BYTES) return null
        val source = text.trim().removePrefix("\uFEFF").trim()
        if (source.isEmpty()) return null
        val fields = when {
            source.startsWith("{") -> runCatching {
                val json = JSONObject(source)
                mapOf(
                    "name" to json.value("name", "provider", "title"),
                    "server" to json.value("server", "host"),
                    "username" to json.value("username", "user"),
                    "password" to json.value("password", "pass"),
                    "url" to json.value("url", "playlist")
                )
            }.getOrNull() ?: return null
            source.startsWith("http://", true) || source.startsWith("https://", true) -> mapOf("url" to source)
            else -> source.lineSequence().mapNotNull { line ->
                val clean = line.trim()
                if (clean.isEmpty() || clean.startsWith('#')) return@mapNotNull null
                val split = clean.indexOf('=')
                if (split <= 0) return@mapNotNull null
                clean.substring(0, split).trim().lowercase() to clean.substring(split + 1).trim()
            }.toMap()
        }
        val name = fields.firstValue("name", "provider", "title").take(80)
        val input = PhoneImportLogic.toInput(
            mapOf(
                "name" to name,
                "url" to fields.firstValue("url", "playlist"),
                "host" to fields.firstValue("server", "host"),
                "user" to fields.firstValue("username", "user"),
                "pass" to fields.firstValue("password", "pass")
            )
        ) ?: return null
        return ProviderImport(name.ifBlank { input.host.ifBlank { "Provider" } }, input)
    }

    private fun JSONObject.value(vararg keys: String): String = keys.firstNotNullOfOrNull { key ->
        optString(key).takeIf { it.isNotBlank() && it != "null" }
    }.orEmpty()

    private fun Map<String, String>.firstValue(vararg keys: String): String =
        keys.firstNotNullOfOrNull { get(it)?.takeIf(String::isNotBlank) }.orEmpty()
}
