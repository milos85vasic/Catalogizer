package services

import (
	"context"
	"crypto/md5"
	"database/sql"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"path/filepath"
	"regexp"
	"strconv"
	"strings"
	"time"

	"go.uber.org/zap"
)

// SubtitleService handles subtitle management, downloading, and translation
type SubtitleService struct {
	db                *sql.DB
	logger            *zap.Logger
	translationService *TranslationService
	httpClient        *http.Client
	apiKeys           map[string]string
	cacheDir          string
}

// SubtitleProvider represents different subtitle providers
type SubtitleProvider string

const (
	ProviderOpenSubtitles SubtitleProvider = "opensubtitles"
	ProviderSubDB         SubtitleProvider = "subdb"
	ProviderYifySubtitles SubtitleProvider = "yifysubtitles"
	ProviderSubscene      SubtitleProvider = "subscene"
	ProviderAddic7ed      SubtitleProvider = "addic7ed"
)

// SubtitleSearchRequest represents a subtitle search request
type SubtitleSearchRequest struct {
	MediaPath     string            `json:"media_path"`
	Title         *string           `json:"title,omitempty"`
	Year          *int              `json:"year,omitempty"`
	Season        *int              `json:"season,omitempty"`
	Episode       *int              `json:"episode,omitempty"`
	Languages     []string          `json:"languages"`
	FileHash      *string           `json:"file_hash,omitempty"`
	FileSize      *int64            `json:"file_size,omitempty"`
	Providers     []SubtitleProvider `json:"providers,omitempty"`
	ForceDownload bool              `json:"force_download"`
}

// SubtitleSearchResult represents a subtitle search result
type SubtitleSearchResult struct {
	ID               string           `json:"id"`
	Provider         SubtitleProvider `json:"provider"`
	Language         string           `json:"language"`
	LanguageCode     string           `json:"language_code"`
	Title            string           `json:"title"`
	DownloadURL      string           `json:"download_url"`
	Format           string           `json:"format"`
	Encoding         string           `json:"encoding"`
	UploadDate       time.Time        `json:"upload_date"`
	Downloads        int              `json:"downloads"`
	Rating           float64          `json:"rating"`
	Comments         int              `json:"comments"`
	IsHearingImpaired bool            `json:"is_hearing_impaired"`
	FrameRate        *float64         `json:"frame_rate,omitempty"`
	FileHash         *string          `json:"file_hash,omitempty"`
	MovieHash        *string          `json:"movie_hash,omitempty"`
	MatchScore       float64          `json:"match_score"`
}

// SubtitleSyncResult represents subtitle synchronization verification
type SubtitleSyncResult struct {
	IsValid        bool     `json:"is_valid"`
	SyncOffset     float64  `json:"sync_offset"` // Milliseconds
	Confidence     float64  `json:"confidence"`  // 0-1
	DetectedFrames int      `json:"detected_frames"`
	SamplePoints   []SyncPoint `json:"sample_points"`
	Recommendation string   `json:"recommendation"`
}

// SyncPoint represents a point used for sync verification
type SyncPoint struct {
	SubtitleTime float64 `json:"subtitle_time"`
	VideoTime    float64 `json:"video_time"`
	Text         string  `json:"text"`
	Confidence   float64 `json:"confidence"`
}

// SubtitleDownloadRequest represents a subtitle download request
type SubtitleDownloadRequest struct {
	MediaItemID   int64              `json:"media_item_id"`
	ResultID      string             `json:"result_id"`
	Language      string             `json:"language"`
	VerifySync    bool               `json:"verify_sync"`
	AutoTranslate []string           `json:"auto_translate,omitempty"` // Languages to auto-translate to
}

// TranslationRequest represents a subtitle translation request
type TranslationRequest struct {
	SubtitleID     string `json:"subtitle_id"`
	SourceLanguage string `json:"source_language"`
	TargetLanguage string `json:"target_language"`
	UseCache       bool   `json:"use_cache"`
}

