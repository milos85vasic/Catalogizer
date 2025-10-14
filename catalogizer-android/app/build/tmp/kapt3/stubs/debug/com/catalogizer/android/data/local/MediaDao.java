package com.catalogizer.android.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001c\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\u0006\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\u000f\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00070\u0013H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00070\u0015H\'J\u0014\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00130\u0018H\'J\u0018\u0010\u0019\u001a\u0004\u0018\u00010\u00072\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001c\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00070\u00132\u0006\u0010\u001b\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u000e\u0010\u001c\u001a\u00020\u0016H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00070\u0015H\'J\u0016\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00160\u00182\u0006\u0010\u001f\u001a\u00020\u0010H\'J\u0014\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00070\u0015H\'J\u0014\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00070\u0015H\'J\u0018\u0010\"\u001a\u0004\u0018\u00010\u00072\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010#\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u00182\u0006\u0010\n\u001a\u00020\u000bH\'J\u001c\u0010$\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00070\u00152\u0006\u0010\u001f\u001a\u00020\u0010H\'J\u001e\u0010%\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00130\u00182\b\b\u0002\u0010&\u001a\u00020\u0016H\'J\u001e\u0010\'\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00130\u00182\b\b\u0002\u0010&\u001a\u00020\u0016H\'J\u000e\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00160\u0018H\'J\u0010\u0010)\u001a\u0004\u0018\u00010\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010*\u001a\u00020\u00032\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00070\u0013H\u00a7@\u00a2\u0006\u0002\u0010,J\u0016\u0010-\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010.\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u0097@\u00a2\u0006\u0002\u0010\bJ\u001c\u0010/\u001a\u00020\u00032\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00070\u0013H\u0097@\u00a2\u0006\u0002\u0010,J\u001c\u00100\u001a\b\u0012\u0004\u0012\u00020\u00070\u00132\u0006\u00101\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u001c\u00102\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00070\u00152\u0006\u00101\u001a\u00020\u0010H\'J\u001e\u00103\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u00104\u001a\u000205H\u00a7@\u00a2\u0006\u0002\u00106J\u001e\u00107\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u00108\u001a\u000205H\u00a7@\u00a2\u0006\u0002\u00106J\u0016\u00109\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u001e\u0010:\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010;\u001a\u00020<H\u00a7@\u00a2\u0006\u0002\u0010=J&\u0010>\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010?\u001a\u00020<2\u0006\u0010@\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010A\u00a8\u0006B"}, d2 = {"Lcom/catalogizer/android/data/local/MediaDao;", "", "deleteAllMedia", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteMedia", "mediaItem", "Lcom/catalogizer/android/data/models/MediaItem;", "(Lcom/catalogizer/android/data/models/MediaItem;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteMediaById", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteOldCachedItems", "timestamp", "deleteOldMedia", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllCached", "", "getAllMediaPaging", "Landroidx/paging/PagingSource;", "", "getAllMediaTypes", "Lkotlinx/coroutines/flow/Flow;", "getById", "getByType", "type", "getCachedItemsCount", "getContinueWatchingPaging", "getCountByType", "mediaType", "getDownloadedPaging", "getFavoritesPaging", "getMediaById", "getMediaByIdFlow", "getMediaByTypePaging", "getRecentlyAdded", "limit", "getTopRated", "getTotalCount", "getTotalDownloadSize", "insertAllMedia", "mediaItems", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertMedia", "insertOrUpdate", "refreshMedia", "searchCached", "query", "searchMediaPaging", "updateDownloadStatus", "isDownloaded", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateFavoriteStatus", "isFavorite", "updateMedia", "updateRating", "rating", "", "(JDLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateWatchProgress", "progress", "lastWatched", "(JDLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface MediaDao {
    
    @androidx.room.Query(value = "SELECT * FROM media_items ORDER BY updated_at DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.paging.PagingSource<java.lang.Integer, com.catalogizer.android.data.models.MediaItem> getAllMediaPaging();
    
    @androidx.room.Query(value = "SELECT * FROM media_items WHERE media_type = :mediaType ORDER BY updated_at DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.paging.PagingSource<java.lang.Integer, com.catalogizer.android.data.models.MediaItem> getMediaByTypePaging(@org.jetbrains.annotations.NotNull()
    java.lang.String mediaType);
    
    @androidx.room.Query(value = "\n        SELECT * FROM media_items\n        WHERE title LIKE \'%\' || :query || \'%\'\n        OR description LIKE \'%\' || :query || \'%\'\n        ORDER BY updated_at DESC\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.paging.PagingSource<java.lang.Integer, com.catalogizer.android.data.models.MediaItem> searchMediaPaging(@org.jetbrains.annotations.NotNull()
    java.lang.String query);
    
    @androidx.room.Query(value = "SELECT * FROM media_items WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMediaById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.models.MediaItem> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM media_items WHERE id = :id")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.catalogizer.android.data.models.MediaItem> getMediaByIdFlow(long id);
    
    @androidx.room.Query(value = "SELECT * FROM media_items WHERE is_favorite = 1 ORDER BY updated_at DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.paging.PagingSource<java.lang.Integer, com.catalogizer.android.data.models.MediaItem> getFavoritesPaging();
    
    @androidx.room.Query(value = "SELECT * FROM media_items WHERE is_downloaded = 1 ORDER BY updated_at DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.paging.PagingSource<java.lang.Integer, com.catalogizer.android.data.models.MediaItem> getDownloadedPaging();
    
    @androidx.room.Query(value = "SELECT * FROM media_items WHERE watch_progress > 0 AND watch_progress < 1 ORDER BY last_watched DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.paging.PagingSource<java.lang.Integer, com.catalogizer.android.data.models.MediaItem> getContinueWatchingPaging();
    
    @androidx.room.Query(value = "SELECT * FROM media_items ORDER BY created_at DESC LIMIT :limit")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.catalogizer.android.data.models.MediaItem>> getRecentlyAdded(int limit);
    
    @androidx.room.Query(value = "SELECT * FROM media_items WHERE rating IS NOT NULL ORDER BY rating DESC LIMIT :limit")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.catalogizer.android.data.models.MediaItem>> getTopRated(int limit);
    
    @androidx.room.Query(value = "SELECT DISTINCT media_type FROM media_items ORDER BY media_type")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<java.lang.String>> getAllMediaTypes();
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM media_items")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Integer> getTotalCount();
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM media_items WHERE media_type = :mediaType")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Integer> getCountByType(@org.jetbrains.annotations.NotNull()
    java.lang.String mediaType);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertMedia(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.models.MediaItem mediaItem, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertAllMedia(@org.jetbrains.annotations.NotNull()
    java.util.List<com.catalogizer.android.data.models.MediaItem> mediaItems, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateMedia(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.models.MediaItem mediaItem, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE media_items SET is_favorite = :isFavorite WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateFavoriteStatus(long id, boolean isFavorite, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE media_items SET watch_progress = :progress, last_watched = :lastWatched WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateWatchProgress(long id, double progress, @org.jetbrains.annotations.NotNull()
    java.lang.String lastWatched, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE media_items SET is_downloaded = :isDownloaded WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateDownloadStatus(long id, boolean isDownloaded, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteMedia(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.models.MediaItem mediaItem, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM media_items WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteMediaById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM media_items")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllMedia(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM media_items WHERE updated_at < :timestamp")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOldMedia(@org.jetbrains.annotations.NotNull()
    java.lang.String timestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM media_items ORDER BY updated_at DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllCached(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.catalogizer.android.data.models.MediaItem>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM media_items WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.models.MediaItem> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM media_items WHERE media_type = :type ORDER BY updated_at DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getByType(@org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.catalogizer.android.data.models.MediaItem>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM media_items WHERE title LIKE \'%\' || :query || \'%\' OR description LIKE \'%\' || :query || \'%\' ORDER BY updated_at DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchCached(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.catalogizer.android.data.models.MediaItem>> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM media_items")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCachedItemsCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "UPDATE media_items SET rating = :rating WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateRating(long id, double rating, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT SUM(file_size) FROM media_items WHERE is_downloaded = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTotalDownloadSize(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "DELETE FROM media_items WHERE updated_at < :timestamp")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOldCachedItems(long timestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Transaction()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object refreshMedia(@org.jetbrains.annotations.NotNull()
    java.util.List<com.catalogizer.android.data.models.MediaItem> mediaItems, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Transaction()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertOrUpdate(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.models.MediaItem mediaItem, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
        
        @androidx.room.Transaction()
        @org.jetbrains.annotations.Nullable()
        public static java.lang.Object refreshMedia(@org.jetbrains.annotations.NotNull()
        com.catalogizer.android.data.local.MediaDao $this, @org.jetbrains.annotations.NotNull()
        java.util.List<com.catalogizer.android.data.models.MediaItem> mediaItems, @org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
            return null;
        }
        
        @androidx.room.Transaction()
        @org.jetbrains.annotations.Nullable()
        public static java.lang.Object insertOrUpdate(@org.jetbrains.annotations.NotNull()
        com.catalogizer.android.data.local.MediaDao $this, @org.jetbrains.annotations.NotNull()
        com.catalogizer.android.data.models.MediaItem mediaItem, @org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
            return null;
        }
    }
}