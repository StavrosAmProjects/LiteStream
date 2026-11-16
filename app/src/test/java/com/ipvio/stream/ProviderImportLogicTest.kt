package com.ipvio.stream

import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream

class ProviderImportLogicTest {
    @Test fun parsesKeyValueXtreamCredentials() {
        val result = ProviderImportLogic.parse("name=Cyprus TV\nserver=https://tv.example:8080\nusername=viewer\npassword=p%26ss")!!
        assertEquals("Cyprus TV", result.name)
        assertTrue(result.input.isXtream)
        assertEquals("https://tv.example:8080", result.input.host)
        assertEquals("p%26ss", result.input.pass)
    }

    @Test fun extractsCredentialsFromCompleteXtreamUrl() {
        val result = ProviderImportLogic.parse("https://tv.example:8080/get.php?username=viewer&password=p%26ss&type=m3u_plus")!!
        assertTrue(result.input.isXtream)
        assertEquals("viewer", result.input.user)
        assertEquals("p&ss", result.input.pass)
        assertEquals("tv.example:8080", result.serverLabel())
        assertTrue(result.input.url.contains("type=m3u_plus"))
    }

    @Test fun keepsXtreamBasePathAndRemovesAuthorityPassword() {
        val result = ProviderImportLogic.parse("https://old:secret@tv.example:8080/portal/get.php?username=u&password=p&type=m3u_plus")!!
        assertEquals("https://tv.example:8080/portal", result.input.host)
        assertTrue(result.input.url.startsWith("https://tv.example:8080/portal/get.php"))
        assertFalse(result.input.url.contains("old:secret"))
    }

    @Test fun parsesJsonAndM3uUrlWithoutExposingQueryInPreview() {
        val json = ProviderImportLogic.parse("""{"name":"Lists","server":"https://tv.example","username":"u","password":"p"}""")!!
        assertEquals("Lists", json.name)
        val m3u = ProviderImportLogic.parse("https://list.example/playlist.m3u?username=u&password=p")!!
        assertFalse(m3u.input.isXtream)
        assertEquals("list.example", m3u.serverLabel())
    }

    @Test fun rejectsMalformedAndOversizedInput() {
        assertNull(ProviderImportLogic.parse("name=Provider\nserver=not a URL\nusername=u\npassword=p"))
        assertNull(ProviderImportLogic.parse("x".repeat(ProviderImportLogic.MAX_BYTES + 1)))
        assertNull(ProviderImportLogic.read(ByteArrayInputStream(ByteArray(ProviderImportLogic.MAX_BYTES + 1))))
    }
}
