package com.ipvio.stream

import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderEditLogicTest {
    @Test fun allowsOnlyUniqueReplacementForExistingTarget() {
        assertTrue(ProviderEditLogic.canReplace(listOf("https://old.example/live", "https://other.example/live"), 0, "https://new.example/live"))
        assertTrue(ProviderEditLogic.canReplace(listOf("https://old.example/live"), 0, "https://old.example/live"))
        assertFalse(ProviderEditLogic.canReplace(listOf("https://old.example/live", "https://other.example/live"), 0, "https://other.example/live"))
        assertFalse(ProviderEditLogic.canReplace(listOf("https://old.example/live"), 2, "https://new.example/live"))
    }

    @Test fun rejectsDifferentUrlsThatCollideInProviderStorageHash() {
        assertEquals("https://host/Aa".hashCode(), "https://host/BB".hashCode())
        assertFalse(ProviderEditLogic.canReplace(listOf("https://old.example/live", "https://host/Aa"), 0, "https://host/BB"))
        assertFalse(ProviderEditLogic.canReplace(listOf("https://host/Aa"), 0, "https://host/BB"))
    }
}