// NewSubtitleService creates a new subtitle service
func NewSubtitleService(db *sql.DB, logger *zap.Logger) *SubtitleService {
	return &SubtitleService{
		db:                db,
		logger:            logger,
		translationService: NewTranslationService(logger),
		httpClient:        &http.Client{Timeout: 30 * time.Second},
		apiKeys:           make(map[string]string),
		cacheDir:          "./cache/subtitles",
	}
}

// SearchSubtitles searches for subtitles across multiple providers
func (s *SubtitleService) SearchSubtitles(ctx context.Context, request *SubtitleSearchRequest) ([]SubtitleSearchResult, error) {
	s.logger.Info("Searching subtitles",
		zap.String("media_path", request.MediaPath),
		zap.Strings("languages", request.Languages))

	var allResults []SubtitleSearchResult

	// Default providers if none specified
	providers := request.Providers
	if len(providers) == 0 {
		providers = []SubtitleProvider{
			ProviderOpenSubtitles,
			ProviderSubDB,
			ProviderYifySubtitles,
		}
	}

	// Search each provider in parallel
	resultsChan := make(chan []SubtitleSearchResult, len(providers))
	errorsChan := make(chan error, len(providers))

	for _, provider := range providers {
		go func(p SubtitleProvider) {
			results, err := s.searchProvider(ctx, p, request)
			if err != nil {
				s.logger.Warn("Provider search failed",
					zap.String("provider", string(p)),
					zap.Error(err))
				errorsChan <- err
				return
			}
			resultsChan <- results
		}(provider)
	}

	// Collect results
	for i := 0; i < len(providers); i++ {
		select {
		case results := <-resultsChan:
			allResults = append(allResults, results...)
		case <-errorsChan:
			// Log error but continue with other providers
		case <-ctx.Done():
			return nil, ctx.Err()
		}
	}

	// Sort by match score and rating
	s.sortSubtitleResults(allResults)

	s.logger.Info("Subtitle search completed",
		zap.Int("total_results", len(allResults)))

	return allResults, nil
}

// DownloadSubtitle downloads a subtitle and optionally verifies sync
func (s *SubtitleService) DownloadSubtitle(ctx context.Context, request *SubtitleDownloadRequest) (*SubtitleTrack, error) {
	s.logger.Info("Downloading subtitle",
		zap.Int64("media_item_id", request.MediaItemID),
		zap.String("result_id", request.ResultID))

	// Get download info from cache or provider
	result, err := s.getDownloadInfo(ctx, request.ResultID)
	if err != nil {
		return nil, fmt.Errorf("failed to get download info: %w", err)
	}

	// Download subtitle content
	content, encoding, err := s.downloadContent(ctx, result.DownloadURL)
	if err != nil {
		return nil, fmt.Errorf("failed to download subtitle content: %w", err)
	}

	// Parse and validate subtitle format
	parsedSubtitle, err := s.parseSubtitle(content, result.Format)
	if err != nil {
		return nil, fmt.Errorf("failed to parse subtitle: %w", err)
	}

	// Create subtitle track
	track := &SubtitleTrack{
		ID:           generateSubtitleID(),
		Language:     result.Language,
		LanguageCode: result.LanguageCode,
		Source:       "downloaded",
		Format:       result.Format,
		Content:      &content,
		IsDefault:    false,
		IsForced:     false,
		Encoding:     encoding,
		SyncOffset:   0.0,
		CreatedAt:    time.Now(),
		VerifiedSync: false,
	}

	// Verify synchronization if requested
	if request.VerifySync {
		syncResult, err := s.verifySynchronization(ctx, request.MediaItemID, track)
		if err != nil {
			s.logger.Warn("Failed to verify subtitle sync", zap.Error(err))
		} else {
			track.VerifiedSync = syncResult.IsValid
			track.SyncOffset = syncResult.SyncOffset
		}
	}

	// Save to database
	if err := s.saveSubtitleTrack(ctx, request.MediaItemID, track); err != nil {
		return nil, fmt.Errorf("failed to save subtitle track: %w", err)
	}

	// Auto-translate to requested languages
	if len(request.AutoTranslate) > 0 {
		go s.autoTranslateSubtitle(ctx, track, request.AutoTranslate)
	}

	s.logger.Info("Subtitle downloaded successfully",
		zap.String("subtitle_id", track.ID),
		zap.String("language", track.Language))

	return track, nil
}

