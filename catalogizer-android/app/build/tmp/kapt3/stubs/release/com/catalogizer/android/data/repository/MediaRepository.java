package com.catalogizer.android.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000|\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010$\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u0012\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f0\u000bJ\u001a\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\f0\u000fH\u0086@\u00a2\u0006\u0002\u0010\tJ\u0012\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00120\u000bJ\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u000b2\u0006\u0010\u0015\u001a\u00020\rJ\u001c\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\r0\u000f2\u0006\u0010\u0017\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019J\"\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\f0\u000f2\u0006\u0010\u001c\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u0012\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00120\u000bJ&\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u0010\u001c\u001a\u00020\u00182\b\b\u0002\u0010\u001f\u001a\u00020 H\u0086@\u00a2\u0006\u0002\u0010!J\u0016\u0010\"\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00100\u000b2\u0006\u0010\u001c\u001a\u00020\u0018J\u001a\u0010#\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00120\u000b2\u0006\u0010\u0015\u001a\u00020\rJ\u001c\u0010$\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00120\u000b2\b\b\u0002\u0010%\u001a\u00020&J\u0014\u0010\'\u001a\b\u0012\u0004\u0012\u00020(0\u000fH\u0086@\u00a2\u0006\u0002\u0010\tJ$\u0010)\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\f0\u000f2\b\b\u0002\u0010*\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010+J$\u0010,\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\f0\u000f2\b\b\u0002\u0010*\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010+J\u001c\u0010-\u001a\b\u0012\u0004\u0012\u00020\r0\u000f2\u0006\u0010\u0017\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019J\f\u0010.\u001a\b\u0012\u0004\u0012\u00020\u00140\u000bJ\u0014\u0010/\u001a\b\u0012\u0004\u0012\u00020\b0\u000fH\u0086@\u00a2\u0006\u0002\u0010\tJ(\u00100\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r010\u000f2\u0006\u0010\u001c\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u001a\u00102\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00120\u000b2\u0006\u00103\u001a\u00020\rJ\u001c\u00104\u001a\b\u0012\u0004\u0012\u00020\b0\u000f2\u0006\u0010\u0017\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019J0\u00105\u001a\b\u0012\u0004\u0012\u00020\b0\u000f2\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u00106\u001a\u0002072\n\b\u0002\u00108\u001a\u0004\u0018\u00010\u0018H\u0086@\u00a2\u0006\u0002\u00109R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006:"}, d2 = {"Lcom/catalogizer/android/data/repository/MediaRepository;", "", "api", "Lcom/catalogizer/android/data/remote/CatalogizerApi;", "mediaDao", "Lcom/catalogizer/android/data/local/MediaDao;", "(Lcom/catalogizer/android/data/remote/CatalogizerApi;Lcom/catalogizer/android/data/local/MediaDao;)V", "clearCache", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllMediaTypes", "Lkotlinx/coroutines/flow/Flow;", "", "", "getContinueWatching", "Lcom/catalogizer/android/data/remote/ApiResult;", "Lcom/catalogizer/android/data/models/MediaItem;", "getContinueWatchingPaging", "Landroidx/paging/PagingData;", "getCountByType", "", "mediaType", "getDownloadUrl", "mediaId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getExternalMetadata", "Lcom/catalogizer/android/data/models/ExternalMetadata;", "id", "getFavoritesPaging", "getMediaById", "forceRefresh", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMediaByIdFlow", "getMediaByTypePaging", "getMediaPaging", "searchRequest", "Lcom/catalogizer/android/data/models/MediaSearchRequest;", "getMediaStats", "Lcom/catalogizer/android/data/models/MediaStats;", "getPopularMedia", "limit", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getRecentMedia", "getStreamUrl", "getTotalCount", "refreshAllMedia", "refreshMetadata", "", "searchMediaPaging", "query", "toggleFavorite", "updateWatchProgress", "progress", "", "position", "(JDLjava/lang/Long;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public final class MediaRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.catalogizer.android.data.remote.CatalogizerApi api = null;
    @org.jetbrains.annotations.NotNull()
    private final com.catalogizer.android.data.local.MediaDao mediaDao = null;
    
    @javax.inject.Inject()
    public MediaRepository(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.remote.CatalogizerApi api, @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.local.MediaDao mediaDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<com.catalogizer.android.data.models.MediaItem>> getMediaPaging(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.models.MediaSearchRequest searchRequest) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<com.catalogizer.android.data.models.MediaItem>> getMediaByTypePaging(@org.jetbrains.annotations.NotNull()
    java.lang.String mediaType) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<com.catalogizer.android.data.models.MediaItem>> searchMediaPaging(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<com.catalogizer.android.data.models.MediaItem>> getFavoritesPaging() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<com.catalogizer.android.data.models.MediaItem>> getContinueWatchingPaging() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getMediaById(long id, boolean forceRefresh, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<com.catalogizer.android.data.models.MediaItem>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.catalogizer.android.data.models.MediaItem> getMediaByIdFlow(long id) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object refreshMetadata(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<java.util.Map<java.lang.String, java.lang.String>>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getExternalMetadata(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<java.util.List<com.catalogizer.android.data.models.ExternalMetadata>>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getMediaStats(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<com.catalogizer.android.data.models.MediaStats>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getRecentMedia(int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<java.util.List<com.catalogizer.android.data.models.MediaItem>>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getPopularMedia(int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<java.util.List<com.catalogizer.android.data.models.MediaItem>>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object toggleFavorite(long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<kotlin.Unit>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateWatchProgress(long mediaId, double progress, @org.jetbrains.annotations.Nullable()
    java.lang.Long position, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<kotlin.Unit>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object refreshAllMedia(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<kotlin.Unit>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearCache(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getStreamUrl(long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<java.lang.String>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getDownloadUrl(long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<java.lang.String>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<java.lang.String>> getAllMediaTypes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Integer> getTotalCount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Integer> getCountByType(@org.jetbrains.annotations.NotNull()
    java.lang.String mediaType) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getContinueWatching(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<java.util.List<com.catalogizer.android.data.models.MediaItem>>> $completion) {
        return null;
    }
}