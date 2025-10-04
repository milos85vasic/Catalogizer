package com.catalogizer.catalog.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConfigManagerTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun testConfigCreationAndLoading() {
        val configPath = tempDir.resolve("test_config.json")
        val configManager = ConfigManager(configPath)

        // Load config (should create default)
        val config = configManager.loadConfig()

        // Verify default configuration
        assertEquals("./catalog.db", config.database.path)
        assertEquals("catalogizer_default_key", config.database.password)
        assertTrue(config.scanning.enableIncrementalScanning)
        assertTrue(config.virtualFileSystem.enabled)
        assertEquals(0, config.smbRoots.size)
    }

    @Test
    fun testConfigValidation() {
        val configPath = tempDir.resolve("test_config.json")
        val configManager = ConfigManager(configPath)

        // Test valid configuration
        val validConfig = CatalogConfig.createDefault()
        val validationResult = configManager.validateConfig(validConfig)
        assertTrue(validationResult.isValid)
        assertEquals(0, validationResult.errors.size)

        // Test invalid configuration with duplicate SMB root names
        val invalidConfig = validConfig.copy(
            smbRoots = listOf(
                SmbRootConfig(
                    name = "duplicate",
                    host = "host1",
                    share = "share1",
                    credentials = SmbCredentialsConfig("user1", "pass1")
                ),
                SmbRootConfig(
                    name = "duplicate",
                    host = "host2",
                    share = "share2",
                    credentials = SmbCredentialsConfig("user2", "pass2")
                )
            )
        )

        val invalidValidationResult = configManager.validateConfig(invalidConfig)
        assertFalse(invalidValidationResult.isValid)
        assertTrue(invalidValidationResult.errors.any { it.contains("Duplicate SMB root name") })
    }

    @Test
    fun testSmbRootManagement() {
        val configPath = tempDir.resolve("test_config.json")
        val configManager = ConfigManager(configPath)

        var config = configManager.loadConfig()

        // Add SMB root
        val smbRoot = SmbRootConfig(
            name = "test_root",
            host = "192.168.1.100",
            share = "test_share",
            credentials = SmbCredentialsConfig("test_user", "test_pass")
        )

        config = configManager.addSmbRoot(config, smbRoot)
        assertEquals(1, config.smbRoots.size)
        assertEquals("test_root", config.smbRoots[0].name)

        // Update SMB root
        val updatedRoot = smbRoot.copy(enabled = false)
        config = configManager.updateSmbRoot(config, updatedRoot)
        assertFalse(config.smbRoots[0].enabled)

        // Remove SMB root
        config = configManager.removeSmbRoot(config, "test_root")
        assertEquals(0, config.smbRoots.size)
    }
}