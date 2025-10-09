package services

import (
	"context"
	"database/sql"
	"encoding/json"
	"fmt"
	"time"

	"go.uber.org/zap"
)

type PlaybackPositionService struct {
	db     *sql.DB
	logger *zap.Logger
}

type PlaybackPosition struct {
	ID              int64     `json:"id" db:"id"`
	UserID          int64     `json:"user_id" db:"user_id"`
	MediaItemID     int64     `json:"media_item_id" db:"media_item_id"`
	Position        int64     `json:"position" db:"position"` // Position in milliseconds
	Duration        int64     `json:"duration" db:"duration"` // Total duration in milliseconds
	PercentComplete float64   `json:"percent_complete" db:"percent_complete"`
	LastPlayed      time.Time `json:"last_played" db:"last_played"`
	IsCompleted     bool      `json:"is_completed" db:"is_completed"`
	DeviceInfo      string    `json:"device_info" db:"device_info"`
	PlaybackQuality string    `json:"playback_quality" db:"playback_quality"`
	CreatedAt       time.Time `json:"created_at" db:"created_at"`
	UpdatedAt       time.Time `json:"updated_at" db:"updated_at"`
}

type PlaybackBookmark struct {
	ID          int64     `json:"id" db:"id"`
	UserID      int64     `json:"user_id" db:"user_id"`
	MediaItemID int64     `json:"media_item_id" db:"media_item_id"`
	Position    int64     `json:"position" db:"position"`
	Name        string    `json:"name" db:"name"`
	Description string    `json:"description" db:"description"`
	CreatedAt   time.Time `json:"created_at" db:"created_at"`
}

type PlaybackHistory struct {
	ID              int64     `json:"id" db:"id"`
	UserID          int64     `json:"user_id" db:"user_id"`
	MediaItemID     int64     `json:"media_item_id" db:"media_item_id"`
	StartTime       time.Time `json:"start_time" db:"start_time"`
	EndTime         *time.Time `json:"end_time" db:"end_time"`
	Duration        int64     `json:"duration" db:"duration"`
	PercentWatched  float64   `json:"percent_watched" db:"percent_watched"`
	DeviceInfo      string    `json:"device_info" db:"device_info"`
	PlaybackQuality string    `json:"playback_quality" db:"playback_quality"`
	WasCompleted    bool      `json:"was_completed" db:"was_completed"`
}

type UpdatePositionRequest struct {
	UserID          int64  `json:"user_id"`
	MediaItemID     int64  `json:"media_item_id"`
	Position        int64  `json:"position"`
	Duration        int64  `json:"duration"`
	DeviceInfo      string `json:"device_info"`
	PlaybackQuality string `json:"playback_quality"`
}

type BookmarkRequest struct {
	UserID      int64  `json:"user_id"`
	MediaItemID int64  `json:"media_item_id"`
	Position    int64  `json:"position"`
	Name        string `json:"name"`
	Description string `json:"description"`
}

type PlaybackStatsRequest struct {
	UserID    int64      `json:"user_id"`
	StartDate *time.Time `json:"start_date"`
	EndDate   *time.Time `json:"end_date"`
	MediaType string     `json:"media_type"`
	Limit     int        `json:"limit"`
}

type PlaybackStats struct {
	TotalPlaytime     int64                    `json:"total_playtime"`
	TotalMediaItems   int64                    `json:"total_media_items"`
	CompletedItems    int64                    `json:"completed_items"`
	MostPlayedGenres  []GenreStats             `json:"most_played_genres"`
	RecentlyWatched   []PlaybackHistory        `json:"recently_watched"`
	TopArtists        []ArtistStats            `json:"top_artists"`
	PlaybackByHour    map[string]int64         `json:"playback_by_hour"`
	WatchTimeByDevice map[string]int64         `json:"watch_time_by_device"`
}

type GenreStats struct {
	Genre     string `json:"genre"`
	Count     int64  `json:"count"`
	Duration  int64  `json:"duration"`
}

type ArtistStats struct {
	Artist   string `json:"artist"`
	Count    int64  `json:"count"`
	Duration int64  `json:"duration"`
}

func NewPlaybackPositionService(db *sql.DB, logger *zap.Logger) *PlaybackPositionService {
	return &PlaybackPositionService{
		db:     db,
		logger: logger,
	}
}

