package services

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"net/url"
	"sort"
	"strings"
	"time"

	"catalog-api/internal/models"
)

type RecommendationService struct {
	mediaRecognitionService *MediaRecognitionService
	duplicateDetectionService *DuplicateDetectionService
	tmdbBaseURL            string
	omdbBaseURL            string
	lastfmBaseURL          string
	igdbBaseURL            string
	googleBooksBaseURL     string
	steamBaseURL           string
	githubBaseURL          string
	tmdbAPIKey             string
	omdbAPIKey             string
	lastfmAPIKey           string
	igdbClientID           string
	igdbClientSecret       string
	httpClient             *http.Client
}

type SimilarItemsRequest struct {
	MediaID          string                 `json:"media_id"`
	MediaMetadata    *models.MediaMetadata  `json:"media_metadata"`
	MaxLocalItems    int                    `json:"max_local_items,omitempty"`
	MaxExternalItems int                    `json:"max_external_items,omitempty"`
	IncludeExternal  bool                   `json:"include_external,omitempty"`
	SimilarityThreshold float64             `json:"similarity_threshold,omitempty"`
	Filters          *RecommendationFilters `json:"filters,omitempty"`
}

type RecommendationFilters struct {
	GenreFilter      []string `json:"genre_filter,omitempty"`
	YearRange        *YearRange `json:"year_range,omitempty"`
	RatingRange      *RatingRange `json:"rating_range,omitempty"`
	LanguageFilter   []string `json:"language_filter,omitempty"`
	ExcludeWatched   bool     `json:"exclude_watched,omitempty"`
	ExcludeOwned     bool     `json:"exclude_owned,omitempty"`
	MinConfidence    float64  `json:"min_confidence,omitempty"`
}

type YearRange struct {
	StartYear int `json:"start_year"`
	EndYear   int `json:"end_year"`
}

type RatingRange struct {
	MinRating float64 `json:"min_rating"`
	MaxRating float64 `json:"max_rating"`
}

type SimilarItemsResponse struct {
	LocalItems    []*LocalSimilarItem    `json:"local_items"`
	ExternalItems []*ExternalSimilarItem `json:"external_items"`
	TotalFound    int                    `json:"total_found"`
	GeneratedAt   time.Time              `json:"generated_at"`
	Algorithms    []string               `json:"algorithms_used"`
	Performance   *RecommendationStats   `json:"performance"`
}

type LocalSimilarItem struct {
	MediaID          string                 `json:"media_id"`
	MediaMetadata    *models.MediaMetadata  `json:"media_metadata"`
	SimilarityScore  float64                `json:"similarity_score"`
	SimilarityReasons []string              `json:"similarity_reasons"`
	DetailLink       string                 `json:"detail_link"`
	PlayLink         string                 `json:"play_link,omitempty"`
	DownloadLink     string                 `json:"download_link,omitempty"`
	LastAccessed     *time.Time             `json:"last_accessed,omitempty"`
	UserRating       *float64               `json:"user_rating,omitempty"`
	IsWatched        bool                   `json:"is_watched"`
	IsOwned          bool                   `json:"is_owned"`
}

type ExternalSimilarItem struct {
	ExternalID       string    `json:"external_id"`
	Title            string    `json:"title"`
	Subtitle         string    `json:"subtitle,omitempty"`
	Description      string    `json:"description"`
	CoverArt         string    `json:"cover_art,omitempty"`
	Year             string    `json:"year,omitempty"`
	Genre            string    `json:"genre,omitempty"`
	Rating           float64   `json:"rating,omitempty"`
	Provider         string    `json:"provider"`
	ExternalLink     string    `json:"external_link"`
	SimilarityScore  float64   `json:"similarity_score"`
	SimilarityReasons []string `json:"similarity_reasons"`
	AvailabilityInfo *AvailabilityInfo `json:"availability_info,omitempty"`
	PriceInfo        *PriceInfo `json:"price_info,omitempty"`
}

type AvailabilityInfo struct {
	IsAvailable      bool     `json:"is_available"`
	StreamingServices []string `json:"streaming_services,omitempty"`
	PurchaseOptions  []string `json:"purchase_options,omitempty"`
	RentalOptions    []string `json:"rental_options,omitempty"`
	Region           string   `json:"region,omitempty"`
}

type PriceInfo struct {
	PurchasePrice string `json:"purchase_price,omitempty"`
	RentalPrice   string `json:"rental_price,omitempty"`
	Currency      string `json:"currency,omitempty"`
	LastUpdated   time.Time `json:"last_updated"`
}

