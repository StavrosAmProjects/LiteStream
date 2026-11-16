package com.ipvio.stream

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.ssl.SSLException

/** Only reports what a small response check observed; it does not claim the player can decode the stream. */
object StreamProbeLogic {
    enum class Kind { DNS, TLS, TIMEOUT, CONNECTION, INVALID_ADDRESS, HTTP, HLS, RESPONDS }
    data class Result(val kind: Kind, val status: Int = 0)

    fun response(status: Int, contentType: String?, prefix: String): Result = when {
        status !in 200..299 -> Result(Kind.HTTP, status)
        prefix.trimStart('\uFEFF', ' ', '\t', '\r', '\n').startsWith("#EXTM3U", ignoreCase = true) ||
            contentType.orEmpty().contains("mpegurl", ignoreCase = true) -> Result(Kind.HLS, status)
        else -> Result(Kind.RESPONDS, status)
    }

    fun failure(error: Throwable): Result {
        var cause: Throwable? = error
        repeat(8) {
            when (cause) {
                is UnknownHostException -> return Result(Kind.DNS)
                is SSLException -> return Result(Kind.TLS)
                is SocketTimeoutException -> return Result(Kind.TIMEOUT)
                is ConnectException -> return Result(Kind.CONNECTION)
                is IllegalArgumentException -> return Result(Kind.INVALID_ADDRESS)
            }
            cause = cause?.cause
        }
        return Result(Kind.CONNECTION)
    }

    fun label(result: Result): String = when (result.kind) {
        Kind.DNS -> "DNS-Auflösung fehlgeschlagen. Prüfe das TV-Netzwerk oder den Server des Anbieters."
        Kind.TLS -> "TLS-Verbindung fehlgeschlagen. Zertifikat oder TLS-Einstellungen sind möglicherweise inkompatibel."
        Kind.TIMEOUT -> "Zeitüberschreitung: Der Server hat nicht rechtzeitig geantwortet."
        Kind.CONNECTION -> "Netzwerkverbindung konnte nicht hergestellt werden."
        Kind.INVALID_ADDRESS -> "Die Stream-Adresse ist ungültig."
        Kind.HTTP -> "Der Server antwortet mit HTTP {#0}. Prüfe die Antwort oder Zugangsdaten des Anbieters."
        Kind.HLS -> "HLS-Playlist erkannt. Wiedergabe kann trotzdem an Segmenten oder am Format scheitern."
        Kind.RESPONDS -> "Der Server antwortet (HTTP {#0}); das Stream-Format ist nicht bestätigt."
    }.replace("{#0}", result.status.toString())
}