// TranslateSubtitle translates a subtitle to another language
func (s *SubtitleService) TranslateSubtitle(ctx context.Context, request *TranslationRequest) (*SubtitleTrack, error) {
	s.logger.Info("Translating subtitle",
		zap.String("subtitle_id", request.SubtitleID),
		zap.String("target_language", request.TargetLanguage))

	// Check cache first
	if request.UseCache {
		if cached := s.getCachedTranslation(ctx, request.SubtitleID, request.TargetLanguage); cached != nil {
			return cached, nil
		}
	}

	// Get original subtitle
	original, err := s.getSubtitleTrack(ctx, request.SubtitleID)
	if err != nil {
		return nil, fmt.Errorf("failed to get original subtitle: %w", err)
	}

	// Parse subtitle for translation
	lines, err := s.parseSubtitleLines(*original.Content)
	if err != nil {
		return nil, fmt.Errorf("failed to parse subtitle lines: %w", err)
	}

	// Translate each line
	translatedLines, err := s.translateLines(ctx, lines, request.SourceLanguage, request.TargetLanguage)
	if err != nil {
		return nil, fmt.Errorf("failed to translate lines: %w", err)
	}

	// Reconstruct subtitle content
	translatedContent, err := s.reconstructSubtitle(original.Format, translatedLines)
	if err != nil {
		return nil, fmt.Errorf("failed to reconstruct subtitle: %w", err)
	}

	// Create translated subtitle track
	translatedTrack := &SubtitleTrack{
		ID:           generateSubtitleID(),
		Language:     getLanguageName(request.TargetLanguage),
		LanguageCode: request.TargetLanguage,
		Source:       "translated",
		Format:       original.Format,
		Content:      &translatedContent,
		IsDefault:    false,
		IsForced:     false,
		Encoding:     original.Encoding,
		SyncOffset:   original.SyncOffset,
		CreatedAt:    time.Now(),
		VerifiedSync: original.VerifiedSync,
	}

	// Save translated subtitle
	if err := s.saveCachedTranslation(ctx, request.SubtitleID, request.TargetLanguage, translatedTrack); err != nil {
		s.logger.Warn("Failed to cache translation", zap.Error(err))
	}

	return translatedTrack, nil
}

// GetSubtitles returns all subtitles for a media item
func (s *SubtitleService) GetSubtitles(ctx context.Context, mediaItemID int64) ([]SubtitleTrack, error) {
	query := `
		SELECT id, language, language_code, source, format, path, content,
		       is_default, is_forced, encoding, sync_offset, created_at, verified_sync
		FROM subtitle_tracks WHERE media_item_id = ?
		ORDER BY is_default DESC, language`

	rows, err := s.db.QueryContext(ctx, query, mediaItemID)
	if err != nil {
		return nil, fmt.Errorf("failed to query subtitles: %w", err)
	}
	defer rows.Close()

	var subtitles []SubtitleTrack
	for rows.Next() {
		var track SubtitleTrack
		var content sql.NullString
		var path sql.NullString

		err := rows.Scan(
			&track.ID, &track.Language, &track.LanguageCode, &track.Source,
			&track.Format, &path, &content, &track.IsDefault, &track.IsForced,
			&track.Encoding, &track.SyncOffset, &track.CreatedAt, &track.VerifiedSync,
		)
		if err != nil {
			return nil, fmt.Errorf("failed to scan subtitle: %w", err)
		}

		if path.Valid {
			track.Path = &path.String
		}
		if content.Valid {
			track.Content = &content.String
		}

		subtitles = append(subtitles, track)
	}

	return subtitles, nil
}

