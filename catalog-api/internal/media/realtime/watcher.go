package realtime

import (
	"catalog-api/internal/media/analyzer"
	"catalog-api/internal/media/database"
	"catalog-api/internal/media/models"
	"context"
	"encoding/json"
	"path/filepath"
	"sync"
	"time"

	"github.com/fsnotify/fsnotify"
	"go.uber.org/zap"
)

// SMBChangeWatcher monitors SMB shares for changes and triggers real-time analysis
type SMBChangeWatcher struct {
	mediaDB        *database.MediaDatabase
	analyzer       *analyzer.MediaAnalyzer
	logger         *zap.Logger
	watchers       map[string]*fsnotify.Watcher
	watcherMu      sync.RWMutex
	changeQueue    chan ChangeEvent
	workers        int
	stopCh         chan struct{}
	wg             sync.WaitGroup
	debounceMap    map[string]*time.Timer
	debounceMu     sync.Mutex
	debounceDelay  time.Duration
}

// ChangeEvent represents a file system change
type ChangeEvent struct {
	Path      string
	SmbRoot   string
	Operation string // created, modified, deleted, moved
	Timestamp time.Time
	Size      int64
	IsDir     bool
}

// NewSMBChangeWatcher creates a new change watcher
func NewSMBChangeWatcher(mediaDB *database.MediaDatabase, analyzer *analyzer.MediaAnalyzer, logger *zap.Logger) *SMBChangeWatcher {
	return &SMBChangeWatcher{
		mediaDB:       mediaDB,
		analyzer:      analyzer,
		logger:        logger,
		watchers:      make(map[string]*fsnotify.Watcher),
		changeQueue:   make(chan ChangeEvent, 10000),
		workers:       2,
		debounceMap:   make(map[string]*time.Timer),
		debounceDelay: 2 * time.Second, // Wait 2 seconds before processing changes
		stopCh:        make(chan struct{}),
	}
}

// Start starts the change watcher
func (w *SMBChangeWatcher) Start() error {
	w.logger.Info("Starting SMB change watcher", zap.Int("workers", w.workers))

	// Start worker goroutines
	for i := 0; i < w.workers; i++ {
		w.wg.Add(1)
		go w.changeWorker(i)
	}

	return nil
}

// Stop stops the change watcher
func (w *SMBChangeWatcher) Stop() {
	w.logger.Info("Stopping SMB change watcher")

	// Stop all file watchers
	w.watcherMu.Lock()
	for path, watcher := range w.watchers {
		watcher.Close()
		w.logger.Debug("Closed watcher", zap.String("path", path))
	}
	w.watchers = make(map[string]*fsnotify.Watcher)
	w.watcherMu.Unlock()

	// Stop workers
	close(w.stopCh)
	w.wg.Wait()

	w.logger.Info("SMB change watcher stopped")
}

// WatchSMBPath adds a new SMB path to watch
func (w *SMBChangeWatcher) WatchSMBPath(smbRoot, localMountPath string) error {
	w.watcherMu.Lock()
	defer w.watcherMu.Unlock()

	// Check if already watching
	if _, exists := w.watchers[smbRoot]; exists {
		return nil
	}

	// Create new watcher
	watcher, err := fsnotify.NewWatcher()
	if err != nil {
		return err
	}

	// Add path to watcher
	err = watcher.Add(localMountPath)
	if err != nil {
		watcher.Close()
		return err
	}

	w.watchers[smbRoot] = watcher

	// Start monitoring goroutine
	w.wg.Add(1)
	go w.monitorPath(smbRoot, localMountPath, watcher)

	w.logger.Info("Started watching SMB path",
		zap.String("smb_root", smbRoot),
		zap.String("local_path", localMountPath))

	return nil
}

// UnwatchSMBPath removes an SMB path from watching
func (w *SMBChangeWatcher) UnwatchSMBPath(smbRoot string) {
	w.watcherMu.Lock()
	defer w.watcherMu.Unlock()

	if watcher, exists := w.watchers[smbRoot]; exists {
		watcher.Close()
		delete(w.watchers, smbRoot)
		w.logger.Info("Stopped watching SMB path", zap.String("smb_root", smbRoot))
	}
}

