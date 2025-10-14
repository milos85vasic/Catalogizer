package com.catalogizer.android.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u0014\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00110\u0010H\'J\u0018\u0010\u0012\u001a\u0004\u0018\u00010\u00052\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u001c\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00110\u00102\u0006\u0010\f\u001a\u00020\rH\'J\u0016\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J0\u0010\u0016\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u0019\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u001a\u00a8\u0006\u001b"}, d2 = {"Lcom/catalogizer/android/data/local/DownloadDao;", "", "deleteDownload", "", "downloadItem", "Lcom/catalogizer/android/data/local/DownloadItem;", "(Lcom/catalogizer/android/data/local/DownloadItem;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteDownloadByMediaId", "mediaId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteDownloadsByStatus", "status", "Lcom/catalogizer/android/data/local/DownloadStatus;", "(Lcom/catalogizer/android/data/local/DownloadStatus;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllDownloads", "Lkotlinx/coroutines/flow/Flow;", "", "getDownloadByMediaId", "getDownloadsByStatus", "insertDownload", "updateDownload", "updateDownloadProgress", "progress", "", "updatedAt", "(JFLcom/catalogizer/android/data/local/DownloadStatus;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface DownloadDao {
    
    @androidx.room.Query(value = "SELECT * FROM download_items ORDER BY created_at DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.catalogizer.android.data.local.DownloadItem>> getAllDownloads();
    
    @androidx.room.Query(value = "SELECT * FROM download_items WHERE status = :status")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.catalogizer.android.data.local.DownloadItem>> getDownloadsByStatus(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.local.DownloadStatus status);
    
    @androidx.room.Query(value = "SELECT * FROM download_items WHERE media_id = :mediaId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDownloadByMediaId(long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.local.DownloadItem> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertDownload(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.local.DownloadItem downloadItem, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateDownload(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.local.DownloadItem downloadItem, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE download_items SET progress = :progress, status = :status, updated_at = :updatedAt WHERE media_id = :mediaId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateDownloadProgress(long mediaId, float progress, @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.local.DownloadStatus status, long updatedAt, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteDownload(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.local.DownloadItem downloadItem, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM download_items WHERE media_id = :mediaId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteDownloadByMediaId(long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM download_items WHERE status = :status")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteDownloadsByStatus(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.local.DownloadStatus status, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}