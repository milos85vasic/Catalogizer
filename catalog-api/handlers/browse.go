package handlers

import (
	"net/http"
	"strconv"

	"catalog-api/models"
	"catalog-api/repository"
	"catalog-api/utils"

	"github.com/gin-gonic/gin"
)

// BrowseHandler handles browse operations
type BrowseHandler struct {
	fileRepo *repository.FileRepository
}

// NewBrowseHandler creates a new browse handler
func NewBrowseHandler(fileRepo *repository.FileRepository) *BrowseHandler {
	return &BrowseHandler{
		fileRepo: fileRepo,
	}
}

// GetSmbRoots godoc
// @Summary Get all SMB roots
// @Description Retrieve all configured SMB roots
// @Tags browse
// @Accept json
// @Produce json
// @Success 200 {array} models.SmbRoot
// @Failure 500 {object} utils.ErrorResponse
// @Router /api/browse/roots [get]
func (h *BrowseHandler) GetSmbRoots(c *gin.Context) {
	ctx := c.Request.Context()

	roots, err := h.fileRepo.GetSmbRoots(ctx)
	if err != nil {
		utils.ErrorResponse(c, http.StatusInternalServerError, "Failed to get SMB roots", err)
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"success": true,
		"data":    roots,
	})
}

// BrowseDirectory godoc
// @Summary Browse directory contents
// @Description Get files and directories within a specific path
// @Tags browse
// @Accept json
// @Produce json
// @Param smb_root path string true "SMB root name"
// @Param path query string false "Directory path" default("/")
// @Param page query int false "Page number" default(1)
// @Param limit query int false "Items per page" default(100)
// @Param sort_by query string false "Sort field (name, size, modified_at, created_at, path, extension)" default("name")
// @Param sort_order query string false "Sort order (asc, desc)" default("asc")
// @Success 200 {object} models.SearchResult
// @Failure 400 {object} utils.ErrorResponse
// @Failure 500 {object} utils.ErrorResponse
// @Router /api/browse/{smb_root} [get]
func (h *BrowseHandler) BrowseDirectory(c *gin.Context) {
	ctx := c.Request.Context()

	smbRoot := c.Param("smb_root")
	if smbRoot == "" {
		utils.ErrorResponse(c, http.StatusBadRequest, "SMB root name is required", nil)
		return
	}

	// Parse query parameters
	path := c.DefaultQuery("path", "/")
	page, _ := strconv.Atoi(c.DefaultQuery("page", "1"))
	limit, _ := strconv.Atoi(c.DefaultQuery("limit", "100"))
	sortBy := c.DefaultQuery("sort_by", "name")
	sortOrder := c.DefaultQuery("sort_order", "asc")

	// Validate pagination
	if page < 1 {
		page = 1
	}
	if limit < 1 || limit > 1000 {
		limit = 100
	}

	// Validate sort parameters
	validSortFields := map[string]bool{
		"name":        true,
		"size":        true,
		"modified_at": true,
		"created_at":  true,
		"path":        true,
		"extension":   true,
	}
	if !validSortFields[sortBy] {
		sortBy = "name"
	}

	if sortOrder != "asc" && sortOrder != "desc" {
		sortOrder = "asc"
	}

	pagination := models.PaginationOptions{
		Page:  page,
		Limit: limit,
	}

	sort := models.SortOptions{
		Field: sortBy,
		Order: sortOrder,
	}

	result, err := h.fileRepo.GetDirectoryContents(ctx, smbRoot, path, pagination, sort)
	if err != nil {
		utils.ErrorResponse(c, http.StatusInternalServerError, "Failed to browse directory", err)
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"success": true,
		"data":    result,
	})
}

