package com.catalogizer.catalog.config

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class ConfigManager(private val configPath: Path) {

    private val logger = LoggerFactory.getLogger(ConfigManager::class.java)
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    constructor(configPath: String) : this(Paths.get(configPath))

    @Throws(IOException::class)
    fun loadConfig(): CatalogConfig {
        return if (Files.exists(configPath)) {
            try {
                val jsonContent = Files.readString(configPath)
                json.decodeFromString<CatalogConfig>(jsonContent).also {
                    logger.info("Configuration loaded from: $configPath")
                }
            } catch (e: Exception) {
                logger.error("Failed to parse configuration file: ${e.message}", e)
                throw IOException("Invalid configuration file format", e)
            }
        } else {
            logger.info("Configuration file not found, creating default: $configPath")
            val defaultConfig = CatalogConfig.createDefault()
            saveConfig(defaultConfig)
            defaultConfig
        }
    }

    @Throws(IOException::class)
    fun saveConfig(config: CatalogConfig) {
        try {
            // Ensure parent directory exists
            configPath.parent?.let { parent ->
                if (!Files.exists(parent)) {
                    Files.createDirectories(parent)
                }
            }

            val jsonContent = json.encodeToString(config)
            Files.writeString(
                configPath,
                jsonContent,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
            )

            logger.info("Configuration saved to: $configPath")
        } catch (e: Exception) {
            logger.error("Failed to save configuration: ${e.message}", e)
            throw IOException("Failed to save configuration file", e)
        }
    }

    @Throws(IOException::class)
    fun addSmbRoot(config: CatalogConfig, smbRoot: SmbRootConfig): CatalogConfig {
        // Validate unique name
        if (config.smbRoots.any { it.name == smbRoot.name }) {
            throw IllegalArgumentException("SMB root with name '${smbRoot.name}' already exists")
        }

        val updatedConfig = config.copy(
            smbRoots = config.smbRoots + smbRoot
        )

        saveConfig(updatedConfig)
        logger.info("Added SMB root: ${smbRoot.name}")
        return updatedConfig
    }

    @Throws(IOException::class)
    fun removeSmbRoot(config: CatalogConfig, name: String): CatalogConfig {
        val updatedConfig = config.copy(
            smbRoots = config.smbRoots.filter { it.name != name }
        )

        saveConfig(updatedConfig)
        logger.info("Removed SMB root: $name")
        return updatedConfig
    }

    @Throws(IOException::class)
    fun updateSmbRoot(config: CatalogConfig, smbRoot: SmbRootConfig): CatalogConfig {
        val index = config.smbRoots.indexOfFirst { it.name == smbRoot.name }
        if (index == -1) {
            throw IllegalArgumentException("SMB root with name '${smbRoot.name}' not found")
        }

        val updatedRoots = config.smbRoots.toMutableList()
        updatedRoots[index] = smbRoot

        val updatedConfig = config.copy(smbRoots = updatedRoots)
        saveConfig(updatedConfig)
        logger.info("Updated SMB root: ${smbRoot.name}")
        return updatedConfig
    }

    fun validateConfig(config: CatalogConfig): ConfigValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        // Validate database config
        if (config.database.password.length < 8) {
            warnings.add("Database password is shorter than 8 characters")
        }

        if (config.database.maxPoolSize <= 0) {
            errors.add("Database max pool size must be positive")
        }

        // Validate SMB roots
        val rootNames = mutableSetOf<String>()
        val virtualPaths = mutableSetOf<String>()

        config.smbRoots.forEach { root ->
            // Check for duplicate names
            if (!rootNames.add(root.name)) {
                errors.add("Duplicate SMB root name: ${root.name}")
            }

            // Check for duplicate virtual paths
            val virtualPath = root.getEffectiveVirtualPath()
            if (!virtualPaths.add(virtualPath)) {
                errors.add("Duplicate virtual path: $virtualPath")
            }

            // Validate credentials
            if (root.credentials.username.isBlank()) {
                errors.add("SMB root '${root.name}' has empty username")
            }

            if (root.credentials.password.isBlank()) {
                warnings.add("SMB root '${root.name}' has empty password")
            }

            // Validate host
            if (root.host.isBlank()) {
                errors.add("SMB root '${root.name}' has empty host")
            }

            // Validate share
            if (root.share.isBlank()) {
                errors.add("SMB root '${root.name}' has empty share")
            }

            // Validate port
            if (root.port <= 0 || root.port > 65535) {
                errors.add("SMB root '${root.name}' has invalid port: ${root.port}")
            }

            // Validate scan interval
            if (root.scanIntervalMinutes <= 0) {
                errors.add("SMB root '${root.name}' has invalid scan interval: ${root.scanIntervalMinutes}")
            }
        }

        // Validate performance config
        if (config.performance.maxWorkerThreads <= 0) {
            errors.add("Max worker threads must be positive")
        }

        if (config.performance.ioThreads <= 0) {
            errors.add("IO threads must be positive")
        }

        // Validate scanning config
        if (config.scanning.maxConcurrentScans <= 0) {
            errors.add("Max concurrent scans must be positive")
        }

        if (config.scanning.scanBatchSize <= 0) {
            errors.add("Scan batch size must be positive")
        }

        return ConfigValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }

    fun getConfigPath(): Path = configPath
}

data class ConfigValidationResult(
    val isValid: Boolean,
    val errors: List<String>,
    val warnings: List<String>
)