// VerifySynchronization checks if subtitles are properly synchronized with video
func (s *SubtitleService) verifySynchronization(ctx context.Context, mediaItemID int64, track *SubtitleTrack) (*SubtitleSyncResult, error) {
	s.logger.Debug("Verifying subtitle synchronization",
		zap.Int64("media_item_id", mediaItemID),
		zap.String("subtitle_id", track.ID))

	// Get video metadata
	videoInfo, err := s.getVideoInfo(ctx, mediaItemID)
	if err != nil {
		return nil, fmt.Errorf("failed to get video info: %w", err)
	}

	// Parse subtitle timing
	lines, err := s.parseSubtitleLines(*track.Content)
	if err != nil {
		return nil, fmt.Errorf("failed to parse subtitle lines: %w", err)
	}

	// Analyze timing patterns
	samplePoints := s.extractSamplePoints(lines, videoInfo.Duration)

	// Calculate sync offset and confidence
	syncOffset, confidence := s.calculateSyncOffset(samplePoints, videoInfo)

	result := &SubtitleSyncResult{
		IsValid:        confidence > 0.7, // 70% confidence threshold
		SyncOffset:     syncOffset,
		Confidence:     confidence,
		DetectedFrames: len(samplePoints),
		SamplePoints:   samplePoints,
	}

	if result.IsValid {
		result.Recommendation = "Subtitle synchronization is good"
	} else if confidence > 0.4 {
		result.Recommendation = fmt.Sprintf("Subtitle may need %+.1fs offset", syncOffset/1000)
	} else {
		result.Recommendation = "Subtitle synchronization is poor"
	}

	return result, nil
}

// Provider-specific search implementations
func (s *SubtitleService) searchProvider(ctx context.Context, provider SubtitleProvider, request *SubtitleSearchRequest) ([]SubtitleSearchResult, error) {
	switch provider {
	case ProviderOpenSubtitles:
		return s.searchOpenSubtitles(ctx, request)
	case ProviderSubDB:
		return s.searchSubDB(ctx, request)
	case ProviderYifySubtitles:
		return s.searchYifySubtitles(ctx, request)
	default:
		return nil, fmt.Errorf("unsupported provider: %s", provider)
	}
}

func (s *SubtitleService) searchOpenSubtitles(ctx context.Context, request *SubtitleSearchRequest) ([]SubtitleSearchResult, error) {
	// Implementation for OpenSubtitles API
	// This is a simplified version - real implementation would use their API

	s.logger.Debug("Searching OpenSubtitles", zap.String("title", getStringValue(request.Title)))

	// Mock results for demonstration
	results := []SubtitleSearchResult{
		{
			ID:           "os_1",
			Provider:     ProviderOpenSubtitles,
			Language:     "English",
			LanguageCode: "en",
			Title:        "Sample Movie (2024)",
			DownloadURL:  "https://dl.opensubtitles.org/sample1.srt",
			Format:       "srt",
			Encoding:     "utf-8",
			UploadDate:   time.Now().AddDate(0, 0, -7),
			Downloads:    1500,
			Rating:       4.2,
			Comments:     23,
			MatchScore:   0.95,
		},
	}

	return results, nil
}

func (s *SubtitleService) searchSubDB(ctx context.Context, request *SubtitleSearchRequest) ([]SubtitleSearchResult, error) {
	// Implementation for SubDB
	s.logger.Debug("Searching SubDB")
	return []SubtitleSearchResult{}, nil
}

func (s *SubtitleService) searchYifySubtitles(ctx context.Context, request *SubtitleSearchRequest) ([]SubtitleSearchResult, error) {
	// Implementation for YifySubtitles
	s.logger.Debug("Searching YifySubtitles")
	return []SubtitleSearchResult{}, nil
}

