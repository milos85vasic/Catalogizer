package services

import (
	"catalogizer/internal/config"
	"catalogizer/internal/models"
	"context"
	"fmt"
	"io"
	"net"
	"os"
	"path/filepath"
	"strings"
	"time"

	"github.com/hirochachacha/go-smb2"
	"go.uber.org/zap"
)

// SMBServiceInterface defines the interface for SMB operations
type SMBServiceInterface interface {
	GetHosts() []string
	ListFiles(hostName, path string) ([]os.FileInfo, error)
	DownloadFile(hostName, remotePath, localPath string) error
	UploadFile(hostName, localPath, remotePath string) error
	CopyFile(sourceHost, sourcePath, destHost, destPath string) error
	CreateRemoteDir(share *smb2.Share, path string) error
	FileExists(hostName, path string) (bool, error)
	ListDirectory(hostName, path string) ([]*models.FileInfo, error)
	IsConnected(hostName string) bool
	GetFileSize(hostName, path string) (int64, error)
	CreateDirectory(hostName, path string) error
	DeleteDirectory(hostName, path string) error
	DirectoryExists(hostName, path string) (bool, error)
	IsValidSMBPath(path string) bool
	ParseSMBPath(path string) models.SMBPath
}

type SMBService struct {
	config *config.Config
	logger *zap.Logger
}

func NewSMBService(cfg *config.Config, logger *zap.Logger) *SMBService {
	return &SMBService{
		config: cfg,
		logger: logger,
	}
}

func (s *SMBService) getConnection(hostName string) (*smb2.Session, error) {
	var smbHost *config.SMBHost
	for _, host := range s.config.SMB.Hosts {
		if host.Name == hostName {
			smbHost = &host
			break
		}
	}

	if smbHost == nil {
		return nil, fmt.Errorf("SMB host not found: %s", hostName)
	}

	conn, err := net.Dial("tcp", fmt.Sprintf("%s:%d", smbHost.Host, smbHost.Port))
	if err != nil {
		return nil, fmt.Errorf("failed to connect to SMB host: %w", err)
	}

	d := &smb2.Dialer{
		Initiator: &smb2.NTLMInitiator{
			User:     smbHost.Username,
			Password: smbHost.Password,
			Domain:   smbHost.Domain,
		},
	}

	session, err := d.Dial(conn)
	if err != nil {
		conn.Close()
		return nil, fmt.Errorf("failed to create SMB session: %w", err)
	}

	return session, nil
}

func (s *SMBService) ListFiles(hostName, path string) ([]os.FileInfo, error) {
	session, err := s.getConnection(hostName)
	if err != nil {
		return nil, err
	}
	defer session.Logoff()

	var smbHost *config.SMBHost
	for _, host := range s.config.SMB.Hosts {
		if host.Name == hostName {
			smbHost = &host
			break
		}
	}

	share, err := session.Mount(smbHost.Share)
	if err != nil {
		return nil, fmt.Errorf("failed to mount share: %w", err)
	}
	defer share.Umount()

	files, err := share.ReadDir(path)
	if err != nil {
		return nil, fmt.Errorf("failed to read directory: %w", err)
	}

	return files, nil
}

func (s *SMBService) DownloadFile(hostName, remotePath, localPath string) error {
	session, err := s.getConnection(hostName)
	if err != nil {
		return err
	}
	defer session.Logoff()

	var smbHost *config.SMBHost
	for _, host := range s.config.SMB.Hosts {
		if host.Name == hostName {
			smbHost = &host
			break
		}
	}

	share, err := session.Mount(smbHost.Share)
	if err != nil {
		return fmt.Errorf("failed to mount share: %w", err)
	}
	defer share.Umount()

	// Create local directory if it doesn't exist
	if err := os.MkdirAll(filepath.Dir(localPath), 0755); err != nil {
		return fmt.Errorf("failed to create local directory: %w", err)
	}

	// Open remote file
	remoteFile, err := share.Open(remotePath)
	if err != nil {
		return fmt.Errorf("failed to open remote file: %w", err)
	}
	defer remoteFile.Close()

	// Create local file
	localFile, err := os.Create(localPath)
	if err != nil {
		return fmt.Errorf("failed to create local file: %w", err)
	}
	defer localFile.Close()

	// Copy data in chunks
	buf := make([]byte, s.config.SMB.ChunkSize)
	_, err = io.CopyBuffer(localFile, remoteFile, buf)
	if err != nil {
		return fmt.Errorf("failed to copy file: %w", err)
	}

	s.logger.Info("File downloaded successfully",
		zap.String("remote", remotePath),
		zap.String("local", localPath))

	return nil
}

