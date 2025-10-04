package com.catalogizer.samba

import java.io.IOException

class SambaUtils(config: SmbConnectionConfig) {

    private val client = SmbClient(config)
    val fileOperations = SmbFileOperations(client)
    val directoryOperations = SmbDirectoryOperations(client)

    @Throws(IOException::class)
    fun testConnection(): Boolean {
        return client.testConnection()
    }

    companion object {
        fun create(
            host: String,
            share: String,
            username: String,
            password: String,
            domain: String = "",
            port: Int = 445,
            timeout: Int = 30000
        ): SambaUtils {
            val credentials = SmbCredentials(username, password, domain)
            val config = SmbConnectionConfig(host, port, share, credentials, timeout)
            return SambaUtils(config)
        }

        fun create(config: SmbConnectionConfig): SambaUtils {
            return SambaUtils(config)
        }
    }
}