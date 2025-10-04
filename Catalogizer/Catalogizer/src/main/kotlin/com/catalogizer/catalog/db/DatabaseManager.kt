package com.catalogizer.catalog.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.nio.file.Path
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

class DatabaseManager(
    private val databasePath: Path,
    private val password: String,
    private val maxPoolSize: Int = 10
) : Closeable {

    private val logger = LoggerFactory.getLogger(DatabaseManager::class.java)
    private lateinit var dataSource: HikariDataSource

    init {
        initializeDatabase()
    }

    private fun initializeDatabase() {
        try {
            // Configure HikariCP with SQLite
            val config = HikariConfig().apply {
                driverClassName = "org.sqlite.JDBC"
                jdbcUrl = "jdbc:sqlite:${databasePath.toAbsolutePath()}"
                maximumPoolSize = maxPoolSize
                minimumIdle = 2
                connectionTimeout = 30000
                idleTimeout = 600000
                maxLifetime = 1800000
                leakDetectionThreshold = 60000

                // SQLite specific settings
                addDataSourceProperty("journal_mode", "WAL")
                addDataSourceProperty("synchronous", "NORMAL")
                addDataSourceProperty("cache_size", "10000")
                addDataSourceProperty("foreign_keys", "true")
                addDataSourceProperty("busy_timeout", "30000")
                addDataSourceProperty("temp_store", "memory")
            }

            dataSource = HikariDataSource(config)
            logger.info("Database connection pool initialized: $databasePath")

            // Initialize schema
            initializeSchema()

        } catch (e: Exception) {
            logger.error("Failed to initialize database: ${e.message}", e)
            throw e
        }
    }

    private fun initializeSchema() {
        withConnection { connection ->
            logger.info("Initializing database schema...")

            // Create tables
            val tables = listOf(
                DatabaseSchema.CREATE_SMB_ROOTS_TABLE,
                DatabaseSchema.CREATE_FILES_TABLE,
                DatabaseSchema.CREATE_FILE_METADATA_TABLE,
                DatabaseSchema.CREATE_DUPLICATES_TABLE,
                DatabaseSchema.CREATE_DUPLICATE_FILES_TABLE,
                DatabaseSchema.CREATE_VIRTUAL_TREE_TABLE,
                DatabaseSchema.CREATE_SCAN_HISTORY_TABLE
            )

            tables.forEach { sql ->
                connection.createStatement().use { statement ->
                    statement.execute(sql)
                }
            }

            // Create indexes
            DatabaseSchema.INDEXES.forEach { sql ->
                connection.createStatement().use { statement ->
                    statement.execute(sql)
                }
            }

            // Create views
            val views = listOf(
                DatabaseSchema.CREATE_DUPLICATE_FILES_VIEW,
                DatabaseSchema.CREATE_VIRTUAL_FILES_VIEW,
                DatabaseSchema.CREATE_FILE_STATS_VIEW
            )

            views.forEach { sql ->
                connection.createStatement().use { statement ->
                    statement.execute(sql)
                }
            }

            // Create triggers
            val triggers = listOf(
                DatabaseSchema.CREATE_UPDATE_TIMESTAMPS_TRIGGER,
                DatabaseSchema.CREATE_DUPLICATE_COUNT_TRIGGER
            )

            triggers.forEach { sql ->
                connection.createStatement().use { statement ->
                    statement.execute(sql)
                }
            }

            logger.info("Database schema initialized successfully")
        }
    }

    fun <T> withConnection(action: (Connection) -> T): T {
        return dataSource.connection.use { connection ->
            action(connection)
        }
    }

    fun <T> withTransaction(action: (Connection) -> T): T {
        return withConnection { connection ->
            connection.autoCommit = false
            try {
                val result = action(connection)
                connection.commit()
                result
            } catch (e: Exception) {
                connection.rollback()
                throw e
            } finally {
                connection.autoCommit = true
            }
        }
    }

    fun getDataSource(): DataSource = dataSource

    @Throws(SQLException::class)
    fun vacuum() {
        withConnection { connection ->
            logger.info("Starting database vacuum...")
            connection.createStatement().use { statement ->
                statement.execute("VACUUM")
            }
            logger.info("Database vacuum completed")
        }
    }

    @Throws(SQLException::class)
    fun analyze() {
        withConnection { connection ->
            logger.info("Starting database analysis...")
            connection.createStatement().use { statement ->
                statement.execute("ANALYZE")
            }
            logger.info("Database analysis completed")
        }
    }

    @Throws(SQLException::class)
    fun getDatabaseStats(): DatabaseStats {
        return withConnection { connection ->
            val stats = DatabaseStats()

            // Get table sizes
            connection.createStatement().use { statement ->
                val result = statement.executeQuery("""
                    SELECT
                        name,
                        (SELECT COUNT(*) FROM pragma_table_info(name)) as column_count
                    FROM sqlite_master
                    WHERE type = 'table'
                    AND name NOT LIKE 'sqlite_%'
                """)

                while (result.next()) {
                    val tableName = result.getString("name")
                    val columnCount = result.getInt("column_count")

                    // Get row count for each table
                    val countResult = statement.executeQuery("SELECT COUNT(*) as count FROM $tableName")
                    val rowCount = if (countResult.next()) countResult.getLong("count") else 0L

                    stats.tableStats[tableName] = TableStats(rowCount, columnCount)
                }
            }

            // Get database size
            connection.createStatement().use { statement ->
                val result = statement.executeQuery("PRAGMA page_count")
                if (result.next()) {
                    val pageCount = result.getLong(1)
                    val pageSizeResult = statement.executeQuery("PRAGMA page_size")
                    val pageSize = if (pageSizeResult.next()) pageSizeResult.getLong(1) else 4096L
                    stats.databaseSizeBytes = pageCount * pageSize
                }
            }

            stats
        }
    }

    fun healthCheck(): DatabaseHealthStatus {
        return try {
            withConnection { connection ->
                // Test basic connectivity
                connection.createStatement().use { statement ->
                    statement.executeQuery("SELECT 1").use { result ->
                        if (!result.next()) {
                            return@withConnection DatabaseHealthStatus.UNHEALTHY
                        }
                    }
                }

                // Check integrity
                connection.createStatement().use { statement ->
                    statement.executeQuery("PRAGMA integrity_check").use { result ->
                        if (result.next()) {
                            val integrity = result.getString(1)
                            if (integrity != "ok") {
                                logger.warn("Database integrity check failed: $integrity")
                                return@withConnection DatabaseHealthStatus.DEGRADED
                            }
                        }
                    }
                }
            }

            DatabaseHealthStatus.HEALTHY
        } catch (e: Exception) {
            logger.error("Database health check failed: ${e.message}", e)
            DatabaseHealthStatus.UNHEALTHY
        }
    }

    override fun close() {
        try {
            if (::dataSource.isInitialized && !dataSource.isClosed) {
                dataSource.close()
                logger.info("Database connection pool closed")
            }
        } catch (e: Exception) {
            logger.error("Error closing database: ${e.message}", e)
        }
    }
}

data class DatabaseStats(
    var databaseSizeBytes: Long = 0,
    val tableStats: MutableMap<String, TableStats> = mutableMapOf()
)

data class TableStats(
    val rowCount: Long,
    val columnCount: Int
)

enum class DatabaseHealthStatus {
    HEALTHY,
    DEGRADED,
    UNHEALTHY
}