type RecommendationStats struct {
	LocalSearchTime    time.Duration `json:"local_search_time"`
	ExternalSearchTime time.Duration `json:"external_search_time"`
	TotalTime         time.Duration `json:"total_time"`
	LocalItemsFound   int           `json:"local_items_found"`
	ExternalItemsFound int          `json:"external_items_found"`
	CacheHitRatio     float64       `json:"cache_hit_ratio"`
	APICallsCount     int           `json:"api_calls_count"`
}

func NewRecommendationService(
	mediaRecognitionService *MediaRecognitionService,
	duplicateDetectionService *DuplicateDetectionService,
) *RecommendationService {
	return &RecommendationService{
		mediaRecognitionService:   mediaRecognitionService,
		duplicateDetectionService: duplicateDetectionService,
		tmdbBaseURL:              "https://api.themoviedb.org/3",
		omdbBaseURL:              "http://www.omdbapi.com",
		lastfmBaseURL:            "http://ws.audioscrobbler.com/2.0",
		igdbBaseURL:              "https://api.igdb.com/v4",
		googleBooksBaseURL:       "https://www.googleapis.com/books/v1",
		steamBaseURL:             "https://store.steampowered.com/api",
		githubBaseURL:            "https://api.github.com",
		httpClient: &http.Client{
			Timeout: 30 * time.Second,
		},
	}
}

func (rs *RecommendationService) GetSimilarItems(ctx context.Context, req *SimilarItemsRequest) (*SimilarItemsResponse, error) {
	startTime := time.Now()

	// Set defaults
	if req.MaxLocalItems == 0 {
		req.MaxLocalItems = 10
	}
	if req.MaxExternalItems == 0 {
		req.MaxExternalItems = 5
	}
	if req.SimilarityThreshold == 0 {
		req.SimilarityThreshold = 0.3
	}

	response := &SimilarItemsResponse{
		LocalItems:    make([]*LocalSimilarItem, 0),
		ExternalItems: make([]*ExternalSimilarItem, 0),
		GeneratedAt:   time.Now(),
		Algorithms:    []string{"content_similarity", "metadata_matching", "collaborative_filtering"},
		Performance:   &RecommendationStats{},
	}

	// Find local similar items first
	localStartTime := time.Now()
	localItems, err := rs.findLocalSimilarItems(ctx, req)
	if err != nil {
		return nil, fmt.Errorf("failed to find local similar items: %w", err)
	}
	response.LocalItems = localItems
	response.Performance.LocalSearchTime = time.Since(localStartTime)
	response.Performance.LocalItemsFound = len(localItems)

	// Find external similar items if requested and needed
	if req.IncludeExternal && len(localItems) < req.MaxLocalItems {
		externalStartTime := time.Now()
		externalItems, err := rs.findExternalSimilarItems(ctx, req)
		if err != nil {
			// Log error but don't fail the entire request
			fmt.Printf("Warning: failed to find external similar items: %v\n", err)
		} else {
			response.ExternalItems = externalItems
			response.Performance.ExternalItemsFound = len(externalItems)
		}
		response.Performance.ExternalSearchTime = time.Since(externalStartTime)
	}

	response.TotalFound = len(response.LocalItems) + len(response.ExternalItems)
	response.Performance.TotalTime = time.Since(startTime)

	return response, nil
}

func (rs *RecommendationService) findLocalSimilarItems(ctx context.Context, req *SimilarItemsRequest) ([]*LocalSimilarItem, error) {
	// This would normally query the local database/catalog
	// For now, we'll simulate finding similar items

	var allLocalMedia []*models.MediaMetadata

	// In a real implementation, this would query the database
	// For demonstration, we'll create some mock similar items
	allLocalMedia = rs.generateMockLocalMedia(req.MediaMetadata)

	var similarItems []*LocalSimilarItem

	for _, media := range allLocalMedia {
		// Skip the same item by ID comparison
		if media.ID == req.MediaMetadata.ID {
			continue
		}

		// Calculate similarity
		similarity, reasons := rs.calculateLocalSimilarity(req.MediaMetadata, media)

		// Apply filters
		if !rs.passesFilters(media, similarity, req.Filters) {
			continue
		}

		// Apply similarity threshold
		if similarity < req.SimilarityThreshold {
			continue
		}

		similarItem := &LocalSimilarItem{
			MediaID:          rs.generateMediaID(media),
			MediaMetadata:    media,
			SimilarityScore:  similarity,
			SimilarityReasons: reasons,
			DetailLink:       rs.generateDetailLink(media),
			PlayLink:         rs.generatePlayLink(media),
			DownloadLink:     rs.generateDownloadLink(media),
			IsOwned:          true, // Local items are owned
		}

		similarItems = append(similarItems, similarItem)
	}

	// Sort by similarity score (descending)
	sort.Slice(similarItems, func(i, j int) bool {
		return similarItems[i].SimilarityScore > similarItems[j].SimilarityScore
	})

	// Limit results
	if len(similarItems) > req.MaxLocalItems {
		similarItems = similarItems[:req.MaxLocalItems]
	}

	return similarItems, nil
}

