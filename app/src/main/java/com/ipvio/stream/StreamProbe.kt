package com.ipvio.stream

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import okio.Buffer
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Checks only the first small response chunk; credentials and response text never leave this function. */
object StreamProbe {
    suspend fun check(url: String): StreamProbeLogic.Result = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).header("Range", "bytes=0-511").get().build()
            Net.withTimeouts(4, 4).newBuilder().callTimeout(8, TimeUnit.SECONDS).build()
                .newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use StreamProbeLogic.response(response.code, response.header("Content-Type"), "")
                    val prefix = Buffer().also { buffer -> response.body?.source()?.read(buffer, 512L) }
                        .readUtf8()
                    StreamProbeLogic.response(response.code, response.header("Content-Type"), prefix)
                }
        } catch (e: IOException) {
            StreamProbeLogic.failure(e)
        } catch (e: IllegalArgumentException) {
            StreamProbeLogic.failure(e)
        }
    }
}
