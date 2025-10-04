package models

import (
	"time"
)

// FileInfo represents a file or directory in the catalog
type FileInfo struct {
	ID           int64     `json:"id" db:"id"`
	Name         string    `json:"name" db:"name"`
	Path         string    `json:"path" db:"path"`
	IsDirectory  bool      `json:"is_directory" db:"is_directory"`
	Size         int64     `json:"size" db:"size"`
	LastModified time.Time `json:"last_modified" db:"last_modified"`
	Hash         *string   `json:"hash,omitempty" db:"hash"`
	Extension    *string   `json:"extension,omitempty" db:"extension"`
	MimeType     *string   `json:"mime_type,omitempty" db:"mime_type"`
	ParentID     *int64    `json:"parent_id,omitempty" db:"parent_id"`
	SmbRoot      string    `json:"smb_root" db:"smb_root"`
	CreatedAt    time.Time `json:"created_at" db:"created_at"`
	UpdatedAt    time.Time `json:"updated_at" db:"updated_at"`
}

// DirectoryStats represents statistics for a directory
type DirectoryStats struct {
	Path           string `json:"path"`
	TotalSize      int64  `json:"total_size"`
	FileCount      int64  `json:"file_count"`
	DirectoryCount int64  `json:"directory_count"`
	DuplicateCount int64  `json:"duplicate_count"`
}

// DuplicateGroup represents a group of duplicate files
type DuplicateGroup struct {
	Hash      string     `json:"hash"`
	Size      int64      `json:"size"`
	Count     int        `json:"count"`
	Files     []FileInfo `json:"files"`
	TotalSize int64      `json:"total_size"`
}

// SearchRequest represents a search request
type SearchRequest struct {
	Query       string   `json:"query" form:"query"`
	Path        string   `json:"path" form:"path"`
	Extension   string   `json:"extension" form:"extension"`
	MimeType    string   `json:"mime_type" form:"mime_type"`
	MinSize     *int64   `json:"min_size" form:"min_size"`
	MaxSize     *int64   `json:"max_size" form:"max_size"`
	SmbRoots    []string `json:"smb_roots" form:"smb_roots"`
	IsDirectory *bool    `json:"is_directory" form:"is_directory"`
	Limit       int      `json:"limit" form:"limit"`
	Offset      int      `json:"offset" form:"offset"`
	SortBy      string   `json:"sort_by" form:"sort_by"`
	SortOrder   string   `json:"sort_order" form:"sort_order"`
}

// CopyRequest represents a copy operation request
type CopyRequest struct {
	SourcePath      string `json:"source_path" binding:"required"`
	DestinationPath string `json:"destination_path" binding:"required"`
	SmbRoot         string `json:"smb_root"`
	Overwrite       bool   `json:"overwrite"`
}

// DownloadRequest represents a download request
type DownloadRequest struct {
	Paths   []string `json:"paths" binding:"required"`
	Format  string   `json:"format"` // zip, tar, tar.gz
	SmbRoot string   `json:"smb_root"`
}

// Statistics models
type OverallStats struct {
	TotalFiles       int64 `json:"total_files"`
	TotalDirectories int64 `json:"total_directories"`
	TotalSize        int64 `json:"total_size"`
	DuplicateFiles   int64 `json:"duplicate_files"`
	DuplicateSize    int64 `json:"duplicate_size"`
	SmbRootCount     int64 `json:"smb_root_count"`
}

type FileTypeStats struct {
	Extension string `json:"extension"`
	Count     int64  `json:"count"`
	TotalSize int64  `json:"total_size"`
	AvgSize   int64  `json:"avg_size"`
}

type SizeDistribution struct {
	Range string `json:"range"`
	Count int64  `json:"count"`
	Size  int64  `json:"size"`
}