func (s *PlaybackPositionService) UpdatePosition(ctx context.Context, req *UpdatePositionRequest) error {
	s.logger.Info("Updating playback position",
		zap.Int64("user_id", req.UserID),
		zap.Int64("media_item_id", req.MediaItemID),
		zap.Int64("position", req.Position),
		zap.Int64("duration", req.Duration))

	percentComplete := float64(req.Position) / float64(req.Duration) * 100
	isCompleted := percentComplete >= 90.0

	query := `
		INSERT INTO playback_positions (
			user_id, media_item_id, position, duration, percent_complete,
			last_played, is_completed, device_info, playback_quality, created_at, updated_at
		) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, NOW(), NOW())
		ON CONFLICT (user_id, media_item_id)
		DO UPDATE SET
			position = EXCLUDED.position,
			duration = EXCLUDED.duration,
			percent_complete = EXCLUDED.percent_complete,
			last_played = EXCLUDED.last_played,
			is_completed = EXCLUDED.is_completed,
			device_info = EXCLUDED.device_info,
			playback_quality = EXCLUDED.playback_quality,
			updated_at = NOW()
	`

	_, err := s.db.ExecContext(ctx, query,
		req.UserID, req.MediaItemID, req.Position, req.Duration,
		percentComplete, time.Now(), isCompleted, req.DeviceInfo, req.PlaybackQuality)

	if err != nil {
		s.logger.Error("Failed to update playback position", zap.Error(err))
		return fmt.Errorf("failed to update playback position: %w", err)
	}

	if isCompleted {
		if err := s.recordPlaybackHistory(ctx, req, true); err != nil {
			s.logger.Warn("Failed to record playback history", zap.Error(err))
		}
	}

	return nil
}

func (s *PlaybackPositionService) GetPosition(ctx context.Context, userID, mediaItemID int64) (*PlaybackPosition, error) {
	s.logger.Debug("Getting playback position",
		zap.Int64("user_id", userID),
		zap.Int64("media_item_id", mediaItemID))

	query := `
		SELECT id, user_id, media_item_id, position, duration, percent_complete,
			   last_played, is_completed, device_info, playback_quality, created_at, updated_at
		FROM playback_positions
		WHERE user_id = $1 AND media_item_id = $2
	`

	var position PlaybackPosition
	err := s.db.QueryRowContext(ctx, query, userID, mediaItemID).Scan(
		&position.ID, &position.UserID, &position.MediaItemID,
		&position.Position, &position.Duration, &position.PercentComplete,
		&position.LastPlayed, &position.IsCompleted, &position.DeviceInfo,
		&position.PlaybackQuality, &position.CreatedAt, &position.UpdatedAt,
	)

	if err == sql.ErrNoRows {
		return nil, nil
	}
	if err != nil {
		s.logger.Error("Failed to get playback position", zap.Error(err))
		return nil, fmt.Errorf("failed to get playback position: %w", err)
	}

	return &position, nil
}

func (s *PlaybackPositionService) GetContinueWatching(ctx context.Context, userID int64, limit int) ([]PlaybackPosition, error) {
	s.logger.Debug("Getting continue watching list",
		zap.Int64("user_id", userID),
		zap.Int("limit", limit))

	query := `
		SELECT pp.id, pp.user_id, pp.media_item_id, pp.position, pp.duration,
			   pp.percent_complete, pp.last_played, pp.is_completed,
			   pp.device_info, pp.playback_quality, pp.created_at, pp.updated_at
		FROM playback_positions pp
		INNER JOIN media_items mi ON pp.media_item_id = mi.id
		WHERE pp.user_id = $1
		  AND pp.percent_complete BETWEEN 5 AND 90
		  AND pp.last_played > NOW() - INTERVAL '30 days'
		ORDER BY pp.last_played DESC
		LIMIT $2
	`

	rows, err := s.db.QueryContext(ctx, query, userID, limit)
	if err != nil {
		s.logger.Error("Failed to get continue watching list", zap.Error(err))
		return nil, fmt.Errorf("failed to get continue watching: %w", err)
	}
	defer rows.Close()

	var positions []PlaybackPosition
	for rows.Next() {
		var position PlaybackPosition
		err := rows.Scan(
			&position.ID, &position.UserID, &position.MediaItemID,
			&position.Position, &position.Duration, &position.PercentComplete,
			&position.LastPlayed, &position.IsCompleted, &position.DeviceInfo,
			&position.PlaybackQuality, &position.CreatedAt, &position.UpdatedAt,
		)
		if err != nil {
			s.logger.Error("Failed to scan playback position", zap.Error(err))
			continue
		}
		positions = append(positions, position)
	}

	return positions, nil
}

