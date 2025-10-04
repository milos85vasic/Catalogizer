package handlers

import (
	"catalog-api/internal/models"
	"catalog-api/internal/services"
	"fmt"
	"net/http"
	"os"
	"path/filepath"
	"strings"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
)

type CopyHandler struct {
	catalogService *services.CatalogService
	smbService     *services.SMBService
	tempDir        string
	logger         *zap.Logger
}

func NewCopyHandler(catalogService *services.CatalogService, smbService *services.SMBService, tempDir string, logger *zap.Logger) *CopyHandler {
	return &CopyHandler{
		catalogService: catalogService,
		smbService:     smbService,
		tempDir:        tempDir,
		logger:         logger,
	}
}

// @Summary Copy file between SMB shares
// @Description Copy a file from one SMB location to another
// @Tags copy
// @Accept json
// @Param request body models.CopyRequest true "Copy request"
// @Produce json
// @Success 200 {object} map[string]string
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /api/v1/copy/smb [post]
func (h *CopyHandler) CopyToSMB(c *gin.Context) {
	var req models.CopyRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}

	if req.SourcePath == "" || req.DestinationPath == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Source and destination paths are required"})
		return
	}

	// Parse source and destination
	sourceHost, sourcePath := h.parseHostPath(req.SourcePath)
	destHost, destPath := h.parseHostPath(req.DestinationPath)

	if sourceHost == "" || destHost == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid host:path format. Use 'host:path'"})
		return
	}

	// Check if destination exists and handle overwrite
	if !req.Overwrite {
		exists, err := h.smbService.FileExists(destHost, destPath)
		if err != nil {
			h.logger.Error("Failed to check destination file", zap.Error(err))
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to check destination"})
			return
		}
		if exists {
			c.JSON(http.StatusConflict, gin.H{"error": "Destination file already exists"})
			return
		}
	}

	// Perform copy
	err := h.smbService.CopyFile(sourceHost, sourcePath, destHost, destPath)
	if err != nil {
		h.logger.Error("Failed to copy file via SMB",
			zap.String("source", req.SourcePath),
			zap.String("destination", req.DestinationPath),
			zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Copy operation failed"})
		return
	}

	h.logger.Info("File copied successfully via SMB",
		zap.String("source", req.SourcePath),
		zap.String("destination", req.DestinationPath))

	c.JSON(http.StatusOK, gin.H{
		"message":     "File copied successfully",
		"source":      req.SourcePath,
		"destination": req.DestinationPath,
	})
}

