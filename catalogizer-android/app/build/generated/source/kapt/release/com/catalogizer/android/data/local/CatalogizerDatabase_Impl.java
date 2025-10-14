package com.catalogizer.android.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class CatalogizerDatabase_Impl extends CatalogizerDatabase {
  private volatile MediaDao _mediaDao;

  private volatile SearchHistoryDao _searchHistoryDao;

  private volatile DownloadDao _downloadDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `media_items` (`id` INTEGER NOT NULL, `title` TEXT NOT NULL, `media_type` TEXT NOT NULL, `year` INTEGER, `description` TEXT, `cover_image` TEXT, `rating` REAL, `quality` TEXT, `file_size` INTEGER, `duration` INTEGER, `directory_path` TEXT NOT NULL, `smb_path` TEXT, `created_at` TEXT NOT NULL, `updated_at` TEXT NOT NULL, `external_metadata` TEXT, `versions` TEXT, `is_favorite` INTEGER NOT NULL, `watch_progress` REAL NOT NULL, `last_watched` TEXT, `is_downloaded` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `search_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `query` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `resultsCount` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `download_items` (`media_id` INTEGER NOT NULL, `title` TEXT NOT NULL, `coverImage` TEXT, `downloadUrl` TEXT NOT NULL, `localPath` TEXT, `progress` REAL NOT NULL, `status` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`media_id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'de2500d8a163217af1e1eae1ffcf29b5')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `media_items`");
        db.execSQL("DROP TABLE IF EXISTS `search_history`");
        db.execSQL("DROP TABLE IF EXISTS `download_items`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsMediaItems = new HashMap<String, TableInfo.Column>(20);
        _columnsMediaItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("media_type", new TableInfo.Column("media_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("year", new TableInfo.Column("year", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("cover_image", new TableInfo.Column("cover_image", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("rating", new TableInfo.Column("rating", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("quality", new TableInfo.Column("quality", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("file_size", new TableInfo.Column("file_size", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("duration", new TableInfo.Column("duration", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("directory_path", new TableInfo.Column("directory_path", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("smb_path", new TableInfo.Column("smb_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("created_at", new TableInfo.Column("created_at", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("updated_at", new TableInfo.Column("updated_at", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("external_metadata", new TableInfo.Column("external_metadata", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("versions", new TableInfo.Column("versions", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("is_favorite", new TableInfo.Column("is_favorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("watch_progress", new TableInfo.Column("watch_progress", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("last_watched", new TableInfo.Column("last_watched", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItems.put("is_downloaded", new TableInfo.Column("is_downloaded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMediaItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMediaItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMediaItems = new TableInfo("media_items", _columnsMediaItems, _foreignKeysMediaItems, _indicesMediaItems);
        final TableInfo _existingMediaItems = TableInfo.read(db, "media_items");
        if (!_infoMediaItems.equals(_existingMediaItems)) {
          return new RoomOpenHelper.ValidationResult(false, "media_items(com.catalogizer.android.data.models.MediaItem).\n"
                  + " Expected:\n" + _infoMediaItems + "\n"
                  + " Found:\n" + _existingMediaItems);
        }
        final HashMap<String, TableInfo.Column> _columnsSearchHistory = new HashMap<String, TableInfo.Column>(4);
        _columnsSearchHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("query", new TableInfo.Column("query", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("resultsCount", new TableInfo.Column("resultsCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSearchHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSearchHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSearchHistory = new TableInfo("search_history", _columnsSearchHistory, _foreignKeysSearchHistory, _indicesSearchHistory);
        final TableInfo _existingSearchHistory = TableInfo.read(db, "search_history");
        if (!_infoSearchHistory.equals(_existingSearchHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "search_history(com.catalogizer.android.data.local.SearchHistory).\n"
                  + " Expected:\n" + _infoSearchHistory + "\n"
                  + " Found:\n" + _existingSearchHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsDownloadItems = new HashMap<String, TableInfo.Column>(9);
        _columnsDownloadItems.put("media_id", new TableInfo.Column("media_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadItems.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadItems.put("coverImage", new TableInfo.Column("coverImage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadItems.put("downloadUrl", new TableInfo.Column("downloadUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadItems.put("localPath", new TableInfo.Column("localPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadItems.put("progress", new TableInfo.Column("progress", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadItems.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadItems.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadItems.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDownloadItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDownloadItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDownloadItems = new TableInfo("download_items", _columnsDownloadItems, _foreignKeysDownloadItems, _indicesDownloadItems);
        final TableInfo _existingDownloadItems = TableInfo.read(db, "download_items");
        if (!_infoDownloadItems.equals(_existingDownloadItems)) {
          return new RoomOpenHelper.ValidationResult(false, "download_items(com.catalogizer.android.data.local.DownloadItem).\n"
                  + " Expected:\n" + _infoDownloadItems + "\n"
                  + " Found:\n" + _existingDownloadItems);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "de2500d8a163217af1e1eae1ffcf29b5", "d95516ee4adfe31219b0f2ecc41c2c55");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "media_items","search_history","download_items");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `media_items`");
      _db.execSQL("DELETE FROM `search_history`");
      _db.execSQL("DELETE FROM `download_items`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MediaDao.class, MediaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SearchHistoryDao.class, SearchHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DownloadDao.class, DownloadDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public MediaDao mediaDao() {
    if (_mediaDao != null) {
      return _mediaDao;
    } else {
      synchronized(this) {
        if(_mediaDao == null) {
          _mediaDao = new MediaDao_Impl(this);
        }
        return _mediaDao;
      }
    }
  }

  @Override
  public SearchHistoryDao searchHistoryDao() {
    if (_searchHistoryDao != null) {
      return _searchHistoryDao;
    } else {
      synchronized(this) {
        if(_searchHistoryDao == null) {
          _searchHistoryDao = new SearchHistoryDao_Impl(this);
        }
        return _searchHistoryDao;
      }
    }
  }

  @Override
  public DownloadDao downloadDao() {
    if (_downloadDao != null) {
      return _downloadDao;
    } else {
      synchronized(this) {
        if(_downloadDao == null) {
          _downloadDao = new DownloadDao_Impl(this);
        }
        return _downloadDao;
      }
    }
  }
}
