package com.catalogizer.catalog.smb

import com.catalogizer.catalog.config.SmbRootConfig
import com.catalogizer.samba.SambaUtils
import com.catalogizer.samba.SmbFileInfo
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages SMB connections and file operations using the Samba module
 * Provides caching and connection pooling for efficient SMB operations
 */
class SmbFileManager {

    private val logger = LoggerFactory.getLogger(SmbFileManager::class.java)
    private val connectionCache = ConcurrentHashMap<String, SambaUtils>()

    fun getSambaUtils(smbRootConfig: SmbRootConfig): SambaUtils {
        val connectionKey = "${smbRootConfig.host}:${smbRootConfig.port}:${smbRootConfig.share}:${smbRootConfig.credentials.username}"

        return connectionCache.computeIfAbsent(connectionKey) {
            logger.debug("Creating new SMB connection for: ${smbRootConfig.name}")
            SambaUtils.create(smbRootConfig.toSmbConnectionConfig())
        }
    }

    @Throws(IOException::class)
    fun testConnection(smbRootConfig: SmbRootConfig): Boolean {
        return try {
            val sambaUtils = getSambaUtils(smbRootConfig)
            val result = sambaUtils.testConnection()
            logger.debug("Connection test for ${smbRootConfig.name}: $result")
            result
        } catch (e: Exception) {
            logger.warn("Connection test failed for ${smbRootConfig.name}: ${e.message}")
            false
        }
    }

    @Throws(IOException::class)
    fun listFiles(smbRootConfig: SmbRootConfig, path: String = ""): List<CatalogSmbFile> {
        val sambaUtils = getSambaUtils(smbRootConfig)

        return try {
            val files = sambaUtils.fileOperations.listFiles(path)
            files.map { smbFileInfo ->
                CatalogSmbFile(
                    smbRootConfig = smbRootConfig,
                    smbFileInfo = smbFileInfo,
                    relativePath = if (path.isEmpty()) smbFileInfo.name else "$path/${smbFileInfo.name}"
                )
            }
        } catch (e: Exception) {
            logger.error("Failed to list files in path '$path' for SMB root '${smbRootConfig.name}': ${e.message}", e)
            throw IOException("Failed to list SMB files", e)
        }
    }

    @Throws(IOException::class)
    fun getFileInfo(smbRootConfig: SmbRootConfig, path: String): CatalogSmbFile? {
        val sambaUtils = getSambaUtils(smbRootConfig)

        return try {
            val fileInfo = sambaUtils.fileOperations.getFileInfo(path)
            fileInfo?.let { smbFileInfo ->
                CatalogSmbFile(
                    smbRootConfig = smbRootConfig,
                    smbFileInfo = smbFileInfo,
                    relativePath = path
                )
            }
        } catch (e: Exception) {
            logger.error("Failed to get file info for '$path' in SMB root '${smbRootConfig.name}': ${e.message}", e)
            throw IOException("Failed to get SMB file info", e)
        }
    }

    @Throws(IOException::class)
    fun readFile(smbRootConfig: SmbRootConfig, path: String): InputStream {
        val sambaUtils = getSambaUtils(smbRootConfig)

        return try {
            val fileData = sambaUtils.fileOperations.readFile(path)
            fileData.inputStream()
        } catch (e: Exception) {
            logger.error("Failed to read file '$path' from SMB root '${smbRootConfig.name}': ${e.message}", e)
            throw IOException("Failed to read SMB file", e)
        }
    }

    @Throws(IOException::class)
    fun getFileSize(smbRootConfig: SmbRootConfig, path: String): Long {
        val fileInfo = getFileInfo(smbRootConfig, path)
        return fileInfo?.smbFileInfo?.size ?: 0L
    }

    @Throws(IOException::class)
    fun fileExists(smbRootConfig: SmbRootConfig, path: String): Boolean {
        val sambaUtils = getSambaUtils(smbRootConfig)

        return try {
            sambaUtils.fileOperations.fileExists(path)
        } catch (e: Exception) {
            logger.warn("Failed to check file existence for '$path' in SMB root '${smbRootConfig.name}': ${e.message}")
            false
        }
    }

