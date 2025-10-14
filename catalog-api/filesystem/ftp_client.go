package filesystem

import (
	"context"
	"fmt"
	"io"
	"path/filepath"
	"time"

	"github.com/jlaffaye/ftp"
)

// FTPConfig contains FTP connection configuration
type FTPConfig struct {
	Host     string `json:"host"`
	Port     int    `json:"port"`
	Username string `json:"username"`
	Password string `json:"password"`
	Path     string `json:"path"` // Base path on the FTP server
}

// FTPClient implements FileSystemClient for FTP protocol
type FTPClient struct {
	config    *FTPConfig
	client    *ftp.ServerConn
	connected bool
}

// NewFTPClient creates a new FTP client
func NewFTPClient(config *FTPConfig) *FTPClient {
	return &FTPClient{
		config:    config,
		connected: false,
	}
}

// Connect establishes the FTP connection
func (c *FTPClient) Connect(ctx context.Context) error {
	addr := fmt.Sprintf("%s:%d", c.config.Host, c.config.Port)

	client, err := ftp.Dial(addr, ftp.DialWithTimeout(30*time.Second))
	if err != nil {
		return fmt.Errorf("failed to connect to FTP server: %w", err)
	}

	err = client.Login(c.config.Username, c.config.Password)
	if err != nil {
		client.Quit()
		return fmt.Errorf("failed to login to FTP server: %w", err)
	}

	// Change to base directory if specified
	if c.config.Path != "" {
		err = client.ChangeDir(c.config.Path)
		if err != nil {
			client.Quit()
			return fmt.Errorf("failed to change to base directory %s: %w", c.config.Path, err)
		}
	}

	c.client = client
	c.connected = true
	return nil
}

// Disconnect closes the FTP connection
func (c *FTPClient) Disconnect(ctx context.Context) error {
	if c.client != nil {
		err := c.client.Quit()
		c.client = nil
		c.connected = false
		return err
	}
	c.connected = false
	return nil
}

// IsConnected returns true if the client is connected
func (c *FTPClient) IsConnected() bool {
	return c.connected && c.client != nil
}

// TestConnection tests the FTP connection
func (c *FTPClient) TestConnection(ctx context.Context) error {
	if !c.IsConnected() {
		return fmt.Errorf("not connected")
	}
	_, err := c.client.CurrentDir()
	return err
}

// resolvePath resolves a relative path within the FTP base directory
func (c *FTPClient) resolvePath(path string) string {
	if c.config.Path != "" {
		return c.config.Path + "/" + path
	}
	return path
}

// ReadFile reads a file from the FTP server
func (c *FTPClient) ReadFile(ctx context.Context, path string) (io.ReadCloser, error) {
	if !c.IsConnected() {
		return nil, fmt.Errorf("not connected")
	}
	fullPath := c.resolvePath(path)
	resp, err := c.client.Retr(fullPath)
	if err != nil {
		return nil, fmt.Errorf("failed to retrieve FTP file %s: %w", fullPath, err)
	}
	return resp, nil
}

// WriteFile writes a file to the FTP server
func (c *FTPClient) WriteFile(ctx context.Context, path string, data io.Reader) error {
	if !c.IsConnected() {
		return fmt.Errorf("not connected")
	}
	fullPath := c.resolvePath(path)

	// Ensure the directory exists
	dir := filepath.Dir(fullPath)
	if dir != "." && dir != "/" {
		err := c.client.MakeDir(dir)
		if err != nil {
			// Ignore error if directory already exists
			// FTP doesn't have a standard way to check if directory exists
		}
	}

	err := c.client.Stor(fullPath, data)
	if err != nil {
		return fmt.Errorf("failed to store FTP file %s: %w", fullPath, err)
	}
	return nil
}

// GetFileInfo gets information about a file
func (c *FTPClient) GetFileInfo(ctx context.Context, path string) (*FileInfo, error) {
	if !c.IsConnected() {
		return nil, fmt.Errorf("not connected")
	}
	fullPath := c.resolvePath(path)

	// Try to get file size
	size, err := c.client.FileSize(fullPath)
	if err != nil {
		return nil, fmt.Errorf("failed to get FTP file info %s: %w", fullPath, err)
	}

	// Get modification time - fallback since ModTime might not be available
	modTime := time.Now() // Default fallback

	// Check if it's a directory by trying to list it
	_, err = c.client.List(fullPath)
	isDir := err == nil

	return &FileInfo{
		Name:    filepath.Base(path),
		Size:    size,
		ModTime: modTime,
		IsDir:   isDir,
		Mode:    0644, // Default mode, FTP doesn't provide mode info
		Path:    path,
	}, nil
}