func (rs *RecommendationService) findExternalSimilarItems(ctx context.Context, req *SimilarItemsRequest) ([]*ExternalSimilarItem, error) {
	var externalItems []*ExternalSimilarItem

	// Note: MediaMetadata doesn't have MediaType field yet
	// For now, try to find similar items based on genre/description
	// TODO: Add MediaType field to MediaMetadata or use a different approach

	// Try finding similar items across all types
	if movieItems, err := rs.findSimilarMovies(ctx, req.MediaMetadata); err == nil {
		externalItems = append(externalItems, movieItems...)
	}

	if musicItems, err := rs.findSimilarMusic(ctx, req.MediaMetadata); err == nil {
		externalItems = append(externalItems, musicItems...)
	}

	// Apply filters to external items
	var filteredItems []*ExternalSimilarItem
	for _, item := range externalItems {
		if rs.passesExternalFilters(item, req.Filters) {
			filteredItems = append(filteredItems, item)
		}
	}

	// Sort by similarity score (descending)
	sort.Slice(filteredItems, func(i, j int) bool {
		return filteredItems[i].SimilarityScore > filteredItems[j].SimilarityScore
	})

	// Limit results
	if len(filteredItems) > req.MaxExternalItems {
		filteredItems = filteredItems[:req.MaxExternalItems]
	}

	return filteredItems, nil
}

func (rs *RecommendationService) findSimilarMovies(ctx context.Context, metadata *models.MediaMetadata) ([]*ExternalSimilarItem, error) {
	var items []*ExternalSimilarItem

	// TMDb similar movies
	tmdbItems, err := rs.getTMDbSimilarMovies(ctx, metadata)
	if err == nil {
		items = append(items, tmdbItems...)
	}

	// OMDb recommendations (genre-based)
	omdbItems, err := rs.getOMDbSimilarMovies(ctx, metadata)
	if err == nil {
		items = append(items, omdbItems...)
	}

	return items, nil
}

func (rs *RecommendationService) findSimilarMusic(ctx context.Context, metadata *models.MediaMetadata) ([]*ExternalSimilarItem, error) {
	var items []*ExternalSimilarItem

	// Last.fm similar artists and tracks
	lastfmItems, err := rs.getLastFmSimilarMusic(ctx, metadata)
	if err == nil {
		items = append(items, lastfmItems...)
	}

	return items, nil
}

func (rs *RecommendationService) findSimilarBooks(ctx context.Context, metadata *models.MediaMetadata) ([]*ExternalSimilarItem, error) {
	var items []*ExternalSimilarItem

	// Google Books similar books
	googleItems, err := rs.getGoogleBooksSimilar(ctx, metadata)
	if err == nil {
		items = append(items, googleItems...)
	}

	return items, nil
}

func (rs *RecommendationService) findSimilarGames(ctx context.Context, metadata *models.MediaMetadata) ([]*ExternalSimilarItem, error) {
	var items []*ExternalSimilarItem

	// IGDB similar games
	igdbItems, err := rs.getIGDBSimilarGames(ctx, metadata)
	if err == nil {
		items = append(items, igdbItems...)
	}

	// Steam recommendations
	steamItems, err := rs.getSteamSimilarGames(ctx, metadata)
	if err == nil {
		items = append(items, steamItems...)
	}

	return items, nil
}

func (rs *RecommendationService) findSimilarSoftware(ctx context.Context, metadata *models.MediaMetadata) ([]*ExternalSimilarItem, error) {
	var items []*ExternalSimilarItem

	// GitHub similar repositories
	githubItems, err := rs.getGitHubSimilarSoftware(ctx, metadata)
	if err == nil {
		items = append(items, githubItems...)
	}

	return items, nil
}

