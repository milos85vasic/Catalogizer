package com.catalogizer.android.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DownloadDao_Impl implements DownloadDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DownloadItem> __insertionAdapterOfDownloadItem;

  private final EntityDeletionOrUpdateAdapter<DownloadItem> __deletionAdapterOfDownloadItem;

  private final EntityDeletionOrUpdateAdapter<DownloadItem> __updateAdapterOfDownloadItem;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDownloadProgress;

  private final SharedSQLiteStatement __preparedStmtOfDeleteDownloadByMediaId;

  private final SharedSQLiteStatement __preparedStmtOfDeleteDownloadsByStatus;

  public DownloadDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDownloadItem = new EntityInsertionAdapter<DownloadItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `download_items` (`media_id`,`title`,`coverImage`,`downloadUrl`,`localPath`,`progress`,`status`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DownloadItem entity) {
        statement.bindLong(1, entity.getMediaId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getCoverImage() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getCoverImage());
        }
        if (entity.getDownloadUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDownloadUrl());
        }
        if (entity.getLocalPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getLocalPath());
        }
        statement.bindDouble(6, entity.getProgress());
        statement.bindString(7, __DownloadStatus_enumToString(entity.getStatus()));
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfDownloadItem = new EntityDeletionOrUpdateAdapter<DownloadItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `download_items` WHERE `media_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DownloadItem entity) {
        statement.bindLong(1, entity.getMediaId());
      }
    };
    this.__updateAdapterOfDownloadItem = new EntityDeletionOrUpdateAdapter<DownloadItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `download_items` SET `media_id` = ?,`title` = ?,`coverImage` = ?,`downloadUrl` = ?,`localPath` = ?,`progress` = ?,`status` = ?,`created_at` = ?,`updated_at` = ? WHERE `media_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DownloadItem entity) {
        statement.bindLong(1, entity.getMediaId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getCoverImage() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getCoverImage());
        }
        if (entity.getDownloadUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDownloadUrl());
        }
        if (entity.getLocalPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getLocalPath());
        }
        statement.bindDouble(6, entity.getProgress());
        statement.bindString(7, __DownloadStatus_enumToString(entity.getStatus()));
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
        statement.bindLong(10, entity.getMediaId());
      }
    };
    this.__preparedStmtOfUpdateDownloadProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE download_items SET progress = ?, status = ?, updated_at = ? WHERE media_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteDownloadByMediaId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM download_items WHERE media_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteDownloadsByStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM download_items WHERE status = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertDownload(final DownloadItem downloadItem,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDownloadItem.insert(downloadItem);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDownload(final DownloadItem downloadItem,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDownloadItem.handle(downloadItem);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDownload(final DownloadItem downloadItem,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDownloadItem.handle(downloadItem);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDownloadProgress(final long mediaId, final float progress,
      final DownloadStatus status, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDownloadProgress.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, progress);
        _argIndex = 2;
        _stmt.bindString(_argIndex, __DownloadStatus_enumToString(status));
        _argIndex = 3;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, mediaId);
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
          __preparedStmtOfUpdateDownloadProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDownloadByMediaId(final long mediaId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteDownloadByMediaId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, mediaId);
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
          __preparedStmtOfDeleteDownloadByMediaId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDownloadsByStatus(final DownloadStatus status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteDownloadsByStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, __DownloadStatus_enumToString(status));
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
          __preparedStmtOfDeleteDownloadsByStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DownloadItem>> getAllDownloads() {
    final String _sql = "SELECT * FROM download_items ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"download_items"}, new Callable<List<DownloadItem>>() {
      @Override
      @NonNull
      public List<DownloadItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMediaId = CursorUtil.getColumnIndexOrThrow(_cursor, "media_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(_cursor, "coverImage");
          final int _cursorIndexOfDownloadUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadUrl");
          final int _cursorIndexOfLocalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "localPath");
          final int _cursorIndexOfProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "progress");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<DownloadItem> _result = new ArrayList<DownloadItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DownloadItem _item;
            final long _tmpMediaId;
            _tmpMediaId = _cursor.getLong(_cursorIndexOfMediaId);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpCoverImage;
            if (_cursor.isNull(_cursorIndexOfCoverImage)) {
              _tmpCoverImage = null;
            } else {
              _tmpCoverImage = _cursor.getString(_cursorIndexOfCoverImage);
            }
            final String _tmpDownloadUrl;
            if (_cursor.isNull(_cursorIndexOfDownloadUrl)) {
              _tmpDownloadUrl = null;
            } else {
              _tmpDownloadUrl = _cursor.getString(_cursorIndexOfDownloadUrl);
            }
            final String _tmpLocalPath;
            if (_cursor.isNull(_cursorIndexOfLocalPath)) {
              _tmpLocalPath = null;
            } else {
              _tmpLocalPath = _cursor.getString(_cursorIndexOfLocalPath);
            }
            final float _tmpProgress;
            _tmpProgress = _cursor.getFloat(_cursorIndexOfProgress);
            final DownloadStatus _tmpStatus;
            _tmpStatus = __DownloadStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DownloadItem(_tmpMediaId,_tmpTitle,_tmpCoverImage,_tmpDownloadUrl,_tmpLocalPath,_tmpProgress,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DownloadItem>> getDownloadsByStatus(final DownloadStatus status) {
    final String _sql = "SELECT * FROM download_items WHERE status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, __DownloadStatus_enumToString(status));
    return CoroutinesRoom.createFlow(__db, false, new String[] {"download_items"}, new Callable<List<DownloadItem>>() {
      @Override
      @NonNull
      public List<DownloadItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMediaId = CursorUtil.getColumnIndexOrThrow(_cursor, "media_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(_cursor, "coverImage");
          final int _cursorIndexOfDownloadUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadUrl");
          final int _cursorIndexOfLocalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "localPath");
          final int _cursorIndexOfProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "progress");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<DownloadItem> _result = new ArrayList<DownloadItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DownloadItem _item;
            final long _tmpMediaId;
            _tmpMediaId = _cursor.getLong(_cursorIndexOfMediaId);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpCoverImage;
            if (_cursor.isNull(_cursorIndexOfCoverImage)) {
              _tmpCoverImage = null;
            } else {
              _tmpCoverImage = _cursor.getString(_cursorIndexOfCoverImage);
            }
            final String _tmpDownloadUrl;
            if (_cursor.isNull(_cursorIndexOfDownloadUrl)) {
              _tmpDownloadUrl = null;
            } else {
              _tmpDownloadUrl = _cursor.getString(_cursorIndexOfDownloadUrl);
            }
            final String _tmpLocalPath;
            if (_cursor.isNull(_cursorIndexOfLocalPath)) {
              _tmpLocalPath = null;
            } else {
              _tmpLocalPath = _cursor.getString(_cursorIndexOfLocalPath);
            }
            final float _tmpProgress;
            _tmpProgress = _cursor.getFloat(_cursorIndexOfProgress);
            final DownloadStatus _tmpStatus;
            _tmpStatus = __DownloadStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DownloadItem(_tmpMediaId,_tmpTitle,_tmpCoverImage,_tmpDownloadUrl,_tmpLocalPath,_tmpProgress,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getDownloadByMediaId(final long mediaId,
      final Continuation<? super DownloadItem> $completion) {
    final String _sql = "SELECT * FROM download_items WHERE media_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, mediaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DownloadItem>() {
      @Override
      @Nullable
      public DownloadItem call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMediaId = CursorUtil.getColumnIndexOrThrow(_cursor, "media_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfCoverImage = CursorUtil.getColumnIndexOrThrow(_cursor, "coverImage");
          final int _cursorIndexOfDownloadUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadUrl");
          final int _cursorIndexOfLocalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "localPath");
          final int _cursorIndexOfProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "progress");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final DownloadItem _result;
          if (_cursor.moveToFirst()) {
            final long _tmpMediaId;
            _tmpMediaId = _cursor.getLong(_cursorIndexOfMediaId);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpCoverImage;
            if (_cursor.isNull(_cursorIndexOfCoverImage)) {
              _tmpCoverImage = null;
            } else {
              _tmpCoverImage = _cursor.getString(_cursorIndexOfCoverImage);
            }
            final String _tmpDownloadUrl;
            if (_cursor.isNull(_cursorIndexOfDownloadUrl)) {
              _tmpDownloadUrl = null;
            } else {
              _tmpDownloadUrl = _cursor.getString(_cursorIndexOfDownloadUrl);
            }
            final String _tmpLocalPath;
            if (_cursor.isNull(_cursorIndexOfLocalPath)) {
              _tmpLocalPath = null;
            } else {
              _tmpLocalPath = _cursor.getString(_cursorIndexOfLocalPath);
            }
            final float _tmpProgress;
            _tmpProgress = _cursor.getFloat(_cursorIndexOfProgress);
            final DownloadStatus _tmpStatus;
            _tmpStatus = __DownloadStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DownloadItem(_tmpMediaId,_tmpTitle,_tmpCoverImage,_tmpDownloadUrl,_tmpLocalPath,_tmpProgress,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt);
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

  private String __DownloadStatus_enumToString(@NonNull final DownloadStatus _value) {
    switch (_value) {
      case PENDING: return "PENDING";
      case DOWNLOADING: return "DOWNLOADING";
      case COMPLETED: return "COMPLETED";
      case FAILED: return "FAILED";
      case PAUSED: return "PAUSED";
      case CANCELLED: return "CANCELLED";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private DownloadStatus __DownloadStatus_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "PENDING": return DownloadStatus.PENDING;
      case "DOWNLOADING": return DownloadStatus.DOWNLOADING;
      case "COMPLETED": return DownloadStatus.COMPLETED;
      case "FAILED": return DownloadStatus.FAILED;
      case "PAUSED": return DownloadStatus.PAUSED;
      case "CANCELLED": return DownloadStatus.CANCELLED;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
