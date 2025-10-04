package com.catalogizer.catalog.scanner

import com.catalogizer.catalog.config.SmbRootConfig
import com.catalogizer.catalog.db.DatabaseManager
import com.catalogizer.catalog.hash.DuplicateDetector
import com.catalogizer.catalog.hash.HashingEngine
import com.catalogizer.catalog.hash.HashingOptions
import com.catalogizer.catalog.metadata.MetadataExtractor
import com.catalogizer.catalog.metadata.MetadataRepository
import com.catalogizer.catalog.smb.CatalogSmbFile
import com.catalogizer.catalog.smb.SmbFileManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.io.IOException
import java.sql.PreparedStatement
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class SmbFileScanner(
    private val databaseManager: DatabaseManager,
    private val hashingEngine: HashingEngine,
    private val metadataExtractor: MetadataExtractor,
    private val duplicateDetector: DuplicateDetector,
    private val metadataRepository: MetadataRepository,
    private val smbFileManager: SmbFileManager = SmbFileManager()
) {

    private val logger = LoggerFactory.getLogger(SmbFileScanner::class.java)
    private val activeScanners = ConcurrentHashMap<String, ScanJob>()

    fun startScan(smbRootConfig: SmbRootConfig, scanType: ScanType = ScanType.FULL): Flow<ScanProgress> = flow {
        val scanId = UUID.randomUUID().toString()
        val scanJob = ScanJob(
            id = scanId,
            smbRootConfig = smbRootConfig,
            scanType = scanType,
            startTime = System.currentTimeMillis()
        )

        activeScanners[scanId] = scanJob

        try {
            logger.info("Starting ${scanType.name} scan for SMB root: ${smbRootConfig.name}")

            // Initialize scan history record
            val scanHistoryId = initializeScanHistory(smbRootConfig, scanType)
            scanJob.scanHistoryId = scanHistoryId

            emit(ScanProgress(scanId, ScanStatus.INITIALIZING, 0, 0, 0, 0))

            // Test SMB connection
            if (!smbFileManager.testConnection(smbRootConfig)) {
                throw IOException("Failed to connect to SMB root: ${smbRootConfig.name}")
            }

            emit(ScanProgress(scanId, ScanStatus.SCANNING, 0, 0, 0, 0))

            // Perform the scan
            when (scanType) {
                ScanType.FULL -> performFullScan(scanJob, smbRootConfig) { progress ->
                    emit(progress)
                }
                ScanType.INCREMENTAL -> performIncrementalScan(scanJob, smbRootConfig) { progress ->
                    emit(progress)
                }
                ScanType.VERIFICATION -> performVerificationScan(scanJob, smbRootConfig) { progress ->
                    emit(progress)
                }
            }

            // Mark scan as completed
            completeScanHistory(scanHistoryId, scanJob)
            emit(ScanProgress(scanId, ScanStatus.COMPLETED, scanJob.filesProcessed.get(), scanJob.filesAdded.get(), scanJob.filesUpdated.get(), scanJob.errorsCount.get()))

            logger.info("Completed scan for SMB root: ${smbRootConfig.name}")

        } catch (e: Exception) {
            logger.error("Scan failed for SMB root: ${smbRootConfig.name}", e)
            scanJob.scanHistoryId?.let { failScanHistory(it, e.message ?: "Unknown error") }
            emit(ScanProgress(scanId, ScanStatus.FAILED, scanJob.filesProcessed.get(), scanJob.filesAdded.get(), scanJob.filesUpdated.get(), scanJob.errorsCount.get(), e.message))
        } finally {
            activeScanners.remove(scanId)
        }
    }

    private suspend fun performFullScan(
        scanJob: ScanJob,
        smbRootConfig: SmbRootConfig,
        progressCallback: suspend (ScanProgress) -> Unit
    ) {
        val smbRootId = getSmbRootId(smbRootConfig)
        val discoveredFiles = mutableSetOf<String>()

        // Scan all files recursively
        scanDirectoryRecursive(
            path = "",
            smbRootId = smbRootId,
            smbRootConfig = smbRootConfig,
            scanJob = scanJob,
            discoveredFiles = discoveredFiles,
            progressCallback = progressCallback
        )

        // Mark files that were not discovered as deleted
        markMissingFilesAsDeleted(smbRootId, discoveredFiles)
    }

    private suspend fun performIncrementalScan(
        scanJob: ScanJob,
        smbRootConfig: SmbRootConfig,
        progressCallback: suspend (ScanProgress) -> Unit
    ) {
        val smbRootId = getSmbRootId(smbRootConfig)
        val lastScanTime = getLastScanTime(smbRootId)

        // Get recently modified files from database
        val recentFiles = getRecentlyModifiedFiles(smbRootId, lastScanTime)

        // Verify and update each file
        recentFiles.forEach { fileRecord ->
            try {
                val catalogFile = smbFileManager.getFileInfo(smbRootConfig, fileRecord.path)
                if (catalogFile != null) {
                    if (catalogFile.smbFileInfo.lastModified.time > fileRecord.modifiedAt) {
                        // File was modified, process it
                        processFileUpdate(catalogFile, smbRootId, smbRootConfig, scanJob)
                        scanJob.filesUpdated.incrementAndGet()
                    }
                } else {
                    // File no longer exists
                    markFileAsDeleted(fileRecord.id)
                    scanJob.filesUpdated.incrementAndGet()
                }

                scanJob.filesProcessed.incrementAndGet()

                if (scanJob.filesProcessed.get() % 100 == 0L) {
                    progressCallback(ScanProgress(scanJob.id, ScanStatus.SCANNING, scanJob.filesProcessed.get(), scanJob.filesAdded.get(), scanJob.filesUpdated.get(), scanJob.errorsCount.get()))
                }

            } catch (e: Exception) {
                logger.warn("Failed to verify file: ${fileRecord.path}", e)
                scanJob.errorsCount.incrementAndGet()
            }
        }
    }

    private suspend fun performVerificationScan(
        scanJob: ScanJob,
        smbRootConfig: SmbRootConfig,
        progressCallback: suspend (ScanProgress) -> Unit
    ) {
        val smbRootId = getSmbRootId(smbRootConfig)
        val allFiles = getAllFiles(smbRootId)

        // Verify each file still exists and hasn't changed
        allFiles.forEach { fileRecord ->
            try {
                val catalogFile = smbFileManager.getFileInfo(smbRootConfig, fileRecord.path)
                if (catalogFile == null) {
                    markFileAsDeleted(fileRecord.id)
                    scanJob.filesUpdated.incrementAndGet()
                } else if (catalogFile.smbFileInfo.lastModified.time > fileRecord.modifiedAt || catalogFile.smbFileInfo.size != fileRecord.size) {
                    // File changed, update it
                    processFileUpdate(catalogFile, smbRootId, smbRootConfig, scanJob)
                    scanJob.filesUpdated.incrementAndGet()
                }

                updateFileVerificationTime(fileRecord.id)
                scanJob.filesProcessed.incrementAndGet()

                if (scanJob.filesProcessed.get() % 100 == 0L) {
                    progressCallback(ScanProgress(scanJob.id, ScanStatus.SCANNING, scanJob.filesProcessed.get(), scanJob.filesAdded.get(), scanJob.filesUpdated.get(), scanJob.errorsCount.get()))
                }

            } catch (e: Exception) {
                logger.warn("Failed to verify file: ${fileRecord.path}", e)
                scanJob.errorsCount.incrementAndGet()
            }
        }
    }

    private suspend fun scanDirectoryRecursive(
        path: String,
        smbRootId: Long,
        smbRootConfig: SmbRootConfig,
        scanJob: ScanJob,
        discoveredFiles: MutableSet<String>,
        progressCallback: suspend (ScanProgress) -> Unit,
        currentDepth: Int = 0
    ) {
        if (smbRootConfig.maxDepth > 0 && currentDepth >= smbRootConfig.maxDepth) {
            return
        }

        try {
            val fileList = smbFileManager.listFiles(smbRootConfig, path)

            for (catalogFile in fileList) {
                discoveredFiles.add(catalogFile.smbFileInfo.path)

                try {
                    processFileInfo(catalogFile, smbRootId, smbRootConfig, scanJob)

                    if (catalogFile.smbFileInfo.isDirectory) {
                        // Recursively scan subdirectory
                        scanDirectoryRecursive(
                            path = catalogFile.smbFileInfo.path,
                            smbRootId = smbRootId,
                            smbRootConfig = smbRootConfig,
                            scanJob = scanJob,
                            discoveredFiles = discoveredFiles,
                            progressCallback = progressCallback,
                            currentDepth = currentDepth + 1
                        )
                    }

                    scanJob.filesProcessed.incrementAndGet()

                    if (scanJob.filesProcessed.get() % 50 == 0L) {
                        progressCallback(ScanProgress(scanJob.id, ScanStatus.SCANNING, scanJob.filesProcessed.get(), scanJob.filesAdded.get(), scanJob.filesUpdated.get(), scanJob.errorsCount.get()))
                    }

                } catch (e: Exception) {
                    logger.warn("Failed to process file: ${catalogFile.smbFileInfo.path}", e)
                    scanJob.errorsCount.incrementAndGet()
                }
            }
        } catch (e: Exception) {
            logger.warn("Failed to list directory: $path", e)
            scanJob.errorsCount.incrementAndGet()
        }
    }

    private suspend fun processFileInfo(
        catalogFile: CatalogSmbFile,
        smbRootId: Long,
        smbRootConfig: SmbRootConfig,
        scanJob: ScanJob
    ) {
        val existingFile = getExistingFile(smbRootId, catalogFile.smbFileInfo.path)

        if (existingFile == null) {
            // New file
            if (shouldProcessFile(catalogFile, smbRootConfig)) {
                val fileId = insertNewFile(catalogFile, smbRootId)

                if (!catalogFile.smbFileInfo.isDirectory && smbRootConfig.enableDuplicateDetection) {
                    processFileForDuplicates(fileId, catalogFile, smbRootConfig)
                }

                if (!catalogFile.smbFileInfo.isDirectory && smbRootConfig.enableMetadataExtraction) {
                    processFileMetadata(fileId, catalogFile, smbRootConfig)
                }

                scanJob.filesAdded.incrementAndGet()
            }
        } else {
            // Existing file, check if it needs updating
            if (catalogFile.smbFileInfo.lastModified.time > existingFile.modifiedAt || catalogFile.smbFileInfo.size != existingFile.size) {
                processFileUpdate(catalogFile, smbRootId, smbRootConfig, scanJob)
                scanJob.filesUpdated.incrementAndGet()
            }
        }
    }

    private suspend fun processFileUpdate(
        catalogFile: CatalogSmbFile,
        smbRootId: Long,
        smbRootConfig: SmbRootConfig,
        scanJob: ScanJob
    ) {
        val fileId = updateExistingFile(catalogFile, smbRootId)

        if (!catalogFile.smbFileInfo.isDirectory && smbRootConfig.enableDuplicateDetection) {
            processFileForDuplicates(fileId, catalogFile, smbRootConfig)
        }

        if (!catalogFile.smbFileInfo.isDirectory && smbRootConfig.enableMetadataExtraction) {
            processFileMetadata(fileId, catalogFile, smbRootConfig)
        }
    }

    private suspend fun processFileForDuplicates(
        fileId: Long,
        catalogFile: CatalogSmbFile,
        smbRootConfig: SmbRootConfig
    ) {
        try {
            smbFileManager.readFile(smbRootConfig, catalogFile.smbFileInfo.path).use { inputStream ->
                val hashingFuture = hashingEngine.computeHashes(
                    inputStream,
                    catalogFile.smbFileInfo.size,
                    HashingOptions()
                )

                hashingFuture.whenComplete { hashes, throwable ->
                    if (throwable != null) {
                        logger.warn("Failed to compute hashes for file: ${catalogFile.smbFileInfo.path}", throwable)
                    } else {
                        // Update file with hashes
                        updateFileHashes(fileId, hashes)

                        // Detect duplicates
                        val duplicateResult = duplicateDetector.detectDuplicates(fileId, hashes)
                        logger.debug("Duplicate detection result for ${catalogFile.smbFileInfo.path}: isDuplicate=${duplicateResult.isDuplicate}")
                    }
                }
            }
        } catch (e: Exception) {
            logger.warn("Failed to process file for duplicates: ${catalogFile.smbFileInfo.path}", e)
        }
    }

    private suspend fun processFileMetadata(
        fileId: Long,
        catalogFile: CatalogSmbFile,
        smbRootConfig: SmbRootConfig
    ) {
        try {
            smbFileManager.readFile(smbRootConfig, catalogFile.smbFileInfo.path).use { inputStream ->
                val metadataFuture = metadataExtractor.extractMetadata(inputStream, catalogFile.smbFileInfo.name)

                metadataFuture.whenComplete { metadata, throwable ->
                    if (throwable != null) {
                        logger.warn("Failed to extract metadata for file: ${catalogFile.smbFileInfo.path}", throwable)
                    } else {
                        metadataRepository.saveMetadata(fileId, metadata)
                        logger.debug("Saved metadata for file: ${catalogFile.smbFileInfo.path}")
                    }
                }
            }
        } catch (e: Exception) {
            logger.warn("Failed to process file metadata: ${catalogFile.smbFileInfo.path}", e)
        }
    }

    private fun shouldProcessFile(catalogFile: CatalogSmbFile, smbRootConfig: SmbRootConfig): Boolean {
        return catalogFile.matches(smbRootConfig.includePatterns, smbRootConfig.excludePatterns)
    }

    fun getActiveScanners(): Map<String, ScanJob> = activeScanners.toMap()

    fun stopScan(scanId: String): Boolean {
        return activeScanners[scanId]?.let { scanJob ->
            scanJob.isCancelled = true
            activeScanners.remove(scanId)
            scanJob.scanHistoryId?.let { failScanHistory(it, "Cancelled by user") }
            true
        } ?: false
    }

    // Database operations (simplified - would need full implementations)
    private fun getSmbRootId(smbRootConfig: SmbRootConfig): Long = 1L // Placeholder
    private fun getLastScanTime(smbRootId: Long): Long = 0L // Placeholder
    private fun getRecentlyModifiedFiles(smbRootId: Long, since: Long): List<FileRecord> = emptyList() // Placeholder
    private fun getAllFiles(smbRootId: Long): List<FileRecord> = emptyList() // Placeholder
    private fun getExistingFile(smbRootId: Long, path: String): FileRecord? = null // Placeholder
    private fun insertNewFile(catalogFile: CatalogSmbFile, smbRootId: Long): Long = 1L // Placeholder
    private fun updateExistingFile(catalogFile: CatalogSmbFile, smbRootId: Long): Long = 1L // Placeholder
    private fun updateFileHashes(fileId: Long, hashes: com.catalogizer.catalog.hash.FileHashes) {} // Placeholder
    private fun markFileAsDeleted(fileId: Long) {} // Placeholder
    private fun markMissingFilesAsDeleted(smbRootId: Long, discoveredFiles: Set<String>) {} // Placeholder
    private fun updateFileVerificationTime(fileId: Long) {} // Placeholder
    private fun initializeScanHistory(smbRootConfig: SmbRootConfig, scanType: ScanType): Long = 1L // Placeholder
    private fun completeScanHistory(scanHistoryId: Long, scanJob: ScanJob) {} // Placeholder
    private fun failScanHistory(scanHistoryId: Long, errorMessage: String) {} // Placeholder
}

data class ScanJob(
    val id: String,
    val smbRootConfig: SmbRootConfig,
    val scanType: ScanType,
    val startTime: Long,
    var scanHistoryId: Long? = null,
    var isCancelled: Boolean = false,
    val filesProcessed: AtomicLong = AtomicLong(0),
    val filesAdded: AtomicLong = AtomicLong(0),
    val filesUpdated: AtomicLong = AtomicLong(0),
    val errorsCount: AtomicLong = AtomicLong(0)
)

data class ScanProgress(
    val scanId: String,
    val status: ScanStatus,
    val filesProcessed: Long,
    val filesAdded: Long,
    val filesUpdated: Long,
    val errors: Long,
    val errorMessage: String? = null
)

data class FileRecord(
    val id: Long,
    val path: String,
    val size: Long,
    val modifiedAt: Long
)

enum class ScanType {
    FULL,
    INCREMENTAL,
    VERIFICATION
}

enum class ScanStatus {
    INITIALIZING,
    SCANNING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED
}