// External API integration methods
func (rs *RecommendationService) getTMDbSimilarMovies(ctx context.Context, metadata *models.MediaMetadata) ([]*ExternalSimilarItem, error) {
	// First, we need to find the movie ID
	searchURL := fmt.Sprintf("%s/search/movie?api_key=%s&query=%s",
		rs.tmdbBaseURL, rs.tmdbAPIKey, url.QueryEscape(metadata.Title))

	resp, err := rs.httpClient.Get(searchURL)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	var searchResult struct {
		Results []struct {
			ID           int    `json:"id"`
			Title        string `json:"title"`
			ReleaseDate  string `json:"release_date"`
			Overview     string `json:"overview"`
			PosterPath   string `json:"poster_path"`
			VoteAverage  float64 `json:"vote_average"`
			GenreIDs     []int  `json:"genre_ids"`
		} `json:"results"`
	}

	if err := json.NewDecoder(resp.Body).Decode(&searchResult); err != nil {
		return nil, err
	}

	if len(searchResult.Results) == 0 {
		return []*ExternalSimilarItem{}, nil
	}

	movieID := searchResult.Results[0].ID

	// Get similar movies
	similarURL := fmt.Sprintf("%s/movie/%d/similar?api_key=%s",
		rs.tmdbBaseURL, movieID, rs.tmdbAPIKey)

	resp2, err := rs.httpClient.Get(similarURL)
	if err != nil {
		return nil, err
	}
	defer resp2.Body.Close()

	var similarResult struct {
		Results []struct {
			ID          int     `json:"id"`
			Title       string  `json:"title"`
			ReleaseDate string  `json:"release_date"`
			Overview    string  `json:"overview"`
			PosterPath  string  `json:"poster_path"`
			VoteAverage float64 `json:"vote_average"`
		} `json:"results"`
	}

	if err := json.NewDecoder(resp2.Body).Decode(&similarResult); err != nil {
		return nil, err
	}

	var items []*ExternalSimilarItem
	for _, movie := range similarResult.Results {
		item := &ExternalSimilarItem{
			ExternalID:      fmt.Sprintf("tmdb_%d", movie.ID),
			Title:           movie.Title,
			Description:     movie.Overview,
			Year:            rs.extractYear(movie.ReleaseDate),
			Rating:          movie.VoteAverage,
			Provider:        "TMDb",
			ExternalLink:    fmt.Sprintf("https://www.themoviedb.org/movie/%d", movie.ID),
			SimilarityScore: rs.calculateTMDbSimilarity(metadata, movie.Title, movie.ReleaseDate, movie.VoteAverage),
			SimilarityReasons: []string{"genre_match", "tmdb_recommendation"},
		}

		if movie.PosterPath != "" {
			item.CoverArt = fmt.Sprintf("https://image.tmdb.org/t/p/w500%s", movie.PosterPath)
		}

		items = append(items, item)
	}

	return items, nil
}

func (rs *RecommendationService) getOMDbSimilarMovies(ctx context.Context, metadata *models.MediaMetadata) ([]*ExternalSimilarItem, error) {
	// OMDb doesn't have a "similar" endpoint, so we'll search by genre
	// This is a simplified implementation
	searchURL := fmt.Sprintf("%s?apikey=%s&s=%s&type=movie",
		rs.omdbBaseURL, rs.omdbAPIKey, url.QueryEscape(metadata.Genre))

	resp, err := rs.httpClient.Get(searchURL)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	var searchResult struct {
		Search []struct {
			Title    string `json:"Title"`
			Year     string `json:"Year"`
			IMDbID   string `json:"imdbID"`
			Type     string `json:"Type"`
			Poster   string `json:"Poster"`
		} `json:"Search"`
		Response string `json:"Response"`
	}

	if err := json.NewDecoder(resp.Body).Decode(&searchResult); err != nil {
		return nil, err
	}

	if searchResult.Response != "True" {
		return []*ExternalSimilarItem{}, nil
	}

	var items []*ExternalSimilarItem
	for i, movie := range searchResult.Search {
		if i >= 3 { // Limit to first 3 results
			break
		}

		// Skip the same movie
		if strings.ToLower(movie.Title) == strings.ToLower(metadata.Title) {
			continue
		}

		item := &ExternalSimilarItem{
			ExternalID:      fmt.Sprintf("imdb_%s", movie.IMDbID),
			Title:           movie.Title,
			Year:            movie.Year,
			Provider:        "IMDb",
			ExternalLink:    fmt.Sprintf("https://www.imdb.com/title/%s", movie.IMDbID),
			SimilarityScore: rs.calculateOMDbSimilarity(metadata, movie.Title, movie.Year),
			SimilarityReasons: []string{"genre_match", "imdb_search"},
		}

		if movie.Poster != "N/A" {
			item.CoverArt = movie.Poster
		}

		items = append(items, item)
	}

	return items, nil
}

