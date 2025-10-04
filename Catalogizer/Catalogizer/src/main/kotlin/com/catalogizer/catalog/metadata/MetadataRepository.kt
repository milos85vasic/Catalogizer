package com.catalogizer.catalog.metadata

import com.catalogizer.catalog.db.DatabaseManager
import org.slf4j.LoggerFactory
import java.sql.PreparedStatement
import java.sql.SQLException

class MetadataRepository(private val databaseManager: DatabaseManager) {

    private val logger = LoggerFactory.getLogger(MetadataRepository::class.java)

    fun saveMetadata(fileId: Long, metadata: FileMetadata) {
        databaseManager.withTransaction { connection ->
            // Clear existing metadata for this file
            val deleteSql = "DELETE FROM file_metadata WHERE file_id = ?"
            connection.prepareStatement(deleteSql).use { statement ->
                statement.setLong(1, fileId)
                statement.executeUpdate()
            }

            // Insert new metadata
            val insertSql = """
                INSERT INTO file_metadata (file_id, metadata_key, metadata_value, metadata_type,
                                          searchable_content, created_at)
                VALUES (?, ?, ?, ?, ?, strftime('%s', 'now'))
            """

            connection.prepareStatement(insertSql).use { statement ->
                // Store searchable text content
                if (!metadata.searchableText.isNullOrBlank()) {
                    statement.setLong(1, fileId)
                    statement.setString(2, "searchable_text")
                    statement.setString(3, metadata.searchableText)
                    statement.setString(4, "text")
                    statement.setString(5, metadata.searchableText)
                    statement.addBatch()
                }

                // Store all properties
                metadata.properties.forEach { (key, value) ->
                    if (value != null) {
                        statement.setLong(1, fileId)
                        statement.setString(2, key)
                        statement.setString(3, value)
                        statement.setString(4, detectMetadataType(value))
                        statement.setString(5, if (isSearchableProperty(key)) value else null)
                        statement.addBatch()
                    }
                }

                // Store basic file metadata
                statement.setLong(1, fileId)
                statement.setString(2, "mime_type")
                statement.setString(3, metadata.mimeType)
                statement.setString(4, "string")
                statement.setString(5, metadata.mimeType)
                statement.addBatch()

                statement.setLong(1, fileId)
                statement.setString(2, "file_type")
                statement.setString(3, metadata.fileType.name)
                statement.setString(4, "string")
                statement.setString(5, metadata.fileType.name)
                statement.addBatch()

                statement.setLong(1, fileId)
                statement.setString(2, "extraction_success")
                statement.setString(3, metadata.extractionSuccess.toString())
                statement.setString(4, "boolean")
                statement.setString(5, null)
                statement.addBatch()

                if (metadata.errorMessage != null) {
                    statement.setLong(1, fileId)
                    statement.setString(2, "extraction_error")
                    statement.setString(3, metadata.errorMessage)
                    statement.setString(4, "string")
                    statement.setString(5, null)
                    statement.addBatch()
                }

                statement.executeBatch()
            }

            logger.debug("Saved metadata for file ID: $fileId")
        }
    }

    fun getMetadata(fileId: Long): Map<String, MetadataEntry> {
        return databaseManager.withConnection { connection ->
            val sql = """
                SELECT metadata_key, metadata_value, metadata_type, searchable_content, created_at
                FROM file_metadata
                WHERE file_id = ?
                ORDER BY metadata_key
            """

            val metadata = mutableMapOf<String, MetadataEntry>()

            connection.prepareStatement(sql).use { statement ->
                statement.setLong(1, fileId)
                val result = statement.executeQuery()

                while (result.next()) {
                    val key = result.getString("metadata_key")
                    metadata[key] = MetadataEntry(
                        key = key,
                        value = result.getString("metadata_value"),
                        type = result.getString("metadata_type"),
                        searchableContent = result.getString("searchable_content"),
                        createdAt = result.getLong("created_at")
                    )
                }
            }

            metadata
        }
    }

