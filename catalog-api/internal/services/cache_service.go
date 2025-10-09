package services

import (
	"context"
	"crypto/md5"
	"database/sql"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"time"

	"go.uber.org/zap"
)

type CacheService struct {
	db     *sql.DB
	logger *zap.Logger
}

type CacheEntry struct {
	ID        int64     `json:"id" db:"id"`
	CacheKey  string    `json:"cache_key" db:"cache_key"`
	Value     string    `json:"value" db:"value"`
	ExpiresAt time.Time `json:"expires_at" db:"expires_at"`
	CreatedAt time.Time `json:"created_at" db:"created_at"`
	UpdatedAt time.Time `json:"updated_at" db:"updated_at"`
}

type MediaMetadataCache struct {
	ID          int64     `json:"id"`
	MediaItemID int64     `json:"media_item_id"`
	MetadataType string   `json:"metadata_type"`
	Provider    string    `json:"provider"`
	Data        string    `json:"data"`
	Quality     float64   `json:"quality"`
	ExpiresAt   time.Time `json:"expires_at"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
}

type APICache struct {
	ID          int64     `json:"id"`
	Provider    string    `json:"provider"`
	Endpoint    string    `json:"endpoint"`
	RequestHash string    `json:"request_hash"`
	Response    string    `json:"response"`
	StatusCode  int       `json:"status_code"`
	ExpiresAt   time.Time `json:"expires_at"`
	CreatedAt   time.Time `json:"created_at"`
}

type ThumbnailCache struct {
	ID        int64     `json:"id"`
	VideoID   int64     `json:"video_id"`
	Position  int64     `json:"position"`
	URL       string    `json:"url"`
	Width     int       `json:"width"`
	Height    int       `json:"height"`
	FileSize  int64     `json:"file_size"`
	CreatedAt time.Time `json:"created_at"`
}

type CacheStats struct {
	TotalEntries     int64   `json:"total_entries"`
	TotalSize        int64   `json:"total_size"`
	HitRate          float64 `json:"hit_rate"`
	MissRate         float64 `json:"miss_rate"`
	ExpiredEntries   int64   `json:"expired_entries"`
	CachesByType     map[string]int64 `json:"caches_by_type"`
	CachesByProvider map[string]int64 `json:"caches_by_provider"`
	RecentActivity   []CacheActivity  `json:"recent_activity"`
}

type CacheActivity struct {
	Type      string    `json:"type"`
	Key       string    `json:"key"`
	Provider  string    `json:"provider"`
	Hit       bool      `json:"hit"`
	Timestamp time.Time `json:"timestamp"`
}

const (
	DefaultCacheTTL        = 24 * time.Hour
	MetadataCacheTTL       = 7 * 24 * time.Hour
	ThumbnailCacheTTL      = 30 * 24 * time.Hour
	APICacheTTL            = 1 * time.Hour
	TranslationCacheTTL    = 30 * 24 * time.Hour
	SubtitleCacheTTL       = 7 * 24 * time.Hour
	LyricsCacheTTL         = 14 * 24 * time.Hour
	CoverArtCacheTTL       = 30 * 24 * time.Hour
)

func NewCacheService(db *sql.DB, logger *zap.Logger) *CacheService {
	return &CacheService{
		db:     db,
		logger: logger,
	}
}

func (s *CacheService) Set(ctx context.Context, key string, value interface{}, ttl time.Duration) error {
	s.logger.Debug("Setting cache entry",
		zap.String("key", key),
		zap.Duration("ttl", ttl))

	valueJSON, err := json.Marshal(value)
	if err != nil {
		return fmt.Errorf("failed to marshal value: %w", err)
	}

	expiresAt := time.Now().Add(ttl)

	query := `
		INSERT INTO cache_entries (cache_key, value, expires_at, created_at, updated_at)
		VALUES ($1, $2, $3, NOW(), NOW())
		ON CONFLICT (cache_key)
		DO UPDATE SET
			value = EXCLUDED.value,
			expires_at = EXCLUDED.expires_at,
			updated_at = NOW()
	`

	_, err = s.db.ExecContext(ctx, query, key, string(valueJSON), expiresAt)
	if err != nil {
		s.logger.Error("Failed to set cache entry", zap.Error(err))
		return fmt.Errorf("failed to set cache entry: %w", err)
	}

	s.recordCacheActivity(ctx, "SET", key, "", true)
	return nil
}

func (s *CacheService) Get(ctx context.Context, key string, dest interface{}) (bool, error) {
	s.logger.Debug("Getting cache entry", zap.String("key", key))

	query := `
		SELECT value, expires_at
		FROM cache_entries
		WHERE cache_key = $1 AND expires_at > NOW()
	`

	var valueJSON string
	var expiresAt time.Time

	err := s.db.QueryRowContext(ctx, query, key).Scan(&valueJSON, &expiresAt)
	if err == sql.ErrNoRows {
		s.recordCacheActivity(ctx, "GET", key, "", false)
		return false, nil
	}
	if err != nil {
		s.logger.Error("Failed to get cache entry", zap.Error(err))
		s.recordCacheActivity(ctx, "GET", key, "", false)
		return false, fmt.Errorf("failed to get cache entry: %w", err)
	}

	if err := json.Unmarshal([]byte(valueJSON), dest); err != nil {
		s.logger.Error("Failed to unmarshal cache value", zap.Error(err))
		s.recordCacheActivity(ctx, "GET", key, "", false)
		return false, fmt.Errorf("failed to unmarshal cache value: %w", err)
	}

	s.recordCacheActivity(ctx, "GET", key, "", true)
	return true, nil
}

func (s *CacheService) Delete(ctx context.Context, key string) error {
	s.logger.Debug("Deleting cache entry", zap.String("key", key))

	query := `DELETE FROM cache_entries WHERE cache_key = $1`

	result, err := s.db.ExecContext(ctx, query, key)
	if err != nil {
		s.logger.Error("Failed to delete cache entry", zap.Error(err))
		return fmt.Errorf("failed to delete cache entry: %w", err)
	}

	rowsAffected, _ := result.RowsAffected()
	s.recordCacheActivity(ctx, "DELETE", key, "", rowsAffected > 0)

	return nil
}

func (s *CacheService) Clear(ctx context.Context, pattern string) error {
	s.logger.Info("Clearing cache entries", zap.String("pattern", pattern))

	var query string
	var args []interface{}

	if pattern == "" {
		query = `DELETE FROM cache_entries`
	} else {
		query = `DELETE FROM cache_entries WHERE cache_key LIKE $1`
		args = append(args, pattern)
	}

	result, err := s.db.ExecContext(ctx, query, args...)
	if err != nil {
		s.logger.Error("Failed to clear cache entries", zap.Error(err))
		return fmt.Errorf("failed to clear cache entries: %w", err)
	}

	rowsAffected, _ := result.RowsAffected()
	s.logger.Info("Cleared cache entries", zap.Int64("count", rowsAffected))

	return nil
}

func (s *CacheService) SetMediaMetadata(ctx context.Context, mediaItemID int64, metadataType, provider string, data interface{}, quality float64) error {
	s.logger.Debug("Setting media metadata cache",
		zap.Int64("media_item_id", mediaItemID),
		zap.String("type", metadataType),
		zap.String("provider", provider))

	dataJSON, err := json.Marshal(data)
	if err != nil {
		return fmt.Errorf("failed to marshal metadata: %w", err)
	}

	expiresAt := time.Now().Add(MetadataCacheTTL)

	query := `
		INSERT INTO media_metadata_cache (
			media_item_id, metadata_type, provider, data, quality, expires_at, created_at, updated_at
		) VALUES ($1, $2, $3, $4, $5, $6, NOW(), NOW())
		ON CONFLICT (media_item_id, metadata_type, provider)
		DO UPDATE SET
			data = EXCLUDED.data,
			quality = EXCLUDED.quality,
			expires_at = EXCLUDED.expires_at,
			updated_at = NOW()
	`

	_, err = s.db.ExecContext(ctx, query, mediaItemID, metadataType, provider, string(dataJSON), quality, expiresAt)
	if err != nil {
		s.logger.Error("Failed to set media metadata cache", zap.Error(err))
		return fmt.Errorf("failed to set media metadata: %w", err)
	}

	cacheKey := fmt.Sprintf("metadata:%d:%s:%s", mediaItemID, metadataType, provider)
	s.recordCacheActivity(ctx, "SET", cacheKey, provider, true)

	return nil
}

func (s *CacheService) GetMediaMetadata(ctx context.Context, mediaItemID int64, metadataType, provider string, dest interface{}) (bool, float64, error) {
	s.logger.Debug("Getting media metadata cache",
		zap.Int64("media_item_id", mediaItemID),
		zap.String("type", metadataType),
		zap.String("provider", provider))

	query := `
		SELECT data, quality, expires_at
		FROM media_metadata_cache
		WHERE media_item_id = $1 AND metadata_type = $2 AND provider = $3 AND expires_at > NOW()
		ORDER BY quality DESC, updated_at DESC
		LIMIT 1
	`

	var dataJSON string
	var quality float64
	var expiresAt time.Time

	err := s.db.QueryRowContext(ctx, query, mediaItemID, metadataType, provider).Scan(&dataJSON, &quality, &expiresAt)
	cacheKey := fmt.Sprintf("metadata:%d:%s:%s", mediaItemID, metadataType, provider)

	if err == sql.ErrNoRows {
		s.recordCacheActivity(ctx, "GET", cacheKey, provider, false)
		return false, 0, nil
	}
	if err != nil {
		s.logger.Error("Failed to get media metadata cache", zap.Error(err))
		s.recordCacheActivity(ctx, "GET", cacheKey, provider, false)
		return false, 0, fmt.Errorf("failed to get media metadata: %w", err)
	}

	if err := json.Unmarshal([]byte(dataJSON), dest); err != nil {
		s.logger.Error("Failed to unmarshal metadata", zap.Error(err))
		s.recordCacheActivity(ctx, "GET", cacheKey, provider, false)
		return false, 0, fmt.Errorf("failed to unmarshal metadata: %w", err)
	}

	s.recordCacheActivity(ctx, "GET", cacheKey, provider, true)
	return true, quality, nil
}

func (s *CacheService) SetAPIResponse(ctx context.Context, provider, endpoint string, requestData interface{}, response interface{}, statusCode int, ttl time.Duration) error {
	s.logger.Debug("Setting API response cache",
		zap.String("provider", provider),
		zap.String("endpoint", endpoint))

	requestHash, err := s.hashRequest(requestData)
	if err != nil {
		return fmt.Errorf("failed to hash request: %w", err)
	}

	responseJSON, err := json.Marshal(response)
	if err != nil {
		return fmt.Errorf("failed to marshal response: %w", err)
	}

	expiresAt := time.Now().Add(ttl)

	query := `
		INSERT INTO api_cache (provider, endpoint, request_hash, response, status_code, expires_at, created_at)
		VALUES ($1, $2, $3, $4, $5, $6, NOW())
		ON CONFLICT (provider, endpoint, request_hash)
		DO UPDATE SET
			response = EXCLUDED.response,
			status_code = EXCLUDED.status_code,
			expires_at = EXCLUDED.expires_at,
			created_at = NOW()
	`

	_, err = s.db.ExecContext(ctx, query, provider, endpoint, requestHash, string(responseJSON), statusCode, expiresAt)
	if err != nil {
		s.logger.Error("Failed to set API response cache", zap.Error(err))
		return fmt.Errorf("failed to set API response: %w", err)
	}

	cacheKey := fmt.Sprintf("api:%s:%s:%s", provider, endpoint, requestHash)
	s.recordCacheActivity(ctx, "SET", cacheKey, provider, true)

	return nil
}

func (s *CacheService) GetAPIResponse(ctx context.Context, provider, endpoint string, requestData interface{}, dest interface{}) (bool, int, error) {
	s.logger.Debug("Getting API response cache",
		zap.String("provider", provider),
		zap.String("endpoint", endpoint))

	requestHash, err := s.hashRequest(requestData)
	if err != nil {
		return false, 0, fmt.Errorf("failed to hash request: %w", err)
	}

	query := `
		SELECT response, status_code, expires_at
		FROM api_cache
		WHERE provider = $1 AND endpoint = $2 AND request_hash = $3 AND expires_at > NOW()
	`

	var responseJSON string
	var statusCode int
	var expiresAt time.Time

	err = s.db.QueryRowContext(ctx, query, provider, endpoint, requestHash).Scan(&responseJSON, &statusCode, &expiresAt)
	cacheKey := fmt.Sprintf("api:%s:%s:%s", provider, endpoint, requestHash)

	if err == sql.ErrNoRows {
		s.recordCacheActivity(ctx, "GET", cacheKey, provider, false)
		return false, 0, nil
	}
	if err != nil {
		s.logger.Error("Failed to get API response cache", zap.Error(err))
		s.recordCacheActivity(ctx, "GET", cacheKey, provider, false)
		return false, 0, fmt.Errorf("failed to get API response: %w", err)
	}

	if err := json.Unmarshal([]byte(responseJSON), dest); err != nil {
		s.logger.Error("Failed to unmarshal API response", zap.Error(err))
		s.recordCacheActivity(ctx, "GET", cacheKey, provider, false)
		return false, statusCode, fmt.Errorf("failed to unmarshal API response: %w", err)
	}

	s.recordCacheActivity(ctx, "GET", cacheKey, provider, true)
	return true, statusCode, nil
}

func (s *CacheService) SetThumbnail(ctx context.Context, videoID, position int64, url string, width, height int, fileSize int64) error {
	s.logger.Debug("Setting thumbnail cache",
		zap.Int64("video_id", videoID),
		zap.Int64("position", position))

	query := `
		INSERT INTO thumbnail_cache (video_id, position, url, width, height, file_size, created_at)
		VALUES ($1, $2, $3, $4, $5, $6, NOW())
		ON CONFLICT (video_id, position, width, height)
		DO UPDATE SET
			url = EXCLUDED.url,
			file_size = EXCLUDED.file_size,
			created_at = NOW()
	`

	_, err := s.db.ExecContext(ctx, query, videoID, position, url, width, height, fileSize)
	if err != nil {
		s.logger.Error("Failed to set thumbnail cache", zap.Error(err))
		return fmt.Errorf("failed to set thumbnail: %w", err)
	}

	cacheKey := fmt.Sprintf("thumbnail:%d:%d:%dx%d", videoID, position, width, height)
	s.recordCacheActivity(ctx, "SET", cacheKey, "thumbnail", true)

	return nil
}

func (s *CacheService) GetThumbnail(ctx context.Context, videoID, position int64, width, height int) (*ThumbnailCache, error) {
	s.logger.Debug("Getting thumbnail cache",
		zap.Int64("video_id", videoID),
		zap.Int64("position", position))

	query := `
		SELECT id, video_id, position, url, width, height, file_size, created_at
		FROM thumbnail_cache
		WHERE video_id = $1 AND position = $2 AND width = $3 AND height = $4
		ORDER BY created_at DESC
		LIMIT 1
	`

	var thumbnail ThumbnailCache
	err := s.db.QueryRowContext(ctx, query, videoID, position, width, height).Scan(
		&thumbnail.ID, &thumbnail.VideoID, &thumbnail.Position, &thumbnail.URL,
		&thumbnail.Width, &thumbnail.Height, &thumbnail.FileSize, &thumbnail.CreatedAt,
	)

	cacheKey := fmt.Sprintf("thumbnail:%d:%d:%dx%d", videoID, position, width, height)

	if err == sql.ErrNoRows {
		s.recordCacheActivity(ctx, "GET", cacheKey, "thumbnail", false)
		return nil, nil
	}
	if err != nil {
		s.logger.Error("Failed to get thumbnail cache", zap.Error(err))
		s.recordCacheActivity(ctx, "GET", cacheKey, "thumbnail", false)
		return nil, fmt.Errorf("failed to get thumbnail: %w", err)
	}

	s.recordCacheActivity(ctx, "GET", cacheKey, "thumbnail", true)
	return &thumbnail, nil
}

func (s *CacheService) SetTranslation(ctx context.Context, sourceText, sourceLang, targetLang, provider string, translation string) error {
	s.logger.Debug("Setting translation cache",
		zap.String("source_lang", sourceLang),
		zap.String("target_lang", targetLang),
		zap.String("provider", provider))

	key := fmt.Sprintf("translation:%s:%s:%s:%s", provider, sourceLang, targetLang, s.hashString(sourceText))

	translationData := map[string]interface{}{
		"source_text":   sourceText,
		"source_lang":   sourceLang,
		"target_lang":   targetLang,
		"translation":   translation,
		"provider":      provider,
		"cached_at":     time.Now(),
	}

	return s.Set(ctx, key, translationData, TranslationCacheTTL)
}

func (s *CacheService) GetTranslation(ctx context.Context, sourceText, sourceLang, targetLang, provider string) (string, bool, error) {
	s.logger.Debug("Getting translation cache",
		zap.String("source_lang", sourceLang),
		zap.String("target_lang", targetLang),
		zap.String("provider", provider))

	key := fmt.Sprintf("translation:%s:%s:%s:%s", provider, sourceLang, targetLang, s.hashString(sourceText))

	var translationData map[string]interface{}
	found, err := s.Get(ctx, key, &translationData)
	if err != nil || !found {
		return "", false, err
	}

	if translation, ok := translationData["translation"].(string); ok {
		return translation, true, nil
	}

	return "", false, fmt.Errorf("invalid translation data in cache")
}

func (s *CacheService) SetSubtitle(ctx context.Context, videoID int64, language, provider string, subtitle *SubtitleTrack) error {
	s.logger.Debug("Setting subtitle cache",
		zap.Int64("video_id", videoID),
		zap.String("language", language),
		zap.String("provider", provider))

	key := fmt.Sprintf("subtitle:%d:%s:%s", videoID, language, provider)
	return s.Set(ctx, key, subtitle, SubtitleCacheTTL)
}

func (s *CacheService) GetSubtitle(ctx context.Context, videoID int64, language, provider string) (*SubtitleTrack, bool, error) {
	s.logger.Debug("Getting subtitle cache",
		zap.Int64("video_id", videoID),
		zap.String("language", language),
		zap.String("provider", provider))

	key := fmt.Sprintf("subtitle:%d:%s:%s", videoID, language, provider)

	var subtitle SubtitleTrack
	found, err := s.Get(ctx, key, &subtitle)
	if err != nil || !found {
		return nil, false, err
	}

	return &subtitle, true, nil
}

func (s *CacheService) SetLyrics(ctx context.Context, artist, title, provider string, lyrics *LyricsData) error {
	s.logger.Debug("Setting lyrics cache",
		zap.String("artist", artist),
		zap.String("title", title),
		zap.String("provider", provider))

	key := fmt.Sprintf("lyrics:%s:%s:%s", provider, s.hashString(artist), s.hashString(title))
	return s.Set(ctx, key, lyrics, LyricsCacheTTL)
}

func (s *CacheService) GetLyrics(ctx context.Context, artist, title, provider string) (*LyricsData, bool, error) {
	s.logger.Debug("Getting lyrics cache",
		zap.String("artist", artist),
		zap.String("title", title),
		zap.String("provider", provider))

	key := fmt.Sprintf("lyrics:%s:%s:%s", provider, s.hashString(artist), s.hashString(title))

	var lyrics LyricsData
	found, err := s.Get(ctx, key, &lyrics)
	if err != nil || !found {
		return nil, false, err
	}

	return &lyrics, true, nil
}

func (s *CacheService) SetCoverArt(ctx context.Context, artist, album, provider string, coverArt *CoverArt) error {
	s.logger.Debug("Setting cover art cache",
		zap.String("artist", artist),
		zap.String("album", album),
		zap.String("provider", provider))

	key := fmt.Sprintf("coverart:%s:%s:%s", provider, s.hashString(artist), s.hashString(album))
	return s.Set(ctx, key, coverArt, CoverArtCacheTTL)
}

func (s *CacheService) GetCoverArt(ctx context.Context, artist, album, provider string) (*CoverArt, bool, error) {
	s.logger.Debug("Getting cover art cache",
		zap.String("artist", artist),
		zap.String("album", album),
		zap.String("provider", provider))

	key := fmt.Sprintf("coverart:%s:%s:%s", provider, s.hashString(artist), s.hashString(album))

	var coverArt CoverArt
	found, err := s.Get(ctx, key, &coverArt)
	if err != nil || !found {
		return nil, false, err
	}

	return &coverArt, true, nil
}

func (s *CacheService) GetStats(ctx context.Context) (*CacheStats, error) {
	s.logger.Debug("Getting cache statistics")

	stats := &CacheStats{
		CachesByType:     make(map[string]int64),
		CachesByProvider: make(map[string]int64),
	}

	if err := s.getBasicStats(ctx, stats); err != nil {
		return nil, err
	}

	if err := s.getCachesByType(ctx, stats); err != nil {
		s.logger.Warn("Failed to get caches by type", zap.Error(err))
	}

	if err := s.getCachesByProvider(ctx, stats); err != nil {
		s.logger.Warn("Failed to get caches by provider", zap.Error(err))
	}

	if err := s.getRecentActivity(ctx, stats); err != nil {
		s.logger.Warn("Failed to get recent activity", zap.Error(err))
	}

	if err := s.calculateHitRate(ctx, stats); err != nil {
		s.logger.Warn("Failed to calculate hit rate", zap.Error(err))
	}

	return stats, nil
}

func (s *CacheService) CleanupExpired(ctx context.Context) error {
	s.logger.Info("Cleaning up expired cache entries")

	tables := []string{
		"cache_entries",
		"media_metadata_cache",
		"api_cache",
	}

	totalCleaned := int64(0)

	for _, table := range tables {
		query := fmt.Sprintf("DELETE FROM %s WHERE expires_at <= NOW()", table)
		result, err := s.db.ExecContext(ctx, query)
		if err != nil {
			s.logger.Error("Failed to cleanup expired entries",
				zap.String("table", table),
				zap.Error(err))
			continue
		}

		rowsAffected, _ := result.RowsAffected()
		totalCleaned += rowsAffected

		s.logger.Debug("Cleaned expired entries",
			zap.String("table", table),
			zap.Int64("count", rowsAffected))
	}

	s.logger.Info("Completed cache cleanup", zap.Int64("total_cleaned", totalCleaned))
	return nil
}

func (s *CacheService) hashRequest(data interface{}) (string, error) {
	jsonData, err := json.Marshal(data)
	if err != nil {
		return "", err
	}
	return s.hashString(string(jsonData)), nil
}

func (s *CacheService) hashString(text string) string {
	hash := md5.Sum([]byte(text))
	return hex.EncodeToString(hash[:])
}

func (s *CacheService) recordCacheActivity(ctx context.Context, activityType, key, provider string, hit bool) {
	go func() {
		query := `
			INSERT INTO cache_activity (type, cache_key, provider, hit, timestamp)
			VALUES ($1, $2, $3, $4, NOW())
		`
		_, err := s.db.ExecContext(context.Background(), query, activityType, key, provider, hit)
		if err != nil {
			s.logger.Debug("Failed to record cache activity", zap.Error(err))
		}
	}()
}

func (s *CacheService) getBasicStats(ctx context.Context, stats *CacheStats) error {
	query := `
		SELECT
			COUNT(*) as total_entries,
			COALESCE(SUM(LENGTH(value)), 0) as total_size,
			COUNT(CASE WHEN expires_at <= NOW() THEN 1 END) as expired_entries
		FROM cache_entries
	`

	return s.db.QueryRowContext(ctx, query).Scan(
		&stats.TotalEntries, &stats.TotalSize, &stats.ExpiredEntries)
}

func (s *CacheService) getCachesByType(ctx context.Context, stats *CacheStats) error {
	query := `
		SELECT
			CASE
				WHEN cache_key LIKE 'translation:%' THEN 'translation'
				WHEN cache_key LIKE 'subtitle:%' THEN 'subtitle'
				WHEN cache_key LIKE 'lyrics:%' THEN 'lyrics'
				WHEN cache_key LIKE 'coverart:%' THEN 'coverart'
				WHEN cache_key LIKE 'api:%' THEN 'api'
				WHEN cache_key LIKE 'metadata:%' THEN 'metadata'
				WHEN cache_key LIKE 'thumbnail:%' THEN 'thumbnail'
				ELSE 'other'
			END as cache_type,
			COUNT(*) as count
		FROM cache_entries
		WHERE expires_at > NOW()
		GROUP BY cache_type
		ORDER BY count DESC
	`

	rows, err := s.db.QueryContext(ctx, query)
	if err != nil {
		return err
	}
	defer rows.Close()

	for rows.Next() {
		var cacheType string
		var count int64
		if err := rows.Scan(&cacheType, &count); err == nil {
			stats.CachesByType[cacheType] = count
		}
	}

	return nil
}

func (s *CacheService) getCachesByProvider(ctx context.Context, stats *CacheStats) error {
	query := `
		SELECT provider, COUNT(*) as count
		FROM api_cache
		WHERE expires_at > NOW()
		GROUP BY provider
		ORDER BY count DESC
	`

	rows, err := s.db.QueryContext(ctx, query)
	if err != nil {
		return err
	}
	defer rows.Close()

	for rows.Next() {
		var provider string
		var count int64
		if err := rows.Scan(&provider, &count); err == nil {
			stats.CachesByProvider[provider] = count
		}
	}

	return nil
}

func (s *CacheService) getRecentActivity(ctx context.Context, stats *CacheStats) error {
	query := `
		SELECT type, cache_key, provider, hit, timestamp
		FROM cache_activity
		WHERE timestamp > NOW() - INTERVAL '1 hour'
		ORDER BY timestamp DESC
		LIMIT 100
	`

	rows, err := s.db.QueryContext(ctx, query)
	if err != nil {
		return err
	}
	defer rows.Close()

	for rows.Next() {
		var activity CacheActivity
		if err := rows.Scan(&activity.Type, &activity.Key, &activity.Provider, &activity.Hit, &activity.Timestamp); err == nil {
			stats.RecentActivity = append(stats.RecentActivity, activity)
		}
	}

	return nil
}

func (s *CacheService) calculateHitRate(ctx context.Context, stats *CacheStats) error {
	query := `
		SELECT
			COUNT(CASE WHEN hit = true THEN 1 END) as hits,
			COUNT(CASE WHEN hit = false THEN 1 END) as misses,
			COUNT(*) as total
		FROM cache_activity
		WHERE timestamp > NOW() - INTERVAL '24 hours'
	`

	var hits, misses, total int64
	err := s.db.QueryRowContext(ctx, query).Scan(&hits, &misses, &total)
	if err != nil {
		return err
	}

	if total > 0 {
		stats.HitRate = float64(hits) / float64(total) * 100
		stats.MissRate = float64(misses) / float64(total) * 100
	}

	return nil
}

func (s *CacheService) Warmup(ctx context.Context) error {
	s.logger.Info("Starting cache warmup")

	return nil
}

func (s *CacheService) InvalidateByPattern(ctx context.Context, pattern string) error {
	s.logger.Info("Invalidating cache entries by pattern", zap.String("pattern", pattern))

	query := `DELETE FROM cache_entries WHERE cache_key LIKE $1`
	result, err := s.db.ExecContext(ctx, query, pattern)
	if err != nil {
		return fmt.Errorf("failed to invalidate cache entries: %w", err)
	}

	rowsAffected, _ := result.RowsAffected()
	s.logger.Info("Invalidated cache entries", zap.Int64("count", rowsAffected))

	return nil
}