func (s *PlaybackPositionService) CreateBookmark(ctx context.Context, req *BookmarkRequest) (*PlaybackBookmark, error) {
	s.logger.Info("Creating playback bookmark",
		zap.Int64("user_id", req.UserID),
		zap.Int64("media_item_id", req.MediaItemID),
		zap.String("name", req.Name))

	query := `
		INSERT INTO playback_bookmarks (user_id, media_item_id, position, name, description, created_at)
		VALUES ($1, $2, $3, $4, $5, NOW())
		RETURNING id, created_at
	`

	var bookmark PlaybackBookmark
	err := s.db.QueryRowContext(ctx, query,
		req.UserID, req.MediaItemID, req.Position, req.Name, req.Description).Scan(
		&bookmark.ID, &bookmark.CreatedAt)

	if err != nil {
		s.logger.Error("Failed to create bookmark", zap.Error(err))
		return nil, fmt.Errorf("failed to create bookmark: %w", err)
	}

	bookmark.UserID = req.UserID
	bookmark.MediaItemID = req.MediaItemID
	bookmark.Position = req.Position
	bookmark.Name = req.Name
	bookmark.Description = req.Description

	return &bookmark, nil
}

func (s *PlaybackPositionService) GetBookmarks(ctx context.Context, userID, mediaItemID int64) ([]PlaybackBookmark, error) {
	s.logger.Debug("Getting bookmarks",
		zap.Int64("user_id", userID),
		zap.Int64("media_item_id", mediaItemID))

	query := `
		SELECT id, user_id, media_item_id, position, name, description, created_at
		FROM playback_bookmarks
		WHERE user_id = $1 AND media_item_id = $2
		ORDER BY position ASC
	`

	rows, err := s.db.QueryContext(ctx, query, userID, mediaItemID)
	if err != nil {
		s.logger.Error("Failed to get bookmarks", zap.Error(err))
		return nil, fmt.Errorf("failed to get bookmarks: %w", err)
	}
	defer rows.Close()

	var bookmarks []PlaybackBookmark
	for rows.Next() {
		var bookmark PlaybackBookmark
		err := rows.Scan(
			&bookmark.ID, &bookmark.UserID, &bookmark.MediaItemID,
			&bookmark.Position, &bookmark.Name, &bookmark.Description,
			&bookmark.CreatedAt,
		)
		if err != nil {
			s.logger.Error("Failed to scan bookmark", zap.Error(err))
			continue
		}
		bookmarks = append(bookmarks, bookmark)
	}

	return bookmarks, nil
}

