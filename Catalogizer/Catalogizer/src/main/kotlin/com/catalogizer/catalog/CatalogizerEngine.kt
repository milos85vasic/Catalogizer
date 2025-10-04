package com.catalogizer.catalog

import com.catalogizer.catalog.config.CatalogConfig
import com.catalogizer.catalog.config.ConfigManager
import com.catalogizer.catalog.config.SmbRootConfig
import com.catalogizer.catalog.db.DatabaseManager
import com.catalogizer.catalog.hash.DuplicateDetector
import com.catalogizer.catalog.hash.HashingEngine
import com.catalogizer.catalog.metadata.MetadataExtractor
import com.catalogizer.catalog.metadata.MetadataRepository
import com.catalogizer.catalog.scanner.ScanProgress
import com.catalogizer.catalog.scanner.ScanType
import com.catalogizer.catalog.scanner.SmbFileScanner
import com.catalogizer.catalog.search.SearchEngine
import com.catalogizer.catalog.search.SearchRequest
import com.catalogizer.catalog.search.SearchResult
import com.catalogizer.catalog.virtual.VirtualFileSystemManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class CatalogizerEngine(
    configPath: Path,
    databasePassword: String? = null
) : Closeable {

    private val logger = LoggerFactory.getLogger(CatalogizerEngine::class.java)
    private val configManager = ConfigManager(configPath)
    private var config: CatalogConfig
    private val databaseManager: DatabaseManager
    private val hashingEngine: HashingEngine
    private val metadataExtractor: MetadataExtractor
    private val duplicateDetector: DuplicateDetector
    private val metadataRepository: MetadataRepository
    private val smbFileScanner: SmbFileScanner
    private val searchEngine: SearchEngine
    private val virtualFileSystemManager: VirtualFileSystemManager
    private val scheduledExecutor: ScheduledExecutorService
    private val activeScans = ConcurrentHashMap<String, Job>()

    init {
        logger.info("Initializing Catalogizer Engine...")

        // Load configuration
        config = configManager.loadConfig()
        if (databasePassword != null) {
            config = config.copy(
                database = config.database.copy(password = databasePassword)
            )
        }

        // Validate configuration
        val validation = configManager.validateConfig(config)
        if (!validation.isValid) {
            throw IllegalArgumentException("Invalid configuration: ${validation.errors.joinToString(", ")}")
        }

        validation.warnings.forEach { warning ->
            logger.warn("Configuration warning: $warning")
        }

        // Initialize core components
        databaseManager = DatabaseManager(
            databasePath = config.database.getPath(),
            password = config.database.password,
            maxPoolSize = config.database.maxPoolSize
        )

        hashingEngine = HashingEngine(config.scanning.hashingThreads)
        metadataExtractor = MetadataExtractor(config.performance.ioThreads)
        duplicateDetector = DuplicateDetector(databaseManager)
        metadataRepository = MetadataRepository(databaseManager)

        smbFileScanner = SmbFileScanner(
            databaseManager = databaseManager,
            hashingEngine = hashingEngine,
            metadataExtractor = metadataExtractor,
            duplicateDetector = duplicateDetector,
            metadataRepository = metadataRepository
        )

        searchEngine = SearchEngine(databaseManager)
        virtualFileSystemManager = VirtualFileSystemManager(databaseManager, config.virtualFileSystem)

        scheduledExecutor = Executors.newScheduledThreadPool(4)

        // Start scheduled tasks
        startScheduledTasks()

        logger.info("Catalogizer Engine initialized successfully")
    }

    fun startFullScan(smbRootName: String): Flow<ScanProgress>? {
        val smbRoot = config.smbRoots.find { it.name == smbRootName }
        if (smbRoot == null) {
            logger.error("SMB root not found: $smbRootName")
            return null
        }

        return startScan(smbRoot, ScanType.FULL)
    }

    fun startIncrementalScan(smbRootName: String): Flow<ScanProgress>? {
        val smbRoot = config.smbRoots.find { it.name == smbRootName }
        if (smbRoot == null) {
            logger.error("SMB root not found: $smbRootName")
            return null
        }

        return startScan(smbRoot, ScanType.INCREMENTAL)
    }

    fun startVerificationScan(smbRootName: String): Flow<ScanProgress>? {
        val smbRoot = config.smbRoots.find { it.name == smbRootName }
        if (smbRoot == null) {
            logger.error("SMB root not found: $smbRootName")
            return null
        }

        return startScan(smbRoot, ScanType.VERIFICATION)
    }

    private fun startScan(smbRoot: SmbRootConfig, scanType: ScanType): Flow<ScanProgress> {
        logger.info("Starting ${scanType.name} scan for SMB root: ${smbRoot.name}")
        return smbFileScanner.startScan(smbRoot, scanType)
    }

    fun stopScan(scanId: String): Boolean {
        val stopped = smbFileScanner.stopScan(scanId)
        activeScans[scanId]?.cancel()
        activeScans.remove(scanId)
        return stopped
    }

    fun getActiveScanners(): Map<String, Any> {
        return smbFileScanner.getActiveScanners()
    }

    fun search(searchRequest: SearchRequest): SearchResult {
        return searchEngine.search(searchRequest)
    }

    fun rebuildVirtualFileSystem() {
        logger.info("Rebuilding virtual file system...")
        virtualFileSystemManager.rebuildVirtualTree()
        logger.info("Virtual file system rebuilt successfully")
    }

    fun addSmbRoot(smbRootConfig: SmbRootConfig) {
        config = configManager.addSmbRoot(config, smbRootConfig)
        logger.info("Added SMB root: ${smbRootConfig.name}")
    }

    fun removeSmbRoot(name: String) {
        config = configManager.removeSmbRoot(config, name)
        logger.info("Removed SMB root: $name")
    }

    fun updateSmbRoot(smbRootConfig: SmbRootConfig) {
        config = configManager.updateSmbRoot(config, smbRootConfig)
        logger.info("Updated SMB root: ${smbRootConfig.name}")
    }

    fun getSmbRoots(): List<SmbRootConfig> {
        return config.smbRoots
    }

    fun getConfiguration(): CatalogConfig {
        return config
    }

    fun updateConfiguration(newConfig: CatalogConfig) {
        val validation = configManager.validateConfig(newConfig)
        if (!validation.isValid) {
            throw IllegalArgumentException("Invalid configuration: ${validation.errors.joinToString(", ")}")
        }

        configManager.saveConfig(newConfig)
        config = newConfig
        logger.info("Configuration updated successfully")
    }

    fun getSystemStats(): SystemStats {
        val dbStats = databaseManager.getDatabaseStats()
        val indexStats = searchEngine.getIndexStats()
        val virtualStats = virtualFileSystemManager.getVirtualTreeStats()

        return SystemStats(
            databaseStats = dbStats,
            indexStats = indexStats,
            virtualTreeStats = virtualStats,
            activeScans = smbFileScanner.getActiveScanners().size,
            uptime = System.currentTimeMillis() - startTime
        )
    }

    fun performMaintenance() {
        logger.info("Starting system maintenance...")

        // Database maintenance
        if (config.database.vacuumIntervalHours > 0) {
            databaseManager.vacuum()
        }

        if (config.database.analyzeIntervalHours > 0) {
            databaseManager.analyze()
        }

        // Search index optimization
        searchEngine.optimize()

        // Virtual file system rebuild
        if (config.virtualFileSystem.enableAutoCleanup) {
            virtualFileSystemManager.rebuildVirtualTree()
        }

        logger.info("System maintenance completed")
    }

    private fun startScheduledTasks() {
        // Automatic scanning
        config.smbRoots.filter { it.enabled }.forEach { smbRoot ->
            scheduledExecutor.scheduleAtFixedRate({
                try {
                    logger.debug("Starting scheduled scan for: ${smbRoot.name}")
                    runBlocking {
                        startScan(smbRoot, ScanType.INCREMENTAL)
                    }
                } catch (e: Exception) {
                    logger.error("Scheduled scan failed for: ${smbRoot.name}", e)
                }
            }, smbRoot.scanIntervalMinutes.toLong(), smbRoot.scanIntervalMinutes.toLong(), TimeUnit.MINUTES)
        }

        // Database maintenance
        if (config.database.vacuumIntervalHours > 0) {
            scheduledExecutor.scheduleAtFixedRate({
                try {
                    databaseManager.vacuum()
                } catch (e: Exception) {
                    logger.error("Scheduled vacuum failed", e)
                }
            }, config.database.vacuumIntervalHours.toLong(), config.database.vacuumIntervalHours.toLong(), TimeUnit.HOURS)
        }

        if (config.database.analyzeIntervalHours > 0) {
            scheduledExecutor.scheduleAtFixedRate({
                try {
                    databaseManager.analyze()
                } catch (e: Exception) {
                    logger.error("Scheduled analyze failed", e)
                }
            }, config.database.analyzeIntervalHours.toLong(), config.database.analyzeIntervalHours.toLong(), TimeUnit.HOURS)
        }

        // Health checks
        if (config.monitoring.enableHealthChecks) {
            scheduledExecutor.scheduleAtFixedRate({
                try {
                    val healthStatus = databaseManager.healthCheck()
                    if (healthStatus != com.catalogizer.catalog.db.DatabaseHealthStatus.HEALTHY) {
                        logger.warn("Database health check failed: $healthStatus")
                    }
                } catch (e: Exception) {
                    logger.error("Health check failed", e)
                }
            }, config.monitoring.healthCheckIntervalMinutes.toLong(), config.monitoring.healthCheckIntervalMinutes.toLong(), TimeUnit.MINUTES)
        }

        logger.info("Scheduled tasks started")
    }

    override fun close() {
        logger.info("Shutting down Catalogizer Engine...")

        try {
            // Cancel active scans
            activeScans.values.forEach { it.cancel() }
            activeScans.clear()

            // Shutdown scheduled executor
            scheduledExecutor.shutdown()
            if (!scheduledExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow()
            }

            // Close components
            searchEngine.close()
            hashingEngine.shutdown()
            metadataExtractor.shutdown()
            databaseManager.close()

            logger.info("Catalogizer Engine shut down successfully")

        } catch (e: Exception) {
            logger.error("Error during shutdown", e)
        }
    }

    companion object {
        private val startTime = System.currentTimeMillis()
    }
}

data class SystemStats(
    val databaseStats: com.catalogizer.catalog.db.DatabaseStats,
    val indexStats: com.catalogizer.catalog.search.IndexStats,
    val virtualTreeStats: com.catalogizer.catalog.virtual.VirtualTreeStats,
    val activeScans: Int,
    val uptime: Long
)