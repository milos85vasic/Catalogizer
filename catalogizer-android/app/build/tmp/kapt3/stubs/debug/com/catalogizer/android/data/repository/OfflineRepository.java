package com.catalogizer.android.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0006\n\u0002\b\u0011\u0018\u0000 N2\u00020\u0001:\u0001NB\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u001c\u0010\u001c\u001a\u00020\u001d2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020 0\u001fH\u0086@\u00a2\u0006\u0002\u0010!J$\u0010\"\u001a\u00020\u001d2\u0006\u0010#\u001a\u00020\u000e2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020 0\u001fH\u0086@\u00a2\u0006\u0002\u0010%J\u000e\u0010&\u001a\u00020\u001dH\u0086@\u00a2\u0006\u0002\u0010\'J\u000e\u0010(\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\'J\u000e\u0010)\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\'J\u0018\u0010*\u001a\u0004\u0018\u00010 2\u0006\u0010+\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010,J\u001c\u0010-\u001a\b\u0012\u0004\u0012\u00020 0\u001f2\u0006\u0010.\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010/J\u0014\u00100\u001a\b\u0012\u0004\u0012\u00020 0\u001fH\u0086@\u00a2\u0006\u0002\u0010\'J\u0014\u00101\u001a\b\u0012\u0004\u0012\u00020\u000e0\u001fH\u0086@\u00a2\u0006\u0002\u0010\'J\u000e\u00102\u001a\u000203H\u0086@\u00a2\u0006\u0002\u0010\'J\u000e\u00104\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\'J\u0016\u00105\u001a\u00020\u00122\u0006\u00106\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010/J\u0016\u00107\u001a\u00020\u00122\u0006\u00108\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010,J\u000e\u00109\u001a\u00020\u001dH\u0086@\u00a2\u0006\u0002\u0010\'J\u000e\u0010:\u001a\u00020\u001dH\u0086@\u00a2\u0006\u0002\u0010\'J\u001e\u0010;\u001a\u00020\u001d2\u0006\u0010<\u001a\u00020\u00182\u0006\u0010=\u001a\u00020>H\u0086@\u00a2\u0006\u0002\u0010?J\u001c\u0010@\u001a\b\u0012\u0004\u0012\u00020 0\u001f2\u0006\u0010#\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010/J\u0016\u0010A\u001a\u00020\u001d2\u0006\u0010B\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010CJ\u0016\u0010D\u001a\u00020\u001d2\u0006\u0010E\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010/J\u0016\u0010F\u001a\u00020\u001d2\u0006\u0010B\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010CJ\u0016\u0010G\u001a\u00020\u001d2\u0006\u0010H\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010,J\u0016\u0010I\u001a\u00020\u001d2\u0006\u0010J\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010CJ\u0016\u0010K\u001a\u00020\u00122\u0006\u0010<\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010,J\u001e\u0010L\u001a\u00020\u001d2\u0006\u0010<\u001a\u00020\u00182\u0006\u0010M\u001a\u00020>H\u0086@\u00a2\u0006\u0002\u0010?R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00120\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0010R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0010R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006O"}, d2 = {"Lcom/catalogizer/android/data/repository/OfflineRepository;", "", "database", "Lcom/catalogizer/android/data/local/CatalogizerDatabase;", "syncManager", "Lcom/catalogizer/android/data/sync/SyncManager;", "context", "Landroid/content/Context;", "(Lcom/catalogizer/android/data/local/CatalogizerDatabase;Lcom/catalogizer/android/data/sync/SyncManager;Landroid/content/Context;)V", "dataStore", "Landroidx/datastore/core/DataStore;", "Landroidx/datastore/preferences/core/Preferences;", "downloadQuality", "Lkotlinx/coroutines/flow/Flow;", "", "getDownloadQuality", "()Lkotlinx/coroutines/flow/Flow;", "isAutoDownloadEnabled", "", "isOfflineModeEnabled", "isWifiOnlyEnabled", "json", "Lkotlinx/serialization/json/Json;", "storageLimitMB", "", "getStorageLimitMB", "syncOperationDao", "Lcom/catalogizer/android/data/local/SyncOperationDao;", "cacheMediaItems", "", "items", "", "Lcom/catalogizer/android/data/models/MediaItem;", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "cacheSearchQuery", "query", "results", "(Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "cleanupOldCache", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "exportOfflineData", "getAvailableStorageBytes", "getCachedMediaById", "id", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCachedMediaByType", "type", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCachedMediaItems", "getCachedSearchQueries", "getOfflineStats", "Lcom/catalogizer/android/data/repository/OfflineStats;", "getUsedStorageBytes", "importOfflineData", "exportedData", "isStorageAvailable", "requiredBytes", "onNetworkAvailable", "onNetworkUnavailable", "rateMediaOffline", "mediaId", "rating", "", "(JDLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchCachedMedia", "setAutoDownload", "enabled", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setDownloadQuality", "quality", "setOfflineMode", "setStorageLimit", "limitMB", "setWifiOnly", "wifiOnly", "toggleFavoriteOffline", "updateWatchProgressOffline", "progress", "Companion", "app_debug"})
public final class OfflineRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.catalogizer.android.data.local.CatalogizerDatabase database = null;
    @org.jetbrains.annotations.NotNull()
    private final com.catalogizer.android.data.sync.SyncManager syncManager = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.catalogizer.android.data.local.SyncOperationDao syncOperationDao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.serialization.json.Json json = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> dataStore = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> OFFLINE_MODE_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> AUTO_DOWNLOAD_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> DOWNLOAD_QUALITY_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> WIFI_ONLY_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Long> STORAGE_LIMIT_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> CACHED_SEARCH_QUERIES_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.Boolean> isOfflineModeEnabled = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.Boolean> isAutoDownloadEnabled = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.String> downloadQuality = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.Boolean> isWifiOnlyEnabled = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.Long> storageLimitMB = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.catalogizer.android.data.repository.OfflineRepository.Companion Companion = null;
    
    public OfflineRepository(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.local.CatalogizerDatabase database, @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.sync.SyncManager syncManager, @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Boolean> isOfflineModeEnabled() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Boolean> isAutoDownloadEnabled() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> getDownloadQuality() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Boolean> isWifiOnlyEnabled() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Long> getStorageLimitMB() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setOfflineMode(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setAutoDownload(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setDownloadQuality(@org.jetbrains.annotations.NotNull()
    java.lang.String quality, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setWifiOnly(boolean wifiOnly, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setStorageLimit(long limitMB, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object cacheMediaItems(@org.jetbrains.annotations.NotNull()
    java.util.List<com.catalogizer.android.data.models.MediaItem> items, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCachedMediaItems(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.catalogizer.android.data.models.MediaItem>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCachedMediaById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.models.MediaItem> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCachedMediaByType(@org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.catalogizer.android.data.models.MediaItem>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object cacheSearchQuery(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    java.util.List<com.catalogizer.android.data.models.MediaItem> results, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCachedSearchQueries(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object searchCachedMedia(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.catalogizer.android.data.models.MediaItem>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateWatchProgressOffline(long mediaId, double progress, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object toggleFavoriteOffline(long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object rateMediaOffline(long mediaId, double rating, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getUsedStorageBytes(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAvailableStorageBytes(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object isStorageAvailable(long requiredBytes, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object cleanupOldCache(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object onNetworkAvailable(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object onNetworkUnavailable(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object exportOfflineData(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object importOfflineData(@org.jetbrains.annotations.NotNull()
    java.lang.String exportedData, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getOfflineStats(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.repository.OfflineStats> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/catalogizer/android/data/repository/OfflineRepository$Companion;", "", "()V", "AUTO_DOWNLOAD_KEY", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "CACHED_SEARCH_QUERIES_KEY", "", "DOWNLOAD_QUALITY_KEY", "OFFLINE_MODE_KEY", "STORAGE_LIMIT_KEY", "", "WIFI_ONLY_KEY", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}