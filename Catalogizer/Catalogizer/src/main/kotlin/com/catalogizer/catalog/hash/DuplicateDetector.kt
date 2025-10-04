package com.catalogizer.catalog.hash

import com.catalogizer.catalog.db.DatabaseManager
import org.slf4j.LoggerFactory
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.concurrent.ConcurrentHashMap

class DuplicateDetector(private val databaseManager: DatabaseManager) {

    private val logger = LoggerFactory.getLogger(DuplicateDetector::class.java)
    private val duplicateCache = ConcurrentHashMap<String, DuplicateGroup>()

    fun detectDuplicates(fileId: Long, hashes: FileHashes): DuplicateDetectionResult {
        return databaseManager.withTransaction { connection ->
            val primaryHash = hashes.contentHash ?: hashes.sha256Hash ?: hashes.md5Hash
            if (primaryHash == null) {
                return@withTransaction DuplicateDetectionResult(
                    isDuplicate = false,
                    duplicateGroupId = null,
                    duplicateCount = 0,
                    existingFiles = emptyList()
                )
            }

            // Check if this hash already exists
            val existingDuplicateId = findExistingDuplicateGroup(connection, primaryHash, "content")

            if (existingDuplicateId != null) {
                // Add to existing duplicate group
                addFileToDuplicateGroup(connection, existingDuplicateId, fileId)
                markFileAsDuplicate(connection, fileId)

                val duplicateInfo = getDuplicateGroupInfo(connection, existingDuplicateId)
                logger.info("File $fileId added to existing duplicate group $existingDuplicateId")

                DuplicateDetectionResult(
                    isDuplicate = true,
                    duplicateGroupId = existingDuplicateId,
                    duplicateCount = duplicateInfo.fileCount,
                    existingFiles = duplicateInfo.fileIds
                )
            } else {
                // Check if any files with this hash exist
                val existingFiles = findFilesWithHash(connection, primaryHash)

                if (existingFiles.isNotEmpty()) {
                    // Create new duplicate group
                    val duplicateGroupId = createDuplicateGroup(connection, primaryHash, "content")

                    // Add all files to the group
                    existingFiles.forEach { existingFileId ->
                        addFileToDuplicateGroup(connection, duplicateGroupId, existingFileId)
                        markFileAsDuplicate(connection, existingFileId)
                    }

                    // Add current file
                    addFileToDuplicateGroup(connection, duplicateGroupId, fileId)
                    markFileAsDuplicate(connection, fileId)

                    logger.info("Created new duplicate group $duplicateGroupId with ${existingFiles.size + 1} files")

                    DuplicateDetectionResult(
                        isDuplicate = true,
                        duplicateGroupId = duplicateGroupId,
                        duplicateCount = existingFiles.size + 1,
                        existingFiles = existingFiles + fileId
                    )
                } else {
                    // No duplicates found
                    DuplicateDetectionResult(
                        isDuplicate = false,
                        duplicateGroupId = null,
                        duplicateCount = 0,
                        existingFiles = emptyList()
                    )
                }
            }
        }
    }

    private fun findExistingDuplicateGroup(
        connection: java.sql.Connection,
        hashValue: String,
        hashType: String
    ): Long? {
        val sql = "SELECT id FROM duplicates WHERE hash_value = ? AND hash_type = ?"
        return connection.prepareStatement(sql).use { statement ->
            statement.setString(1, hashValue)
            statement.setString(2, hashType)
            val result = statement.executeQuery()
            if (result.next()) result.getLong("id") else null
        }
    }

    private fun findFilesWithHash(connection: java.sql.Connection, hashValue: String): List<Long> {
        val sql = """
            SELECT id FROM files
            WHERE (content_hash = ? OR sha256_hash = ? OR md5_hash = ?)
            AND is_deleted = 0 AND is_accessible = 1
        """
        return connection.prepareStatement(sql).use { statement ->
            statement.setString(1, hashValue)
            statement.setString(2, hashValue)
            statement.setString(3, hashValue)
            val result = statement.executeQuery()
            val files = mutableListOf<Long>()
            while (result.next()) {
                files.add(result.getLong("id"))
            }
            files
        }
    }