func (s *PlaybackPositionService) DeleteBookmark(ctx context.Context, userID, bookmarkID int64) error {
	s.logger.Info("Deleting bookmark",
		zap.Int64("user_id", userID),
		zap.Int64("bookmark_id", bookmarkID))

	query := `DELETE FROM playback_bookmarks WHERE id = $1 AND user_id = $2`

	result, err := s.db.ExecContext(ctx, query, bookmarkID, userID)
	if err != nil {
		s.logger.Error("Failed to delete bookmark", zap.Error(err))
		return fmt.Errorf("failed to delete bookmark: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return fmt.Errorf("failed to get rows affected: %w", err)
	}

	if rowsAffected == 0 {
		return fmt.Errorf("bookmark not found or access denied")
	}

	return nil
}

func (s *PlaybackPositionService) GetPlaybackStats(ctx context.Context, req *PlaybackStatsRequest) (*PlaybackStats, error) {
	s.logger.Debug("Getting playback statistics",
		zap.Int64("user_id", req.UserID))

	stats := &PlaybackStats{
		MostPlayedGenres:  make([]GenreStats, 0),
		RecentlyWatched:   make([]PlaybackHistory, 0),
		TopArtists:        make([]ArtistStats, 0),
		PlaybackByHour:    make(map[string]int64),
		WatchTimeByDevice: make(map[string]int64),
	}

	if err := s.getTotalPlaytime(ctx, req, stats); err != nil {
		s.logger.Error("Failed to get total playtime", zap.Error(err))
		return nil, err
	}

	if err := s.getRecentlyWatched(ctx, req, stats); err != nil {
		s.logger.Error("Failed to get recently watched", zap.Error(err))
		return nil, err
	}

	if err := s.getPlaybackByHour(ctx, req, stats); err != nil {
		s.logger.Error("Failed to get playback by hour", zap.Error(err))
		return nil, err
	}

	if err := s.getWatchTimeByDevice(ctx, req, stats); err != nil {
		s.logger.Error("Failed to get watch time by device", zap.Error(err))
		return nil, err
	}

	return stats, nil
}

func (s *PlaybackPositionService) recordPlaybackHistory(ctx context.Context, req *UpdatePositionRequest, completed bool) error {
	query := `
		INSERT INTO playback_history (
			user_id, media_item_id, start_time, end_time, duration,
			percent_watched, device_info, playback_quality, was_completed
		) VALUES ($1, $2, NOW() - INTERVAL '%d milliseconds', NOW(), $3, $4, $5, $6, $7)
	`

	percentWatched := float64(req.Position) / float64(req.Duration) * 100

	_, err := s.db.ExecContext(ctx, fmt.Sprintf(query, req.Position),
		req.UserID, req.MediaItemID, req.Duration, percentWatched,
		req.DeviceInfo, req.PlaybackQuality, completed)

	return err
}

func (s *PlaybackPositionService) getTotalPlaytime(ctx context.Context, req *PlaybackStatsRequest, stats *PlaybackStats) error {
	query := `
		SELECT
			COALESCE(SUM(duration), 0) as total_playtime,
			COUNT(*) as total_items,
			COUNT(CASE WHEN was_completed THEN 1 END) as completed_items
		FROM playback_history
		WHERE user_id = $1
	`

	args := []interface{}{req.UserID}
	if req.StartDate != nil {
		query += " AND start_time >= $2"
		args = append(args, *req.StartDate)
	}
	if req.EndDate != nil {
		if req.StartDate != nil {
			query += " AND start_time <= $3"
		} else {
			query += " AND start_time <= $2"
		}
		args = append(args, *req.EndDate)
	}

	err := s.db.QueryRowContext(ctx, query, args...).Scan(
		&stats.TotalPlaytime, &stats.TotalMediaItems, &stats.CompletedItems)

	return err
}

func (s *PlaybackPositionService) getRecentlyWatched(ctx context.Context, req *PlaybackStatsRequest, stats *PlaybackStats) error {
	limit := req.Limit
	if limit <= 0 {
		limit = 20
	}

	query := `
		SELECT id, user_id, media_item_id, start_time, end_time, duration,
			   percent_watched, device_info, playback_quality, was_completed
		FROM playback_history
		WHERE user_id = $1
		ORDER BY start_time DESC
		LIMIT $2
	`

	rows, err := s.db.QueryContext(ctx, query, req.UserID, limit)
	if err != nil {
		return err
	}
	defer rows.Close()

	for rows.Next() {
		var history PlaybackHistory
		err := rows.Scan(
			&history.ID, &history.UserID, &history.MediaItemID,
			&history.StartTime, &history.EndTime, &history.Duration,
			&history.PercentWatched, &history.DeviceInfo,
			&history.PlaybackQuality, &history.WasCompleted,
		)
		if err != nil {
			continue
		}
		stats.RecentlyWatched = append(stats.RecentlyWatched, history)
	}

	return nil
}

func (s *PlaybackPositionService) getPlaybackByHour(ctx context.Context, req *PlaybackStatsRequest, stats *PlaybackStats) error {
	query := `
		SELECT
			EXTRACT(HOUR FROM start_time) as hour,
			COUNT(*) as count
		FROM playback_history
		WHERE user_id = $1
		GROUP BY EXTRACT(HOUR FROM start_time)
		ORDER BY hour
	`

	rows, err := s.db.QueryContext(ctx, query, req.UserID)
	if err != nil {
		return err
	}
	defer rows.Close()

	for rows.Next() {
		var hour int
		var count int64
		err := rows.Scan(&hour, &count)
		if err != nil {
			continue
		}
		stats.PlaybackByHour[fmt.Sprintf("%02d:00", hour)] = count
	}

	return nil
}

func (s *PlaybackPositionService) getWatchTimeByDevice(ctx context.Context, req *PlaybackStatsRequest, stats *PlaybackStats) error {
	query := `
		SELECT
			device_info,
			SUM(duration) as total_duration
		FROM playback_history
		WHERE user_id = $1
		GROUP BY device_info
		ORDER BY total_duration DESC
	`

	rows, err := s.db.QueryContext(ctx, query, req.UserID)
	if err != nil {
		return err
	}
	defer rows.Close()

	for rows.Next() {
		var device string
		var duration int64
		err := rows.Scan(&device, &duration)
		if err != nil {
			continue
		}
		stats.WatchTimeByDevice[device] = duration
	}

	return nil
}

func (s *PlaybackPositionService) CleanupOldPositions(ctx context.Context, olderThan time.Duration) error {
	s.logger.Info("Cleaning up old playback positions",
		zap.Duration("older_than", olderThan))

	query := `
		DELETE FROM playback_positions
		WHERE last_played < $1 AND is_completed = true
	`

	cutoff := time.Now().Add(-olderThan)
	result, err := s.db.ExecContext(ctx, query, cutoff)
	if err != nil {
		s.logger.Error("Failed to cleanup old positions", zap.Error(err))
		return fmt.Errorf("failed to cleanup old positions: %w", err)
	}

	rowsAffected, _ := result.RowsAffected()
	s.logger.Info("Cleaned up old playback positions",
		zap.Int64("rows_affected", rowsAffected))

	return nil
}

func (s *PlaybackPositionService) SyncAcrossDevices(ctx context.Context, userID int64) error {
	s.logger.Debug("Syncing playback positions across devices",
		zap.Int64("user_id", userID))

	return nil
}