// Helper functions
func (s *SubtitleService) downloadContent(ctx context.Context, url string) (string, string, error) {
	req, err := http.NewRequestWithContext(ctx, "GET", url, nil)
	if err != nil {
		return "", "", err
	}

	resp, err := s.httpClient.Do(req)
	if err != nil {
		return "", "", err
	}
	defer resp.Body.Close()

	content, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", "", err
	}

	// Detect encoding
	encoding := detectEncoding(content)

	return string(content), encoding, nil
}

func (s *SubtitleService) parseSubtitle(content, format string) (interface{}, error) {
	switch strings.ToLower(format) {
	case "srt":
		return s.parseSRT(content)
	case "vtt":
		return s.parseVTT(content)
	case "ass", "ssa":
		return s.parseASS(content)
	default:
		return nil, fmt.Errorf("unsupported subtitle format: %s", format)
	}
}

func (s *SubtitleService) parseSRT(content string) ([]SubtitleLine, error) {
	var lines []SubtitleLine

	// Simple SRT parser
	re := regexp.MustCompile(`(\d+)\s*\n(\d{2}:\d{2}:\d{2},\d{3}) --> (\d{2}:\d{2}:\d{2},\d{3})\s*\n((?:[^\n]*\n?)+?)(?:\n|$)`)
	matches := re.FindAllStringSubmatch(content, -1)

	for _, match := range matches {
		if len(match) >= 5 {
			startTime, _ := parseTimestamp(match[2])
			endTime, _ := parseTimestamp(match[3])
			text := strings.TrimSpace(match[4])

			lines = append(lines, SubtitleLine{
				StartTime: startTime,
				EndTime:   &endTime,
				Text:      text,
			})
		}
	}

	return lines, nil
}

func (s *SubtitleService) parseVTT(content string) (interface{}, error) {
	// WebVTT parser implementation
	return nil, fmt.Errorf("VTT parsing not implemented")
}

func (s *SubtitleService) parseASS(content string) (interface{}, error) {
	// ASS/SSA parser implementation
	return nil, fmt.Errorf("ASS parsing not implemented")
}

func parseTimestamp(timestamp string) (float64, error) {
	// Parse SRT timestamp format: 00:01:23,456
	re := regexp.MustCompile(`(\d{2}):(\d{2}):(\d{2}),(\d{3})`)
	matches := re.FindStringSubmatch(timestamp)

	if len(matches) != 5 {
		return 0, fmt.Errorf("invalid timestamp format")
	}

	hours, _ := strconv.Atoi(matches[1])
	minutes, _ := strconv.Atoi(matches[2])
	seconds, _ := strconv.Atoi(matches[3])
	milliseconds, _ := strconv.Atoi(matches[4])

	total := float64(hours*3600 + minutes*60 + seconds) + float64(milliseconds)/1000.0
	return total, nil
}

func detectEncoding(data []byte) string {
	// Simple encoding detection - in practice you'd use a more sophisticated library
	if len(data) > 3 && data[0] == 0xEF && data[1] == 0xBB && data[2] == 0xBF {
		return "utf-8"
	}
	return "utf-8" // Default assumption
}

func generateSubtitleID() string {
	return fmt.Sprintf("sub_%d", time.Now().UnixNano())
}

func getStringValue(s *string) string {
	if s == nil {
		return ""
	}
	return *s
}

func (s *SubtitleService) sortSubtitleResults(results []SubtitleSearchResult) {
	// Sort by match score descending, then by rating
	for i := 0; i < len(results)-1; i++ {
		for j := i + 1; j < len(results); j++ {
			if results[i].MatchScore < results[j].MatchScore ||
				(results[i].MatchScore == results[j].MatchScore && results[i].Rating < results[j].Rating) {
				results[i], results[j] = results[j], results[i]
			}
		}
	}
}

// Additional methods would be implemented for:
// - getDownloadInfo
// - saveSubtitleTrack
// - autoTranslateSubtitle
// - getCachedTranslation
// - saveCachedTranslation
// - parseSubtitleLines
// - translateLines
// - reconstructSubtitle
// - getVideoInfo
// - extractSamplePoints
// - calculateSyncOffset
// etc.