package filesystem

import (
	"fmt"
)

// DefaultClientFactory implements ClientFactory for all supported protocols
type DefaultClientFactory struct{}

// NewDefaultClientFactory creates a new default client factory
func NewDefaultClientFactory() *DefaultClientFactory {
	return &DefaultClientFactory{}
}

// CreateClient creates a filesystem client based on the storage configuration
func (f *DefaultClientFactory) CreateClient(config *StorageConfig) (FileSystemClient, error) {
	switch config.Protocol {
	case "smb":
		smbConfig := &SmbConfig{
			Host:     getStringSetting(config.Settings, "host", ""),
			Port:     getIntSetting(config.Settings, "port", 445),
			Share:    getStringSetting(config.Settings, "share", ""),
			Username: getStringSetting(config.Settings, "username", ""),
			Password: getStringSetting(config.Settings, "password", ""),
			Domain:   getStringSetting(config.Settings, "domain", "WORKGROUP"),
		}
		return NewSmbClient(smbConfig), nil

	case "ftp":
		ftpConfig := &FTPConfig{
			Host:     getStringSetting(config.Settings, "host", ""),
			Port:     getIntSetting(config.Settings, "port", 21),
			Username: getStringSetting(config.Settings, "username", ""),
			Password: getStringSetting(config.Settings, "password", ""),
			Path:     getStringSetting(config.Settings, "path", ""),
		}
		return NewFTPClient(ftpConfig), nil

	case "nfs":
		nfsConfig := &NFSConfig{
			Host:       getStringSetting(config.Settings, "host", ""),
			Path:       getStringSetting(config.Settings, "path", ""),
			MountPoint: getStringSetting(config.Settings, "mount_point", ""),
			Options:    getStringSetting(config.Settings, "options", "vers=3"),
		}
		return NewNFSClient(nfsConfig), nil

	case "webdav":
		webdavConfig := &WebDAVConfig{
			URL:      getStringSetting(config.Settings, "url", ""),
			Username: getStringSetting(config.Settings, "username", ""),
			Password: getStringSetting(config.Settings, "password", ""),
			Path:     getStringSetting(config.Settings, "path", ""),
		}
		return NewWebDAVClient(webdavConfig), nil

	case "local":
		localConfig := &LocalConfig{
			BasePath: getStringSetting(config.Settings, "base_path", ""),
		}
		return NewLocalClient(localConfig), nil

	default:
		return nil, fmt.Errorf("unsupported protocol: %s", config.Protocol)
	}
}

// SupportedProtocols returns the list of supported protocols
func (f *DefaultClientFactory) SupportedProtocols() []string {
	return []string{"smb", "ftp", "nfs", "webdav", "local"}
}

// Helper functions to extract settings
func getStringSetting(settings map[string]interface{}, key, defaultValue string) string {
	if val, ok := settings[key]; ok {
		if str, ok := val.(string); ok {
			return str
		}
	}
	return defaultValue
}

func getIntSetting(settings map[string]interface{}, key string, defaultValue int) int {
	if val, ok := settings[key]; ok {
		if num, ok := val.(int); ok {
			return num
		}
		if floatNum, ok := val.(float64); ok {
			return int(floatNum)
		}
	}
	return defaultValue
}