// ListDirectory lists files in a directory
func (c *FTPClient) ListDirectory(ctx context.Context, path string) ([]*FileInfo, error) {
	if !c.IsConnected() {
		return nil, fmt.Errorf("not connected")
	}
	fullPath := c.resolvePath(path)

	entries, err := c.client.List(fullPath)
	if err != nil {
		return nil, fmt.Errorf("failed to list FTP directory %s: %w", fullPath, err)
	}

	var files []*FileInfo
	for _, entry := range entries {
		files = append(files, &FileInfo{
			Name:    entry.Name,
			Size:    int64(entry.Size),
			ModTime: entry.Time,
			IsDir:   entry.Type == ftp.EntryTypeFolder,
			Mode:    0644, // Default mode
			Path:    path + "/" + entry.Name,
		})
	}

	return files, nil
}

// FileExists checks if a file exists
func (c *FTPClient) FileExists(ctx context.Context, path string) (bool, error) {
	if !c.IsConnected() {
		return false, fmt.Errorf("not connected")
	}
	fullPath := c.resolvePath(path)

	// Try to get file size - if it succeeds, file exists
	_, err := c.client.FileSize(fullPath)
	if err != nil {
		// Try to list the file's directory to see if it exists
		dir := filepath.Dir(fullPath)
		name := filepath.Base(fullPath)
		entries, err := c.client.List(dir)
		if err != nil {
			return false, fmt.Errorf("failed to check FTP file existence %s: %w", fullPath, err)
		}
		for _, entry := range entries {
			if entry.Name == name {
				return true, nil
			}
		}
		return false, nil
	}
	return true, nil
}

// CreateDirectory creates a directory
func (c *FTPClient) CreateDirectory(ctx context.Context, path string) error {
	if !c.IsConnected() {
		return fmt.Errorf("not connected")
	}
	fullPath := c.resolvePath(path)
	err := c.client.MakeDir(fullPath)
	if err != nil {
		return fmt.Errorf("failed to create FTP directory %s: %w", fullPath, err)
	}
	return nil
}

// DeleteDirectory deletes a directory
func (c *FTPClient) DeleteDirectory(ctx context.Context, path string) error {
	if !c.IsConnected() {
		return fmt.Errorf("not connected")
	}
	fullPath := c.resolvePath(path)
	err := c.client.RemoveDir(fullPath)
	if err != nil {
		return fmt.Errorf("failed to delete FTP directory %s: %w", fullPath, err)
	}
	return nil
}

// DeleteFile deletes a file
func (c *FTPClient) DeleteFile(ctx context.Context, path string) error {
	if !c.IsConnected() {
		return fmt.Errorf("not connected")
	}
	fullPath := c.resolvePath(path)
	err := c.client.Delete(fullPath)
	if err != nil {
		return fmt.Errorf("failed to delete FTP file %s: %w", fullPath, err)
	}
	return nil
}

// CopyFile copies a file on the FTP server
func (c *FTPClient) CopyFile(ctx context.Context, srcPath, dstPath string) error {
	if !c.IsConnected() {
		return fmt.Errorf("not connected")
	}

	// FTP doesn't have a native copy command, so we need to download and upload
	srcFullPath := c.resolvePath(srcPath)
	dstFullPath := c.resolvePath(dstPath)

	// Read source file
	resp, err := c.client.Retr(srcFullPath)
	if err != nil {
		return fmt.Errorf("failed to retrieve source file %s: %w", srcFullPath, err)
	}
	defer resp.Close()

	// Ensure destination directory exists
	dstDir := filepath.Dir(dstFullPath)
	if dstDir != "." && dstDir != "/" {
		c.client.MakeDir(dstDir) // Ignore error if directory exists
	}

	// Write to destination
	err = c.client.Stor(dstFullPath, resp)
	if err != nil {
		return fmt.Errorf("failed to store destination file %s: %w", dstFullPath, err)
	}

	return nil
}

// GetProtocol returns the protocol name
func (c *FTPClient) GetProtocol() string {
	return "ftp"
}

// GetConfig returns the FTP configuration
func (c *FTPClient) GetConfig() interface{} {
	return c.config
}
