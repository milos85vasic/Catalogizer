package com.catalogizer.samba

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SmbCredentialsTest {

    @Test
    fun testSerialization() {
        val credentials = SmbCredentials("user", "pass", "domain")
        val json = Json.encodeToString(credentials)
        val deserialized = Json.decodeFromString<SmbCredentials>(json)

        assertEquals(credentials.username, deserialized.username)
        assertEquals(credentials.password, deserialized.password)
        assertEquals(credentials.domain, deserialized.domain)
    }

    @Test
    fun testDefaultDomain() {
        val credentials = SmbCredentials("user", "pass")
        assertEquals("", credentials.domain)
    }
}