    private fun createDuplicateGroup(
        connection: java.sql.Connection,
        hashValue: String,
        hashType: String
    ): Long {
        val sql = """
            INSERT INTO duplicates (hash_value, hash_type, file_count, total_size_bytes,
                                  first_discovered_at, last_updated_at)
            VALUES (?, ?, 0, 0, strftime('%s', 'now'), strftime('%s', 'now'))
        """
        return connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS).use { statement ->
            statement.setString(1, hashValue)
            statement.setString(2, hashType)
            statement.executeUpdate()

            val keys = statement.generatedKeys
            if (keys.next()) {
                keys.getLong(1)
            } else {
                throw SQLException("Failed to create duplicate group")
            }
        }
    }

    private fun addFileToDuplicateGroup(connection: java.sql.Connection, duplicateId: Long, fileId: Long) {
        val sql = "INSERT OR IGNORE INTO duplicate_files (duplicate_id, file_id, added_at) VALUES (?, ?, strftime('%s', 'now'))"
        connection.prepareStatement(sql).use { statement ->
            statement.setLong(1, duplicateId)
            statement.setLong(2, fileId)
            statement.executeUpdate()
        }
    }

    private fun markFileAsDuplicate(connection: java.sql.Connection, fileId: Long) {
        val sql = "UPDATE files SET is_duplicate = 1 WHERE id = ?"
        connection.prepareStatement(sql).use { statement ->
            statement.setLong(1, fileId)
            statement.executeUpdate()
        }
    }

    private fun getDuplicateGroupInfo(connection: java.sql.Connection, duplicateId: Long): DuplicateGroupInfo {
        val sql = """
            SELECT d.file_count, d.total_size_bytes,
                   GROUP_CONCAT(df.file_id) as file_ids
            FROM duplicates d
            LEFT JOIN duplicate_files df ON d.id = df.duplicate_id
            WHERE d.id = ?
            GROUP BY d.id
        """
        return connection.prepareStatement(sql).use { statement ->
            statement.setLong(1, duplicateId)
            val result = statement.executeQuery()
            if (result.next()) {
                val fileIdsStr = result.getString("file_ids")
                val fileIds = if (fileIdsStr != null) {
                    fileIdsStr.split(",").mapNotNull { it.toLongOrNull() }
                } else {
                    emptyList()
                }

                DuplicateGroupInfo(
                    fileCount = result.getInt("file_count"),
                    totalSize = result.getLong("total_size_bytes"),
                    fileIds = fileIds
                )
            } else {
                DuplicateGroupInfo(0, 0, emptyList())
            }
        }
    }

    fun getAllDuplicateGroups(): List<DuplicateGroup> {
        return databaseManager.withConnection { connection ->
            val sql = """
                SELECT d.id, d.hash_value, d.hash_type, d.file_count, d.total_size_bytes,
                       d.first_discovered_at, d.last_updated_at
                FROM duplicates d
                WHERE d.file_count > 1
                ORDER BY d.total_size_bytes DESC
            """

            connection.prepareStatement(sql).use { statement ->
                val result = statement.executeQuery()
                val duplicates = mutableListOf<DuplicateGroup>()

                while (result.next()) {
                    duplicates.add(
                        DuplicateGroup(
                            id = result.getLong("id"),
                            hashValue = result.getString("hash_value"),
                            hashType = result.getString("hash_type"),
                            fileCount = result.getInt("file_count"),
                            totalSize = result.getLong("total_size_bytes"),
                            firstDiscoveredAt = result.getLong("first_discovered_at"),
                            lastUpdatedAt = result.getLong("last_updated_at"),
                            files = emptyList() // Loaded separately if needed
                        )
                    )
                }

                duplicates
            }
        }
    }

    fun getDuplicateGroupFiles(duplicateGroupId: Long): List<DuplicateFileInfo> {
        return databaseManager.withConnection { connection ->
            val sql = """
                SELECT f.id, f.path, f.name, f.size_bytes, f.modified_at,
                       sr.name as smb_root_name, sr.host, sr.share
                FROM duplicate_files df
                JOIN files f ON df.file_id = f.id
                JOIN smb_roots sr ON f.smb_root_id = sr.id
                WHERE df.duplicate_id = ? AND f.is_deleted = 0
                ORDER BY f.path
            """

            connection.prepareStatement(sql).use { statement ->
                statement.setLong(1, duplicateGroupId)
                val result = statement.executeQuery()
                val files = mutableListOf<DuplicateFileInfo>()

                while (result.next()) {
                    files.add(
                        DuplicateFileInfo(
                            fileId = result.getLong("id"),
                            path = result.getString("path"),
                            name = result.getString("name"),
                            size = result.getLong("size_bytes"),
                            modifiedAt = result.getLong("modified_at"),
                            smbRootName = result.getString("smb_root_name"),
                            host = result.getString("host"),
                            share = result.getString("share")
                        )
                    )
                }

                files
            }
        }
    }

    fun removeDuplicateGroup(duplicateGroupId: Long) {
        databaseManager.withTransaction { connection ->
            // Unmark files as duplicates
            val updateFilesSql = """
                UPDATE files SET is_duplicate = 0
                WHERE id IN (SELECT file_id FROM duplicate_files WHERE duplicate_id = ?)
            """
            connection.prepareStatement(updateFilesSql).use { statement ->
                statement.setLong(1, duplicateGroupId)
                statement.executeUpdate()
            }

            // Delete duplicate files associations
            val deleteDuplicateFilesSql = "DELETE FROM duplicate_files WHERE duplicate_id = ?"
            connection.prepareStatement(deleteDuplicateFilesSql).use { statement ->
                statement.setLong(1, duplicateGroupId)
                statement.executeUpdate()
            }

            // Delete duplicate group
            val deleteDuplicateSql = "DELETE FROM duplicates WHERE id = ?"
            connection.prepareStatement(deleteDuplicateSql).use { statement ->
                statement.setLong(1, duplicateGroupId)
                statement.executeUpdate()
            }

            logger.info("Removed duplicate group: $duplicateGroupId")
        }
    }
}

data class DuplicateDetectionResult(
    val isDuplicate: Boolean,
    val duplicateGroupId: Long?,
    val duplicateCount: Int,
    val existingFiles: List<Long>
)

data class DuplicateGroup(
    val id: Long,
    val hashValue: String,
    val hashType: String,
    val fileCount: Int,
    val totalSize: Long,
    val firstDiscoveredAt: Long,
    val lastUpdatedAt: Long,
    val files: List<DuplicateFileInfo>
)

data class DuplicateGroupInfo(
    val fileCount: Int,
    val totalSize: Long,
    val fileIds: List<Long>
)

data class DuplicateFileInfo(
    val fileId: Long,
    val path: String,
    val name: String,
    val size: Long,
    val modifiedAt: Long,
    val smbRootName: String,
    val host: String,
    val share: String
)