// GetFileInfo godoc
// @Summary Get file information
// @Description Get detailed information about a specific file
// @Tags browse
// @Accept json
// @Produce json
// @Param id path int true "File ID"
// @Success 200 {object} models.FileWithMetadata
// @Failure 400 {object} utils.ErrorResponse
// @Failure 404 {object} utils.ErrorResponse
// @Failure 500 {object} utils.ErrorResponse
// @Router /api/browse/file/{id} [get]
func (h *BrowseHandler) GetFileInfo(c *gin.Context) {
	ctx := c.Request.Context()

	idStr := c.Param("id")
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		utils.ErrorResponse(c, http.StatusBadRequest, "Invalid file ID", err)
		return
	}

	file, err := h.fileRepo.GetFileByID(ctx, id)
	if err != nil {
		if err.Error() == "file not found" {
			utils.ErrorResponse(c, http.StatusNotFound, "File not found", err)
			return
		}
		utils.ErrorResponse(c, http.StatusInternalServerError, "Failed to get file info", err)
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"success": true,
		"data":    file,
	})
}

// GetDirectorySizes godoc
// @Summary Get directories sorted by size
// @Description Retrieve directories sorted by their total size
// @Tags browse
// @Accept json
// @Produce json
// @Param smb_root path string true "SMB root name"
// @Param page query int false "Page number" default(1)
// @Param limit query int false "Items per page" default(50)
// @Param ascending query bool false "Sort in ascending order" default(false)
// @Success 200 {array} models.DirectoryInfo
// @Failure 400 {object} utils.ErrorResponse
// @Failure 500 {object} utils.ErrorResponse
// @Router /api/browse/{smb_root}/sizes [get]
func (h *BrowseHandler) GetDirectorySizes(c *gin.Context) {
	ctx := c.Request.Context()

	smbRoot := c.Param("smb_root")
	if smbRoot == "" {
		utils.ErrorResponse(c, http.StatusBadRequest, "SMB root name is required", nil)
		return
	}

	// Parse query parameters
	page, _ := strconv.Atoi(c.DefaultQuery("page", "1"))
	limit, _ := strconv.Atoi(c.DefaultQuery("limit", "50"))
	ascending, _ := strconv.ParseBool(c.DefaultQuery("ascending", "false"))

	// Validate pagination
	if page < 1 {
		page = 1
	}
	if limit < 1 || limit > 500 {
		limit = 50
	}

	pagination := models.PaginationOptions{
		Page:  page,
		Limit: limit,
	}

	directories, err := h.fileRepo.GetDirectoriesSortedBySize(ctx, smbRoot, pagination, ascending)
	if err != nil {
		utils.ErrorResponse(c, http.StatusInternalServerError, "Failed to get directory sizes", err)
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"success": true,
		"data":    directories,
	})
}

// GetDirectoryDuplicates godoc
// @Summary Get directories sorted by duplicate count
// @Description Retrieve directories sorted by their number of duplicate files
// @Tags browse
// @Accept json
// @Produce json
// @Param smb_root path string true "SMB root name"
// @Param page query int false "Page number" default(1)
// @Param limit query int false "Items per page" default(50)
// @Param ascending query bool false "Sort in ascending order" default(false)
// @Success 200 {array} models.DirectoryInfo
// @Failure 400 {object} utils.ErrorResponse
// @Failure 500 {object} utils.ErrorResponse
// @Router /api/browse/{smb_root}/duplicates [get]
func (h *BrowseHandler) GetDirectoryDuplicates(c *gin.Context) {
	ctx := c.Request.Context()

	smbRoot := c.Param("smb_root")
	if smbRoot == "" {
		utils.ErrorResponse(c, http.StatusBadRequest, "SMB root name is required", nil)
		return
	}

	// Parse query parameters
	page, _ := strconv.Atoi(c.DefaultQuery("page", "1"))
	limit, _ := strconv.Atoi(c.DefaultQuery("limit", "50"))
	ascending, _ := strconv.ParseBool(c.DefaultQuery("ascending", "false"))

	// Validate pagination
	if page < 1 {
		page = 1
	}
	if limit < 1 || limit > 500 {
		limit = 50
	}

	pagination := models.PaginationOptions{
		Page:  page,
		Limit: limit,
	}

	directories, err := h.fileRepo.GetDirectoriesSortedByDuplicates(ctx, smbRoot, pagination, ascending)
	if err != nil {
		utils.ErrorResponse(c, http.StatusInternalServerError, "Failed to get directory duplicates", err)
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"success": true,
		"data":    directories,
	})
}