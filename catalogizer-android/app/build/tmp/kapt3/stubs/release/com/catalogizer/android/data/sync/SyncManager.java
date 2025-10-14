package com.catalogizer.android.data.sync;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u0000 62\u00020\u0001:\u00016B/\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\u000e\u0010\u0017\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u000e\u0010\u001a\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010\u0019J\u000e\u0010\u001c\u001a\u00020\u001bH\u0082@\u00a2\u0006\u0002\u0010\u0019J\u001e\u0010\u001d\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!H\u0086@\u00a2\u0006\u0002\u0010\"J\u001e\u0010#\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010$\u001a\u00020%H\u0086@\u00a2\u0006\u0002\u0010&J&\u0010\'\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010(\u001a\u00020%2\u0006\u0010)\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010*J\u000e\u0010+\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u0006\u0010,\u001a\u00020\u0018J\u0006\u0010-\u001a\u00020\u0018J\u0016\u0010.\u001a\u00020\u00182\u0006\u0010/\u001a\u000200H\u0082@\u00a2\u0006\u0002\u00101J\u0016\u00102\u001a\u00020\u00182\u0006\u0010/\u001a\u000200H\u0082@\u00a2\u0006\u0002\u00101J\u000e\u00103\u001a\u00020\u0018H\u0082@\u00a2\u0006\u0002\u0010\u0019J\u0016\u00104\u001a\u00020\u00182\u0006\u0010/\u001a\u000200H\u0082@\u00a2\u0006\u0002\u00101J\u000e\u00105\u001a\u00020\u0018H\u0082@\u00a2\u0006\u0002\u0010\u0019R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0012R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u00067"}, d2 = {"Lcom/catalogizer/android/data/sync/SyncManager;", "", "database", "Lcom/catalogizer/android/data/local/CatalogizerDatabase;", "api", "Lcom/catalogizer/android/data/remote/CatalogizerApi;", "authRepository", "Lcom/catalogizer/android/data/repository/AuthRepository;", "mediaRepository", "Lcom/catalogizer/android/data/repository/MediaRepository;", "context", "Landroid/content/Context;", "(Lcom/catalogizer/android/data/local/CatalogizerDatabase;Lcom/catalogizer/android/data/remote/CatalogizerApi;Lcom/catalogizer/android/data/repository/AuthRepository;Lcom/catalogizer/android/data/repository/MediaRepository;Landroid/content/Context;)V", "_syncStatus", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/catalogizer/android/data/sync/SyncStatus;", "syncOperationDao", "error/NonExistentClass", "Lerror/NonExistentClass;", "syncStatus", "Lkotlinx/coroutines/flow/StateFlow;", "getSyncStatus", "()Lkotlinx/coroutines/flow/StateFlow;", "clearFailedOperations", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "performManualSync", "Lcom/catalogizer/android/data/sync/SyncResult;", "performSyncInternal", "queueFavoriteToggle", "mediaId", "", "isFavorite", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "queueRatingUpdate", "rating", "", "(JDLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "queueWatchProgressUpdate", "progress", "timestamp", "(JDJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "retryFailedOperations", "startPeriodicSync", "stopPeriodicSync", "syncFavoriteStatus", "operation", "Lcom/catalogizer/android/data/sync/SyncOperation;", "(Lcom/catalogizer/android/data/sync/SyncOperation;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "syncRating", "syncUserPreferences", "syncWatchProgress", "updatePendingOperationsCount", "Companion", "app_release"})
public final class SyncManager {
    @org.jetbrains.annotations.NotNull()
    private final com.catalogizer.android.data.local.CatalogizerDatabase database = null;
    @org.jetbrains.annotations.NotNull()
    private final com.catalogizer.android.data.remote.CatalogizerApi api = null;
    @org.jetbrains.annotations.NotNull()
    private final com.catalogizer.android.data.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.catalogizer.android.data.repository.MediaRepository mediaRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.catalogizer.android.data.sync.SyncStatus> _syncStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.catalogizer.android.data.sync.SyncStatus> syncStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final error.NonExistentClass syncOperationDao = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String SYNC_WORK_NAME = "catalogizer_sync";
    private static final long BACKGROUND_SYNC_INTERVAL_HOURS = 6L;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    @org.jetbrains.annotations.NotNull()
    public static final com.catalogizer.android.data.sync.SyncManager.Companion Companion = null;
    
    @javax.inject.Inject()
    public SyncManager(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.local.CatalogizerDatabase database, @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.remote.CatalogizerApi api, @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.repository.AuthRepository authRepository, @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.repository.MediaRepository mediaRepository, @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.catalogizer.android.data.sync.SyncStatus> getSyncStatus() {
        return null;
    }
    
    public final void startPeriodicSync() {
    }
    
    public final void stopPeriodicSync() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object performManualSync(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.sync.SyncResult> $completion) {
        return null;
    }
    
    private final java.lang.Object performSyncInternal(kotlin.coroutines.Continuation<? super com.catalogizer.android.data.sync.SyncResult> $completion) {
        return null;
    }
    
    private final java.lang.Object syncWatchProgress(com.catalogizer.android.data.sync.SyncOperation operation, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object syncFavoriteStatus(com.catalogizer.android.data.sync.SyncOperation operation, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object syncRating(com.catalogizer.android.data.sync.SyncOperation operation, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object syncUserPreferences(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object queueWatchProgressUpdate(long mediaId, double progress, long timestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object queueFavoriteToggle(long mediaId, boolean isFavorite, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object queueRatingUpdate(long mediaId, double rating, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object updatePendingOperationsCount(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearFailedOperations(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object retryFailedOperations(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/catalogizer/android/data/sync/SyncManager$Companion;", "", "()V", "BACKGROUND_SYNC_INTERVAL_HOURS", "", "MAX_RETRY_ATTEMPTS", "", "SYNC_WORK_NAME", "", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}