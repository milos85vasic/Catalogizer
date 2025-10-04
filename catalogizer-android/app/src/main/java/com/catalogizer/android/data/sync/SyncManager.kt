package com.catalogizer.android.data.sync

import android.content.Context
import androidx.work.*
import androidx.work.ExistingPeriodicWorkPolicy
import com.catalogizer.android.data.local.CatalogizerDatabase
import com.catalogizer.android.data.remote.CatalogizerApi
import com.catalogizer.android.data.repository.AuthRepository
import com.catalogizer.android.data.repository.MediaRepository
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class SyncStatus(
    val isRunning: Boolean = false,
    val lastSyncTime: Long? = null,
    val lastSyncResult: SyncResult? = null,
    val pendingOperations: Int = 0
)

@Serializable
data class SyncResult(
    val success: Boolean,
    val timestamp: Long,
    val syncedItems: Int = 0,
    val failedItems: Int = 0,
    val errorMessage: String? = null
)

@Singleton
class SyncManager @Inject constructor(
    private val database: CatalogizerDatabase,
    private val api: CatalogizerApi,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository,
    private val context: Context
) {

    private val _syncStatus = MutableStateFlow(SyncStatus())
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    companion object {
        private const val SYNC_WORK_NAME = "catalogizer_sync"
        private const val BACKGROUND_SYNC_INTERVAL_HOURS = 6L
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    fun startPeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            BACKGROUND_SYNC_INTERVAL_HOURS,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    fun stopPeriodicSync() {
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
    }

    suspend fun performManualSync(): SyncResult {
        if (_syncStatus.value.isRunning) {
            return SyncResult(
                success = false,
                timestamp = System.currentTimeMillis(),
                errorMessage = "Sync already in progress"
            )
        }

        _syncStatus.update { it.copy(isRunning = true) }

        return try {
            val result = performSyncInternal()

            _syncStatus.update {
                it.copy(
                    isRunning = false,
                    lastSyncTime = result.timestamp,
                    lastSyncResult = result
                )
            }

            result
        } catch (e: Exception) {
            val result = SyncResult(
                success = false,
                timestamp = System.currentTimeMillis(),
                errorMessage = e.message ?: "Unknown sync error"
            )

            _syncStatus.update {
                it.copy(
                    isRunning = false,
                    lastSyncTime = result.timestamp,
                    lastSyncResult = result
                )
            }

            result
        }
    }

    private suspend fun performSyncInternal(): SyncResult {
        // Check authentication
        if (!authRepository.isTokenValid()) {
            return SyncResult(
                success = false,
                timestamp = System.currentTimeMillis(),
                errorMessage = "Not authenticated"
            )
        }

        var syncedItems = 0
        var failedItems = 0

        try {
            // 1. Sync pending operations (uploads, favorites, progress updates)
            val pendingOps = database.syncOperationDao().getPendingOperations()
            _syncStatus.update { it.copy(pendingOperations = pendingOps.size) }

            for (operation in pendingOps) {
                try {
                    when (operation.type) {
                        SyncOperationType.UPDATE_PROGRESS -> {
                            syncWatchProgress(operation)
                        }
                        SyncOperationType.TOGGLE_FAVORITE -> {
                            syncFavoriteStatus(operation)
                        }
                        SyncOperationType.UPLOAD_RATING -> {
                            syncRating(operation)
                        }
                    }

                    // Mark operation as completed
                    database.syncOperationDao().deleteOperation(operation.id)
                    syncedItems++
                } catch (e: Exception) {
                    // Update retry count
                    if (operation.retryCount < MAX_RETRY_ATTEMPTS) {
                        database.syncOperationDao().updateRetryCount(
                            operation.id,
                            operation.retryCount + 1
                        )
                    } else {
                        // Max retries reached, delete operation
                        database.syncOperationDao().deleteOperation(operation.id)
                    }
                    failedItems++
                }
            }

            // 2. Download media updates from server
            val lastSyncTime = _syncStatus.value.lastSyncTime ?: 0L
            val updatedMedia = api.getUpdatedMedia(lastSyncTime).data ?: emptyList()

            // Update local database with server changes
            for (mediaItem in updatedMedia) {
                try {
                    database.mediaDao().insertOrUpdate(mediaItem)
                    syncedItems++
                } catch (e: Exception) {
                    failedItems++
                }
            }

            // 3. Sync user preferences and settings
            syncUserPreferences()

            return SyncResult(
                success = true,
                timestamp = System.currentTimeMillis(),
                syncedItems = syncedItems,
                failedItems = failedItems
            )

        } catch (e: Exception) {
            return SyncResult(
                success = false,
                timestamp = System.currentTimeMillis(),
                syncedItems = syncedItems,
                failedItems = failedItems,
                errorMessage = e.message
            )
        }
    }

    private suspend fun syncWatchProgress(operation: SyncOperation) {
        val progressData = operation.data?.let {
            kotlinx.serialization.json.Json.decodeFromString<WatchProgressData>(it)
        } ?: return

        api.updateWatchProgress(
            progressData.mediaId,
            progressData.progress,
            progressData.timestamp
        )
    }

    private suspend fun syncFavoriteStatus(operation: SyncOperation) {
        val favoriteData = operation.data?.let {
            kotlinx.serialization.json.Json.decodeFromString<FavoriteData>(it)
        } ?: return

        api.setFavoriteStatus(favoriteData.mediaId, favoriteData.isFavorite)
    }

    private suspend fun syncRating(operation: SyncOperation) {
        val ratingData = operation.data?.let {
            kotlinx.serialization.json.Json.decodeFromString<RatingData>(it)
        } ?: return

        api.rateMedia(ratingData.mediaId, ratingData.rating)
    }

    private suspend fun syncUserPreferences() {
        try {
            val serverPrefs = api.getUserPreferences().data
            serverPrefs?.let {
                // Update local preferences with server data
                // This would involve updating SharedPreferences or DataStore
            }
        } catch (e: Exception) {
            // Ignore preference sync errors for now
        }
    }

    // Public methods for queueing offline operations
    suspend fun queueWatchProgressUpdate(mediaId: Long, progress: Double, timestamp: Long) {
        val data = WatchProgressData(mediaId, progress, timestamp)
        val operation = SyncOperation(
            type = SyncOperationType.UPDATE_PROGRESS,
            mediaId = mediaId,
            data = kotlinx.serialization.json.Json.encodeToString(data),
            timestamp = System.currentTimeMillis()
        )

        database.syncOperationDao().insertOperation(operation)
        updatePendingOperationsCount()
    }

    suspend fun queueFavoriteToggle(mediaId: Long, isFavorite: Boolean) {
        val data = FavoriteData(mediaId, isFavorite)
        val operation = SyncOperation(
            type = SyncOperationType.TOGGLE_FAVORITE,
            mediaId = mediaId,
            data = kotlinx.serialization.json.Json.encodeToString(data),
            timestamp = System.currentTimeMillis()
        )

        database.syncOperationDao().insertOperation(operation)
        updatePendingOperationsCount()
    }

    suspend fun queueRatingUpdate(mediaId: Long, rating: Double) {
        val data = RatingData(mediaId, rating)
        val operation = SyncOperation(
            type = SyncOperationType.UPLOAD_RATING,
            mediaId = mediaId,
            data = kotlinx.serialization.json.Json.encodeToString(data),
            timestamp = System.currentTimeMillis()
        )

        database.syncOperationDao().insertOperation(operation)
        updatePendingOperationsCount()
    }

    private suspend fun updatePendingOperationsCount() {
        val count = database.syncOperationDao().getPendingOperationsCount()
        _syncStatus.update { it.copy(pendingOperations = count) }
    }

    suspend fun clearFailedOperations() {
        database.syncOperationDao().deleteFailedOperations(MAX_RETRY_ATTEMPTS)
        updatePendingOperationsCount()
    }

    suspend fun retryFailedOperations() {
        database.syncOperationDao().resetRetryCount()
        updatePendingOperationsCount()
    }
}

@Serializable
data class WatchProgressData(
    val mediaId: Long,
    val progress: Double,
    val timestamp: Long
)

@Serializable
data class FavoriteData(
    val mediaId: Long,
    val isFavorite: Boolean
)

@Serializable
data class RatingData(
    val mediaId: Long,
    val rating: Double
)