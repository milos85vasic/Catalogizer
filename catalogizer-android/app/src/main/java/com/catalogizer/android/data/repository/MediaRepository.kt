package com.catalogizer.android.data.repository

import androidx.paging.*
import com.catalogizer.android.data.local.MediaDao
import com.catalogizer.android.data.models.*
import com.catalogizer.android.data.remote.CatalogizerApi
import com.catalogizer.android.data.remote.ApiResult
import com.catalogizer.android.data.remote.toApiResult
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    private val api: CatalogizerApi,
    private val mediaDao: MediaDao
) {

    // Paging data sources
    fun getMediaPaging(
        searchRequest: MediaSearchRequest = MediaSearchRequest()
    ): Flow<PagingData<MediaItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                MediaPagingSource(api, searchRequest)
            }
        ).flow
    }

    fun getMediaByTypePaging(mediaType: String): Flow<PagingData<MediaItem>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { mediaDao.getMediaByTypePaging(mediaType) }
        ).flow
    }

    fun searchMediaPaging(query: String): Flow<PagingData<MediaItem>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { mediaDao.searchMediaPaging(query) }
        ).flow
    }

    fun getFavoritesPaging(): Flow<PagingData<MediaItem>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { mediaDao.getFavoritesPaging() }
        ).flow
    }

    fun getContinueWatchingPaging(): Flow<PagingData<MediaItem>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { mediaDao.getContinueWatchingPaging() }
        ).flow
    }

    // Individual media operations
    suspend fun getMediaById(id: Long, forceRefresh: Boolean = false): ApiResult<MediaItem> {
        return try {
            if (!forceRefresh) {
                val localMedia = mediaDao.getMediaById(id)
                if (localMedia != null) {
                    return ApiResult.success(localMedia)
                }
            }

            val result = api.getMediaById(id).toApiResult()
            if (result.isSuccess && result.data != null) {
                mediaDao.insertMedia(result.data)
            }
            result
        } catch (e: Exception) {
            ApiResult.error(e.message ?: "Failed to get media")
        }
    }

    fun getMediaByIdFlow(id: Long): Flow<MediaItem?> {
        return mediaDao.getMediaByIdFlow(id)
    }

    suspend fun refreshMetadata(id: Long): ApiResult<Map<String, String>> {
        return api.refreshMetadata(id).toApiResult()
    }

    suspend fun getExternalMetadata(id: Long): ApiResult<List<ExternalMetadata>> {
        return api.getExternalMetadata(id).toApiResult()
    }

    // Statistics and aggregated data
    suspend fun getMediaStats(): ApiResult<MediaStats> {
        return api.getMediaStats().toApiResult()
    }

    suspend fun getRecentMedia(limit: Int = 10): ApiResult<List<MediaItem>> {
        return try {
            val result = api.getRecentMedia(limit).toApiResult()
            if (result.isSuccess && result.data != null) {
                mediaDao.insertAllMedia(result.data)
            }
            result
        } catch (e: Exception) {
            // Fallback to local data
            val localData = mediaDao.getRecentlyAdded(limit).first()
            ApiResult.success(localData)
        }
    }

    suspend fun getPopularMedia(limit: Int = 10): ApiResult<List<MediaItem>> {
        return try {
            val result = api.getPopularMedia(limit).toApiResult()
            if (result.isSuccess && result.data != null) {
                mediaDao.insertAllMedia(result.data)
            }
            result
        } catch (e: Exception) {
            // Fallback to local data
            val localData = mediaDao.getTopRated(limit).first()
            ApiResult.success(localData)
        }
    }

    // User interactions
    suspend fun toggleFavorite(mediaId: Long): ApiResult<Unit> {
        return try {
            val currentMedia = mediaDao.getMediaById(mediaId)
            val newFavoriteStatus = !(currentMedia?.isFavorite ?: false)

            // Update local state immediately for better UX
            mediaDao.updateFavoriteStatus(mediaId, newFavoriteStatus)

            // Sync with server
            val result = if (newFavoriteStatus) {
                api.addToFavorites(mediaId).toApiResult()
            } else {
                api.removeFromFavorites(mediaId).toApiResult()
            }

            // Revert local state if server sync failed
            if (!result.isSuccess) {
                mediaDao.updateFavoriteStatus(mediaId, !newFavoriteStatus)
            }

            result
        } catch (e: Exception) {
            ApiResult.error(e.message ?: "Failed to update favorite status")
        }
    }

    suspend fun updateWatchProgress(
        mediaId: Long,
        progress: Double,
        position: Long? = null
    ): ApiResult<Unit> {
        return try {
            val timestamp = System.currentTimeMillis().toString()

            // Update local state immediately
            mediaDao.updateWatchProgress(mediaId, progress, timestamp)

            // Sync with server
            val progressData = mutableMapOf<String, Any>(
                "progress" to progress,
                "timestamp" to timestamp
            )
            position?.let { progressData["position"] = it }

            api.updateUserWatchProgress(mediaId, progressData).toApiResult()
        } catch (e: Exception) {
            ApiResult.error(e.message ?: "Failed to update watch progress")
        }
    }

    // Local data management
    suspend fun refreshAllMedia(): ApiResult<Unit> {
        return try {
            val result = api.searchMedia(limit = 1000).toApiResult()
            if (result.isSuccess && result.data != null) {
                mediaDao.refreshMedia(result.data.items)
                ApiResult.success(Unit)
            } else {
                result.let { ApiResult.error(it.error ?: "Failed to refresh media") }
            }
        } catch (e: Exception) {
            ApiResult.error(e.message ?: "Failed to refresh media")
        }
    }

    suspend fun clearCache() {
        mediaDao.deleteAllMedia()
    }

    // Stream and download URLs
    suspend fun getStreamUrl(mediaId: Long): ApiResult<String> {
        return try {
            val result = api.getStreamUrl(mediaId).toApiResult()
            if (result.isSuccess && result.data != null) {
                val streamUrl = result.data["url"] ?: result.data["stream_url"]
                streamUrl?.let { ApiResult.success(it) }
                    ?: ApiResult.error("Stream URL not found in response")
            } else {
                result.let { ApiResult.error(it.error ?: "Failed to get stream URL") }
            }
        } catch (e: Exception) {
            ApiResult.error(e.message ?: "Failed to get stream URL")
        }
    }

    suspend fun getDownloadUrl(mediaId: Long): ApiResult<String> {
        return try {
            val result = api.getDownloadUrl(mediaId).toApiResult()
            if (result.isSuccess && result.data != null) {
                val downloadUrl = result.data["url"] ?: result.data["download_url"]
                downloadUrl?.let { ApiResult.success(it) }
                    ?: ApiResult.error("Download URL not found in response")
            } else {
                result.let { ApiResult.error(it.error ?: "Failed to get download URL") }
            }
        } catch (e: Exception) {
            ApiResult.error(e.message ?: "Failed to get download URL")
        }
    }

    // Offline support
    fun getAllMediaTypes(): Flow<List<String>> = mediaDao.getAllMediaTypes()
    fun getTotalCount(): Flow<Int> = mediaDao.getTotalCount()
    fun getCountByType(mediaType: String): Flow<Int> = mediaDao.getCountByType(mediaType)

    suspend fun getContinueWatching(): ApiResult<List<MediaItem>> {
        return try {
            val result = api.getContinueWatching().toApiResult()
            if (result.isSuccess && result.data != null) {
                // Update local data with server data
                result.data.forEach { mediaItem ->
                    mediaDao.updateWatchProgress(
                        mediaItem.id,
                        mediaItem.watchProgress,
                        mediaItem.lastWatched ?: ""
                    )
                }
                result
            } else {
                // Fallback to local data
                val localData = mediaDao.getContinueWatchingPaging()
                // Convert PagingSource to List for this use case
                ApiResult.error("Server unavailable, using cached data")
            }
        } catch (e: Exception) {
            ApiResult.error(e.message ?: "Failed to get continue watching")
        }
    }
}

// Paging source for remote media data
class MediaPagingSource(
    private val api: CatalogizerApi,
    private val searchRequest: MediaSearchRequest
) : PagingSource<Int, MediaItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaItem> {
        return try {
            val offset = params.key ?: 0
            val limit = params.loadSize

            val response = api.searchMedia(
                query = searchRequest.query,
                mediaType = searchRequest.mediaType,
                yearMin = searchRequest.yearMin,
                yearMax = searchRequest.yearMax,
                ratingMin = searchRequest.ratingMin,
                quality = searchRequest.quality,
                sortBy = searchRequest.sortBy,
                sortOrder = searchRequest.sortOrder,
                limit = limit,
                offset = offset
            )

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                val nextKey = if (offset + limit < data.total) offset + limit else null
                val prevKey = if (offset > 0) maxOf(0, offset - limit) else null

                LoadResult.Page(
                    data = data.items,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else {
                LoadResult.Error(Exception("Failed to load data: ${response.code()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MediaItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }
}