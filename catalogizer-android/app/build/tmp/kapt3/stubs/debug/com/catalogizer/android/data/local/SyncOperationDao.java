package com.catalogizer.android.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\n\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0016\u0010\r\u001a\u00020\u00032\u0006\u0010\u0011\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001e\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0018H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0018H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u001a\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\bJ \u0010\u001b\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u001c\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00182\u0006\u0010\u0014\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u001dJ\u001c\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00182\u0006\u0010\u0013\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0018H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u000e\u0010 \u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\bJ\u000e\u0010!\u001a\b\u0012\u0004\u0012\u00020\u000b0\"H\'J\u0016\u0010#\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0010J\"\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00050\u00182\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0018H\u00a7@\u00a2\u0006\u0002\u0010&J\u000e\u0010\'\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010(\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u001e\u0010)\u001a\u00020\u00032\u0006\u0010\u0011\u001a\u00020\u00052\u0006\u0010*\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010+\u00a8\u0006,"}, d2 = {"Lcom/catalogizer/android/data/local/SyncOperationDao;", "", "cleanupOldOperations", "", "cutoffTime", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAllOperations", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteFailedOperations", "maxRetries", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteOperation", "operation", "Lcom/catalogizer/android/data/sync/SyncOperation;", "(Lcom/catalogizer/android/data/sync/SyncOperation;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "operationId", "deleteOperationsByMediaAndType", "mediaId", "type", "Lcom/catalogizer/android/data/sync/SyncOperationType;", "(JLcom/catalogizer/android/data/sync/SyncOperationType;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllOperations", "", "getFailedOperations", "getFailedOperationsCount", "getOperationByMediaAndType", "getOperationsByType", "(Lcom/catalogizer/android/data/sync/SyncOperationType;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getOperationsForMedia", "getPendingOperations", "getPendingOperationsCount", "getPendingOperationsCountFlow", "Lkotlinx/coroutines/flow/Flow;", "insertOperation", "insertOperations", "operations", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "resetRetryCount", "updateOperation", "updateRetryCount", "retryCount", "(JILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface SyncOperationDao {
    
    @androidx.room.Query(value = "SELECT * FROM sync_operations ORDER BY timestamp ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllOperations(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.catalogizer.android.data.sync.SyncOperation>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM sync_operations WHERE retryCount < maxRetries ORDER BY timestamp ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPendingOperations(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.catalogizer.android.data.sync.SyncOperation>> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM sync_operations WHERE retryCount < maxRetries")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPendingOperationsCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM sync_operations WHERE retryCount < maxRetries")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Integer> getPendingOperationsCountFlow();
    
    @androidx.room.Query(value = "SELECT * FROM sync_operations WHERE mediaId = :mediaId AND type = :type LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getOperationByMediaAndType(long mediaId, @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.sync.SyncOperationType type, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.sync.SyncOperation> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertOperation(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.sync.SyncOperation operation, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertOperations(@org.jetbrains.annotations.NotNull()
    java.util.List<com.catalogizer.android.data.sync.SyncOperation> operations, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.Long>> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateOperation(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.sync.SyncOperation operation, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE sync_operations SET retryCount = :retryCount WHERE id = :operationId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateRetryCount(long operationId, int retryCount, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE sync_operations SET retryCount = 0")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object resetRetryCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOperation(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.sync.SyncOperation operation, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM sync_operations WHERE id = :operationId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOperation(long operationId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM sync_operations WHERE retryCount >= :maxRetries")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteFailedOperations(int maxRetries, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM sync_operations WHERE mediaId = :mediaId AND type = :type")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOperationsByMediaAndType(long mediaId, @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.sync.SyncOperationType type, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM sync_operations")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllOperations(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM sync_operations WHERE retryCount >= maxRetries")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getFailedOperations(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.catalogizer.android.data.sync.SyncOperation>> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM sync_operations WHERE retryCount >= maxRetries")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getFailedOperationsCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM sync_operations WHERE type = :type ORDER BY timestamp ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getOperationsByType(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.sync.SyncOperationType type, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.catalogizer.android.data.sync.SyncOperation>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM sync_operations WHERE mediaId = :mediaId ORDER BY timestamp ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getOperationsForMedia(long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.catalogizer.android.data.sync.SyncOperation>> $completion);
    
    @androidx.room.Query(value = "DELETE FROM sync_operations WHERE timestamp < :cutoffTime AND retryCount >= maxRetries")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object cleanupOldOperations(long cutoffTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}