func (s *SMBService) UploadFile(hostName, localPath, remotePath string) error {
	session, err := s.getConnection(hostName)
	if err != nil {
		return err
	}
	defer session.Logoff()

	var smbHost *config.SMBHost
	for _, host := range s.config.SMB.Hosts {
		if host.Name == hostName {
			smbHost = &host
			break
		}
	}

	share, err := session.Mount(smbHost.Share)
	if err != nil {
		return fmt.Errorf("failed to mount share: %w", err)
	}
	defer share.Umount()

	// Create remote directory if it doesn't exist
	remoteDir := filepath.Dir(remotePath)
	if remoteDir != "." && remoteDir != "/" {
		if err := s.CreateRemoteDir(share, remoteDir); err != nil {
			return fmt.Errorf("failed to create remote directory: %w", err)
		}
	}

	// Open local file
	localFile, err := os.Open(localPath)
	if err != nil {
		return fmt.Errorf("failed to open local file: %w", err)
	}
	defer localFile.Close()

	// Create remote file
	remoteFile, err := share.Create(remotePath)
	if err != nil {
		return fmt.Errorf("failed to create remote file: %w", err)
	}
	defer remoteFile.Close()

	// Copy data in chunks
	buf := make([]byte, s.config.SMB.ChunkSize)
	_, err = io.CopyBuffer(remoteFile, localFile, buf)
	if err != nil {
		return fmt.Errorf("failed to copy file: %w", err)
	}

	s.logger.Info("File uploaded successfully",
		zap.String("local", localPath),
		zap.String("remote", remotePath))

	return nil
}

func (s *SMBService) CopyFile(sourceHost, sourcePath, destHost, destPath string) error {
	_, cancel := context.WithTimeout(context.Background(), time.Duration(s.config.SMB.Timeout)*time.Second)
	defer cancel()

	// Create a temporary file for the transfer
	tempFile, err := os.CreateTemp(s.config.Catalog.TempDir, "smb_copy_*")
	if err != nil {
		return fmt.Errorf("failed to create temp file: %w", err)
	}
	defer os.Remove(tempFile.Name())
	defer tempFile.Close()

	// Download from source
	if err := s.DownloadFile(sourceHost, sourcePath, tempFile.Name()); err != nil {
		return fmt.Errorf("failed to download from source: %w", err)
	}

	// Upload to destination
	if err := s.UploadFile(destHost, tempFile.Name(), destPath); err != nil {
		return fmt.Errorf("failed to upload to destination: %w", err)
	}

	s.logger.Info("File copied successfully",
		zap.String("source", fmt.Sprintf("%s:%s", sourceHost, sourcePath)),
		zap.String("destination", fmt.Sprintf("%s:%s", destHost, destPath)))

	return nil
}

func (s *SMBService) CreateRemoteDir(share *smb2.Share, path string) error {
	parts := strings.Split(path, "/")
	currentPath := ""

	for _, part := range parts {
		if part == "" {
			continue
		}

		if currentPath == "" {
			currentPath = part
		} else {
			currentPath = currentPath + "/" + part
		}

		// Try to create the directory (ignore error if it already exists)
		err := share.Mkdir(currentPath, 0755)
		if err != nil && !strings.Contains(err.Error(), "already exists") {
			return fmt.Errorf("failed to create directory %s: %w", currentPath, err)
		}
	}

	return nil
}