// monitorPath monitors a specific path for changes
func (w *SMBChangeWatcher) monitorPath(smbRoot, localPath string, watcher *fsnotify.Watcher) {
	defer w.wg.Done()

	for {
		select {
		case <-w.stopCh:
			return

		case event, ok := <-watcher.Events:
			if !ok {
				return
			}

			w.handleFileSystemEvent(smbRoot, event)

		case err, ok := <-watcher.Errors:
			if !ok {
				return
			}
			w.logger.Error("File watcher error",
				zap.String("smb_root", smbRoot),
				zap.Error(err))
		}
	}
}

// handleFileSystemEvent processes a file system event
func (w *SMBChangeWatcher) handleFileSystemEvent(smbRoot string, event fsnotify.Event) {
	// Determine operation type
	var operation string
	switch {
	case event.Op&fsnotify.Create == fsnotify.Create:
		operation = "created"
	case event.Op&fsnotify.Write == fsnotify.Write:
		operation = "modified"
	case event.Op&fsnotify.Remove == fsnotify.Remove:
		operation = "deleted"
	case event.Op&fsnotify.Rename == fsnotify.Rename:
		operation = "moved"
	default:
		return // Ignore other events
	}

	// Get file info
	var size int64
	var isDir bool
	if operation != "deleted" {
		if info, err := filepath.Glob(event.Name); err == nil && len(info) > 0 {
			// This is a simplified check - in real implementation,
			// you'd use proper file stat
			isDir = filepath.Ext(event.Name) == ""
		}
	}

	changeEvent := ChangeEvent{
		Path:      event.Name,
		SmbRoot:   smbRoot,
		Operation: operation,
		Timestamp: time.Now(),
		Size:      size,
		IsDir:     isDir,
	}

	// Debounce changes to avoid processing rapid consecutive changes
	w.debounceChange(changeEvent)
}

// debounceChange debounces file changes to avoid excessive processing
func (w *SMBChangeWatcher) debounceChange(event ChangeEvent) {
	w.debounceMu.Lock()
	defer w.debounceMu.Unlock()

	key := event.SmbRoot + ":" + event.Path

	// Cancel existing timer
	if timer, exists := w.debounceMap[key]; exists {
		timer.Stop()
	}

	// Create new timer
	w.debounceMap[key] = time.AfterFunc(w.debounceDelay, func() {
		w.debounceMu.Lock()
		delete(w.debounceMap, key)
		w.debounceMu.Unlock()

		// Send to processing queue
		select {
		case w.changeQueue <- event:
		default:
			w.logger.Warn("Change queue full, dropping event",
				zap.String("path", event.Path),
				zap.String("operation", event.Operation))
		}
	})
}

// changeWorker processes change events
func (w *SMBChangeWatcher) changeWorker(workerID int) {
	defer w.wg.Done()

	w.logger.Info("Change worker started", zap.Int("worker_id", workerID))

	for {
		select {
		case <-w.stopCh:
			return

		case event := <-w.changeQueue:
			w.processChange(event, workerID)
		}
	}
}

// processChange processes a single change event
func (w *SMBChangeWatcher) processChange(event ChangeEvent, workerID int) {
	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()

	w.logger.Debug("Processing change event",
		zap.Int("worker_id", workerID),
		zap.String("path", event.Path),
		zap.String("operation", event.Operation),
		zap.String("smb_root", event.SmbRoot))

	// Log the change
	if err := w.logChange(event); err != nil {
		w.logger.Error("Failed to log change", zap.Error(err))
	}

	switch event.Operation {
	case "created", "modified":
		w.handleCreateOrModify(ctx, event)
	case "deleted":
		w.handleDelete(ctx, event)
	case "moved":
		w.handleMove(ctx, event)
	}
}

// logChange logs the change to the database
func (w *SMBChangeWatcher) logChange(event ChangeEvent) error {
	oldDataJSON, _ := json.Marshal(map[string]interface{}{
		"path":      event.Path,
		"operation": event.Operation,
		"timestamp": event.Timestamp,
		"size":      event.Size,
		"is_dir":    event.IsDir,
	})

	query := `
		INSERT INTO change_log (entity_type, entity_id, change_type, new_data, detected_at, processed)
		VALUES (?, ?, ?, ?, ?, ?)
	`

	_, err := w.mediaDB.GetDB().Exec(query,
		"file", event.Path, event.Operation, string(oldDataJSON), event.Timestamp, false)

	return err
}

