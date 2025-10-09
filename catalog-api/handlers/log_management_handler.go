package handlers

import (
	"encoding/json"
	"net/http"
	"strconv"
	"time"

	"catalog-api/models"
	"catalog-api/services"

	"github.com/gorilla/mux"
)

type LogManagementHandler struct {
	logManagementService *services.LogManagementService
	permissionService    *services.PermissionService
}

func NewLogManagementHandler(logManagementService *services.LogManagementService, permissionService *services.PermissionService) *LogManagementHandler {
	return &LogManagementHandler{
		logManagementService: logManagementService,
		permissionService:    permissionService,
	}
}

func (h *LogManagementHandler) CreateLogCollection(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)

	if !h.permissionService.HasPermission(userID, "log_management", "write") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	var request models.LogCollectionRequest
	if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	collection, err := h.logManagementService.CollectLogs(userID, &request)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(collection)
}

func (h *LogManagementHandler) GetLogCollection(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)
	vars := mux.Vars(r)
	collectionID, err := strconv.Atoi(vars["id"])
	if err != nil {
		http.Error(w, "Invalid collection ID", http.StatusBadRequest)
		return
	}

	if !h.permissionService.HasPermission(userID, "log_management", "read") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	collection, err := h.logManagementService.GetLogCollection(collectionID, userID)
	if err != nil {
		http.Error(w, err.Error(), http.StatusNotFound)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(collection)
}

func (h *LogManagementHandler) ListLogCollections(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)

	if !h.permissionService.HasPermission(userID, "log_management", "read") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	// Parse query parameters
	limitStr := r.URL.Query().Get("limit")
	offsetStr := r.URL.Query().Get("offset")

	limit := 20 // Default limit
	offset := 0 // Default offset

	if limitStr != "" {
		if parsedLimit, err := strconv.Atoi(limitStr); err == nil && parsedLimit > 0 {
			limit = parsedLimit
		}
	}

	if offsetStr != "" {
		if parsedOffset, err := strconv.Atoi(offsetStr); err == nil && parsedOffset >= 0 {
			offset = parsedOffset
		}
	}

	collections, err := h.logManagementService.GetLogCollectionsByUser(userID, limit, offset)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]interface{}{
		"collections": collections,
		"limit":       limit,
		"offset":      offset,
	})
}

func (h *LogManagementHandler) GetLogEntries(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)
	vars := mux.Vars(r)
	collectionID, err := strconv.Atoi(vars["id"])
	if err != nil {
		http.Error(w, "Invalid collection ID", http.StatusBadRequest)
		return
	}

	if !h.permissionService.HasPermission(userID, "log_management", "read") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	filters := h.parseLogEntryFilters(r)
	entries, err := h.logManagementService.GetLogEntries(collectionID, userID, filters)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]interface{}{
		"entries": entries,
		"filters": filters,
	})
}

func (h *LogManagementHandler) CreateLogShare(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)

	if !h.permissionService.HasPermission(userID, "log_management", "share") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	var request models.LogShareRequest
	if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	share, err := h.logManagementService.CreateLogShare(userID, &request)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(share)
}

func (h *LogManagementHandler) GetLogShare(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	token := vars["token"]

	share, err := h.logManagementService.GetLogShare(token)
	if err != nil {
		http.Error(w, err.Error(), http.StatusNotFound)
		return
	}

	// Get the collection
	collection, err := h.logManagementService.GetLogCollection(share.CollectionID, share.UserID)
	if err != nil {
		http.Error(w, err.Error(), http.StatusNotFound)
		return
	}

	// Check permissions
	canRead := false
	for _, permission := range share.Permissions {
		if permission == "read" {
			canRead = true
			break
		}
	}

	if !canRead {
		http.Error(w, "Access denied", http.StatusForbidden)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]interface{}{
		"share":      share,
		"collection": collection,
	})
}

func (h *LogManagementHandler) RevokeLogShare(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)
	vars := mux.Vars(r)
	shareID, err := strconv.Atoi(vars["id"])
	if err != nil {
		http.Error(w, "Invalid share ID", http.StatusBadRequest)
		return
	}

	if !h.permissionService.HasPermission(userID, "log_management", "share") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	err = h.logManagementService.RevokeLogShare(shareID, userID)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusNoContent)
}

func (h *LogManagementHandler) ExportLogs(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)
	vars := mux.Vars(r)
	collectionID, err := strconv.Atoi(vars["id"])
	if err != nil {
		http.Error(w, "Invalid collection ID", http.StatusBadRequest)
		return
	}

	if !h.permissionService.HasPermission(userID, "log_management", "read") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	format := r.URL.Query().Get("format")
	if format == "" {
		format = "json" // Default format
	}

	data, err := h.logManagementService.ExportLogs(collectionID, userID, format)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	// Set appropriate content type and headers
	switch format {
	case "json":
		w.Header().Set("Content-Type", "application/json")
		w.Header().Set("Content-Disposition", "attachment; filename=logs.json")
	case "csv":
		w.Header().Set("Content-Type", "text/csv")
		w.Header().Set("Content-Disposition", "attachment; filename=logs.csv")
	case "txt":
		w.Header().Set("Content-Type", "text/plain")
		w.Header().Set("Content-Disposition", "attachment; filename=logs.txt")
	case "zip":
		w.Header().Set("Content-Type", "application/zip")
		w.Header().Set("Content-Disposition", "attachment; filename=logs.zip")
	default:
		w.Header().Set("Content-Type", "application/octet-stream")
		w.Header().Set("Content-Disposition", "attachment; filename=logs.bin")
	}

	w.Write(data)
}

