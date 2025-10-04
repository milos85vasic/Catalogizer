package com.catalogizer.catalog.search

import com.catalogizer.catalog.db.DatabaseManager
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.*
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.store.FSDirectory
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.ConcurrentHashMap

class SearchEngine(
    private val databaseManager: DatabaseManager,
    private val indexPath: Path = Paths.get("./lucene_index")
) : Closeable {

    private val logger = LoggerFactory.getLogger(SearchEngine::class.java)
    private val analyzer = StandardAnalyzer()
    private val directory = FSDirectory.open(indexPath)
    private val indexWriter: IndexWriter
    private val searchManager: SearcherManager
    private val queryCache = ConcurrentHashMap<String, Query>()

    init {
        val config = IndexWriterConfig(analyzer).apply {
            openMode = IndexWriterConfig.OpenMode.CREATE_OR_APPEND
            ramBufferSizeMB = 256.0
        }
        indexWriter = IndexWriter(directory, config)
        searchManager = SearcherManager(indexWriter, SearcherFactory())

        logger.info("Search engine initialized with index path: $indexPath")
    }

    fun indexFile(fileRecord: CatalogFileRecord) {
        try {
            val document = Document().apply {
                // Basic file information
                add(LongPoint("file_id", fileRecord.fileId))
                add(StoredField("file_id", fileRecord.fileId))
                add(TextField("path", fileRecord.path, Field.Store.YES))
                add(TextField("name", fileRecord.name, Field.Store.YES))
                add(StoredField("smb_root_name", fileRecord.smbRootName))

                // File properties
                add(LongPoint("size", fileRecord.size))
                add(StoredField("size", fileRecord.size))
                add(LongPoint("modified_date", fileRecord.modifiedAt))
                add(StoredField("modified_date", fileRecord.modifiedAt))

                // File type and MIME
                add(StringField("extension", fileRecord.extension ?: "", Field.Store.YES))
                add(StringField("mime_type", fileRecord.mimeType ?: "", Field.Store.YES))
                add(StringField("file_type", fileRecord.fileType ?: "", Field.Store.YES))

                // Status flags
                add(StringField("is_directory", if (fileRecord.isDirectory) "true" else "false", Field.Store.YES))
                add(StringField("is_duplicate", if (fileRecord.isDuplicate) "true" else "false", Field.Store.YES))

                // Searchable content
                if (!fileRecord.searchableContent.isNullOrBlank()) {
                    add(TextField("content", fileRecord.searchableContent, Field.Store.NO))
                }

                // Metadata fields
                fileRecord.metadata.forEach { (key, value) ->
                    add(TextField("meta_$key", value, Field.Store.YES))
                    add(TextField("metadata", "$key:$value", Field.Store.NO))
                }

                // Combined search field
                val combinedText = buildString {
                    append(fileRecord.name)
                    append(" ")
                    append(fileRecord.path)
                    if (!fileRecord.searchableContent.isNullOrBlank()) {
                        append(" ")
                        append(fileRecord.searchableContent)
                    }
                    fileRecord.metadata.values.forEach { value ->
                        append(" ")
                        append(value)
                    }
                }
                add(TextField("all", combinedText, Field.Store.NO))

                // Date-based fields for faceted search
                val modifiedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(fileRecord.modifiedAt), ZoneOffset.UTC)
                add(StringField("year", modifiedDate.year.toString(), Field.Store.YES))
                add(StringField("month", String.format("%04d-%02d", modifiedDate.year, modifiedDate.monthValue), Field.Store.YES))
                add(StringField("day", String.format("%04d-%02d-%02d", modifiedDate.year, modifiedDate.monthValue, modifiedDate.dayOfMonth), Field.Store.YES))

                // Size categories for faceted search
                val sizeCategory = when {
                    fileRecord.size == 0L -> "empty"
                    fileRecord.size < 1024 -> "tiny"
                    fileRecord.size < 1024 * 1024 -> "small"
                    fileRecord.size < 10 * 1024 * 1024 -> "medium"
                    fileRecord.size < 100 * 1024 * 1024 -> "large"
                    fileRecord.size < 1024 * 1024 * 1024 -> "huge"
                    else -> "massive"
                }
                add(StringField("size_category", sizeCategory, Field.Store.YES))
            }

            // Delete existing document if it exists
            indexWriter.deleteDocuments(LongPoint.newExactQuery("file_id", fileRecord.fileId))

            // Add new document
            indexWriter.addDocument(document)

            logger.debug("Indexed file: ${fileRecord.path}")

        } catch (e: Exception) {
            logger.error("Failed to index file: ${fileRecord.path}", e)
        }
    }

    fun removeFile(fileId: Long) {
        try {
            indexWriter.deleteDocuments(LongPoint.newExactQuery("file_id", fileId))
            logger.debug("Removed file from index: $fileId")
        } catch (e: Exception) {
            logger.error("Failed to remove file from index: $fileId", e)
        }
    }

    fun search(searchRequest: SearchRequest): SearchResult {
        return try {
            searchManager.maybeRefreshBlocking()
            val searcher = searchManager.acquire()

            try {
                val query = buildQuery(searchRequest)
                val sort = buildSort(searchRequest.sortBy, searchRequest.sortOrder)

                val topDocs = if (sort != null) {
                    searcher.search(query, searchRequest.limit + searchRequest.offset, sort)
                } else {
                    searcher.search(query, searchRequest.limit + searchRequest.offset)
                }

                val totalHits = topDocs.totalHits.value
                val hits = topDocs.scoreDocs
                    .drop(searchRequest.offset)
                    .take(searchRequest.limit)
                    .map { scoreDoc ->
                        val doc = searcher.doc(scoreDoc.doc)
                        SearchHit(
                            fileId = doc.get("file_id").toLong(),
                            path = doc.get("path"),
                            name = doc.get("name"),
                            smbRootName = doc.get("smb_root_name"),
                            size = doc.get("size").toLong(),
                            modifiedAt = doc.get("modified_date").toLong(),
                            extension = doc.get("extension"),
                            mimeType = doc.get("mime_type"),
                            fileType = doc.get("file_type"),
                            isDirectory = doc.get("is_directory") == "true",
                            isDuplicate = doc.get("is_duplicate") == "true",
                            score = scoreDoc.score,
                            metadata = extractMetadata(doc)
                        )
                    }

                val facets = if (searchRequest.includeFacets) {
                    buildFacets(searcher, query)
                } else {
                    emptyMap()
                }

                SearchResult(
                    hits = hits,
                    totalHits = totalHits,
                    facets = facets,
                    queryTime = System.currentTimeMillis() - System.currentTimeMillis() // Placeholder
                )

            } finally {
                searchManager.release(searcher)
            }

        } catch (e: Exception) {
            logger.error("Search failed for query: ${searchRequest.query}", e)
            SearchResult(emptyList(), 0, emptyMap(), 0)
        }
    }

    private fun buildQuery(searchRequest: SearchRequest): Query {
        val cacheKey = searchRequest.toCacheKey()
        return queryCache.computeIfAbsent(cacheKey) {
            val booleanQuery = BooleanQuery.Builder()

            // Main text query
            if (searchRequest.query.isNotBlank()) {
                val parser = QueryParser("all", analyzer)
                val textQuery = parser.parse(searchRequest.query)
                booleanQuery.add(textQuery, BooleanClause.Occur.MUST)
            }

            // File type filters
            if (searchRequest.fileTypes.isNotEmpty()) {
                val fileTypeQuery = BooleanQuery.Builder()
                searchRequest.fileTypes.forEach { fileType ->
                    fileTypeQuery.add(TermQuery(Term("file_type", fileType)), BooleanClause.Occur.SHOULD)
                }
                booleanQuery.add(fileTypeQuery.build(), BooleanClause.Occur.MUST)
            }

            // Size range filter
            if (searchRequest.minSize != null || searchRequest.maxSize != null) {
                val sizeQuery = LongPoint.newRangeQuery(
                    "size",
                    searchRequest.minSize ?: 0L,
                    searchRequest.maxSize ?: Long.MAX_VALUE
                )
                booleanQuery.add(sizeQuery, BooleanClause.Occur.MUST)
            }

            // Date range filter
            if (searchRequest.modifiedAfter != null || searchRequest.modifiedBefore != null) {
                val dateQuery = LongPoint.newRangeQuery(
                    "modified_date",
                    searchRequest.modifiedAfter ?: 0L,
                    searchRequest.modifiedBefore ?: Long.MAX_VALUE
                )
                booleanQuery.add(dateQuery, BooleanClause.Occur.MUST)
            }

            // Path filter
            if (searchRequest.pathPattern.isNotBlank()) {
                val pathQuery = WildcardQuery(Term("path", "*${searchRequest.pathPattern}*"))
                booleanQuery.add(pathQuery, BooleanClause.Occur.MUST)
            }

            // SMB root filter
            if (searchRequest.smbRoots.isNotEmpty()) {
                val rootQuery = BooleanQuery.Builder()
                searchRequest.smbRoots.forEach { root ->
                    rootQuery.add(TermQuery(Term("smb_root_name", root)), BooleanClause.Occur.SHOULD)
                }
                booleanQuery.add(rootQuery.build(), BooleanClause.Occur.MUST)
            }

            // Include/exclude duplicates
            when (searchRequest.duplicateFilter) {
                DuplicateFilter.ONLY_DUPLICATES -> {
                    booleanQuery.add(TermQuery(Term("is_duplicate", "true")), BooleanClause.Occur.MUST)
                }
                DuplicateFilter.EXCLUDE_DUPLICATES -> {
                    booleanQuery.add(TermQuery(Term("is_duplicate", "false")), BooleanClause.Occur.MUST)
                }
                DuplicateFilter.ALL -> {
                    // No filter
                }
            }

            // Include/exclude directories
            if (!searchRequest.includeDirectories) {
                booleanQuery.add(TermQuery(Term("is_directory", "false")), BooleanClause.Occur.MUST)
            }

            // Metadata filters
            searchRequest.metadataFilters.forEach { (key, value) ->
                val metadataQuery = TermQuery(Term("meta_$key", value))
                booleanQuery.add(metadataQuery, BooleanClause.Occur.MUST)
            }

            booleanQuery.build()
        }
    }

    private fun buildSort(sortBy: SortField?, sortOrder: SortOrder): Sort? {
        return when (sortBy) {
            SortField.NAME -> Sort(org.apache.lucene.search.SortField("name", org.apache.lucene.search.SortField.Type.STRING, sortOrder == SortOrder.DESC))
            SortField.SIZE -> Sort(org.apache.lucene.search.SortField("size", org.apache.lucene.search.SortField.Type.LONG, sortOrder == SortOrder.DESC))
            SortField.MODIFIED_DATE -> Sort(org.apache.lucene.search.SortField("modified_date", org.apache.lucene.search.SortField.Type.LONG, sortOrder == SortOrder.DESC))
            SortField.RELEVANCE -> null // Use default relevance scoring
            null -> null
        }
    }

    private fun buildFacets(searcher: IndexSearcher, query: Query): Map<String, List<FacetValue>> {
        val facets = mutableMapOf<String, List<FacetValue>>()

        try {
            // File type facets
            facets["file_types"] = buildTermFacets(searcher, query, "file_type")

            // Size category facets
            facets["size_categories"] = buildTermFacets(searcher, query, "size_category")

            // Year facets
            facets["years"] = buildTermFacets(searcher, query, "year")

            // SMB root facets
            facets["smb_roots"] = buildTermFacets(searcher, query, "smb_root_name")

        } catch (e: Exception) {
            logger.warn("Failed to build facets", e)
        }

        return facets
    }

    private fun buildTermFacets(searcher: IndexSearcher, query: Query, field: String): List<FacetValue> {
        // Simplified facet implementation - would need proper faceting library for production
        val termCounts = mutableMapOf<String, Int>()
        val docs = searcher.search(query, 10000)

        for (scoreDoc in docs.scoreDocs) {
            val doc = searcher.doc(scoreDoc.doc)
            val term = doc.get(field)
            if (term != null) {
                termCounts[term] = termCounts.getOrDefault(term, 0) + 1
            }
        }

        return termCounts.entries
            .sortedByDescending { it.value }
            .take(20)
            .map { FacetValue(it.key, it.value) }
    }

    private fun extractMetadata(doc: Document): Map<String, String> {
        return doc.fields
            .filter { it.name().startsWith("meta_") }
            .associate {
                it.name().removePrefix("meta_") to it.stringValue()
            }
    }

    fun commit() {
        try {
            indexWriter.commit()
        } catch (e: Exception) {
            logger.error("Failed to commit index", e)
        }
    }

    fun optimize() {
        try {
            indexWriter.forceMerge(1)
            logger.info("Index optimization completed")
        } catch (e: Exception) {
            logger.error("Failed to optimize index", e)
        }
    }

    fun getIndexStats(): IndexStats {
        return try {
            searchManager.maybeRefreshBlocking()
            val searcher = searchManager.acquire()

            try {
                val reader = searcher.indexReader
                IndexStats(
                    documentCount = reader.numDocs(),
                    deletedDocuments = reader.numDeletedDocs(),
                    indexSizeBytes = directory.listAll().sumOf { fileName ->
                        directory.fileLength(fileName)
                    }
                )
            } finally {
                searchManager.release(searcher)
            }
        } catch (e: Exception) {
            logger.error("Failed to get index stats", e)
            IndexStats(0, 0, 0L)
        }
    }

    override fun close() {
        try {
            indexWriter.close()
            searchManager.close()
            directory.close()
            analyzer.close()
            logger.info("Search engine closed")
        } catch (e: Exception) {
            logger.error("Error closing search engine", e)
        }
    }
}