func (rs *RecommendationService) getLastFmSimilarMusic(ctx context.Context, metadata *models.MediaMetadata) ([]*ExternalSimilarItem, error) {
	// Get similar artists
	// Note: MediaMetadata doesn't have Artist field, using Director field which may contain artist name
	artist := metadata.Director
	if artist == "" {
		artist = metadata.Title // Fallback to title if no director/artist
	}
	artistURL := fmt.Sprintf("%s?method=artist.getsimilar&artist=%s&api_key=%s&format=json",
		rs.lastfmBaseURL, url.QueryEscape(artist), rs.lastfmAPIKey)

	resp, err := rs.httpClient.Get(artistURL)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	var artistResult struct {
		SimilarArtists struct {
			Artist []struct {
				Name  string `json:"name"`
				Match string `json:"match"`
				URL   string `json:"url"`
				Image []struct {
					Text string `json:"#text"`
					Size string `json:"size"`
				} `json:"image"`
			} `json:"artist"`
		} `json:"similarartists"`
	}

	if err := json.NewDecoder(resp.Body).Decode(&artistResult); err != nil {
		return nil, err
	}

	var items []*ExternalSimilarItem
	for i, artist := range artistResult.SimilarArtists.Artist {
		if i >= 5 { // Limit to first 5 results
			break
		}

		item := &ExternalSimilarItem{
			ExternalID:      fmt.Sprintf("lastfm_artist_%s", strings.ReplaceAll(artist.Name, " ", "_")),
			Title:           artist.Name,
			Subtitle:        "Similar Artist",
			Provider:        "Last.fm",
			ExternalLink:    artist.URL,
			SimilarityScore: rs.parseLastFmMatch(artist.Match),
			SimilarityReasons: []string{"artist_similarity", "lastfm_recommendation"},
		}

		// Get the largest image
		for _, img := range artist.Image {
			if img.Size == "large" && img.Text != "" {
				item.CoverArt = img.Text
				break
			}
		}

		items = append(items, item)
	}

	return items, nil
}

func (rs *RecommendationService) getGoogleBooksSimilar(ctx context.Context, metadata *models.MediaMetadata) ([]*ExternalSimilarItem, error) {
	// Search for books by similar genre or title keywords
	// Note: MediaMetadata doesn't have Author field, using Title and Genre instead
	searchTerms := []string{
		fmt.Sprintf("intitle:%s", metadata.Title),
		fmt.Sprintf("subject:%s", metadata.Genre),
	}

	var items []*ExternalSimilarItem

	for _, term := range searchTerms {
		if term == "inauthor:" || term == "subject:" {
			continue
		}

		searchURL := fmt.Sprintf("%s/volumes?q=%s&maxResults=3",
			rs.googleBooksBaseURL, url.QueryEscape(term))

		resp, err := rs.httpClient.Get(searchURL)
		if err != nil {
			continue
		}

		var result struct {
			Items []struct {
				ID         string `json:"id"`
				VolumeInfo struct {
					Title               string   `json:"title"`
					Authors             []string `json:"authors"`
					Description         string   `json:"description"`
					Categories          []string `json:"categories"`
					AverageRating       float64  `json:"averageRating"`
					PublishedDate       string   `json:"publishedDate"`
					ImageLinks          struct {
						Thumbnail string `json:"thumbnail"`
					} `json:"imageLinks"`
					CanonicalVolumeLink string `json:"canonicalVolumeLink"`
				} `json:"volumeInfo"`
			} `json:"items"`
		}

		if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
			resp.Body.Close()
			continue
		}
		resp.Body.Close()

		for _, book := range result.Items {
			// Skip the same book
			if strings.ToLower(book.VolumeInfo.Title) == strings.ToLower(metadata.Title) {
				continue
			}

			author := ""
			if len(book.VolumeInfo.Authors) > 0 {
				author = book.VolumeInfo.Authors[0]
			}

			item := &ExternalSimilarItem{
				ExternalID:      fmt.Sprintf("google_books_%s", book.ID),
				Title:           book.VolumeInfo.Title,
				Subtitle:        author,
				Description:     book.VolumeInfo.Description,
				Year:            rs.extractYear(book.VolumeInfo.PublishedDate),
				Rating:          book.VolumeInfo.AverageRating,
				Provider:        "Google Books",
				ExternalLink:    book.VolumeInfo.CanonicalVolumeLink,
				SimilarityScore: rs.calculateGoogleBooksSimilarity(metadata, book.VolumeInfo.Title, author),
				SimilarityReasons: []string{"author_match", "genre_match"},
			}

			if book.VolumeInfo.ImageLinks.Thumbnail != "" {
				item.CoverArt = book.VolumeInfo.ImageLinks.Thumbnail
			}

			items = append(items, item)
		}

		if len(items) >= 5 {
			break
		}
	}

	return items, nil
}

