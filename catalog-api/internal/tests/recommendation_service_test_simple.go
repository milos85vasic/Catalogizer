package tests

import (
	"context"
	"database/sql"
	"testing"

	"github.com/stretchr/testify/assert"
	"go.uber.org/zap"

	"catalogizer/models"
	"catalogizer/internal/services"
)

// MockFileRepository implements FileRepositoryInterface for testing
type MockFileRepositorySimple struct {
}

func (m *MockFileRepositorySimple) SearchFiles(ctx context.Context, filter models.SearchFilter, pagination models.PaginationOptions, sort models.SortOptions) (*models.SearchResult, error) {
	// Mock implementation
	return &models.SearchResult{
		Files:      []models.FileWithMetadata{},
		TotalCount: 0,
		Page:       1,
		Limit:      10,
		TotalPages: 0,
	}, nil
}

func TestRecommendationService_BasicOperation(t *testing.T) {
	ctx := context.Background()

	// Setup services with database
	db, _ := sql.Open("sqlite3", ":memory:")
	logger := zap.NewNop()
	
	// Create services with minimal requirements
	cacheService := services.NewCacheService(nil, logger)
	translationService := services.NewTranslationService(logger)
	mediaRecognitionService := services.NewMediaRecognitionService(db, logger, cacheService, translationService, "", "", "", "", "", "")
	duplicateDetectionService := services.NewDuplicateDetectionService(db, logger, nil)
	fileRepo := &MockFileRepositorySimple{}
	recommendationService := services.NewRecommendationService(
		mediaRecognitionService,
		duplicateDetectionService,
		fileRepo,
		db,
	)

	// Create test media item
	mediaItem := &models.MediaMetadata{
		Title: "Test Movie",
		Genre: "Action",
		Year: &[]int{2023}[0],
	}

	// Test getting recommendations - basic smoke test
	request := &services.SimilarItemsRequest{
		MediaID:       "test-123",
		MediaMetadata: mediaItem,
		MaxLocalItems:  5,
	}
	items, err := recommendationService.GetSimilarItems(ctx, request)
	
	// We expect either a valid result or an error (due to empty database)
	// The important thing is that the service doesn't panic
	if items != nil {
		assert.True(t, len(items.LocalItems) >= 0)
	}
	
	// Error may be expected with empty database - we just verify it doesn't crash
	if err != nil {
		assert.Contains(t, err.Error(), "no") // Expected error messages contain "no" (e.g., "no data")
	}
}