// @Summary Copy file from SMB to local filesystem
// @Description Copy a file from SMB share to local filesystem
// @Tags copy
// @Accept json
// @Param request body models.CopyRequest true "Copy request"
// @Produce json
// @Success 200 {object} map[string]string
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /api/v1/copy/local [post]
func (h *CopyHandler) CopyToLocal(c *gin.Context) {
	var req models.CopyRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}

	if req.SourcePath == "" || req.DestinationPath == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Source and destination paths are required"})
		return
	}

	// Parse source
	sourceHost, sourcePath := h.parseHostPath(req.SourcePath)
	if sourceHost == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid source format. Use 'host:path'"})
		return
	}

	// Destination is local path
	destPath := req.DestinationPath

	// Check if destination exists and handle overwrite
	if !req.Overwrite {
		if _, err := os.Stat(destPath); err == nil {
			c.JSON(http.StatusConflict, gin.H{"error": "Destination file already exists"})
			return
		}
	}

	// Create destination directory if needed
	if err := os.MkdirAll(filepath.Dir(destPath), 0755); err != nil {
		h.logger.Error("Failed to create destination directory", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create destination directory"})
		return
	}

	// Download from SMB to local
	err := h.smbService.DownloadFile(sourceHost, sourcePath, destPath)
	if err != nil {
		h.logger.Error("Failed to copy file from SMB to local",
			zap.String("source", req.SourcePath),
			zap.String("destination", destPath),
			zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Copy operation failed"})
		return
	}

	h.logger.Info("File copied successfully from SMB to local",
		zap.String("source", req.SourcePath),
		zap.String("destination", destPath))

	c.JSON(http.StatusOK, gin.H{
		"message":     "File copied successfully to local filesystem",
		"source":      req.SourcePath,
		"destination": destPath,
	})
}

// @Summary Upload file from local to SMB
// @Description Upload a file from local filesystem to SMB share
// @Tags copy
// @Accept multipart/form-data
// @Param file formData file true "File to upload"
// @Param destination formData string true "Destination path (host:path)"
// @Param overwrite formData bool false "Overwrite existing file"
// @Produce json
// @Success 200 {object} map[string]string
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /api/v1/copy/upload [post]
func (h *CopyHandler) CopyFromLocal(c *gin.Context) {
	// Get uploaded file
	file, header, err := c.Request.FormFile("file")
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "No file uploaded"})
		return
	}
	defer file.Close()

	destination := c.PostForm("destination")
	if destination == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Destination path is required"})
		return
	}

	overwrite := c.PostForm("overwrite") == "true"

	// Parse destination
	destHost, destPath := h.parseHostPath(destination)
	if destHost == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid destination format. Use 'host:path'"})
		return
	}

	// Check if destination exists and handle overwrite
	if !overwrite {
		exists, err := h.smbService.FileExists(destHost, destPath)
		if err != nil {
			h.logger.Error("Failed to check destination file", zap.Error(err))
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to check destination"})
			return
		}
		if exists {
			c.JSON(http.StatusConflict, gin.H{"error": "Destination file already exists"})
			return
		}
	}

	// Save uploaded file to temp location
	tempFile, err := os.CreateTemp(h.tempDir, "upload_*")
	if err != nil {
		h.logger.Error("Failed to create temp file", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to process upload"})
		return
	}
	defer os.Remove(tempFile.Name())
	defer tempFile.Close()

	// Copy uploaded data to temp file
	_, err = tempFile.ReadFrom(file)
	if err != nil {
		h.logger.Error("Failed to save uploaded file", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to save upload"})
		return
	}

	// Upload to SMB
	err = h.smbService.UploadFile(destHost, tempFile.Name(), destPath)
	if err != nil {
		h.logger.Error("Failed to upload file to SMB",
			zap.String("filename", header.Filename),
			zap.String("destination", destination),
			zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Upload to SMB failed"})
		return
	}

	h.logger.Info("File uploaded successfully to SMB",
		zap.String("filename", header.Filename),
		zap.String("destination", destination),
		zap.Int64("size", header.Size))

	c.JSON(http.StatusOK, gin.H{
		"message":     "File uploaded successfully",
		"filename":    header.Filename,
		"destination": destination,
		"size":        header.Size,
	})
}

// @Summary List files in SMB directory
// @Description List files and directories in an SMB share
// @Tags smb
// @Param host query string true "SMB host name"
// @Param path path string false "Directory path"
// @Produce json
// @Success 200 {object} map[string]interface{}
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /api/v1/smb/list/{path} [get]
func (h *CopyHandler) ListSMBPath(c *gin.Context) {
	hostName := c.Query("host")
	if hostName == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Host name is required"})
		return
	}

	path := c.Param("path")
	if path == "" {
		path = "/"
	}

	// Remove leading slash if present
	if path[0] == '/' {
		path = path[1:]
	}

	files, err := h.smbService.ListFiles(hostName, path)
	if err != nil {
		h.logger.Error("Failed to list SMB directory",
			zap.String("host", hostName),
			zap.String("path", path),
			zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to list directory"})
		return
	}

	// Convert os.FileInfo to a more JSON-friendly format
	var fileList []map[string]interface{}
	for _, file := range files {
		fileList = append(fileList, map[string]interface{}{
			"name":          file.Name(),
			"size":          file.Size(),
			"is_directory":  file.IsDir(),
			"last_modified": file.ModTime(),
			"mode":          file.Mode().String(),
		})
	}

	c.JSON(http.StatusOK, gin.H{
		"host":  hostName,
		"path":  path,
		"files": fileList,
		"count": len(fileList),
	})
}

// Helper function to parse host:path format
func (h *CopyHandler) parseHostPath(hostPath string) (string, string) {
	parts := strings.SplitN(hostPath, ":", 2)
	if len(parts) != 2 {
		return "", ""
	}
	return parts[0], parts[1]
}

// @Summary Get available SMB hosts
// @Description Get list of configured SMB hosts
// @Tags smb
// @Produce json
// @Success 200 {array} string
// @Router /api/v1/smb/hosts [get]
func (h *CopyHandler) GetSMBHosts(c *gin.Context) {
	hosts := h.smbService.GetHosts()
	c.JSON(http.StatusOK, gin.H{
		"hosts": hosts,
		"count": len(hosts),
	})
}