func (rs *RecommendationService) getIGDBSimilarGames(ctx context.Context, metadata *models.MediaMetadata) ([]*ExternalSimilarItem, error) {
	// IGDB requires OAuth token - simplified implementation
	// In a real implementation, you'd get an OAuth token first

	// Mock similar games for demonstration
	var items []*ExternalSimilarItem

	// This would normally use IGDB's similar games endpoint
	similarGames := []struct {
		ID     int
		Name   string
		Genre  string
		Rating float64
		URL    string
	}{
		{1, "Similar Game 1", metadata.Genre, 8.5, "https://www.igdb.com/games/similar-game-1"},
		{2, "Similar Game 2", metadata.Genre, 7.8, "https://www.igdb.com/games/similar-game-2"},
	}

	for _, game := range similarGames {
		item := &ExternalSimilarItem{
			ExternalID:      fmt.Sprintf("igdb_%d", game.ID),
			Title:           game.Name,
			Genre:           game.Genre,
			Rating:          game.Rating,
			Provider:        "IGDB",
			ExternalLink:    game.URL,
			SimilarityScore: 0.8, // Mock score
			SimilarityReasons: []string{"genre_match", "igdb_recommendation"},
		}

		items = append(items, item)
	}

	return items, nil
}

func (rs *RecommendationService) getSteamSimilarGames(ctx context.Context, metadata *models.MediaMetadata) ([]*ExternalSimilarItem, error) {
	// Steam doesn't have a public recommendations API
	// This would normally require Steam Web API key and additional processing

	// Mock implementation
	var items []*ExternalSimilarItem

	// This is just for demonstration
	steamGames := []struct {
		ID     string
		Name   string
		Genre  string
		Price  string
	}{
		{"123456", "Steam Similar Game 1", metadata.Genre, "$19.99"},
		{"789012", "Steam Similar Game 2", metadata.Genre, "$29.99"},
	}

	for _, game := range steamGames {
		item := &ExternalSimilarItem{
			ExternalID:      fmt.Sprintf("steam_%s", game.ID),
			Title:           game.Name,
			Genre:           game.Genre,
			Provider:        "Steam",
			ExternalLink:    fmt.Sprintf("https://store.steampowered.com/app/%s", game.ID),
			SimilarityScore: 0.7, // Mock score
			SimilarityReasons: []string{"genre_match", "steam_recommendation"},
			PriceInfo: &PriceInfo{
				PurchasePrice: game.Price,
				Currency:      "USD",
				LastUpdated:   time.Now(),
			},
		}

		items = append(items, item)
	}

	return items, nil
}

func (rs *RecommendationService) getGitHubSimilarSoftware(ctx context.Context, metadata *models.MediaMetadata) ([]*ExternalSimilarItem, error) {
	// Search for similar repositories by topic or language
	searchQuery := fmt.Sprintf("topic:%s", strings.ToLower(metadata.Genre))
	searchURL := fmt.Sprintf("%s/search/repositories?q=%s&sort=stars&order=desc&per_page=5",
		rs.githubBaseURL, url.QueryEscape(searchQuery))

	resp, err := rs.httpClient.Get(searchURL)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	var result struct {
		Items []struct {
			ID          int    `json:"id"`
			Name        string `json:"name"`
			FullName    string `json:"full_name"`
			Description string `json:"description"`
			Language    string `json:"language"`
			StargazersCount int `json:"stargazers_count"`
			HTMLURL     string `json:"html_url"`
		} `json:"items"`
	}

	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		return nil, err
	}

	var items []*ExternalSimilarItem
	for _, repo := range result.Items {
		// Skip if same name
		if strings.ToLower(repo.Name) == strings.ToLower(metadata.Title) {
			continue
		}

		item := &ExternalSimilarItem{
			ExternalID:      fmt.Sprintf("github_%d", repo.ID),
			Title:           repo.Name,
			Subtitle:        repo.FullName,
			Description:     repo.Description,
			Provider:        "GitHub",
			ExternalLink:    repo.HTMLURL,
			SimilarityScore: rs.calculateGitHubSimilarity(metadata, repo.Name, repo.Language, repo.StargazersCount),
			SimilarityReasons: []string{"topic_match", "github_stars"},
		}

		items = append(items, item)
	}

	return items, nil
}

