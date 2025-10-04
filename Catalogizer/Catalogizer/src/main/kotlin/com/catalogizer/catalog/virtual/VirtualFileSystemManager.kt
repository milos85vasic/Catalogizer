package com.catalogizer.catalog.virtual

import com.catalogizer.catalog.config.VirtualFileSystemConfig
import com.catalogizer.catalog.db.DatabaseManager
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class VirtualFileSystemManager(
    private val databaseManager: DatabaseManager,
    private val config: VirtualFileSystemConfig
) {

    private val logger = LoggerFactory.getLogger(VirtualFileSystemManager::class.java)
    private val virtualTreeLock = ReentrantReadWriteLock()
    private val activeLinks = ConcurrentHashMap<String, VirtualNode>()

    init {
        if (config.enabled) {
            initializeVirtualFileSystem()
        }
    }

    private fun initializeVirtualFileSystem() {
        try {
            logger.info("Initializing virtual file system at: ${config.getMountPath()}")

            // Create base directories
            Files.createDirectories(config.getMountPath())
            if (config.enableAutoCleanup) {
                cleanupExistingVirtualTree()
            }

            // Build virtual tree structure
            buildVirtualTree()

            logger.info("Virtual file system initialized successfully")

        } catch (e: Exception) {
            logger.error("Failed to initialize virtual file system", e)
            throw e
        }
    }

    fun rebuildVirtualTree() {
        virtualTreeLock.write {
            try {
                logger.info("Rebuilding virtual file system tree...")

                // Clear existing virtual tree
                clearVirtualTree()

                // Rebuild from database
                buildVirtualTree()

                logger.info("Virtual file system tree rebuilt successfully")

            } catch (e: Exception) {
                logger.error("Failed to rebuild virtual tree", e)
                throw e
            }
        }
    }

    private fun buildVirtualTree() {
        // Create main organizational structures
        createByTypeStructure()
        createBySizeStructure()
        createByDateStructure()
        createDuplicatesStructure()
        createSmbRootStructure()
    }

    private fun createByTypeStructure() {
        val typeBasePath = config.getCategoriesPath()
        Files.createDirectories(typeBasePath)

        databaseManager.withConnection { connection ->
            val sql = """
                SELECT file_type_category, COUNT(*) as count
                FROM files
                WHERE is_deleted = 0 AND is_accessible = 1 AND is_directory = 0
                GROUP BY file_type_category
                ORDER BY count DESC
            """

            connection.prepareStatement(sql).use { statement ->
                val result = statement.executeQuery()
                while (result.next()) {
                    val fileType = result.getString("file_type_category") ?: "unknown"
                    val count = result.getInt("count")

                    createTypeDirectory(typeBasePath, fileType, count)
                }
            }
        }
    }

    private fun createTypeDirectory(basePath: Path, fileType: String, count: Int) {
        val typePath = basePath.resolve(fileType)
        Files.createDirectories(typePath)

        // Create virtual tree entry
        val virtualPath = "/${config.categoriesPath.removePrefix("./virtual_catalog/")}/$fileType"
        insertVirtualTreeEntry(virtualPath, null, true)

        // Link files of this type
        linkFilesByType(typePath, fileType)

        logger.debug("Created type directory: $fileType with $count files")
    }

    private fun linkFilesByType(typePath: Path, fileType: String) {
        databaseManager.withConnection { connection ->
            val sql = """
                SELECT f.id, f.path, f.name, sr.name as smb_root_name, sr.host, sr.share
                FROM files f
                JOIN smb_roots sr ON f.smb_root_id = sr.id
                WHERE f.file_type_category = ? AND f.is_deleted = 0 AND f.is_accessible = 1 AND f.is_directory = 0
                ORDER BY f.name
                LIMIT ?
            """

            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, fileType)
                statement.setInt(2, config.maxLinksPerDirectory)
                val result = statement.executeQuery()

                while (result.next()) {
                    val fileId = result.getLong("id")
                    val fileName = result.getString("name")
                    val smbRootName = result.getString("smb_root_name")

                    val linkPath = typePath.resolve("${smbRootName}_$fileName")
                    val virtualPath = "/${config.categoriesPath.removePrefix("./virtual_catalog/")}/$fileType/${smbRootName}_$fileName"

                    createVirtualLink(linkPath, fileId, virtualPath)
                }
            }
        }
    }

    private fun createBySizeStructure() {
        val sizeBasePath = config.getSizesPath()
        Files.createDirectories(sizeBasePath)

        val sizeCategories = mapOf(
            "empty" to Pair(0L, 0L),
            "tiny" to Pair(1L, 1024L),
            "small" to Pair(1024L, 1024L * 1024L),
            "medium" to Pair(1024L * 1024L, 10L * 1024L * 1024L),
            "large" to Pair(10L * 1024L * 1024L, 100L * 1024L * 1024L),
            "huge" to Pair(100L * 1024L * 1024L, 1024L * 1024L * 1024L),
            "massive" to Pair(1024L * 1024L * 1024L, Long.MAX_VALUE)
        )

        sizeCategories.forEach { (category, range) ->
            createSizeDirectory(sizeBasePath, category, range.first, range.second)
        }
    }

    private fun createSizeDirectory(basePath: Path, category: String, minSize: Long, maxSize: Long) {
        val sizePath = basePath.resolve(category)
        Files.createDirectories(sizePath)

        val virtualPath = "/${config.sizesPath.removePrefix("./virtual_catalog/")}/$category"
        insertVirtualTreeEntry(virtualPath, null, true)

        // Link files in this size range
        linkFilesBySize(sizePath, category, minSize, maxSize)
    }

    private fun linkFilesBySize(sizePath: Path, category: String, minSize: Long, maxSize: Long) {
        databaseManager.withConnection { connection ->
            val sql = """
                SELECT f.id, f.path, f.name, f.size_bytes, sr.name as smb_root_name
                FROM files f
                JOIN smb_roots sr ON f.smb_root_id = sr.id
                WHERE f.size_bytes >= ? AND f.size_bytes < ?
                  AND f.is_deleted = 0 AND f.is_accessible = 1 AND f.is_directory = 0
                ORDER BY f.size_bytes DESC
                LIMIT ?
            """

            connection.prepareStatement(sql).use { statement ->
                statement.setLong(1, minSize)
                statement.setLong(2, if (maxSize == Long.MAX_VALUE) Long.MAX_VALUE else maxSize)
                statement.setInt(3, config.maxLinksPerDirectory)
                val result = statement.executeQuery()

                while (result.next()) {
                    val fileId = result.getLong("id")
                    val fileName = result.getString("name")
                    val fileSize = result.getLong("size_bytes")
                    val smbRootName = result.getString("smb_root_name")

                    val linkPath = sizePath.resolve("${formatFileSize(fileSize)}_${smbRootName}_$fileName")
                    val virtualPath = "/${config.sizesPath.removePrefix("./virtual_catalog/")}/$category/${formatFileSize(fileSize)}_${smbRootName}_$fileName"

                    createVirtualLink(linkPath, fileId, virtualPath)
                }
            }
        }
    }

    private fun createByDateStructure() {
        val dateBasePath = config.getDatesPath()
        Files.createDirectories(dateBasePath)

        databaseManager.withConnection { connection ->
            val sql = """
                SELECT
                    strftime('%Y', datetime(modified_at, 'unixepoch')) as year,
                    strftime('%Y-%m', datetime(modified_at, 'unixepoch')) as year_month,
                    COUNT(*) as count
                FROM files
                WHERE is_deleted = 0 AND is_accessible = 1 AND is_directory = 0
                GROUP BY year, year_month
                ORDER BY year DESC, year_month DESC
            """

            connection.prepareStatement(sql).use { statement ->
                val result = statement.executeQuery()
                val yearMonths = mutableSetOf<String>()

                while (result.next()) {
                    val year = result.getString("year")
                    val yearMonth = result.getString("year_month")
                    val count = result.getInt("count")

                    if (yearMonths.add(yearMonth)) {
                        createDateDirectory(dateBasePath, year, yearMonth, count)
                    }
                }
            }
        }
    }

    private fun createDateDirectory(basePath: Path, year: String, yearMonth: String, count: Int) {
        val yearPath = basePath.resolve(year)
        val monthPath = yearPath.resolve(yearMonth.substring(5)) // Get MM part
        Files.createDirectories(monthPath)

        val virtualPath = "/${config.datesPath.removePrefix("./virtual_catalog/")}/$year/${yearMonth.substring(5)}"
        insertVirtualTreeEntry(virtualPath, null, true)

        // Link files from this month
        linkFilesByDate(monthPath, yearMonth)
    }

    private fun linkFilesByDate(monthPath: Path, yearMonth: String) {
        databaseManager.withConnection { connection ->
            val sql = """
                SELECT f.id, f.path, f.name, f.modified_at, sr.name as smb_root_name
                FROM files f
                JOIN smb_roots sr ON f.smb_root_id = sr.id
                WHERE strftime('%Y-%m', datetime(f.modified_at, 'unixepoch')) = ?
                  AND f.is_deleted = 0 AND f.is_accessible = 1 AND f.is_directory = 0
                ORDER BY f.modified_at DESC
                LIMIT ?
            """

            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, yearMonth)
                statement.setInt(2, config.maxLinksPerDirectory)
                val result = statement.executeQuery()

                while (result.next()) {
                    val fileId = result.getLong("id")
                    val fileName = result.getString("name")
                    val modifiedAt = result.getLong("modified_at")
                    val smbRootName = result.getString("smb_root_name")

                    val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(modifiedAt), ZoneOffset.UTC)
                    val dayStr = date.format(DateTimeFormatter.ofPattern("dd"))

                    val linkPath = monthPath.resolve("${dayStr}_${smbRootName}_$fileName")
                    val virtualPath = "/${config.datesPath.removePrefix("./virtual_catalog/")}/${yearMonth.substring(0, 4)}/${yearMonth.substring(5)}/${dayStr}_${smbRootName}_$fileName"

                    createVirtualLink(linkPath, fileId, virtualPath)
                }
            }
        }
    }

    private fun createDuplicatesStructure() {
        val duplicatesBasePath = config.getDuplicatesPath()
        Files.createDirectories(duplicatesBasePath)

        databaseManager.withConnection { connection ->
            val sql = """
                SELECT d.id, d.hash_value, d.file_count, d.total_size_bytes
                FROM duplicates d
                WHERE d.file_count > 1
                ORDER BY d.total_size_bytes DESC
                LIMIT 1000
            """

            connection.prepareStatement(sql).use { statement ->
                val result = statement.executeQuery()

                while (result.next()) {
                    val duplicateId = result.getLong("id")
                    val hashValue = result.getString("hash_value")
                    val fileCount = result.getInt("file_count")
                    val totalSize = result.getLong("total_size_bytes")

                    createDuplicateGroup(duplicatesBasePath, duplicateId, hashValue, fileCount, totalSize)
                }
            }
        }
    }

    private fun createDuplicateGroup(basePath: Path, duplicateId: Long, hashValue: String, fileCount: Int, totalSize: Long) {
        val hashPrefix = hashValue.take(8)
        val groupName = "${hashPrefix}_${fileCount}files_${formatFileSize(totalSize)}"
        val groupPath = basePath.resolve(groupName)
        Files.createDirectories(groupPath)

        val virtualPath = "/${config.duplicatesPath.removePrefix("./virtual_catalog/")}/$groupName"
        insertVirtualTreeEntry(virtualPath, null, true)

        // Link duplicate files
        linkDuplicateFiles(groupPath, duplicateId, groupName)
    }

    private fun linkDuplicateFiles(groupPath: Path, duplicateId: Long, groupName: String) {
        databaseManager.withConnection { connection ->
            val sql = """
                SELECT f.id, f.path, f.name, sr.name as smb_root_name, sr.host, sr.share
                FROM duplicate_files df
                JOIN files f ON df.file_id = f.id
                JOIN smb_roots sr ON f.smb_root_id = sr.id
                WHERE df.duplicate_id = ? AND f.is_deleted = 0
                ORDER BY f.path
            """

            connection.prepareStatement(sql).use { statement ->
                statement.setLong(1, duplicateId)
                val result = statement.executeQuery()

                var index = 1
                while (result.next()) {
                    val fileId = result.getLong("id")
                    val fileName = result.getString("name")
                    val smbRootName = result.getString("smb_root_name")

                    val linkPath = groupPath.resolve("${index}_${smbRootName}_$fileName")
                    val virtualPath = "/${config.duplicatesPath.removePrefix("./virtual_catalog/")}/$groupName/${index}_${smbRootName}_$fileName"

                    createVirtualLink(linkPath, fileId, virtualPath)
                    index++
                }
            }
        }
    }

    private fun createSmbRootStructure() {
        val mountPath = config.getMountPath()

        databaseManager.withConnection { connection ->
            val sql = """
                SELECT sr.id, sr.name, sr.host, sr.share, COUNT(f.id) as file_count
                FROM smb_roots sr
                LEFT JOIN files f ON sr.id = f.smb_root_id AND f.is_deleted = 0 AND f.is_accessible = 1
                WHERE sr.enabled = 1
                GROUP BY sr.id, sr.name, sr.host, sr.share
                ORDER BY sr.name
            """

            connection.prepareStatement(sql).use { statement ->
                val result = statement.executeQuery()

                while (result.next()) {
                    val smbRootId = result.getLong("id")
                    val rootName = result.getString("name")
                    val host = result.getString("host")
                    val share = result.getString("share")
                    val fileCount = result.getInt("file_count")

                    createSmbRootDirectory(mountPath, smbRootId, rootName, host, share, fileCount)
                }
            }
        }
    }

    private fun createSmbRootDirectory(basePath: Path, smbRootId: Long, rootName: String, host: String, share: String, fileCount: Int) {
        val rootPath = basePath.resolve(rootName)
        Files.createDirectories(rootPath)

        val virtualPath = "/$rootName"
        insertVirtualTreeEntry(virtualPath, null, true)

        // Create symbolic links to original SMB structure
        linkSmbRootFiles(rootPath, smbRootId, rootName)

        logger.debug("Created SMB root directory: $rootName with $fileCount files")
    }

    private fun linkSmbRootFiles(rootPath: Path, smbRootId: Long, rootName: String) {
        // This would create a virtual representation of the SMB directory structure
        // For simplicity, we'll create a flat structure with all files
        databaseManager.withConnection { connection ->
            val sql = """
                SELECT f.id, f.path, f.name, f.is_directory
                FROM files f
                WHERE f.smb_root_id = ? AND f.is_deleted = 0 AND f.is_accessible = 1
                  AND f.parent_path IS NULL OR f.parent_path = ''
                ORDER BY f.is_directory DESC, f.name
                LIMIT ?
            """

            connection.prepareStatement(sql).use { statement ->
                statement.setLong(1, smbRootId)
                statement.setInt(2, config.maxLinksPerDirectory)
                val result = statement.executeQuery()

                while (result.next()) {
                    val fileId = result.getLong("id")
                    val fileName = result.getString("name")
                    val isDirectory = result.getBoolean("is_directory")

                    val linkPath = rootPath.resolve(fileName)
                    val virtualPath = "/$rootName/$fileName"

                    if (isDirectory) {
                        // Create directory and recurse (limited depth)
                        Files.createDirectories(linkPath)
                        insertVirtualTreeEntry(virtualPath, null, true)
                    } else {
                        createVirtualLink(linkPath, fileId, virtualPath)
                    }
                }
            }
        }
    }

    private fun createVirtualLink(linkPath: Path, fileId: Long, virtualPath: String) {
        try {
            if (config.enableSymlinks) {
                // Create a symbolic link to a special file that represents the SMB file
                val targetPath = createSmbFileReference(fileId)
                Files.createSymbolicLink(linkPath, targetPath)
            } else {
                // Create a regular file with metadata
                Files.createFile(linkPath)
                // Set file attributes to indicate it's a virtual file
            }

            // Record in virtual tree
            insertVirtualTreeEntry(virtualPath, fileId, false)

            activeLinks[virtualPath] = VirtualNode(virtualPath, fileId, false)

        } catch (e: Exception) {
            logger.warn("Failed to create virtual link: $linkPath -> fileId:$fileId", e)
        }
    }

    private fun createSmbFileReference(fileId: Long): Path {
        // Create a special directory for SMB file references
        val referencePath = config.getMountPath().resolve(".smb_refs")
        Files.createDirectories(referencePath)
        return referencePath.resolve("file_$fileId.ref")
    }

    private fun insertVirtualTreeEntry(virtualPath: String, targetFileId: Long?, isDirectory: Boolean) {
        databaseManager.withConnection { connection ->
            val sql = """
                INSERT OR REPLACE INTO virtual_tree
                (virtual_path, target_file_id, is_directory, parent_virtual_path, created_at, updated_at)
                VALUES (?, ?, ?, ?, strftime('%s', 'now'), strftime('%s', 'now'))
            """

            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, virtualPath)
                if (targetFileId != null) {
                    statement.setLong(2, targetFileId)
                } else {
                    statement.setNull(2, java.sql.Types.BIGINT)
                }
                statement.setBoolean(3, isDirectory)

                val parentPath = getParentPath(virtualPath)
                if (parentPath != null) {
                    statement.setString(4, parentPath)
                } else {
                    statement.setNull(4, java.sql.Types.VARCHAR)
                }

                statement.executeUpdate()
            }
        }
    }

    private fun getParentPath(virtualPath: String): String? {
        val parentPath = Paths.get(virtualPath).parent?.toString()
        return if (parentPath != null && parentPath != "/") parentPath else null
    }

    private fun cleanupExistingVirtualTree() {
        try {
            if (Files.exists(config.getMountPath())) {
                Files.walkFileTree(config.getMountPath(), object : SimpleFileVisitor<Path>() {
                    override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                        Files.delete(file)
                        return FileVisitResult.CONTINUE
                    }

                    override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                        if (dir != config.getMountPath()) {
                            Files.delete(dir)
                        }
                        return FileVisitResult.CONTINUE
                    }
                })
            }

            // Clear virtual tree from database
            databaseManager.withConnection { connection ->
                connection.prepareStatement("DELETE FROM virtual_tree").use { statement ->
                    statement.executeUpdate()
                }
            }

            activeLinks.clear()
            logger.info("Cleaned up existing virtual tree")

        } catch (e: Exception) {
            logger.warn("Failed to cleanup existing virtual tree", e)
        }
    }

    private fun clearVirtualTree() {
        cleanupExistingVirtualTree()
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "${bytes}B"
            bytes < 1024 * 1024 -> "${bytes / 1024}KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)}MB"
            else -> "${bytes / (1024 * 1024 * 1024)}GB"
        }
    }

    fun getVirtualTreeStats(): VirtualTreeStats {
        return databaseManager.withConnection { connection ->
            val sql = """
                SELECT
                    COUNT(*) as total_entries,
                    SUM(CASE WHEN is_directory = 1 THEN 1 ELSE 0 END) as directories,
                    SUM(CASE WHEN is_directory = 0 THEN 1 ELSE 0 END) as files
                FROM virtual_tree
            """

            connection.prepareStatement(sql).use { statement ->
                val result = statement.executeQuery()
                if (result.next()) {
                    VirtualTreeStats(
                        totalEntries = result.getInt("total_entries"),
                        directories = result.getInt("directories"),
                        files = result.getInt("files"),
                        activeLinks = activeLinks.size
                    )
                } else {
                    VirtualTreeStats(0, 0, 0, 0)
                }
            }
        }
    }
}

data class VirtualNode(
    val virtualPath: String,
    val targetFileId: Long?,
    val isDirectory: Boolean
)

data class VirtualTreeStats(
    val totalEntries: Int,
    val directories: Int,
    val files: Int,
    val activeLinks: Int
)