func (h *LogManagementHandler) StreamLogs(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)

	if !h.permissionService.HasPermission(userID, "log_management", "read") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	// Parse stream filters
	filters := h.parseLogStreamFilters(r)

	// Setup Server-Sent Events
	w.Header().Set("Content-Type", "text/event-stream")
	w.Header().Set("Cache-Control", "no-cache")
	w.Header().Set("Connection", "keep-alive")
	w.Header().Set("Access-Control-Allow-Origin", "*")

	// Get log stream
	logChannel, err := h.logManagementService.StreamLogs(userID, filters)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	// Stream logs to client
	flusher, ok := w.(http.Flusher)
	if !ok {
		http.Error(w, "Streaming not supported", http.StatusInternalServerError)
		return
	}

	for {
		select {
		case entry, ok := <-logChannel:
			if !ok {
				return
			}

			data, err := json.Marshal(entry)
			if err != nil {
				continue
			}

			w.Write([]byte("data: "))
			w.Write(data)
			w.Write([]byte("\n\n"))
			flusher.Flush()

		case <-r.Context().Done():
			return
		}
	}
}

func (h *LogManagementHandler) AnalyzeLogs(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)
	vars := mux.Vars(r)
	collectionID, err := strconv.Atoi(vars["id"])
	if err != nil {
		http.Error(w, "Invalid collection ID", http.StatusBadRequest)
		return
	}

	if !h.permissionService.HasPermission(userID, "log_management", "read") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	analysis, err := h.logManagementService.AnalyzeLogs(collectionID, userID)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(analysis)
}

func (h *LogManagementHandler) GetLogStatistics(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)

	if !h.permissionService.HasPermission(userID, "log_management", "read") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	stats, err := h.logManagementService.GetLogStatistics(userID)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(stats)
}

func (h *LogManagementHandler) GetConfiguration(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)

	if !h.permissionService.HasPermission(userID, "log_management", "admin") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	config := h.logManagementService.GetConfiguration()

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(config)
}

func (h *LogManagementHandler) UpdateConfiguration(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)

	if !h.permissionService.HasPermission(userID, "log_management", "admin") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	var config services.LogManagementConfig
	if err := json.NewDecoder(r.Body).Decode(&config); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	err := h.logManagementService.UpdateConfiguration(&config)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]string{
		"message": "Configuration updated successfully",
	})
}

func (h *LogManagementHandler) CleanupOldLogs(w http.ResponseWriter, r *http.Request) {
	userID := r.Context().Value("user_id").(int)

	if !h.permissionService.HasPermission(userID, "log_management", "admin") {
		http.Error(w, "Insufficient permissions", http.StatusForbidden)
		return
	}

	err := h.logManagementService.CleanupOldLogs()
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]string{
		"message": "Cleanup completed successfully",
	})
}

// Helper methods

func (h *LogManagementHandler) parseLogEntryFilters(r *http.Request) *models.LogEntryFilters {
	filters := &models.LogEntryFilters{}

	if level := r.URL.Query().Get("level"); level != "" {
		filters.Level = level
	}

	if component := r.URL.Query().Get("component"); component != "" {
		filters.Component = component
	}

	if search := r.URL.Query().Get("search"); search != "" {
		filters.Search = search
	}

	if startTime := r.URL.Query().Get("start_time"); startTime != "" {
		if date, err := time.Parse(time.RFC3339, startTime); err == nil {
			filters.StartTime = &date
		}
	}

	if endTime := r.URL.Query().Get("end_time"); endTime != "" {
		if date, err := time.Parse(time.RFC3339, endTime); err == nil {
			filters.EndTime = &date
		}
	}

	if limitStr := r.URL.Query().Get("limit"); limitStr != "" {
		if limit, err := strconv.Atoi(limitStr); err == nil && limit > 0 {
			filters.Limit = limit
		}
	}

	if offsetStr := r.URL.Query().Get("offset"); offsetStr != "" {
		if offset, err := strconv.Atoi(offsetStr); err == nil && offset >= 0 {
			filters.Offset = offset
		}
	}

	return filters
}

func (h *LogManagementHandler) parseLogStreamFilters(r *http.Request) *models.LogStreamFilters {
	filters := &models.LogStreamFilters{}

	if level := r.URL.Query().Get("level"); level != "" {
		filters.Level = level
	}

	if component := r.URL.Query().Get("component"); component != "" {
		filters.Component = component
	}

	if search := r.URL.Query().Get("search"); search != "" {
		filters.Search = search
	}

	return filters
}