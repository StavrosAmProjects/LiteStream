package com.ipvio.stream

import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class StreamProbeLogicTest {
    @Test fun identifiesHlsAndOrdinaryResponsesWithoutOverclaiming() {
        assertEquals(StreamProbeLogic.Kind.HLS, StreamProbeLogic.response(200, "application/vnd.apple.mpegurl", "#EXTM3U\n#EXT-X-VERSION:3").kind)
        assertEquals(StreamProbeLogic.Result(StreamProbeLogic.Kind.RESPONDS, 206), StreamProbeLogic.response(206, "video/mp2t", "\u0000"))
        assertEquals(StreamProbeLogic.Result(StreamProbeLogic.Kind.HTTP, 403), StreamProbeLogic.response(403, null, "secret response"))
    }

    @Test fun classifiesCommonTransportFailuresWithoutUsingTheirMessages() {
        assertEquals(StreamProbeLogic.Kind.DNS, StreamProbeLogic.failure(UnknownHostException("private host")).kind)
        assertEquals(StreamProbeLogic.Kind.TLS, StreamProbeLogic.failure(SSLHandshakeException("certificate detail")).kind)
        assertEquals(StreamProbeLogic.Kind.TIMEOUT, StreamProbeLogic.failure(SocketTimeoutException("private URL")).kind)
    }
}