    /**
     * Lists files recursively up to the specified depth
     */
    @Throws(IOException::class)
    fun listFilesRecursive(
        smbRootConfig: SmbRootConfig,
        path: String = "",
        maxDepth: Int = -1,
        currentDepth: Int = 0
    ): List<CatalogSmbFile> {
        if (maxDepth >= 0 && currentDepth >= maxDepth) {
            return emptyList()
        }

        val result = mutableListOf<CatalogSmbFile>()
        val files = listFiles(smbRootConfig, path)

        for (file in files) {
            result.add(file)

            if (file.smbFileInfo.isDirectory) {
                try {
                    val subFiles = listFilesRecursive(
                        smbRootConfig,
                        file.relativePath,
                        maxDepth,
                        currentDepth + 1
                    )
                    result.addAll(subFiles)
                } catch (e: Exception) {
                    logger.warn("Failed to recursively list directory '${file.relativePath}': ${e.message}")
                }
            }
        }

        return result
    }

    /**
     * Creates a virtual SMB URL for external access
     */
    fun createSmbUrl(smbRootConfig: SmbRootConfig, path: String): String {
        return "smb://${smbRootConfig.host}:${smbRootConfig.port}/${smbRootConfig.share}/$path"
    }

    /**
     * Gets SMB root statistics
     */
    fun getSmbRootStats(smbRootConfig: SmbRootConfig): SmbRootStats {
        return try {
            val sambaUtils = getSambaUtils(smbRootConfig)
            val isConnected = sambaUtils.testConnection()

            if (isConnected) {
                val files = listFiles(smbRootConfig)
                val fileCount = files.count { !it.smbFileInfo.isDirectory }
                val directoryCount = files.count { it.smbFileInfo.isDirectory }
                val totalSize = files.filter { !it.smbFileInfo.isDirectory }
                    .sumOf { it.smbFileInfo.size }

                SmbRootStats(
                    isConnected = true,
                    fileCount = fileCount,
                    directoryCount = directoryCount,
                    totalSize = totalSize,
                    lastAccessTime = System.currentTimeMillis()
                )
            } else {
                SmbRootStats(
                    isConnected = false,
                    fileCount = 0,
                    directoryCount = 0,
                    totalSize = 0L,
                    lastAccessTime = 0L
                )
            }
        } catch (e: Exception) {
            logger.error("Failed to get stats for SMB root '${smbRootConfig.name}': ${e.message}", e)
            SmbRootStats(
                isConnected = false,
                fileCount = 0,
                directoryCount = 0,
                totalSize = 0L,
                lastAccessTime = 0L
            )
        }
    }

    /**
     * Clears connection cache - useful for reconnecting with updated credentials
     */
    fun clearConnectionCache() {
        connectionCache.clear()
        logger.info("SMB connection cache cleared")
    }

    /**
     * Clears specific connection from cache
     */
    fun clearConnection(smbRootConfig: SmbRootConfig) {
        val connectionKey = "${smbRootConfig.host}:${smbRootConfig.port}:${smbRootConfig.share}:${smbRootConfig.credentials.username}"
        connectionCache.remove(connectionKey)
        logger.debug("Cleared SMB connection for: ${smbRootConfig.name}")
    }

    fun getActiveConnections(): Int {
        return connectionCache.size
    }
}

/**
 * Enhanced SMB file representation with cataloging context
 */
data class CatalogSmbFile(
    val smbRootConfig: SmbRootConfig,
    val smbFileInfo: SmbFileInfo,
    val relativePath: String
) {
    val absolutePath: String get() = "/${smbRootConfig.name}/$relativePath"
    val smbUrl: String get() = "smb://${smbRootConfig.host}:${smbRootConfig.port}/${smbRootConfig.share}/$relativePath"
    val extension: String get() = smbFileInfo.name.substringAfterLast('.', "").lowercase()

    fun matches(includePatterns: List<String>, excludePatterns: List<String>): Boolean {
        val filename = smbFileInfo.name.lowercase()

        // Check exclude patterns first
        if (excludePatterns.any { pattern ->
            filename.matches(Regex(pattern.replace("*", ".*")))
        }) {
            return false
        }

        // Check include patterns
        return includePatterns.isEmpty() || includePatterns.any { pattern ->
            filename.matches(Regex(pattern.replace("*", ".*")))
        }
    }
}

data class SmbRootStats(
    val isConnected: Boolean,
    val fileCount: Int,
    val directoryCount: Int,
    val totalSize: Long,
    val lastAccessTime: Long
)