package com.catalogizer.samba

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class SambaUtilsTest {

    @Test
    fun testFactoryMethodWithParameters() {
        val sambaUtils = SambaUtils.create(
            host = "localhost",
            share = "test",
            username = "user",
            password = "pass",
            domain = "domain",
            port = 445,
            timeout = 30000
        )

        assertNotNull(sambaUtils)
        assertNotNull(sambaUtils.fileOperations)
        assertNotNull(sambaUtils.directoryOperations)
    }

    @Test
    fun testFactoryMethodWithConfig() {
        val credentials = SmbCredentials("user", "pass", "domain")
        val config = SmbConnectionConfig("localhost", 445, "test", credentials, 30000)
        val sambaUtils = SambaUtils.create(config)

        assertNotNull(sambaUtils)
        assertNotNull(sambaUtils.fileOperations)
        assertNotNull(sambaUtils.directoryOperations)
    }
}