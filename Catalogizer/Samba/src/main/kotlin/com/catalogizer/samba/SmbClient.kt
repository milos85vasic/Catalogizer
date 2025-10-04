package com.catalogizer.samba

import jcifs.CIFSContext
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*

class SmbClient(private val config: SmbConnectionConfig) {

    private val logger = LoggerFactory.getLogger(SmbClient::class.java)
    private val context: CIFSContext

    init {
        val properties = Properties().apply {
            setProperty("jcifs.smb.client.soTimeout", config.timeout.toString())
            setProperty("jcifs.smb.client.connTimeout", config.timeout.toString())
            setProperty("jcifs.smb.client.responseTimeout", config.timeout.toString())
        }

        val propertyConfig = PropertyConfiguration(properties)
        val baseContext = BaseContext(propertyConfig)

        val authenticator = NtlmPasswordAuthenticator(
            config.credentials.domain,
            config.credentials.username,
            config.credentials.password
        )

        context = baseContext.withCredentials(authenticator)
        logger.info("SMB client initialized for host: ${config.host}")
    }

    @Throws(IOException::class)
    fun testConnection(): Boolean {
        return try {
            val smbFile = SmbFile(config.toSmbUrl(), context)
            smbFile.exists()
            logger.info("SMB connection test successful")
            true
        } catch (e: Exception) {
            logger.error("SMB connection test failed: ${e.message}")
            false
        }
    }

    @Throws(IOException::class)
    fun createSmbFile(path: String): SmbFile {
        val fullUrl = if (path.startsWith("/")) {
            config.toSmbUrl() + path.substring(1)
        } else {
            config.toSmbUrl() + path
        }
        return SmbFile(fullUrl, context)
    }
}