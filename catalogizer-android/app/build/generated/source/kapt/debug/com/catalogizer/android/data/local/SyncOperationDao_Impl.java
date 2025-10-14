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
import com.catalogizer.android.data.sync.SyncOperation;
import com.catalogizer.android.data.sync.SyncOperationType;
import java.lang.Class;
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
public final class SyncOperationDao_Impl implements SyncOperationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SyncOperation> __insertionAdapterOfSyncOperation;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<SyncOperation> __deletionAdapterOfSyncOperation;

  private final EntityDeletionOrUpdateAdapter<SyncOperation> __updateAdapterOfSyncOperation;

  private final SharedSQLiteStatement __preparedStmtOfUpdateRetryCount;

  private final SharedSQLiteStatement __preparedStmtOfResetRetryCount;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOperation;

  private final SharedSQLiteStatement __preparedStmtOfDeleteFailedOperations;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOperationsByMediaAndType;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllOperations;

  private final SharedSQLiteStatement __preparedStmtOfCleanupOldOperations;

  public SyncOperationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSyncOperation = new EntityInsertionAdapter<SyncOperation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sync_operations` (`id`,`type`,`mediaId`,`data`,`timestamp`,`retryCount`,`maxRetries`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncOperation entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromSyncOperationType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp);
        }
        statement.bindLong(3, entity.getMediaId());
        if (entity.getData() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getData());
        }
        statement.bindLong(5, entity.getTimestamp());
        statement.bindLong(6, entity.getRetryCount());
        statement.bindLong(7, entity.getMaxRetries());
      }
    };
    this.__deletionAdapterOfSyncOperation = new EntityDeletionOrUpdateAdapter<SyncOperation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `sync_operations` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncOperation entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfSyncOperation = new EntityDeletionOrUpdateAdapter<SyncOperation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sync_operations` SET `id` = ?,`type` = ?,`mediaId` = ?,`data` = ?,`timestamp` = ?,`retryCount` = ?,`maxRetries` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncOperation entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromSyncOperationType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp);
        }
        statement.bindLong(3, entity.getMediaId());
        if (entity.getData() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getData());
        }
        statement.bindLong(5, entity.getTimestamp());
        statement.bindLong(6, entity.getRetryCount());
        statement.bindLong(7, entity.getMaxRetries());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateRetryCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sync_operations SET retryCount = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfResetRetryCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sync_operations SET retryCount = 0";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOperation = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sync_operations WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteFailedOperations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sync_operations WHERE retryCount >= ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOperationsByMediaAndType = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sync_operations WHERE mediaId = ? AND type = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllOperations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sync_operations";
        return _query;
      }
    };
    this.__preparedStmtOfCleanupOldOperations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sync_operations WHERE timestamp < ? AND retryCount >= maxRetries";
        return _query;
      }
    };
  }

  @Override
  public Object insertOperation(final SyncOperation operation,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSyncOperation.insertAndReturnId(operation);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertOperations(final List<SyncOperation> operations,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfSyncOperation.insertAndReturnIdsList(operations);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOperation(final SyncOperation operation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSyncOperation.handle(operation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateOperation(final SyncOperation operation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSyncOperation.handle(operation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRetryCount(final long operationId, final int retryCount,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateRetryCount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, retryCount);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, operationId);
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
          __preparedStmtOfUpdateRetryCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object resetRetryCount(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfResetRetryCount.acquire();
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
          __preparedStmtOfResetRetryCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOperation(final long operationId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOperation.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, operationId);
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
          __preparedStmtOfDeleteOperation.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteFailedOperations(final int maxRetries,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteFailedOperations.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, maxRetries);
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
          __preparedStmtOfDeleteFailedOperations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOperationsByMediaAndType(final long mediaId, final SyncOperationType type,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOperationsByMediaAndType.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, mediaId);
        _argIndex = 2;
        final String _tmp = __converters.fromSyncOperationType(type);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
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
          __preparedStmtOfDeleteOperationsByMediaAndType.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllOperations(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllOperations.acquire();
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
          __preparedStmtOfDeleteAllOperations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object cleanupOldOperations(final long cutoffTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfCleanupOldOperations.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoffTime);
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
          __preparedStmtOfCleanupOldOperations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllOperations(final Continuation<? super List<SyncOperation>> $completion) {
    final String _sql = "SELECT * FROM sync_operations ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SyncOperation>>() {
      @Override
      @NonNull
      public List<SyncOperation> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMediaId = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaId");
          final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "data");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfMaxRetries = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRetries");
          final List<SyncOperation> _result = new ArrayList<SyncOperation>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncOperation _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SyncOperationType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toSyncOperationType(_tmp);
            final long _tmpMediaId;
            _tmpMediaId = _cursor.getLong(_cursorIndexOfMediaId);
            final String _tmpData;
            if (_cursor.isNull(_cursorIndexOfData)) {
              _tmpData = null;
            } else {
              _tmpData = _cursor.getString(_cursorIndexOfData);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final int _tmpMaxRetries;
            _tmpMaxRetries = _cursor.getInt(_cursorIndexOfMaxRetries);
            _item = new SyncOperation(_tmpId,_tmpType,_tmpMediaId,_tmpData,_tmpTimestamp,_tmpRetryCount,_tmpMaxRetries);
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
  public Object getPendingOperations(final Continuation<? super List<SyncOperation>> $completion) {
    final String _sql = "SELECT * FROM sync_operations WHERE retryCount < maxRetries ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SyncOperation>>() {
      @Override
      @NonNull
      public List<SyncOperation> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMediaId = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaId");
          final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "data");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfMaxRetries = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRetries");
          final List<SyncOperation> _result = new ArrayList<SyncOperation>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncOperation _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SyncOperationType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toSyncOperationType(_tmp);
            final long _tmpMediaId;
            _tmpMediaId = _cursor.getLong(_cursorIndexOfMediaId);
            final String _tmpData;
            if (_cursor.isNull(_cursorIndexOfData)) {
              _tmpData = null;
            } else {
              _tmpData = _cursor.getString(_cursorIndexOfData);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final int _tmpMaxRetries;
            _tmpMaxRetries = _cursor.getInt(_cursorIndexOfMaxRetries);
            _item = new SyncOperation(_tmpId,_tmpType,_tmpMediaId,_tmpData,_tmpTimestamp,_tmpRetryCount,_tmpMaxRetries);
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
  public Object getPendingOperationsCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM sync_operations WHERE retryCount < maxRetries";
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
  public Flow<Integer> getPendingOperationsCountFlow() {
    final String _sql = "SELECT COUNT(*) FROM sync_operations WHERE retryCount < maxRetries";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sync_operations"}, new Callable<Integer>() {
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
  public Object getOperationByMediaAndType(final long mediaId, final SyncOperationType type,
      final Continuation<? super SyncOperation> $completion) {
    final String _sql = "SELECT * FROM sync_operations WHERE mediaId = ? AND type = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, mediaId);
    _argIndex = 2;
    final String _tmp = __converters.fromSyncOperationType(type);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SyncOperation>() {
      @Override
      @Nullable
      public SyncOperation call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMediaId = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaId");
          final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "data");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfMaxRetries = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRetries");
          final SyncOperation _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SyncOperationType _tmpType;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toSyncOperationType(_tmp_1);
            final long _tmpMediaId;
            _tmpMediaId = _cursor.getLong(_cursorIndexOfMediaId);
            final String _tmpData;
            if (_cursor.isNull(_cursorIndexOfData)) {
              _tmpData = null;
            } else {
              _tmpData = _cursor.getString(_cursorIndexOfData);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final int _tmpMaxRetries;
            _tmpMaxRetries = _cursor.getInt(_cursorIndexOfMaxRetries);
            _result = new SyncOperation(_tmpId,_tmpType,_tmpMediaId,_tmpData,_tmpTimestamp,_tmpRetryCount,_tmpMaxRetries);
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
  public Object getFailedOperations(final Continuation<? super List<SyncOperation>> $completion) {
    final String _sql = "SELECT * FROM sync_operations WHERE retryCount >= maxRetries";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SyncOperation>>() {
      @Override
      @NonNull
      public List<SyncOperation> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMediaId = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaId");
          final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "data");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfMaxRetries = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRetries");
          final List<SyncOperation> _result = new ArrayList<SyncOperation>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncOperation _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SyncOperationType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toSyncOperationType(_tmp);
            final long _tmpMediaId;
            _tmpMediaId = _cursor.getLong(_cursorIndexOfMediaId);
            final String _tmpData;
            if (_cursor.isNull(_cursorIndexOfData)) {
              _tmpData = null;
            } else {
              _tmpData = _cursor.getString(_cursorIndexOfData);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final int _tmpMaxRetries;
            _tmpMaxRetries = _cursor.getInt(_cursorIndexOfMaxRetries);
            _item = new SyncOperation(_tmpId,_tmpType,_tmpMediaId,_tmpData,_tmpTimestamp,_tmpRetryCount,_tmpMaxRetries);
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
  public Object getFailedOperationsCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM sync_operations WHERE retryCount >= maxRetries";
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
  public Object getOperationsByType(final SyncOperationType type,
      final Continuation<? super List<SyncOperation>> $completion) {
    final String _sql = "SELECT * FROM sync_operations WHERE type = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromSyncOperationType(type);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SyncOperation>>() {
      @Override
      @NonNull
      public List<SyncOperation> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMediaId = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaId");
          final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "data");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfMaxRetries = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRetries");
          final List<SyncOperation> _result = new ArrayList<SyncOperation>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncOperation _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SyncOperationType _tmpType;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toSyncOperationType(_tmp_1);
            final long _tmpMediaId;
            _tmpMediaId = _cursor.getLong(_cursorIndexOfMediaId);
            final String _tmpData;
            if (_cursor.isNull(_cursorIndexOfData)) {
              _tmpData = null;
            } else {
              _tmpData = _cursor.getString(_cursorIndexOfData);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final int _tmpMaxRetries;
            _tmpMaxRetries = _cursor.getInt(_cursorIndexOfMaxRetries);
            _item = new SyncOperation(_tmpId,_tmpType,_tmpMediaId,_tmpData,_tmpTimestamp,_tmpRetryCount,_tmpMaxRetries);
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
  public Object getOperationsForMedia(final long mediaId,
      final Continuation<? super List<SyncOperation>> $completion) {
    final String _sql = "SELECT * FROM sync_operations WHERE mediaId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, mediaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SyncOperation>>() {
      @Override
      @NonNull
      public List<SyncOperation> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMediaId = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaId");
          final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "data");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfMaxRetries = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRetries");
          final List<SyncOperation> _result = new ArrayList<SyncOperation>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncOperation _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SyncOperationType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toSyncOperationType(_tmp);
            final long _tmpMediaId;
            _tmpMediaId = _cursor.getLong(_cursorIndexOfMediaId);
            final String _tmpData;
            if (_cursor.isNull(_cursorIndexOfData)) {
              _tmpData = null;
            } else {
              _tmpData = _cursor.getString(_cursorIndexOfData);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final int _tmpMaxRetries;
            _tmpMaxRetries = _cursor.getInt(_cursorIndexOfMaxRetries);
            _item = new SyncOperation(_tmpId,_tmpType,_tmpMediaId,_tmpData,_tmpTimestamp,_tmpRetryCount,_tmpMaxRetries);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