    fun searchByMetadata(
        metadataKey: String? = null,
        metadataValue: String? = null,
        searchText: String? = null,
        limit: Int = 1000,
        offset: Int = 0
    ): List<FileMetadataSearchResult> {
        return databaseManager.withConnection { connection ->
            val conditions = mutableListOf<String>()
            val parameters = mutableListOf<Any>()

            var sql = """
                SELECT DISTINCT f.id, f.path, f.name, f.size_bytes, f.modified_at,
                       sr.name as smb_root_name, sr.host, sr.share,
                       fm.metadata_key, fm.metadata_value
                FROM files f
                JOIN file_metadata fm ON f.id = fm.file_id
                JOIN smb_roots sr ON f.smb_root_id = sr.id
                WHERE f.is_deleted = 0 AND f.is_accessible = 1
            """

            if (metadataKey != null) {
                conditions.add("fm.metadata_key = ?")
                parameters.add(metadataKey)
            }

            if (metadataValue != null) {
                conditions.add("fm.metadata_value LIKE ?")
                parameters.add("%$metadataValue%")
            }

            if (searchText != null) {
                conditions.add("fm.searchable_content LIKE ?")
                parameters.add("%$searchText%")
            }

            if (conditions.isNotEmpty()) {
                sql += " AND " + conditions.joinToString(" AND ")
            }

            sql += " ORDER BY f.modified_at DESC LIMIT ? OFFSET ?"
            parameters.add(limit)
            parameters.add(offset)

            connection.prepareStatement(sql).use { statement ->
                parameters.forEachIndexed { index, param ->
                    when (param) {
                        is String -> statement.setString(index + 1, param)
                        is Int -> statement.setInt(index + 1, param)
                        is Long -> statement.setLong(index + 1, param)
                    }
                }

                val result = statement.executeQuery()
                val results = mutableListOf<FileMetadataSearchResult>()

                while (result.next()) {
                    results.add(
                        FileMetadataSearchResult(
                            fileId = result.getLong("id"),
                            path = result.getString("path"),
                            name = result.getString("name"),
                            size = result.getLong("size_bytes"),
                            modifiedAt = result.getLong("modified_at"),
                            smbRootName = result.getString("smb_root_name"),
                            host = result.getString("host"),
                            share = result.getString("share"),
                            matchedMetadataKey = result.getString("metadata_key"),
                            matchedMetadataValue = result.getString("metadata_value")
                        )
                    )
                }

                results
            }
        }
    }

    fun getMetadataStatistics(): MetadataStatistics {
        return databaseManager.withConnection { connection ->
            val stats = MetadataStatistics()

            // Count files with metadata
            val countSql = """
                SELECT COUNT(DISTINCT fm.file_id) as files_with_metadata,
                       COUNT(*) as total_metadata_entries
                FROM file_metadata fm
                JOIN files f ON fm.file_id = f.id
                WHERE f.is_deleted = 0 AND f.is_accessible = 1
            """

            connection.prepareStatement(countSql).use { statement ->
                val result = statement.executeQuery()
                if (result.next()) {
                    stats.filesWithMetadata = result.getLong("files_with_metadata")
                    stats.totalMetadataEntries = result.getLong("total_metadata_entries")
                }
            }

            // Get most common metadata keys
            val keysSql = """
                SELECT metadata_key, COUNT(*) as count
                FROM file_metadata fm
                JOIN files f ON fm.file_id = f.id
                WHERE f.is_deleted = 0 AND f.is_accessible = 1
                GROUP BY metadata_key
                ORDER BY count DESC
                LIMIT 20
            """

            connection.prepareStatement(keysSql).use { statement ->
                val result = statement.executeQuery()
                while (result.next()) {
                    stats.commonMetadataKeys[result.getString("metadata_key")] = result.getLong("count")
                }
            }

            // Get file type distribution
            val typesSql = """
                SELECT metadata_value as file_type, COUNT(*) as count
                FROM file_metadata fm
                JOIN files f ON fm.file_id = f.id
                WHERE fm.metadata_key = 'file_type' AND f.is_deleted = 0 AND f.is_accessible = 1
                GROUP BY metadata_value
                ORDER BY count DESC
            """

            connection.prepareStatement(typesSql).use { statement ->
                val result = statement.executeQuery()
                while (result.next()) {
                    stats.fileTypeDistribution[result.getString("file_type")] = result.getLong("count")
                }
            }

            stats
        }
    }

    private fun detectMetadataType(value: String): String {
        return when {
            value.toBooleanStrictOrNull() != null -> "boolean"
            value.toLongOrNull() != null -> "number"
            value.matches(Regex("\\d{4}-\\d{2}-\\d{2}.*")) -> "date"
            else -> "string"
        }
    }

    private fun isSearchableProperty(key: String): Boolean {
        val searchableKeys = setOf(
            "title", "author", "creator", "subject", "keywords", "description",
            "comments", "album", "artist", "genre", "searchable_text"
        )
        return key in searchableKeys
    }
}

data class MetadataEntry(
    val key: String,
    val value: String,
    val type: String,
    val searchableContent: String?,
    val createdAt: Long
)

data class FileMetadataSearchResult(
    val fileId: Long,
    val path: String,
    val name: String,
    val size: Long,
    val modifiedAt: Long,
    val smbRootName: String,
    val host: String,
    val share: String,
    val matchedMetadataKey: String,
    val matchedMetadataValue: String
)

data class MetadataStatistics(
    var filesWithMetadata: Long = 0,
    var totalMetadataEntries: Long = 0,
    val commonMetadataKeys: MutableMap<String, Long> = mutableMapOf(),
    val fileTypeDistribution: MutableMap<String, Long> = mutableMapOf()
)