func (s *SMBService) FileExists(hostName, path string) (bool, error) {
	session, err := s.getConnection(hostName)
	if err != nil {
		return false, err
	}
	defer session.Logoff()

	var smbHost *config.SMBHost
	for _, host := range s.config.SMB.Hosts {
		if host.Name == hostName {
			smbHost = &host
			break
		}
	}

	share, err := session.Mount(smbHost.Share)
	if err != nil {
		return false, fmt.Errorf("failed to mount share: %w", err)
	}
	defer share.Umount()

	_, err = share.Stat(path)
	if err != nil {
		if os.IsNotExist(err) {
			return false, nil
		}
		return false, fmt.Errorf("failed to stat file: %w", err)
	}

	return true, nil
}

func (s *SMBService) GetHosts() []string {
	var hosts []string
	for _, host := range s.config.SMB.Hosts {
		hosts = append(hosts, host.Name)
	}
	return hosts
}

// IsValidSMBPath checks if the given path is a valid SMB path
func (s *SMBService) IsValidSMBPath(path string) bool {
	// Basic validation for SMB paths like \\host\share\path
	if !strings.HasPrefix(path, "\\\\") {
		return false
	}
	parts := strings.Split(strings.TrimPrefix(path, "\\\\"), "\\")
	return len(parts) >= 2 && parts[0] != "" && parts[1] != ""
}

// ParseSMBPath parses an SMB path into its components
func (s *SMBService) ParseSMBPath(path string) models.SMBPath {
	smbPath := models.SMBPath{Valid: false}

	if !s.IsValidSMBPath(path) {
		return smbPath
	}

	// Remove leading \\ and split
	parts := strings.Split(strings.TrimPrefix(path, "\\\\"), "\\")
	if len(parts) < 2 {
		return smbPath
	}

	smbPath.Server = parts[0]
	smbPath.Share = parts[1]
	if len(parts) > 2 {
		smbPath.Path = strings.Join(parts[2:], "\\")
	}
	smbPath.Valid = true

	return smbPath
}

// Connect establishes a connection to an SMB host (stub implementation)
func (s *SMBService) Connect(hostName string) error {
	// Check if host exists in config
	for _, host := range s.config.SMB.Hosts {
		if host.Name == hostName {
			return nil // Connection successful
		}
	}
	return fmt.Errorf("SMB host not found: %s", hostName)
}

// ListDirectory lists files in a directory on an SMB host
func (s *SMBService) ListDirectory(hostName, path string) ([]*models.FileInfo, error) {
	files, err := s.ListFiles(hostName, path)
	if err != nil {
		return nil, err
	}

	var result []*models.FileInfo
	for _, file := range files {
		fileInfo := &models.FileInfo{
			Name:         file.Name(),
			Path:         path + "/" + file.Name(),
			IsDirectory:  file.IsDir(),
			Size:         file.Size(),
			LastModified: file.ModTime(),
		}
		result = append(result, fileInfo)
	}

	return result, nil
}

// IsConnected checks if connected to an SMB host
func (s *SMBService) IsConnected(hostName string) bool {
	for _, host := range s.config.SMB.Hosts {
		if host.Name == hostName {
			return true
		}
	}
	return false
}

// GetFileSize gets the size of a file on an SMB host
func (s *SMBService) GetFileSize(hostName, path string) (int64, error) {
	files, err := s.ListFiles(hostName, path)
	if err != nil {
		return 0, err
	}
	if len(files) == 0 {
		return 0, fmt.Errorf("file not found")
	}
	return files[0].Size(), nil
}

// CreateDirectory creates a directory on an SMB host
func (s *SMBService) CreateDirectory(hostName, path string) error {
	// This is a stub implementation
	return fmt.Errorf("CreateDirectory not implemented")
}

// DeleteDirectory deletes a directory on an SMB host
func (s *SMBService) DeleteDirectory(hostName, path string) error {
	// This is a stub implementation
	return fmt.Errorf("DeleteDirectory not implemented")
}

// DirectoryExists checks if a directory exists on an SMB host
func (s *SMBService) DirectoryExists(hostName, path string) (bool, error) {
	// This is a stub implementation
	return false, fmt.Errorf("DirectoryExists not implemented")
}