// handleCreateOrModify handles file creation or modification
func (w *SMBChangeWatcher) handleCreateOrModify(ctx context.Context, event ChangeEvent) {
	if event.IsDir {
		// Directory change - trigger directory analysis
		err := w.analyzer.AnalyzeDirectory(ctx, event.Path, event.SmbRoot, 7) // High priority
		if err != nil {
			w.logger.Error("Failed to queue directory analysis",
				zap.String("path", event.Path),
				zap.Error(err))
		}
	} else {
		// File change - check if it belongs to existing media item
		w.handleFileChange(ctx, event)
	}
}

// handleDelete handles file or directory deletion
func (w *SMBChangeWatcher) handleDelete(ctx context.Context, event ChangeEvent) {
	// Mark files as missing in database
	query := `
		UPDATE media_files
		SET last_verified = ?, virtual_smb_link = NULL
		WHERE file_path = ? AND smb_root = ?
	`

	_, err := w.mediaDB.GetDB().Exec(query, time.Now(), event.Path, event.SmbRoot)
	if err != nil {
		w.logger.Error("Failed to mark file as deleted",
			zap.String("path", event.Path),
			zap.Error(err))
	}

	// Check if this affects any media items
	w.checkMediaItemIntegrity(ctx, event.Path, event.SmbRoot)
}

// handleMove handles file or directory moves
func (w *SMBChangeWatcher) handleMove(ctx context.Context, event ChangeEvent) {
	// This is complex to implement properly as we need both old and new paths
	// For now, treat as delete + create
	w.handleDelete(ctx, event)
}

// handleFileChange handles individual file changes
func (w *SMBChangeWatcher) handleFileChange(ctx context.Context, event ChangeEvent) {
	// Check if file belongs to existing media item
	query := `
		SELECT mf.media_item_id, mi.title, mi.media_type_id
		FROM media_files mf
		JOIN media_items mi ON mf.media_item_id = mi.id
		WHERE mf.file_path = ? AND mf.smb_root = ?
	`

	var mediaItemID, mediaTypeID int64
	var title string
	err := w.mediaDB.GetDB().QueryRow(query, event.Path, event.SmbRoot).Scan(&mediaItemID, &title, &mediaTypeID)

	if err == nil {
		// File belongs to existing media item - update verification timestamp
		updateQuery := `UPDATE media_files SET last_verified = ? WHERE file_path = ? AND smb_root = ?`
		w.mediaDB.GetDB().Exec(updateQuery, time.Now(), event.Path, event.SmbRoot)

		w.logger.Debug("Updated existing media file",
			zap.String("path", event.Path),
			zap.String("title", title),
			zap.Int64("media_item_id", mediaItemID))
	} else {
		// New file - analyze parent directory
		parentDir := filepath.Dir(event.Path)
		err := w.analyzer.AnalyzeDirectory(ctx, parentDir, event.SmbRoot, 6) // Medium-high priority
		if err != nil {
			w.logger.Error("Failed to queue directory analysis for new file",
				zap.String("file", event.Path),
				zap.String("parent_dir", parentDir),
				zap.Error(err))
		}
	}
}

// checkMediaItemIntegrity checks if a media item is still valid after file changes
func (w *SMBChangeWatcher) checkMediaItemIntegrity(ctx context.Context, filePath, smbRoot string) {
	// Find media items that might be affected
	query := `
		SELECT DISTINCT mi.id, mi.title, COUNT(mf.id) as file_count
		FROM media_items mi
		JOIN media_files mf ON mi.id = mf.media_item_id
		WHERE mf.smb_root = ? AND mf.file_path LIKE ?
		GROUP BY mi.id, mi.title
	`

	dirPattern := filepath.Dir(filePath) + "%"
	rows, err := w.mediaDB.GetDB().Query(query, smbRoot, dirPattern)
	if err != nil {
		w.logger.Error("Failed to check media item integrity", zap.Error(err))
		return
	}
	defer rows.Close()

	for rows.Next() {
		var mediaItemID int64
		var title string
		var fileCount int

		if err := rows.Scan(&mediaItemID, &title, &fileCount); err != nil {
			continue
		}

		// If media item has very few files remaining, mark for review
		if fileCount <= 1 {
			w.logger.Warn("Media item may be incomplete after file deletion",
				zap.Int64("media_item_id", mediaItemID),
				zap.String("title", title),
				zap.Int("remaining_files", fileCount))

			// Update media item status
			updateQuery := `UPDATE media_items SET status = 'missing', last_updated = ? WHERE id = ?`
			w.mediaDB.GetDB().Exec(updateQuery, time.Now(), mediaItemID)
		}
	}
}

