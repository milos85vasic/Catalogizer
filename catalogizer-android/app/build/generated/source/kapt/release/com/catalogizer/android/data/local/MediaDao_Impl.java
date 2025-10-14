package com.catalogizer.android.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.paging.LimitOffsetPagingSource;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.catalogizer.android.data.models.ExternalMetadata;
import com.catalogizer.android.data.models.MediaItem;
import com.catalogizer.android.data.models.MediaVersion;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class MediaDao_Impl implements MediaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MediaItem> __insertionAdapterOfMediaItem;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<MediaItem> __deletionAdapterOfMediaItem;

  private final EntityDeletionOrUpdateAdapter<MediaItem> __updateAdapterOfMediaItem;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFavoriteStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateWatchProgress;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDownloadStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMediaById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllMedia;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldMedia;

  private final SharedSQLiteStatement __preparedStmtOfUpdateRating;

  public MediaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMediaItem = new EntityInsertionAdapter<MediaItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `media_items` (`id`,`title`,`media_type`,`year`,`description`,`cover_image`,`rating`,`quality`,`file_size`,`duration`,`directory_path`,`smb_path`,`created_at`,`updated_at`,`external_metadata`,`versions`,`is_favorite`,`watch_progress`,`last_watched`,`is_downloaded`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MediaItem entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getMediaType() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMediaType());
        }
        if (entity.getYear() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getYear());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDescription());
        }
        if (entity.getCoverImage() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getCoverImage());
        }
        if (entity.getRating() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getRating());
        }
        if (entity.getQuality() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getQuality());
        }
        if (entity.getFileSize() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getFileSize());
        }
        if (entity.getDuration() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getDuration());
        }
        if (entity.getDirectoryPath() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getDirectoryPath());
        }
        if (entity.getSmbPath() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getSmbPath());
        }
        if (entity.getCreatedAt() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getCreatedAt());
        }
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getUpdatedAt());
        }
        final String _tmp = __converters.fromExternalMetadataList(entity.getExternalMetadata());
        if (_tmp == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, _tmp);
        }
        final String _tmp_1 = __converters.fromMediaVersionList(entity.getVersions());
        if (_tmp_1 == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, _tmp_1);
        }
        final int _tmp_2 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(17, _tmp_2);
        statement.bindDouble(18, entity.getWatchProgress());
        if (entity.getLastWatched() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getLastWatched());
        }
        final int _tmp_3 = entity.isDownloaded() ? 1 : 0;
        statement.bindLong(20, _tmp_3);
      }
    };
    this.__deletionAdapterOfMediaItem = new EntityDeletionOrUpdateAdapter<MediaItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `media_items` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MediaItem entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfMediaItem = new EntityDeletionOrUpdateAdapter<MediaItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `media_items` SET `id` = ?,`title` = ?,`media_type` = ?,`year` = ?,`description` = ?,`cover_image` = ?,`rating` = ?,`quality` = ?,`file_size` = ?,`duration` = ?,`directory_path` = ?,`smb_path` = ?,`created_at` = ?,`updated_at` = ?,`external_metadata` = ?,`versions` = ?,`is_favorite` = ?,`watch_progress` = ?,`last_watched` = ?,`is_downloaded` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MediaItem entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getMediaType() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMediaType());
        }
        if (entity.getYear() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getYear());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDescription());
        }
        if (entity.getCoverImage() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getCoverImage());
        }
        if (entity.getRating() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getRating());
        }
        if (entity.getQuality() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getQuality());
        }
        if (entity.getFileSize() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getFileSize());
        }
        if (entity.getDuration() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getDuration());
        }
        if (entity.getDirectoryPath() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getDirectoryPath());
        }
        if (entity.getSmbPath() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getSmbPath());
        }
        if (entity.getCreatedAt() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getCreatedAt());
        }
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getUpdatedAt());
        }
        final String _tmp = __converters.fromExternalMetadataList(entity.getExternalMetadata());
        if (_tmp == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, _tmp);
        }
        final String _tmp_1 = __converters.fromMediaVersionList(entity.getVersions());
        if (_tmp_1 == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, _tmp_1);
        }
        final int _tmp_2 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(17, _tmp_2);
        statement.bindDouble(18, entity.getWatchProgress());
        if (entity.getLastWatched() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getLastWatched());
        }
        final int _tmp_3 = entity.isDownloaded() ? 1 : 0;
        statement.bindLong(20, _tmp_3);
        statement.bindLong(21, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateFavoriteStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE media_items SET is_favorite = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateWatchProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE media_items SET watch_progress = ?, last_watched = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDownloadStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE media_items SET is_downloaded = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteMediaById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM media_items WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllMedia = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM media_items";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldMedia = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM media_items WHERE updated_at < ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateRating = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE media_items SET rating = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMedia(final MediaItem mediaItem,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMediaItem.insert(mediaItem);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAllMedia(final List<MediaItem> mediaItems,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMediaItem.insert(mediaItems);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMedia(final MediaItem mediaItem,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMediaItem.handle(mediaItem);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMedia(final MediaItem mediaItem,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMediaItem.handle(mediaItem);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object refreshMedia(final List<MediaItem> mediaItems,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> MediaDao.DefaultImpls.refreshMedia(MediaDao_Impl.this, mediaItems, __cont), $completion);
  }

  @Override
  public Object insertOrUpdate(final MediaItem mediaItem,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> MediaDao.DefaultImpls.insertOrUpdate(MediaDao_Impl.this, mediaItem, __cont), $completion);
  }

  @Override
  public Object updateFavoriteStatus(final long id, final boolean isFavorite,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateFavoriteStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isFavorite ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateFavoriteStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateWatchProgress(final long id, final double progress, final String lastWatched,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateWatchProgress.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, progress);
        _argIndex = 2;
        if (lastWatched == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, lastWatched);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateWatchProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDownloadStatus(final long id, final boolean isDownloaded,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDownloadStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isDownloaded ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateDownloadStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMediaById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMediaById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteMediaById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllMedia(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllMedia.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllMedia.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldMedia(final String timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldMedia.acquire();
        int _argIndex = 1;
        if (timestamp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, timestamp);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOldMedia.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRating(final long id, final double rating,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateRating.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, rating);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateRating.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldCachedItems(final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldMedia.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOldMedia.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public PagingSource<Integer, MediaItem> getAllMediaPaging() {
    final String _sql = "SELECT * FROM media_items ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new LimitOffsetPagingSource<MediaItem>(_statement, __db, "media_items") {
      @Override
      @NonNull
      protected List<MediaItem> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(cursor, "title");
        final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(cursor, "media_type");
        final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(cursor, "year");
        final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(cursor, "description");
        final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(cursor, "cover_image");
        final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(cursor, "rating");
        final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(cursor, "quality");
        final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(cursor, "file_size");
        final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(cursor, "duration");
        final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(cursor, "directory_path");
        final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(cursor, "smb_path");
        final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "created_at");
        final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "updated_at");
        final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(cursor, "external_metadata");
        final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(cursor, "versions");
        final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(cursor, "is_favorite");
        final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(cursor, "watch_progress");
        final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(cursor, "last_watched");
        final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(cursor, "is_downloaded");
        final List<MediaItem> _result = new ArrayList<MediaItem>(cursor.getCount());
        while (cursor.moveToNext()) {
          final MediaItem _item;
          final long _tmpId;
          _tmpId = cursor.getLong(_cursorIndexOfId);
          final String _tmpTitle;
          if (cursor.isNull(_cursorIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = cursor.getString(_cursorIndexOfTitle);
          }
          final String _tmpMediaType;
          if (cursor.isNull(_cursorIndexOfMediaType)) {
            _tmpMediaType = null;
          } else {
            _tmpMediaType = cursor.getString(_cursorIndexOfMediaType);
          }
          final Integer _tmpYear;
          if (cursor.isNull(_cursorIndexOfYear)) {
            _tmpYear = null;
          } else {
            _tmpYear = cursor.getInt(_cursorIndexOfYear);
          }
          final String _tmpDescription;
          if (cursor.isNull(_cursorIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = cursor.getString(_cursorIndexOfDescription);
          }
          final String _tmpCoverImage;
          if (cursor.isNull(_cursorIndexOfCoverImage)) {
            _tmpCoverImage = null;
          } else {
            _tmpCoverImage = cursor.getString(_cursorIndexOfCoverImage);
          }
          final Double _tmpRating;
          if (cursor.isNull(_cursorIndexOfRating)) {
            _tmpRating = null;
          } else {
            _tmpRating = cursor.getDouble(_cursorIndexOfRating);
          }
          final String _tmpQuality;
          if (cursor.isNull(_cursorIndexOfQuality)) {
            _tmpQuality = null;
          } else {
            _tmpQuality = cursor.getString(_cursorIndexOfQuality);
          }
          final Long _tmpFileSize;
          if (cursor.isNull(_cursorIndexOfFileSize)) {
            _tmpFileSize = null;
          } else {
            _tmpFileSize = cursor.getLong(_cursorIndexOfFileSize);
          }
          final Integer _tmpDuration;
          if (cursor.isNull(_cursorIndexOfDuration)) {
            _tmpDuration = null;
          } else {
            _tmpDuration = cursor.getInt(_cursorIndexOfDuration);
          }
          final String _tmpDirectoryPath;
          if (cursor.isNull(_cursorIndexOfDirectoryPath)) {
            _tmpDirectoryPath = null;
          } else {
            _tmpDirectoryPath = cursor.getString(_cursorIndexOfDirectoryPath);
          }
          final String _tmpSmbPath;
          if (cursor.isNull(_cursorIndexOfSmbPath)) {
            _tmpSmbPath = null;
          } else {
            _tmpSmbPath = cursor.getString(_cursorIndexOfSmbPath);
          }
          final String _tmpCreatedAt;
          if (cursor.isNull(_cursorIndexOfCreatedAt)) {
            _tmpCreatedAt = null;
          } else {
            _tmpCreatedAt = cursor.getString(_cursorIndexOfCreatedAt);
          }
          final String _tmpUpdatedAt;
          if (cursor.isNull(_cursorIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null;
          } else {
            _tmpUpdatedAt = cursor.getString(_cursorIndexOfUpdatedAt);
          }
          final List<ExternalMetadata> _tmpExternalMetadata;
          final String _tmp;
          if (cursor.isNull(_cursorIndexOfExternalMetadata)) {
            _tmp = null;
          } else {
            _tmp = cursor.getString(_cursorIndexOfExternalMetadata);
          }
          _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
          final List<MediaVersion> _tmpVersions;
          final String _tmp_1;
          if (cursor.isNull(_cursorIndexOfVersions)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = cursor.getString(_cursorIndexOfVersions);
          }
          _tmpVersions = __converters.toMediaVersionList(_tmp_1);
          final boolean _tmpIsFavorite;
          final int _tmp_2;
          _tmp_2 = cursor.getInt(_cursorIndexOfIsFavorite);
          _tmpIsFavorite = _tmp_2 != 0;
          final double _tmpWatchProgress;
          _tmpWatchProgress = cursor.getDouble(_cursorIndexOfWatchProgress);
          final String _tmpLastWatched;
          if (cursor.isNull(_cursorIndexOfLastWatched)) {
            _tmpLastWatched = null;
          } else {
            _tmpLastWatched = cursor.getString(_cursorIndexOfLastWatched);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp_3;
          _tmp_3 = cursor.getInt(_cursorIndexOfIsDownloaded);
          _tmpIsDownloaded = _tmp_3 != 0;
          _item = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public PagingSource<Integer, MediaItem> getMediaByTypePaging(final String mediaType) {
    final String _sql = "SELECT * FROM media_items WHERE media_type = ? ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (mediaType == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, mediaType);
    }
    return new LimitOffsetPagingSource<MediaItem>(_statement, __db, "media_items") {
      @Override
      @NonNull
      protected List<MediaItem> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(cursor, "title");
        final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(cursor, "media_type");
        final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(cursor, "year");
        final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(cursor, "description");
        final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(cursor, "cover_image");
        final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(cursor, "rating");
        final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(cursor, "quality");
        final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(cursor, "file_size");
        final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(cursor, "duration");
        final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(cursor, "directory_path");
        final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(cursor, "smb_path");
        final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "created_at");
        final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "updated_at");
        final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(cursor, "external_metadata");
        final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(cursor, "versions");
        final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(cursor, "is_favorite");
        final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(cursor, "watch_progress");
        final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(cursor, "last_watched");
        final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(cursor, "is_downloaded");
        final List<MediaItem> _result = new ArrayList<MediaItem>(cursor.getCount());
        while (cursor.moveToNext()) {
          final MediaItem _item;
          final long _tmpId;
          _tmpId = cursor.getLong(_cursorIndexOfId);
          final String _tmpTitle;
          if (cursor.isNull(_cursorIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = cursor.getString(_cursorIndexOfTitle);
          }
          final String _tmpMediaType;
          if (cursor.isNull(_cursorIndexOfMediaType)) {
            _tmpMediaType = null;
          } else {
            _tmpMediaType = cursor.getString(_cursorIndexOfMediaType);
          }
          final Integer _tmpYear;
          if (cursor.isNull(_cursorIndexOfYear)) {
            _tmpYear = null;
          } else {
            _tmpYear = cursor.getInt(_cursorIndexOfYear);
          }
          final String _tmpDescription;
          if (cursor.isNull(_cursorIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = cursor.getString(_cursorIndexOfDescription);
          }
          final String _tmpCoverImage;
          if (cursor.isNull(_cursorIndexOfCoverImage)) {
            _tmpCoverImage = null;
          } else {
            _tmpCoverImage = cursor.getString(_cursorIndexOfCoverImage);
          }
          final Double _tmpRating;
          if (cursor.isNull(_cursorIndexOfRating)) {
            _tmpRating = null;
          } else {
            _tmpRating = cursor.getDouble(_cursorIndexOfRating);
          }
          final String _tmpQuality;
          if (cursor.isNull(_cursorIndexOfQuality)) {
            _tmpQuality = null;
          } else {
            _tmpQuality = cursor.getString(_cursorIndexOfQuality);
          }
          final Long _tmpFileSize;
          if (cursor.isNull(_cursorIndexOfFileSize)) {
            _tmpFileSize = null;
          } else {
            _tmpFileSize = cursor.getLong(_cursorIndexOfFileSize);
          }
          final Integer _tmpDuration;
          if (cursor.isNull(_cursorIndexOfDuration)) {
            _tmpDuration = null;
          } else {
            _tmpDuration = cursor.getInt(_cursorIndexOfDuration);
          }
          final String _tmpDirectoryPath;
          if (cursor.isNull(_cursorIndexOfDirectoryPath)) {
            _tmpDirectoryPath = null;
          } else {
            _tmpDirectoryPath = cursor.getString(_cursorIndexOfDirectoryPath);
          }
          final String _tmpSmbPath;
          if (cursor.isNull(_cursorIndexOfSmbPath)) {
            _tmpSmbPath = null;
          } else {
            _tmpSmbPath = cursor.getString(_cursorIndexOfSmbPath);
          }
          final String _tmpCreatedAt;
          if (cursor.isNull(_cursorIndexOfCreatedAt)) {
            _tmpCreatedAt = null;
          } else {
            _tmpCreatedAt = cursor.getString(_cursorIndexOfCreatedAt);
          }
          final String _tmpUpdatedAt;
          if (cursor.isNull(_cursorIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null;
          } else {
            _tmpUpdatedAt = cursor.getString(_cursorIndexOfUpdatedAt);
          }
          final List<ExternalMetadata> _tmpExternalMetadata;
          final String _tmp;
          if (cursor.isNull(_cursorIndexOfExternalMetadata)) {
            _tmp = null;
          } else {
            _tmp = cursor.getString(_cursorIndexOfExternalMetadata);
          }
          _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
          final List<MediaVersion> _tmpVersions;
          final String _tmp_1;
          if (cursor.isNull(_cursorIndexOfVersions)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = cursor.getString(_cursorIndexOfVersions);
          }
          _tmpVersions = __converters.toMediaVersionList(_tmp_1);
          final boolean _tmpIsFavorite;
          final int _tmp_2;
          _tmp_2 = cursor.getInt(_cursorIndexOfIsFavorite);
          _tmpIsFavorite = _tmp_2 != 0;
          final double _tmpWatchProgress;
          _tmpWatchProgress = cursor.getDouble(_cursorIndexOfWatchProgress);
          final String _tmpLastWatched;
          if (cursor.isNull(_cursorIndexOfLastWatched)) {
            _tmpLastWatched = null;
          } else {
            _tmpLastWatched = cursor.getString(_cursorIndexOfLastWatched);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp_3;
          _tmp_3 = cursor.getInt(_cursorIndexOfIsDownloaded);
          _tmpIsDownloaded = _tmp_3 != 0;
          _item = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public PagingSource<Integer, MediaItem> searchMediaPaging(final String query) {
    final String _sql = "\n"
            + "        SELECT * FROM media_items\n"
            + "        WHERE title LIKE '%' || ? || '%'\n"
            + "        OR description LIKE '%' || ? || '%'\n"
            + "        ORDER BY updated_at DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    _argIndex = 2;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    return new LimitOffsetPagingSource<MediaItem>(_statement, __db, "media_items") {
      @Override
      @NonNull
      protected List<MediaItem> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(cursor, "title");
        final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(cursor, "media_type");
        final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(cursor, "year");
        final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(cursor, "description");
        final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(cursor, "cover_image");
        final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(cursor, "rating");
        final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(cursor, "quality");
        final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(cursor, "file_size");
        final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(cursor, "duration");
        final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(cursor, "directory_path");
        final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(cursor, "smb_path");
        final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "created_at");
        final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "updated_at");
        final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(cursor, "external_metadata");
        final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(cursor, "versions");
        final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(cursor, "is_favorite");
        final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(cursor, "watch_progress");
        final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(cursor, "last_watched");
        final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(cursor, "is_downloaded");
        final List<MediaItem> _result = new ArrayList<MediaItem>(cursor.getCount());
        while (cursor.moveToNext()) {
          final MediaItem _item;
          final long _tmpId;
          _tmpId = cursor.getLong(_cursorIndexOfId);
          final String _tmpTitle;
          if (cursor.isNull(_cursorIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = cursor.getString(_cursorIndexOfTitle);
          }
          final String _tmpMediaType;
          if (cursor.isNull(_cursorIndexOfMediaType)) {
            _tmpMediaType = null;
          } else {
            _tmpMediaType = cursor.getString(_cursorIndexOfMediaType);
          }
          final Integer _tmpYear;
          if (cursor.isNull(_cursorIndexOfYear)) {
            _tmpYear = null;
          } else {
            _tmpYear = cursor.getInt(_cursorIndexOfYear);
          }
          final String _tmpDescription;
          if (cursor.isNull(_cursorIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = cursor.getString(_cursorIndexOfDescription);
          }
          final String _tmpCoverImage;
          if (cursor.isNull(_cursorIndexOfCoverImage)) {
            _tmpCoverImage = null;
          } else {
            _tmpCoverImage = cursor.getString(_cursorIndexOfCoverImage);
          }
          final Double _tmpRating;
          if (cursor.isNull(_cursorIndexOfRating)) {
            _tmpRating = null;
          } else {
            _tmpRating = cursor.getDouble(_cursorIndexOfRating);
          }
          final String _tmpQuality;
          if (cursor.isNull(_cursorIndexOfQuality)) {
            _tmpQuality = null;
          } else {
            _tmpQuality = cursor.getString(_cursorIndexOfQuality);
          }
          final Long _tmpFileSize;
          if (cursor.isNull(_cursorIndexOfFileSize)) {
            _tmpFileSize = null;
          } else {
            _tmpFileSize = cursor.getLong(_cursorIndexOfFileSize);
          }
          final Integer _tmpDuration;
          if (cursor.isNull(_cursorIndexOfDuration)) {
            _tmpDuration = null;
          } else {
            _tmpDuration = cursor.getInt(_cursorIndexOfDuration);
          }
          final String _tmpDirectoryPath;
          if (cursor.isNull(_cursorIndexOfDirectoryPath)) {
            _tmpDirectoryPath = null;
          } else {
            _tmpDirectoryPath = cursor.getString(_cursorIndexOfDirectoryPath);
          }
          final String _tmpSmbPath;
          if (cursor.isNull(_cursorIndexOfSmbPath)) {
            _tmpSmbPath = null;
          } else {
            _tmpSmbPath = cursor.getString(_cursorIndexOfSmbPath);
          }
          final String _tmpCreatedAt;
          if (cursor.isNull(_cursorIndexOfCreatedAt)) {
            _tmpCreatedAt = null;
          } else {
            _tmpCreatedAt = cursor.getString(_cursorIndexOfCreatedAt);
          }
          final String _tmpUpdatedAt;
          if (cursor.isNull(_cursorIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null;
          } else {
            _tmpUpdatedAt = cursor.getString(_cursorIndexOfUpdatedAt);
          }
          final List<ExternalMetadata> _tmpExternalMetadata;
          final String _tmp;
          if (cursor.isNull(_cursorIndexOfExternalMetadata)) {
            _tmp = null;
          } else {
            _tmp = cursor.getString(_cursorIndexOfExternalMetadata);
          }
          _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
          final List<MediaVersion> _tmpVersions;
          final String _tmp_1;
          if (cursor.isNull(_cursorIndexOfVersions)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = cursor.getString(_cursorIndexOfVersions);
          }
          _tmpVersions = __converters.toMediaVersionList(_tmp_1);
          final boolean _tmpIsFavorite;
          final int _tmp_2;
          _tmp_2 = cursor.getInt(_cursorIndexOfIsFavorite);
          _tmpIsFavorite = _tmp_2 != 0;
          final double _tmpWatchProgress;
          _tmpWatchProgress = cursor.getDouble(_cursorIndexOfWatchProgress);
          final String _tmpLastWatched;
          if (cursor.isNull(_cursorIndexOfLastWatched)) {
            _tmpLastWatched = null;
          } else {
            _tmpLastWatched = cursor.getString(_cursorIndexOfLastWatched);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp_3;
          _tmp_3 = cursor.getInt(_cursorIndexOfIsDownloaded);
          _tmpIsDownloaded = _tmp_3 != 0;
          _item = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public Object getMediaById(final long id, final Continuation<? super MediaItem> $completion) {
    final String _sql = "SELECT * FROM media_items WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MediaItem>() {
      @Override
      @Nullable
      public MediaItem call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_image");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(_cursor, "directory_path");
          final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(_cursor, "smb_path");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "external_metadata");
          final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(_cursor, "versions");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "watch_progress");
          final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(_cursor, "last_watched");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "is_downloaded");
          final MediaItem _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpMediaType;
            if (_cursor.isNull(_cursorIndexOfMediaType)) {
              _tmpMediaType = null;
            } else {
              _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpCoverImage;
            if (_cursor.isNull(_cursorIndexOfCoverImage)) {
              _tmpCoverImage = null;
            } else {
              _tmpCoverImage = _cursor.getString(_cursorIndexOfCoverImage);
            }
            final Double _tmpRating;
            if (_cursor.isNull(_cursorIndexOfRating)) {
              _tmpRating = null;
            } else {
              _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            }
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Integer _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            }
            final String _tmpDirectoryPath;
            if (_cursor.isNull(_cursorIndexOfDirectoryPath)) {
              _tmpDirectoryPath = null;
            } else {
              _tmpDirectoryPath = _cursor.getString(_cursorIndexOfDirectoryPath);
            }
            final String _tmpSmbPath;
            if (_cursor.isNull(_cursorIndexOfSmbPath)) {
              _tmpSmbPath = null;
            } else {
              _tmpSmbPath = _cursor.getString(_cursorIndexOfSmbPath);
            }
            final String _tmpCreatedAt;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmpCreatedAt = null;
            } else {
              _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final String _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            }
            final List<ExternalMetadata> _tmpExternalMetadata;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfExternalMetadata)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfExternalMetadata);
            }
            _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
            final List<MediaVersion> _tmpVersions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfVersions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfVersions);
            }
            _tmpVersions = __converters.toMediaVersionList(_tmp_1);
            final boolean _tmpIsFavorite;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_2 != 0;
            final double _tmpWatchProgress;
            _tmpWatchProgress = _cursor.getDouble(_cursorIndexOfWatchProgress);
            final String _tmpLastWatched;
            if (_cursor.isNull(_cursorIndexOfLastWatched)) {
              _tmpLastWatched = null;
            } else {
              _tmpLastWatched = _cursor.getString(_cursorIndexOfLastWatched);
            }
            final boolean _tmpIsDownloaded;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp_3 != 0;
            _result = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<MediaItem> getMediaByIdFlow(final long id) {
    final String _sql = "SELECT * FROM media_items WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"media_items"}, new Callable<MediaItem>() {
      @Override
      @Nullable
      public MediaItem call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_image");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(_cursor, "directory_path");
          final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(_cursor, "smb_path");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "external_metadata");
          final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(_cursor, "versions");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "watch_progress");
          final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(_cursor, "last_watched");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "is_downloaded");
          final MediaItem _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpMediaType;
            if (_cursor.isNull(_cursorIndexOfMediaType)) {
              _tmpMediaType = null;
            } else {
              _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpCoverImage;
            if (_cursor.isNull(_cursorIndexOfCoverImage)) {
              _tmpCoverImage = null;
            } else {
              _tmpCoverImage = _cursor.getString(_cursorIndexOfCoverImage);
            }
            final Double _tmpRating;
            if (_cursor.isNull(_cursorIndexOfRating)) {
              _tmpRating = null;
            } else {
              _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            }
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Integer _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            }
            final String _tmpDirectoryPath;
            if (_cursor.isNull(_cursorIndexOfDirectoryPath)) {
              _tmpDirectoryPath = null;
            } else {
              _tmpDirectoryPath = _cursor.getString(_cursorIndexOfDirectoryPath);
            }
            final String _tmpSmbPath;
            if (_cursor.isNull(_cursorIndexOfSmbPath)) {
              _tmpSmbPath = null;
            } else {
              _tmpSmbPath = _cursor.getString(_cursorIndexOfSmbPath);
            }
            final String _tmpCreatedAt;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmpCreatedAt = null;
            } else {
              _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final String _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            }
            final List<ExternalMetadata> _tmpExternalMetadata;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfExternalMetadata)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfExternalMetadata);
            }
            _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
            final List<MediaVersion> _tmpVersions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfVersions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfVersions);
            }
            _tmpVersions = __converters.toMediaVersionList(_tmp_1);
            final boolean _tmpIsFavorite;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_2 != 0;
            final double _tmpWatchProgress;
            _tmpWatchProgress = _cursor.getDouble(_cursorIndexOfWatchProgress);
            final String _tmpLastWatched;
            if (_cursor.isNull(_cursorIndexOfLastWatched)) {
              _tmpLastWatched = null;
            } else {
              _tmpLastWatched = _cursor.getString(_cursorIndexOfLastWatched);
            }
            final boolean _tmpIsDownloaded;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp_3 != 0;
            _result = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public PagingSource<Integer, MediaItem> getFavoritesPaging() {
    final String _sql = "SELECT * FROM media_items WHERE is_favorite = 1 ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new LimitOffsetPagingSource<MediaItem>(_statement, __db, "media_items") {
      @Override
      @NonNull
      protected List<MediaItem> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(cursor, "title");
        final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(cursor, "media_type");
        final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(cursor, "year");
        final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(cursor, "description");
        final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(cursor, "cover_image");
        final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(cursor, "rating");
        final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(cursor, "quality");
        final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(cursor, "file_size");
        final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(cursor, "duration");
        final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(cursor, "directory_path");
        final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(cursor, "smb_path");
        final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "created_at");
        final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "updated_at");
        final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(cursor, "external_metadata");
        final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(cursor, "versions");
        final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(cursor, "is_favorite");
        final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(cursor, "watch_progress");
        final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(cursor, "last_watched");
        final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(cursor, "is_downloaded");
        final List<MediaItem> _result = new ArrayList<MediaItem>(cursor.getCount());
        while (cursor.moveToNext()) {
          final MediaItem _item;
          final long _tmpId;
          _tmpId = cursor.getLong(_cursorIndexOfId);
          final String _tmpTitle;
          if (cursor.isNull(_cursorIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = cursor.getString(_cursorIndexOfTitle);
          }
          final String _tmpMediaType;
          if (cursor.isNull(_cursorIndexOfMediaType)) {
            _tmpMediaType = null;
          } else {
            _tmpMediaType = cursor.getString(_cursorIndexOfMediaType);
          }
          final Integer _tmpYear;
          if (cursor.isNull(_cursorIndexOfYear)) {
            _tmpYear = null;
          } else {
            _tmpYear = cursor.getInt(_cursorIndexOfYear);
          }
          final String _tmpDescription;
          if (cursor.isNull(_cursorIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = cursor.getString(_cursorIndexOfDescription);
          }
          final String _tmpCoverImage;
          if (cursor.isNull(_cursorIndexOfCoverImage)) {
            _tmpCoverImage = null;
          } else {
            _tmpCoverImage = cursor.getString(_cursorIndexOfCoverImage);
          }
          final Double _tmpRating;
          if (cursor.isNull(_cursorIndexOfRating)) {
            _tmpRating = null;
          } else {
            _tmpRating = cursor.getDouble(_cursorIndexOfRating);
          }
          final String _tmpQuality;
          if (cursor.isNull(_cursorIndexOfQuality)) {
            _tmpQuality = null;
          } else {
            _tmpQuality = cursor.getString(_cursorIndexOfQuality);
          }
          final Long _tmpFileSize;
          if (cursor.isNull(_cursorIndexOfFileSize)) {
            _tmpFileSize = null;
          } else {
            _tmpFileSize = cursor.getLong(_cursorIndexOfFileSize);
          }
          final Integer _tmpDuration;
          if (cursor.isNull(_cursorIndexOfDuration)) {
            _tmpDuration = null;
          } else {
            _tmpDuration = cursor.getInt(_cursorIndexOfDuration);
          }
          final String _tmpDirectoryPath;
          if (cursor.isNull(_cursorIndexOfDirectoryPath)) {
            _tmpDirectoryPath = null;
          } else {
            _tmpDirectoryPath = cursor.getString(_cursorIndexOfDirectoryPath);
          }
          final String _tmpSmbPath;
          if (cursor.isNull(_cursorIndexOfSmbPath)) {
            _tmpSmbPath = null;
          } else {
            _tmpSmbPath = cursor.getString(_cursorIndexOfSmbPath);
          }
          final String _tmpCreatedAt;
          if (cursor.isNull(_cursorIndexOfCreatedAt)) {
            _tmpCreatedAt = null;
          } else {
            _tmpCreatedAt = cursor.getString(_cursorIndexOfCreatedAt);
          }
          final String _tmpUpdatedAt;
          if (cursor.isNull(_cursorIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null;
          } else {
            _tmpUpdatedAt = cursor.getString(_cursorIndexOfUpdatedAt);
          }
          final List<ExternalMetadata> _tmpExternalMetadata;
          final String _tmp;
          if (cursor.isNull(_cursorIndexOfExternalMetadata)) {
            _tmp = null;
          } else {
            _tmp = cursor.getString(_cursorIndexOfExternalMetadata);
          }
          _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
          final List<MediaVersion> _tmpVersions;
          final String _tmp_1;
          if (cursor.isNull(_cursorIndexOfVersions)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = cursor.getString(_cursorIndexOfVersions);
          }
          _tmpVersions = __converters.toMediaVersionList(_tmp_1);
          final boolean _tmpIsFavorite;
          final int _tmp_2;
          _tmp_2 = cursor.getInt(_cursorIndexOfIsFavorite);
          _tmpIsFavorite = _tmp_2 != 0;
          final double _tmpWatchProgress;
          _tmpWatchProgress = cursor.getDouble(_cursorIndexOfWatchProgress);
          final String _tmpLastWatched;
          if (cursor.isNull(_cursorIndexOfLastWatched)) {
            _tmpLastWatched = null;
          } else {
            _tmpLastWatched = cursor.getString(_cursorIndexOfLastWatched);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp_3;
          _tmp_3 = cursor.getInt(_cursorIndexOfIsDownloaded);
          _tmpIsDownloaded = _tmp_3 != 0;
          _item = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public PagingSource<Integer, MediaItem> getDownloadedPaging() {
    final String _sql = "SELECT * FROM media_items WHERE is_downloaded = 1 ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new LimitOffsetPagingSource<MediaItem>(_statement, __db, "media_items") {
      @Override
      @NonNull
      protected List<MediaItem> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(cursor, "title");
        final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(cursor, "media_type");
        final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(cursor, "year");
        final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(cursor, "description");
        final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(cursor, "cover_image");
        final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(cursor, "rating");
        final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(cursor, "quality");
        final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(cursor, "file_size");
        final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(cursor, "duration");
        final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(cursor, "directory_path");
        final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(cursor, "smb_path");
        final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "created_at");
        final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "updated_at");
        final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(cursor, "external_metadata");
        final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(cursor, "versions");
        final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(cursor, "is_favorite");
        final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(cursor, "watch_progress");
        final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(cursor, "last_watched");
        final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(cursor, "is_downloaded");
        final List<MediaItem> _result = new ArrayList<MediaItem>(cursor.getCount());
        while (cursor.moveToNext()) {
          final MediaItem _item;
          final long _tmpId;
          _tmpId = cursor.getLong(_cursorIndexOfId);
          final String _tmpTitle;
          if (cursor.isNull(_cursorIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = cursor.getString(_cursorIndexOfTitle);
          }
          final String _tmpMediaType;
          if (cursor.isNull(_cursorIndexOfMediaType)) {
            _tmpMediaType = null;
          } else {
            _tmpMediaType = cursor.getString(_cursorIndexOfMediaType);
          }
          final Integer _tmpYear;
          if (cursor.isNull(_cursorIndexOfYear)) {
            _tmpYear = null;
          } else {
            _tmpYear = cursor.getInt(_cursorIndexOfYear);
          }
          final String _tmpDescription;
          if (cursor.isNull(_cursorIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = cursor.getString(_cursorIndexOfDescription);
          }
          final String _tmpCoverImage;
          if (cursor.isNull(_cursorIndexOfCoverImage)) {
            _tmpCoverImage = null;
          } else {
            _tmpCoverImage = cursor.getString(_cursorIndexOfCoverImage);
          }
          final Double _tmpRating;
          if (cursor.isNull(_cursorIndexOfRating)) {
            _tmpRating = null;
          } else {
            _tmpRating = cursor.getDouble(_cursorIndexOfRating);
          }
          final String _tmpQuality;
          if (cursor.isNull(_cursorIndexOfQuality)) {
            _tmpQuality = null;
          } else {
            _tmpQuality = cursor.getString(_cursorIndexOfQuality);
          }
          final Long _tmpFileSize;
          if (cursor.isNull(_cursorIndexOfFileSize)) {
            _tmpFileSize = null;
          } else {
            _tmpFileSize = cursor.getLong(_cursorIndexOfFileSize);
          }
          final Integer _tmpDuration;
          if (cursor.isNull(_cursorIndexOfDuration)) {
            _tmpDuration = null;
          } else {
            _tmpDuration = cursor.getInt(_cursorIndexOfDuration);
          }
          final String _tmpDirectoryPath;
          if (cursor.isNull(_cursorIndexOfDirectoryPath)) {
            _tmpDirectoryPath = null;
          } else {
            _tmpDirectoryPath = cursor.getString(_cursorIndexOfDirectoryPath);
          }
          final String _tmpSmbPath;
          if (cursor.isNull(_cursorIndexOfSmbPath)) {
            _tmpSmbPath = null;
          } else {
            _tmpSmbPath = cursor.getString(_cursorIndexOfSmbPath);
          }
          final String _tmpCreatedAt;
          if (cursor.isNull(_cursorIndexOfCreatedAt)) {
            _tmpCreatedAt = null;
          } else {
            _tmpCreatedAt = cursor.getString(_cursorIndexOfCreatedAt);
          }
          final String _tmpUpdatedAt;
          if (cursor.isNull(_cursorIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null;
          } else {
            _tmpUpdatedAt = cursor.getString(_cursorIndexOfUpdatedAt);
          }
          final List<ExternalMetadata> _tmpExternalMetadata;
          final String _tmp;
          if (cursor.isNull(_cursorIndexOfExternalMetadata)) {
            _tmp = null;
          } else {
            _tmp = cursor.getString(_cursorIndexOfExternalMetadata);
          }
          _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
          final List<MediaVersion> _tmpVersions;
          final String _tmp_1;
          if (cursor.isNull(_cursorIndexOfVersions)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = cursor.getString(_cursorIndexOfVersions);
          }
          _tmpVersions = __converters.toMediaVersionList(_tmp_1);
          final boolean _tmpIsFavorite;
          final int _tmp_2;
          _tmp_2 = cursor.getInt(_cursorIndexOfIsFavorite);
          _tmpIsFavorite = _tmp_2 != 0;
          final double _tmpWatchProgress;
          _tmpWatchProgress = cursor.getDouble(_cursorIndexOfWatchProgress);
          final String _tmpLastWatched;
          if (cursor.isNull(_cursorIndexOfLastWatched)) {
            _tmpLastWatched = null;
          } else {
            _tmpLastWatched = cursor.getString(_cursorIndexOfLastWatched);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp_3;
          _tmp_3 = cursor.getInt(_cursorIndexOfIsDownloaded);
          _tmpIsDownloaded = _tmp_3 != 0;
          _item = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public PagingSource<Integer, MediaItem> getContinueWatchingPaging() {
    final String _sql = "SELECT * FROM media_items WHERE watch_progress > 0 AND watch_progress < 1 ORDER BY last_watched DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new LimitOffsetPagingSource<MediaItem>(_statement, __db, "media_items") {
      @Override
      @NonNull
      protected List<MediaItem> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(cursor, "title");
        final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(cursor, "media_type");
        final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(cursor, "year");
        final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(cursor, "description");
        final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(cursor, "cover_image");
        final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(cursor, "rating");
        final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(cursor, "quality");
        final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(cursor, "file_size");
        final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(cursor, "duration");
        final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(cursor, "directory_path");
        final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(cursor, "smb_path");
        final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "created_at");
        final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "updated_at");
        final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(cursor, "external_metadata");
        final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(cursor, "versions");
        final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(cursor, "is_favorite");
        final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(cursor, "watch_progress");
        final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(cursor, "last_watched");
        final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(cursor, "is_downloaded");
        final List<MediaItem> _result = new ArrayList<MediaItem>(cursor.getCount());
        while (cursor.moveToNext()) {
          final MediaItem _item;
          final long _tmpId;
          _tmpId = cursor.getLong(_cursorIndexOfId);
          final String _tmpTitle;
          if (cursor.isNull(_cursorIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = cursor.getString(_cursorIndexOfTitle);
          }
          final String _tmpMediaType;
          if (cursor.isNull(_cursorIndexOfMediaType)) {
            _tmpMediaType = null;
          } else {
            _tmpMediaType = cursor.getString(_cursorIndexOfMediaType);
          }
          final Integer _tmpYear;
          if (cursor.isNull(_cursorIndexOfYear)) {
            _tmpYear = null;
          } else {
            _tmpYear = cursor.getInt(_cursorIndexOfYear);
          }
          final String _tmpDescription;
          if (cursor.isNull(_cursorIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = cursor.getString(_cursorIndexOfDescription);
          }
          final String _tmpCoverImage;
          if (cursor.isNull(_cursorIndexOfCoverImage)) {
            _tmpCoverImage = null;
          } else {
            _tmpCoverImage = cursor.getString(_cursorIndexOfCoverImage);
          }
          final Double _tmpRating;
          if (cursor.isNull(_cursorIndexOfRating)) {
            _tmpRating = null;
          } else {
            _tmpRating = cursor.getDouble(_cursorIndexOfRating);
          }
          final String _tmpQuality;
          if (cursor.isNull(_cursorIndexOfQuality)) {
            _tmpQuality = null;
          } else {
            _tmpQuality = cursor.getString(_cursorIndexOfQuality);
          }
          final Long _tmpFileSize;
          if (cursor.isNull(_cursorIndexOfFileSize)) {
            _tmpFileSize = null;
          } else {
            _tmpFileSize = cursor.getLong(_cursorIndexOfFileSize);
          }
          final Integer _tmpDuration;
          if (cursor.isNull(_cursorIndexOfDuration)) {
            _tmpDuration = null;
          } else {
            _tmpDuration = cursor.getInt(_cursorIndexOfDuration);
          }
          final String _tmpDirectoryPath;
          if (cursor.isNull(_cursorIndexOfDirectoryPath)) {
            _tmpDirectoryPath = null;
          } else {
            _tmpDirectoryPath = cursor.getString(_cursorIndexOfDirectoryPath);
          }
          final String _tmpSmbPath;
          if (cursor.isNull(_cursorIndexOfSmbPath)) {
            _tmpSmbPath = null;
          } else {
            _tmpSmbPath = cursor.getString(_cursorIndexOfSmbPath);
          }
          final String _tmpCreatedAt;
          if (cursor.isNull(_cursorIndexOfCreatedAt)) {
            _tmpCreatedAt = null;
          } else {
            _tmpCreatedAt = cursor.getString(_cursorIndexOfCreatedAt);
          }
          final String _tmpUpdatedAt;
          if (cursor.isNull(_cursorIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null;
          } else {
            _tmpUpdatedAt = cursor.getString(_cursorIndexOfUpdatedAt);
          }
          final List<ExternalMetadata> _tmpExternalMetadata;
          final String _tmp;
          if (cursor.isNull(_cursorIndexOfExternalMetadata)) {
            _tmp = null;
          } else {
            _tmp = cursor.getString(_cursorIndexOfExternalMetadata);
          }
          _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
          final List<MediaVersion> _tmpVersions;
          final String _tmp_1;
          if (cursor.isNull(_cursorIndexOfVersions)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = cursor.getString(_cursorIndexOfVersions);
          }
          _tmpVersions = __converters.toMediaVersionList(_tmp_1);
          final boolean _tmpIsFavorite;
          final int _tmp_2;
          _tmp_2 = cursor.getInt(_cursorIndexOfIsFavorite);
          _tmpIsFavorite = _tmp_2 != 0;
          final double _tmpWatchProgress;
          _tmpWatchProgress = cursor.getDouble(_cursorIndexOfWatchProgress);
          final String _tmpLastWatched;
          if (cursor.isNull(_cursorIndexOfLastWatched)) {
            _tmpLastWatched = null;
          } else {
            _tmpLastWatched = cursor.getString(_cursorIndexOfLastWatched);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp_3;
          _tmp_3 = cursor.getInt(_cursorIndexOfIsDownloaded);
          _tmpIsDownloaded = _tmp_3 != 0;
          _item = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public Flow<List<MediaItem>> getRecentlyAdded(final int limit) {
    final String _sql = "SELECT * FROM media_items ORDER BY created_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"media_items"}, new Callable<List<MediaItem>>() {
      @Override
      @NonNull
      public List<MediaItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_image");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(_cursor, "directory_path");
          final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(_cursor, "smb_path");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "external_metadata");
          final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(_cursor, "versions");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "watch_progress");
          final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(_cursor, "last_watched");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "is_downloaded");
          final List<MediaItem> _result = new ArrayList<MediaItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MediaItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpMediaType;
            if (_cursor.isNull(_cursorIndexOfMediaType)) {
              _tmpMediaType = null;
            } else {
              _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpCoverImage;
            if (_cursor.isNull(_cursorIndexOfCoverImage)) {
              _tmpCoverImage = null;
            } else {
              _tmpCoverImage = _cursor.getString(_cursorIndexOfCoverImage);
            }
            final Double _tmpRating;
            if (_cursor.isNull(_cursorIndexOfRating)) {
              _tmpRating = null;
            } else {
              _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            }
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Integer _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            }
            final String _tmpDirectoryPath;
            if (_cursor.isNull(_cursorIndexOfDirectoryPath)) {
              _tmpDirectoryPath = null;
            } else {
              _tmpDirectoryPath = _cursor.getString(_cursorIndexOfDirectoryPath);
            }
            final String _tmpSmbPath;
            if (_cursor.isNull(_cursorIndexOfSmbPath)) {
              _tmpSmbPath = null;
            } else {
              _tmpSmbPath = _cursor.getString(_cursorIndexOfSmbPath);
            }
            final String _tmpCreatedAt;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmpCreatedAt = null;
            } else {
              _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final String _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            }
            final List<ExternalMetadata> _tmpExternalMetadata;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfExternalMetadata)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfExternalMetadata);
            }
            _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
            final List<MediaVersion> _tmpVersions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfVersions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfVersions);
            }
            _tmpVersions = __converters.toMediaVersionList(_tmp_1);
            final boolean _tmpIsFavorite;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_2 != 0;
            final double _tmpWatchProgress;
            _tmpWatchProgress = _cursor.getDouble(_cursorIndexOfWatchProgress);
            final String _tmpLastWatched;
            if (_cursor.isNull(_cursorIndexOfLastWatched)) {
              _tmpLastWatched = null;
            } else {
              _tmpLastWatched = _cursor.getString(_cursorIndexOfLastWatched);
            }
            final boolean _tmpIsDownloaded;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp_3 != 0;
            _item = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MediaItem>> getTopRated(final int limit) {
    final String _sql = "SELECT * FROM media_items WHERE rating IS NOT NULL ORDER BY rating DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"media_items"}, new Callable<List<MediaItem>>() {
      @Override
      @NonNull
      public List<MediaItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_image");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(_cursor, "directory_path");
          final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(_cursor, "smb_path");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "external_metadata");
          final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(_cursor, "versions");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "watch_progress");
          final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(_cursor, "last_watched");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "is_downloaded");
          final List<MediaItem> _result = new ArrayList<MediaItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MediaItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpMediaType;
            if (_cursor.isNull(_cursorIndexOfMediaType)) {
              _tmpMediaType = null;
            } else {
              _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpCoverImage;
            if (_cursor.isNull(_cursorIndexOfCoverImage)) {
              _tmpCoverImage = null;
            } else {
              _tmpCoverImage = _cursor.getString(_cursorIndexOfCoverImage);
            }
            final Double _tmpRating;
            if (_cursor.isNull(_cursorIndexOfRating)) {
              _tmpRating = null;
            } else {
              _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            }
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Integer _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            }
            final String _tmpDirectoryPath;
            if (_cursor.isNull(_cursorIndexOfDirectoryPath)) {
              _tmpDirectoryPath = null;
            } else {
              _tmpDirectoryPath = _cursor.getString(_cursorIndexOfDirectoryPath);
            }
            final String _tmpSmbPath;
            if (_cursor.isNull(_cursorIndexOfSmbPath)) {
              _tmpSmbPath = null;
            } else {
              _tmpSmbPath = _cursor.getString(_cursorIndexOfSmbPath);
            }
            final String _tmpCreatedAt;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmpCreatedAt = null;
            } else {
              _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final String _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            }
            final List<ExternalMetadata> _tmpExternalMetadata;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfExternalMetadata)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfExternalMetadata);
            }
            _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
            final List<MediaVersion> _tmpVersions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfVersions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfVersions);
            }
            _tmpVersions = __converters.toMediaVersionList(_tmp_1);
            final boolean _tmpIsFavorite;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_2 != 0;
            final double _tmpWatchProgress;
            _tmpWatchProgress = _cursor.getDouble(_cursorIndexOfWatchProgress);
            final String _tmpLastWatched;
            if (_cursor.isNull(_cursorIndexOfLastWatched)) {
              _tmpLastWatched = null;
            } else {
              _tmpLastWatched = _cursor.getString(_cursorIndexOfLastWatched);
            }
            final boolean _tmpIsDownloaded;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp_3 != 0;
            _item = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<String>> getAllMediaTypes() {
    final String _sql = "SELECT DISTINCT media_type FROM media_items ORDER BY media_type";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"media_items"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            if (_cursor.isNull(0)) {
              _item = null;
            } else {
              _item = _cursor.getString(0);
            }
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getTotalCount() {
    final String _sql = "SELECT COUNT(*) FROM media_items";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"media_items"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getCountByType(final String mediaType) {
    final String _sql = "SELECT COUNT(*) FROM media_items WHERE media_type = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (mediaType == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, mediaType);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"media_items"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllCached(final Continuation<? super List<MediaItem>> $completion) {
    final String _sql = "SELECT * FROM media_items ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MediaItem>>() {
      @Override
      @NonNull
      public List<MediaItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_image");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(_cursor, "directory_path");
          final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(_cursor, "smb_path");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "external_metadata");
          final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(_cursor, "versions");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "watch_progress");
          final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(_cursor, "last_watched");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "is_downloaded");
          final List<MediaItem> _result = new ArrayList<MediaItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MediaItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpMediaType;
            if (_cursor.isNull(_cursorIndexOfMediaType)) {
              _tmpMediaType = null;
            } else {
              _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpCoverImage;
            if (_cursor.isNull(_cursorIndexOfCoverImage)) {
              _tmpCoverImage = null;
            } else {
              _tmpCoverImage = _cursor.getString(_cursorIndexOfCoverImage);
            }
            final Double _tmpRating;
            if (_cursor.isNull(_cursorIndexOfRating)) {
              _tmpRating = null;
            } else {
              _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            }
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Integer _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            }
            final String _tmpDirectoryPath;
            if (_cursor.isNull(_cursorIndexOfDirectoryPath)) {
              _tmpDirectoryPath = null;
            } else {
              _tmpDirectoryPath = _cursor.getString(_cursorIndexOfDirectoryPath);
            }
            final String _tmpSmbPath;
            if (_cursor.isNull(_cursorIndexOfSmbPath)) {
              _tmpSmbPath = null;
            } else {
              _tmpSmbPath = _cursor.getString(_cursorIndexOfSmbPath);
            }
            final String _tmpCreatedAt;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmpCreatedAt = null;
            } else {
              _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final String _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            }
            final List<ExternalMetadata> _tmpExternalMetadata;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfExternalMetadata)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfExternalMetadata);
            }
            _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
            final List<MediaVersion> _tmpVersions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfVersions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfVersions);
            }
            _tmpVersions = __converters.toMediaVersionList(_tmp_1);
            final boolean _tmpIsFavorite;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_2 != 0;
            final double _tmpWatchProgress;
            _tmpWatchProgress = _cursor.getDouble(_cursorIndexOfWatchProgress);
            final String _tmpLastWatched;
            if (_cursor.isNull(_cursorIndexOfLastWatched)) {
              _tmpLastWatched = null;
            } else {
              _tmpLastWatched = _cursor.getString(_cursorIndexOfLastWatched);
            }
            final boolean _tmpIsDownloaded;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp_3 != 0;
            _item = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super MediaItem> $completion) {
    final String _sql = "SELECT * FROM media_items WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MediaItem>() {
      @Override
      @Nullable
      public MediaItem call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_image");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(_cursor, "directory_path");
          final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(_cursor, "smb_path");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "external_metadata");
          final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(_cursor, "versions");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "watch_progress");
          final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(_cursor, "last_watched");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "is_downloaded");
          final MediaItem _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpMediaType;
            if (_cursor.isNull(_cursorIndexOfMediaType)) {
              _tmpMediaType = null;
            } else {
              _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpCoverImage;
            if (_cursor.isNull(_cursorIndexOfCoverImage)) {
              _tmpCoverImage = null;
            } else {
              _tmpCoverImage = _cursor.getString(_cursorIndexOfCoverImage);
            }
            final Double _tmpRating;
            if (_cursor.isNull(_cursorIndexOfRating)) {
              _tmpRating = null;
            } else {
              _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            }
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Integer _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            }
            final String _tmpDirectoryPath;
            if (_cursor.isNull(_cursorIndexOfDirectoryPath)) {
              _tmpDirectoryPath = null;
            } else {
              _tmpDirectoryPath = _cursor.getString(_cursorIndexOfDirectoryPath);
            }
            final String _tmpSmbPath;
            if (_cursor.isNull(_cursorIndexOfSmbPath)) {
              _tmpSmbPath = null;
            } else {
              _tmpSmbPath = _cursor.getString(_cursorIndexOfSmbPath);
            }
            final String _tmpCreatedAt;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmpCreatedAt = null;
            } else {
              _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final String _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            }
            final List<ExternalMetadata> _tmpExternalMetadata;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfExternalMetadata)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfExternalMetadata);
            }
            _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
            final List<MediaVersion> _tmpVersions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfVersions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfVersions);
            }
            _tmpVersions = __converters.toMediaVersionList(_tmp_1);
            final boolean _tmpIsFavorite;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_2 != 0;
            final double _tmpWatchProgress;
            _tmpWatchProgress = _cursor.getDouble(_cursorIndexOfWatchProgress);
            final String _tmpLastWatched;
            if (_cursor.isNull(_cursorIndexOfLastWatched)) {
              _tmpLastWatched = null;
            } else {
              _tmpLastWatched = _cursor.getString(_cursorIndexOfLastWatched);
            }
            final boolean _tmpIsDownloaded;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp_3 != 0;
            _result = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByType(final String type,
      final Continuation<? super List<MediaItem>> $completion) {
    final String _sql = "SELECT * FROM media_items WHERE media_type = ? ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (type == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, type);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MediaItem>>() {
      @Override
      @NonNull
      public List<MediaItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_image");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(_cursor, "directory_path");
          final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(_cursor, "smb_path");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "external_metadata");
          final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(_cursor, "versions");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "watch_progress");
          final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(_cursor, "last_watched");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "is_downloaded");
          final List<MediaItem> _result = new ArrayList<MediaItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MediaItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpMediaType;
            if (_cursor.isNull(_cursorIndexOfMediaType)) {
              _tmpMediaType = null;
            } else {
              _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpCoverImage;
            if (_cursor.isNull(_cursorIndexOfCoverImage)) {
              _tmpCoverImage = null;
            } else {
              _tmpCoverImage = _cursor.getString(_cursorIndexOfCoverImage);
            }
            final Double _tmpRating;
            if (_cursor.isNull(_cursorIndexOfRating)) {
              _tmpRating = null;
            } else {
              _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            }
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Integer _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            }
            final String _tmpDirectoryPath;
            if (_cursor.isNull(_cursorIndexOfDirectoryPath)) {
              _tmpDirectoryPath = null;
            } else {
              _tmpDirectoryPath = _cursor.getString(_cursorIndexOfDirectoryPath);
            }
            final String _tmpSmbPath;
            if (_cursor.isNull(_cursorIndexOfSmbPath)) {
              _tmpSmbPath = null;
            } else {
              _tmpSmbPath = _cursor.getString(_cursorIndexOfSmbPath);
            }
            final String _tmpCreatedAt;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmpCreatedAt = null;
            } else {
              _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final String _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            }
            final List<ExternalMetadata> _tmpExternalMetadata;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfExternalMetadata)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfExternalMetadata);
            }
            _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
            final List<MediaVersion> _tmpVersions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfVersions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfVersions);
            }
            _tmpVersions = __converters.toMediaVersionList(_tmp_1);
            final boolean _tmpIsFavorite;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_2 != 0;
            final double _tmpWatchProgress;
            _tmpWatchProgress = _cursor.getDouble(_cursorIndexOfWatchProgress);
            final String _tmpLastWatched;
            if (_cursor.isNull(_cursorIndexOfLastWatched)) {
              _tmpLastWatched = null;
            } else {
              _tmpLastWatched = _cursor.getString(_cursorIndexOfLastWatched);
            }
            final boolean _tmpIsDownloaded;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp_3 != 0;
            _item = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchCached(final String query,
      final Continuation<? super List<MediaItem>> $completion) {
    final String _sql = "SELECT * FROM media_items WHERE title LIKE '%' || ? || '%' OR description LIKE '%' || ? || '%' ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    _argIndex = 2;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MediaItem>>() {
      @Override
      @NonNull
      public List<MediaItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_image");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfDirectoryPath = CursorUtil.getColumnIndexOrThrow(_cursor, "directory_path");
          final int _cursorIndexOfSmbPath = CursorUtil.getColumnIndexOrThrow(_cursor, "smb_path");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfExternalMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "external_metadata");
          final int _cursorIndexOfVersions = CursorUtil.getColumnIndexOrThrow(_cursor, "versions");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfWatchProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "watch_progress");
          final int _cursorIndexOfLastWatched = CursorUtil.getColumnIndexOrThrow(_cursor, "last_watched");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "is_downloaded");
          final List<MediaItem> _result = new ArrayList<MediaItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MediaItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpMediaType;
            if (_cursor.isNull(_cursorIndexOfMediaType)) {
              _tmpMediaType = null;
            } else {
              _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpCoverImage;
            if (_cursor.isNull(_cursorIndexOfCoverImage)) {
              _tmpCoverImage = null;
            } else {
              _tmpCoverImage = _cursor.getString(_cursorIndexOfCoverImage);
            }
            final Double _tmpRating;
            if (_cursor.isNull(_cursorIndexOfRating)) {
              _tmpRating = null;
            } else {
              _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            }
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Integer _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            }
            final String _tmpDirectoryPath;
            if (_cursor.isNull(_cursorIndexOfDirectoryPath)) {
              _tmpDirectoryPath = null;
            } else {
              _tmpDirectoryPath = _cursor.getString(_cursorIndexOfDirectoryPath);
            }
            final String _tmpSmbPath;
            if (_cursor.isNull(_cursorIndexOfSmbPath)) {
              _tmpSmbPath = null;
            } else {
              _tmpSmbPath = _cursor.getString(_cursorIndexOfSmbPath);
            }
            final String _tmpCreatedAt;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmpCreatedAt = null;
            } else {
              _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final String _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            }
            final List<ExternalMetadata> _tmpExternalMetadata;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfExternalMetadata)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfExternalMetadata);
            }
            _tmpExternalMetadata = __converters.toExternalMetadataList(_tmp);
            final List<MediaVersion> _tmpVersions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfVersions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfVersions);
            }
            _tmpVersions = __converters.toMediaVersionList(_tmp_1);
            final boolean _tmpIsFavorite;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_2 != 0;
            final double _tmpWatchProgress;
            _tmpWatchProgress = _cursor.getDouble(_cursorIndexOfWatchProgress);
            final String _tmpLastWatched;
            if (_cursor.isNull(_cursorIndexOfLastWatched)) {
              _tmpLastWatched = null;
            } else {
              _tmpLastWatched = _cursor.getString(_cursorIndexOfLastWatched);
            }
            final boolean _tmpIsDownloaded;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp_3 != 0;
            _item = new MediaItem(_tmpId,_tmpTitle,_tmpMediaType,_tmpYear,_tmpDescription,_tmpCoverImage,_tmpRating,_tmpQuality,_tmpFileSize,_tmpDuration,_tmpDirectoryPath,_tmpSmbPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpExternalMetadata,_tmpVersions,_tmpIsFavorite,_tmpWatchProgress,_tmpLastWatched,_tmpIsDownloaded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCachedItemsCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM media_items";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalDownloadSize(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(file_size) FROM media_items WHERE is_downloaded = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
