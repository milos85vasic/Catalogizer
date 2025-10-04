package com.catalogizer.samba

import kotlin.test.Test
import kotlin.test.assertEquals

internal class SmbConnectionConfigTest {

    @Test
    fun testSmbUrlGeneration() {
        val credentials = SmbCredentials("user", "pass", "domain")
        val config = SmbConnectionConfig("192.168.1.100", 445, "shared", credentials)

        assertEquals("smb://192.168.1.100:445/shared/", config.toSmbUrl())
    }

    @Test
    fun testDefaultValues() {
        val credentials = SmbCredentials("user", "pass")
        val config = SmbConnectionConfig("localhost", share = "test", credentials = credentials)

        assertEquals(445, config.port)
        assertEquals(30000, config.timeout)
    }
}