// GetChangeStatistics returns statistics about recent changes
func (w *SMBChangeWatcher) GetChangeStatistics(since time.Time) (map[string]interface{}, error) {
	stats := make(map[string]interface{})

	// Count changes by type
	query := `
		SELECT change_type, COUNT(*) as count
		FROM change_log
		WHERE detected_at >= ?
		GROUP BY change_type
	`

	rows, err := w.mediaDB.GetDB().Query(query, since)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	changeTypes := make(map[string]int)
	for rows.Next() {
		var changeType string
		var count int
		if err := rows.Scan(&changeType, &count); err != nil {
			continue
		}
		changeTypes[changeType] = count
	}
	stats["changes_by_type"] = changeTypes

	// Count total changes
	var totalChanges int
	err = w.mediaDB.GetDB().QueryRow(
		"SELECT COUNT(*) FROM change_log WHERE detected_at >= ?", since).Scan(&totalChanges)
	if err == nil {
		stats["total_changes"] = totalChanges
	}

	// Count unprocessed changes
	var unprocessed int
	err = w.mediaDB.GetDB().QueryRow(
		"SELECT COUNT(*) FROM change_log WHERE processed = false").Scan(&unprocessed)
	if err == nil {
		stats["unprocessed_changes"] = unprocessed
	}

	return stats, nil
}

// ProcessPendingChanges processes any unprocessed changes
func (w *SMBChangeWatcher) ProcessPendingChanges(ctx context.Context) error {
	query := `
		SELECT id, entity_id, change_type, new_data, detected_at
		FROM change_log
		WHERE processed = false
		ORDER BY detected_at ASC
		LIMIT 100
	`

	rows, err := w.mediaDB.GetDB().Query(query)
	if err != nil {
		return err
	}
	defer rows.Close()

	var processedIDs []int64

	for rows.Next() {
		var id int64
		var entityID, changeType, newDataJSON string
		var detectedAt time.Time

		if err := rows.Scan(&id, &entityID, &changeType, &newDataJSON, &detectedAt); err != nil {
			continue
		}

		// Parse change data
		var changeData map[string]interface{}
		if err := json.Unmarshal([]byte(newDataJSON), &changeData); err != nil {
			continue
		}

		// Create change event
		event := ChangeEvent{
			Path:      entityID,
			Operation: changeType,
			Timestamp: detectedAt,
		}

		if smbRoot, ok := changeData["smb_root"].(string); ok {
			event.SmbRoot = smbRoot
		}
		if size, ok := changeData["size"].(float64); ok {
			event.Size = int64(size)
		}
		if isDir, ok := changeData["is_dir"].(bool); ok {
			event.IsDir = isDir
		}

		// Process the change
		w.processChange(event, 0)
		processedIDs = append(processedIDs, id)
	}

	// Mark changes as processed
	if len(processedIDs) > 0 {
		placeholders := strings.Repeat("?,", len(processedIDs))
		placeholders = placeholders[:len(placeholders)-1]

		updateQuery := "UPDATE change_log SET processed = true WHERE id IN (" + placeholders + ")"
		args := make([]interface{}, len(processedIDs))
		for i, id := range processedIDs {
			args[i] = id
		}

		_, err = w.mediaDB.GetDB().Exec(updateQuery, args...)
		if err != nil {
			w.logger.Error("Failed to mark changes as processed", zap.Error(err))
		} else {
			w.logger.Info("Processed pending changes", zap.Int("count", len(processedIDs)))
		}
	}

	return nil
}