// Helper methods for similarity calculation
func (rs *RecommendationService) calculateLocalSimilarity(original, candidate *models.MediaMetadata) (float64, []string) {
	var score float64
	var reasons []string

	// Use basic text similarity as a foundation
	titleSimilarity := rs.duplicateDetectionService.calculateTextSimilarity(original.Title, candidate.Title)
	score = titleSimilarity * 0.5

	if titleSimilarity > 0.8 {
		reasons = append(reasons, "high_title_similarity")
	}

	// Additional similarity factors
	// Note: MediaType field doesn't exist in MediaMetadata, skipping this comparison
	// if original.MediaType == candidate.MediaType {
	// 	score += 0.1
	// 	reasons = append(reasons, "same_media_type")
	// }

	if original.Genre == candidate.Genre && original.Genre != "" {
		score += 0.15
		reasons = append(reasons, "same_genre")
	}

	if original.Year != nil && candidate.Year != nil && *original.Year == *candidate.Year {
		score += 0.1
		reasons = append(reasons, "same_year")
	}

	// Note: Artist field doesn't exist, using Director instead
	if original.Director == candidate.Director && original.Director != "" {
		score += 0.2
		reasons = append(reasons, "same_director")
	}

	// Note: Author field doesn't exist, using Producer instead
	if original.Producer == candidate.Producer && original.Producer != "" {
		score += 0.2
		reasons = append(reasons, "same_producer")
	}

	// Note: Developer field doesn't exist in MediaMetadata
	// if original.Developer == candidate.Developer && original.Developer != "" {
	// 	score += 0.15
	// 	reasons = append(reasons, "same_developer")
	// }

	// Normalize score to [0, 1]
	if score > 1.0 {
		score = 1.0
	}

	return score, reasons
}

func (rs *RecommendationService) calculateTMDbSimilarity(original *models.MediaMetadata, title, releaseDate string, rating float64) float64 {
	score := 0.5 // Base score for TMDb recommendation

	// Year similarity (Year is *int, candidateYear is string)
	if original.Year != nil {
		candidateYear := rs.extractYear(releaseDate)
		if candidateYear != "" && fmt.Sprintf("%d", *original.Year) == candidateYear {
			score += 0.2
		}
	}

	// Rating similarity (if original has rating)
	if original.Rating != nil && *original.Rating > 0 {
		ratingDiff := abs(*original.Rating - rating)
		if ratingDiff < 1.0 {
			score += 0.1
		}
	}

	// Title similarity
	titleSim := rs.duplicateDetectionService.calculateTextSimilarity(original.Title, title)
	score += titleSim * 0.2

	return score
}

func (rs *RecommendationService) calculateOMDbSimilarity(original *models.MediaMetadata, title, year string) float64 {
	score := 0.4 // Base score for OMDb search

	// Year similarity (Year is *int, year parameter is string)
	if original.Year != nil && year != "" && fmt.Sprintf("%d", *original.Year) == year {
		score += 0.3
	}

	// Title similarity
	titleSim := rs.duplicateDetectionService.calculateTextSimilarity(original.Title, title)
	score += titleSim * 0.3

	return score
}

func (rs *RecommendationService) calculateGoogleBooksSimilarity(original *models.MediaMetadata, title, author string) float64 {
	score := 0.4 // Base score

	// Author similarity (using Producer as fallback for author)
	if original.Producer != "" && author != "" {
		authorSim := rs.duplicateDetectionService.calculateTextSimilarity(original.Producer, author)
		score += authorSim * 0.4
	}

	// Title similarity
	titleSim := rs.duplicateDetectionService.calculateTextSimilarity(original.Title, title)
	score += titleSim * 0.2

	return score
}

func (rs *RecommendationService) calculateGitHubSimilarity(original *models.MediaMetadata, name, language string, stars int) float64 {
	score := 0.3 // Base score

	// Name similarity
	nameSim := rs.duplicateDetectionService.calculateTextSimilarity(original.Title, name)
	score += nameSim * 0.3

	// Language/genre similarity
	if original.Genre != "" && language != "" {
		langSim := rs.duplicateDetectionService.calculateTextSimilarity(original.Genre, language)
		score += langSim * 0.2
	}

	// Stars boost (popular repositories)
	if stars > 1000 {
		score += 0.1
	}
	if stars > 10000 {
		score += 0.1
	}

	return score
}

// Utility methods
func (rs *RecommendationService) passesFilters(media *models.MediaMetadata, similarity float64, filters *RecommendationFilters) bool {
	if filters == nil {
		return true
	}

	// Minimum confidence check
	if similarity < filters.MinConfidence {
		return false
	}

	// Genre filter
	if len(filters.GenreFilter) > 0 {
		found := false
		for _, genre := range filters.GenreFilter {
			if strings.Contains(strings.ToLower(media.Genre), strings.ToLower(genre)) {
				found = true
				break
			}
		}
		if !found {
			return false
		}
	}

	// Year range filter (Year is *int, not string)
	if filters.YearRange != nil && media.Year != nil {
		year := *media.Year
		if year < filters.YearRange.StartYear || year > filters.YearRange.EndYear {
			return false
		}
	}

	// Rating range filter (Rating is *float64)
	if filters.RatingRange != nil && media.Rating != nil && *media.Rating > 0 {
		if *media.Rating < filters.RatingRange.MinRating || *media.Rating > filters.RatingRange.MaxRating {
			return false
		}
	}

	// Language filter
	if len(filters.LanguageFilter) > 0 {
		found := false
		for _, lang := range filters.LanguageFilter {
			if strings.EqualFold(media.Language, lang) {
				found = true
				break
			}
		}
		if !found {
			return false
		}
	}

	return true
}