data class CatalogFileRecord(
    val fileId: Long,
    val path: String,
    val name: String,
    val smbRootName: String,
    val size: Long,
    val modifiedAt: Long,
    val extension: String?,
    val mimeType: String?,
    val fileType: String?,
    val isDirectory: Boolean,
    val isDuplicate: Boolean,
    val searchableContent: String?,
    val metadata: Map<String, String>
)

data class SearchRequest(
    val query: String = "",
    val fileTypes: List<String> = emptyList(),
    val minSize: Long? = null,
    val maxSize: Long? = null,
    val modifiedAfter: Long? = null,
    val modifiedBefore: Long? = null,
    val pathPattern: String = "",
    val smbRoots: List<String> = emptyList(),
    val duplicateFilter: DuplicateFilter = DuplicateFilter.ALL,
    val includeDirectories: Boolean = true,
    val metadataFilters: Map<String, String> = emptyMap(),
    val sortBy: SortField? = null,
    val sortOrder: SortOrder = SortOrder.ASC,
    val limit: Int = 100,
    val offset: Int = 0,
    val includeFacets: Boolean = false
) {
    fun toCacheKey(): String {
        return "$query|${fileTypes.joinToString(",")}|$minSize|$maxSize|$modifiedAfter|$modifiedBefore|$pathPattern|${smbRoots.joinToString(",")}|$duplicateFilter|$includeDirectories|${metadataFilters.entries.joinToString(",")}"
    }
}

data class SearchResult(
    val hits: List<SearchHit>,
    val totalHits: Long,
    val facets: Map<String, List<FacetValue>>,
    val queryTime: Long
)

data class SearchHit(
    val fileId: Long,
    val path: String,
    val name: String,
    val smbRootName: String,
    val size: Long,
    val modifiedAt: Long,
    val extension: String?,
    val mimeType: String?,
    val fileType: String?,
    val isDirectory: Boolean,
    val isDuplicate: Boolean,
    val score: Float,
    val metadata: Map<String, String>
)

data class FacetValue(
    val value: String,
    val count: Int
)

data class IndexStats(
    val documentCount: Int,
    val deletedDocuments: Int,
    val indexSizeBytes: Long
)

enum class SortField {
    NAME,
    SIZE,
    MODIFIED_DATE,
    RELEVANCE
}

enum class SortOrder {
    ASC,
    DESC
}

enum class DuplicateFilter {
    ALL,
    ONLY_DUPLICATES,
    EXCLUDE_DUPLICATES
}