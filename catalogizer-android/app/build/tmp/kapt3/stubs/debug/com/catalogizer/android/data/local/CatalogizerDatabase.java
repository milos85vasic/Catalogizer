package com.catalogizer.android.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&J\b\u0010\u000b\u001a\u00020\fH&J\b\u0010\r\u001a\u00020\u000eH&\u00a8\u0006\u0010"}, d2 = {"Lcom/catalogizer/android/data/local/CatalogizerDatabase;", "Landroidx/room/RoomDatabase;", "()V", "downloadDao", "Lcom/catalogizer/android/data/local/DownloadDao;", "favoriteDao", "Lcom/catalogizer/android/data/local/FavoriteDao;", "mediaDao", "Lcom/catalogizer/android/data/local/MediaDao;", "searchHistoryDao", "Lcom/catalogizer/android/data/local/SearchHistoryDao;", "syncOperationDao", "Lcom/catalogizer/android/data/local/SyncOperationDao;", "watchProgressDao", "Lcom/catalogizer/android/data/local/WatchProgressDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.catalogizer.android.data.models.MediaItem.class, com.catalogizer.android.data.local.SearchHistory.class, com.catalogizer.android.data.local.DownloadItem.class, com.catalogizer.android.data.sync.SyncOperation.class, com.catalogizer.android.data.local.WatchProgress.class, com.catalogizer.android.data.local.Favorite.class}, version = 1, exportSchema = false)
@androidx.room.TypeConverters(value = {com.catalogizer.android.data.local.Converters.class})
public abstract class CatalogizerDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.catalogizer.android.data.local.CatalogizerDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.catalogizer.android.data.local.CatalogizerDatabase.Companion Companion = null;
    
    public CatalogizerDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.catalogizer.android.data.local.MediaDao mediaDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.catalogizer.android.data.local.SearchHistoryDao searchHistoryDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.catalogizer.android.data.local.DownloadDao downloadDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.catalogizer.android.data.local.SyncOperationDao syncOperationDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.catalogizer.android.data.local.WatchProgressDao watchProgressDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.catalogizer.android.data.local.FavoriteDao favoriteDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/catalogizer/android/data/local/CatalogizerDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/catalogizer/android/data/local/CatalogizerDatabase;", "getDatabase", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.catalogizer.android.data.local.CatalogizerDatabase getDatabase(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}