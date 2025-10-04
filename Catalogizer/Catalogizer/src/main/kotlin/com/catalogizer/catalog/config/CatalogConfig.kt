package com.catalogizer.catalog.config

import kotlinx.serialization.Serializable
import java.nio.file.Path
import java.nio.file.Paths

@Serializable
data class CatalogConfig(
    val database: DatabaseConfig,
    val scanning: ScanningConfig,
    val virtualFileSystem: VirtualFileSystemConfig,
    val monitoring: MonitoringConfig,
    val performance: PerformanceConfig,
    val smbRoots: List<SmbRootConfig> = emptyList()
) {
    companion object {
        fun createDefault(
            databasePath: String = "./catalog.db",
            databasePassword: String = "catalogizer_default_key"
        ): CatalogConfig {
            return CatalogConfig(
                database = DatabaseConfig(
                    path = databasePath,
                    password = databasePassword
                ),
                scanning = ScanningConfig(),
                virtualFileSystem = VirtualFileSystemConfig(),
                monitoring = MonitoringConfig(),
                performance = PerformanceConfig()
            )
        }
    }
}

@Serializable
data class DatabaseConfig(
    val path: String,
    val password: String,
    val maxPoolSize: Int = 10,
    val vacuumIntervalHours: Int = 24,
    val analyzeIntervalHours: Int = 6,
    val backupIntervalHours: Int = 168, // 1 week
    val backupRetentionDays: Int = 30
) {
    fun getPath(): Path = Paths.get(path)
}

@Serializable
data class ScanningConfig(
    val defaultScanIntervalMinutes: Int = 60,
    val maxConcurrentScans: Int = 3,
    val enableIncrementalScanning: Boolean = true,
    val enableDeepScan: Boolean = true,
    val scanBatchSize: Int = 1000,
    val hashingThreads: Int = Runtime.getRuntime().availableProcessors(),
    val metadataExtractionTimeout: Long = 30000, // 30 seconds
    val enableContentHashing: Boolean = true,
    val enableQuickHash: Boolean = true, // Hash first/last 1MB + size for quick duplicate detection
    val quickHashSizeBytes: Long = 1024 * 1024, // 1MB
    val skipFileExtensions: Set<String> = setOf(".tmp", ".temp", ".log", ".cache"),
    val maxFileSizeForMetadata: Long = 100 * 1024 * 1024 // 100MB
)

@Serializable
data class VirtualFileSystemConfig(
    val enabled: Boolean = true,
    val mountPath: String = "./virtual_catalog",
    val enableSymlinks: Boolean = true,
    val enableHardlinks: Boolean = false,
    val duplicatesPath: String = "./virtual_catalog/duplicates",
    val categoriesPath: String = "./virtual_catalog/by_type",
    val sizesPath: String = "./virtual_catalog/by_size",
    val datesPath: String = "./virtual_catalog/by_date",
    val maxLinksPerDirectory: Int = 10000,
    val enableAutoCleanup: Boolean = true
) {
    fun getMountPath(): Path = Paths.get(mountPath)
    fun getDuplicatesPath(): Path = Paths.get(duplicatesPath)
    fun getCategoriesPath(): Path = Paths.get(categoriesPath)
    fun getSizesPath(): Path = Paths.get(sizesPath)
    fun getDatesPath(): Path = Paths.get(datesPath)
}

@Serializable
data class MonitoringConfig(
    val enableRealTimeMonitoring: Boolean = true,
    val pollIntervalSeconds: Int = 5,
    val changeDetectionDelayMs: Long = 1000,
    val enableEventCoalescing: Boolean = true,
    val maxEventQueueSize: Int = 10000,
    val enableHealthChecks: Boolean = true,
    val healthCheckIntervalMinutes: Int = 5,
    val enableMetrics: Boolean = true,
    val metricsRetentionHours: Int = 72
)

@Serializable
data class PerformanceConfig(
    val enableCaching: Boolean = true,
    val cacheMaxSize: Long = 10000,
    val cacheTtlMinutes: Long = 30,
    val enableParallelProcessing: Boolean = true,
    val maxWorkerThreads: Int = Runtime.getRuntime().availableProcessors() * 2,
    val ioThreads: Int = Runtime.getRuntime().availableProcessors(),
    val connectionTimeoutMs: Long = 30000,
    val readTimeoutMs: Long = 60000,
    val enableBatchProcessing: Boolean = true,
    val batchSize: Int = 1000,
    val enableCompression: Boolean = true
)

@Serializable
data class SmbRootConfig(
    val name: String,
    val host: String,
    val port: Int = 445,
    val share: String,
    val credentials: SmbCredentialsConfig,
    val enabled: Boolean = true,
    val scanIntervalMinutes: Int = 60,
    val priority: Int = 0, // Higher priority = scanned first
    val includePatterns: List<String> = listOf("*"),
    val excludePatterns: List<String> = emptyList(),
    val maxDepth: Int = -1, // -1 = unlimited
    val enableDeepScan: Boolean = true,
    val enableMetadataExtraction: Boolean = true,
    val enableDuplicateDetection: Boolean = true,
    val virtualPath: String? = null // Custom virtual path, null = auto-generate
) {
    fun getEffectiveVirtualPath(): String {
        return virtualPath ?: "/$name"
    }

    fun toSmbConnectionConfig(): com.catalogizer.samba.SmbConnectionConfig {
        return com.catalogizer.samba.SmbConnectionConfig(
            host = host,
            port = port,
            share = share,
            credentials = com.catalogizer.samba.SmbCredentials(
                username = credentials.username,
                password = credentials.password,
                domain = credentials.domain
            ),
            timeout = 30000
        )
    }
}

@Serializable
data class SmbCredentialsConfig(
    val username: String,
    val password: String,
    val domain: String = ""
)