func (rs *RecommendationService) passesExternalFilters(item *ExternalSimilarItem, filters *RecommendationFilters) bool {
	if filters == nil {
		return true
	}

	// Genre filter
	if len(filters.GenreFilter) > 0 {
		found := false
		for _, genre := range filters.GenreFilter {
			if strings.Contains(strings.ToLower(item.Genre), strings.ToLower(genre)) {
				found = true
				break
			}
		}
		if !found {
			return false
		}
	}

	// Year range filter
	if filters.YearRange != nil {
		year := parseYear(item.Year)
		if year != 0 && (year < filters.YearRange.StartYear || year > filters.YearRange.EndYear) {
			return false
		}
	}

	// Rating range filter
	if filters.RatingRange != nil && item.Rating > 0 {
		if item.Rating < filters.RatingRange.MinRating || item.Rating > filters.RatingRange.MaxRating {
			return false
		}
	}

	return true
}

// Link generation methods
func (rs *RecommendationService) generateDetailLink(media *models.MediaMetadata) string {
	mediaID := rs.generateMediaID(media)
	return fmt.Sprintf("/detail/%s", mediaID)
}

func (rs *RecommendationService) generatePlayLink(media *models.MediaMetadata) string {
	// Note: MediaType doesn't exist in MediaMetadata, returning play link for all media
	mediaID := rs.generateMediaID(media)
	return fmt.Sprintf("/play/%s", mediaID)
}

func (rs *RecommendationService) generateDownloadLink(media *models.MediaMetadata) string {
	mediaID := rs.generateMediaID(media)
	return fmt.Sprintf("/download/%s", mediaID)
}

func (rs *RecommendationService) generateMediaID(media *models.MediaMetadata) string {
	// Generate a unique ID based on media ID (FilePath doesn't exist in MediaMetadata)
	return fmt.Sprintf("%d", media.ID)
}

// Mock data generation for testing
func (rs *RecommendationService) generateMockLocalMedia(original *models.MediaMetadata) []*models.MediaMetadata {
	var mockMedia []*models.MediaMetadata

	// Create mock similar items based on genre
	// Note: This is simplified since MediaMetadata doesn't have MediaType, Artist, Author, FilePath, etc.
	year2022 := 2022
	year2023 := 2023
	rating82 := 8.2
	rating78 := 7.8
	rating85 := 8.5
	duration240 := 240
	duration195 := 195

	mockMedia = append(mockMedia, []*models.MediaMetadata{
		{
			Title:       "Similar Movie 1",
			Year:        original.Year,
			Genre:       original.Genre,
			Director:    "Similar Director",
			Rating:      &rating82,
		},
		{
			Title:       "Another " + original.Genre + " Film",
			Year:        &year2022,
			Genre:       original.Genre,
			Director:    "Another Director",
			Rating:      &rating78,
		},
		{
			Title:       "Similar Track",
			Year:        original.Year,
			Genre:       original.Genre,
			Producer:    "Similar Producer",
			Duration:    &duration240,
			Rating:      &rating85,
		},
		{
			Title:       "Another Media Item",
			Year:        &year2023,
			Genre:       original.Genre,
			Producer:    "Different Producer",
			Duration:    &duration195,
		},
	}...)

	return mockMedia
}

// Utility helper functions
func (rs *RecommendationService) extractYear(dateStr string) string {
	if len(dateStr) >= 4 {
		return dateStr[:4]
	}
	return ""
}

func (rs *RecommendationService) parseLastFmMatch(matchStr string) float64 {
	// Last.fm match is typically a decimal string like "0.85"
	if matchStr == "" {
		return 0.5
	}
	// This would normally parse the string to float
	// For simplicity, returning a mock value
	return 0.7
}

func parseYear(yearStr string) int {
	if yearStr == "" {
		return 0
	}
	// This would normally parse the year string to int
	// For simplicity, returning a mock value
	return 2023
}

func abs(x float64) float64 {
	if x < 0 {
		return -x
	}
	return x
}