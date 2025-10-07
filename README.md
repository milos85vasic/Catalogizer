# Catalogizer - Advanced Multi-Protocol Media Collection Management System

Catalogizer is a comprehensive media collection management system that automatically detects, categorizes, and organizes your media files across multiple storage protocols including SMB, FTP, NFS, WebDAV, and local filesystem. It provides real-time monitoring, advanced analytics, and a modern web interface for managing your entire media library.

## 🚀 Features

### Core Capabilities
- **Automated Media Detection**: Identifies 50+ media types including movies, TV shows, music, games, software, documentaries, and more
- **Multi-Protocol Support**: Works with SMB, FTP, NFS, WebDAV, and local filesystem protocols
- **Real-time Monitoring**: Continuously monitors storage sources for changes and updates metadata automatically
- **Protocol Resilience**: Handles temporary disconnections gracefully with automatic reconnection and offline caching
- **Advanced Analytics**: Comprehensive statistics, growth trends, and quality analysis
- **Modern Web Interface**: React-based responsive UI with real-time updates
- **Secure Authentication**: JWT-based auth with role-based access control
- **External Metadata Integration**: Fetches data from TMDB, IMDB, TVDB, MusicBrainz, Spotify, Steam, and more
- **Quality Analysis**: Automatic quality detection and version tracking
- **Encrypted Database**: SQLCipher for secure data storage

### Technical Highlights
- **Go Backend**: High-performance REST API with Gin framework
- **React Frontend**: Modern TypeScript React application with Tailwind CSS
- **Real-time Updates**: WebSocket integration for live data synchronization
- **Resilient Architecture**: Handles temporary SMB disconnections gracefully
- **Scalable Design**: Modular architecture supporting multiple media sources

## 📋 Table of Contents

- [Architecture Overview](#architecture-overview)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [SMB Resilience & Offline Handling](#smb-resilience--offline-handling)
- [API Documentation](#api-documentation)
- [Frontend Documentation](#frontend-documentation)
- [Database Schema](#database-schema)
- [Media Detection Engine](#media-detection-engine)
- [External Providers](#external-providers)
- [Real-time Monitoring](#real-time-monitoring)
- [Security & Authentication](#security--authentication)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Web    │    │   Go REST API   │    │ SQLCipher DB    │
│   Application  │◄──►│     Server      │◄──►│   (Encrypted)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
          │                       │                       │
          │                       │                       │
          ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   WebSocket     │    │ Media Detection │    │  External APIs  │
│   Real-time     │    │     Engine      │    │ TMDB, IMDB, etc │
│    Updates      │    └─────────────────┘    └─────────────────┘
└─────────────────┘             │
                                 ▼
                     ┌─────────────────┐
                     │ Multi-Protocol  │
                     │ File System     │
                     │   Clients       │
                     └─────────────────┘
                               │
                ┌──────────────┼──────────────┐
                │              │              │
        ┌───────▼─────┐ ┌──────▼─────┐ ┌──────▼─────┐
        │ SMB Sources │ │ FTP/NFS    │ │ WebDAV/Local│
        │ Monitoring  │ │ Sources     │ │ Sources     │
        │ (Resilient) │ │ Monitoring  │ │ Monitoring  │
        └─────────────┘ └─────────────┘ └─────────────┘
```

### System Components

1. **catalog-api**: Go-based REST API server
    - Authentication and authorization
    - Media detection and analysis
    - Multi-protocol file system monitoring with resilience
    - External metadata integration
    - WebSocket server for real-time updates

2. **catalog-web**: React TypeScript web application
    - Modern responsive UI
    - Real-time data synchronization
    - Advanced search and filtering
    - Analytics dashboard
    - User management interface

3. **Database Layer**: SQLCipher encrypted SQLite
    - Media metadata storage
    - User and session management
    - Configuration and settings
    - Analysis results and statistics

### Supported Protocols

Catalogizer supports multiple file system protocols for maximum flexibility:

- **SMB/CIFS**: Windows file sharing with automatic reconnection and resilience
- **FTP/FTPS**: File Transfer Protocol with secure variants
- **NFS**: Network File System with automatic mounting
- **WebDAV**: HTTP-based file access over web protocols
- **Local Filesystem**: Direct access to local storage

Each protocol is abstracted through a common interface, allowing seamless switching and future protocol additions.

## 🛠️ Installation & Setup

### Prerequisites

- **Go 1.21+** (for backend)
- **Node.js 18+** and **npm/yarn** (for frontend)
- **SQLCipher** (for encrypted database)
- **Git** (for source control)

### Backend Setup

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd Catalogizer/catalog-api
   ```

2. **Install Go dependencies**:
   ```bash
   go mod tidy
   ```

3. **Set up environment variables**:
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

4. **Initialize the database**:
   ```bash
   go run cmd/migrate/main.go
   ```

5. **Run the API server**:
   ```bash
   go run cmd/server/main.go
   ```

### Frontend Setup

1. **Navigate to frontend directory**:
   ```bash
   cd ../catalog-web
   ```

2. **Install dependencies**:
   ```bash
   npm install
   # or
   yarn install
   ```

3. **Set up environment variables**:
   ```bash
   cp .env.example .env.local
   # Edit .env.local with your API endpoints
   ```

4. **Start development server**:
   ```bash
   npm run dev
   # or
   yarn dev
   ```

5. **Access the application**:
   - Frontend: http://localhost:3000
   - API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger/index.html

## ⚙️ Configuration

### Environment Variables

#### Backend (.env)
```env
# Database Configuration
DB_PATH=./data/catalogizer.db
DB_ENCRYPTION_KEY=your-32-character-encryption-key

# Server Configuration
PORT=8080
HOST=0.0.0.0
GIN_MODE=release

# JWT Configuration
JWT_SECRET=your-jwt-secret-key
JWT_EXPIRY_HOURS=24
REFRESH_TOKEN_EXPIRY_HOURS=168

# SMB Configuration
SMB_SOURCES=smb://server1/media,smb://server2/videos
SMB_USERNAME=your-smb-username
SMB_PASSWORD=your-smb-password
SMB_DOMAIN=your-domain

# SMB Resilience Configuration
SMB_RETRY_ATTEMPTS=5
SMB_RETRY_DELAY_SECONDS=30
SMB_HEALTH_CHECK_INTERVAL=60
SMB_CONNECTION_TIMEOUT=30
SMB_OFFLINE_CACHE_SIZE=1000

# External API Keys
TMDB_API_KEY=your-tmdb-api-key
SPOTIFY_CLIENT_ID=your-spotify-client-id
SPOTIFY_CLIENT_SECRET=your-spotify-client-secret
STEAM_API_KEY=your-steam-api-key

# Monitoring Configuration
WATCH_INTERVAL_SECONDS=30
MAX_CONCURRENT_ANALYSIS=5
ANALYSIS_TIMEOUT_MINUTES=10

# Logging
LOG_LEVEL=info
LOG_FILE=./logs/catalogizer.log
```

#### Frontend (.env.local)
```env
# API Configuration
VITE_API_BASE_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws

# Feature Flags
VITE_ENABLE_ANALYTICS=true
VITE_ENABLE_REALTIME=true
VITE_ENABLE_EXTERNAL_METADATA=true
VITE_ENABLE_OFFLINE_MODE=true
```

## 🔌 SMB Resilience & Offline Handling

Catalogizer is designed to handle temporary SMB source disconnections gracefully, ensuring uninterrupted service even when network storage becomes unavailable.

### Resilience Features

#### 🔄 Automatic Reconnection
- **Exponential Backoff**: Intelligent retry strategy that reduces load during outages
- **Circuit Breaker Pattern**: Prevents cascade failures and enables fast recovery
- **Health Monitoring**: Continuous connection health checks with real-time alerts

#### 💾 Offline Caching
```go
// When SMB sources become unavailable:
type OfflineCache struct {
    entries   map[string]*CacheEntry
    maxSize   int
    eviction  EvictionPolicy
}

// Cached metadata serves user requests
func (c *OfflineCache) GetOrFetch(key string, fetcher func() interface{}) interface{} {
    if value, exists := c.Get(key); exists {
        return value  // Serve from cache when offline
    }
    return fetcher()  // Fetch when online
}
```

#### 🔧 Failure Recovery Process
1. **Detection**: System detects SMB connection failure
2. **Circuit Breaker**: Opens to prevent further failed attempts
3. **Offline Mode**: Activates local cache to serve requests
4. **Background Retry**: Exponential backoff reconnection attempts
5. **Recovery**: Gradual transition back to normal operation
6. **Synchronization**: Cache sync when connection is restored

#### 📊 Real-time Status Monitoring
```bash
# Check SMB source health via API
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/smb/sources/status

# Response shows detailed connection state
{
  "sources": {
    "smb_123": {
      "state": "connected",
      "last_connected": "2024-01-15T10:30:00Z",
      "retry_attempts": 0,
      "is_enabled": true
    }
  },
  "summary": {
    "total": 2,
    "connected": 2,
    "offline": 0
  }
}
```

### Connection States

| State | Description | Behavior |
|-------|-------------|----------|
| 🟢 **Connected** | Normal operation | All features available |
| 🟡 **Reconnecting** | Attempting to reconnect | Limited functionality, cache serving |
| 🔴 **Disconnected** | Temporary failure | Offline mode, background retry |
| ⚫ **Offline** | Extended failure | Full offline mode, manual intervention needed |

### Configuration

```env
# SMB Resilience Settings
SMB_RETRY_ATTEMPTS=5
SMB_RETRY_DELAY_SECONDS=30
SMB_HEALTH_CHECK_INTERVAL=60
SMB_CONNECTION_TIMEOUT=30
SMB_OFFLINE_CACHE_SIZE=1000
```

### Recovery Strategies

#### Automatic Recovery
- **Health Checks**: Every 60 seconds by default
- **Exponential Backoff**: 30s, 60s, 120s, 300s, 600s
- **Circuit Breaker**: Half-open testing after timeout
- **Cache Synchronization**: Automatic when reconnected

#### Manual Recovery
```bash
# Force reconnection attempt
curl -X POST -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/smb/sources/{id}/reconnect

# Reset circuit breaker
curl -X POST -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/system/circuit-breaker/reset
```

### Benefits

✅ **Uninterrupted Service**: Users can browse and search even when SMB sources are down
✅ **Automatic Recovery**: No manual intervention required for temporary outages
✅ **Data Integrity**: Changes are queued and synchronized when connection is restored
✅ **Performance**: Circuit breaker prevents slowdowns from failed connection attempts
✅ **Monitoring**: Real-time visibility into connection health and recovery status

For detailed technical documentation, see [SMB Resilience Architecture](ARCHITECTURE.md#smb-resilience-layer).

## 📱 Client Applications

Catalogizer provides comprehensive client applications for all major platforms:

### 🛠️ Installation Wizard
![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen) ![Tests](https://img.shields.io/badge/Tests-30%2F30-brightgreen) ![Coverage](https://img.shields.io/badge/Coverage-93%25-brightgreen)

- **Desktop Installation Tool**: Cross-platform SMB configuration wizard
- **Network Discovery**: Automatic SMB device scanning and discovery
- **Configuration Management**: Visual SMB source setup with validation
- **User-Friendly Interface**: Step-by-step wizard for easy configuration
- **File Operations**: Load/save configuration files with native dialogs
- **Real-time Testing**: Live SMB connection validation and feedback

| Component | Coverage | Tests | Status |
|-----------|----------|-------|--------|
| React Components | ![92%](https://img.shields.io/badge/92%25-brightgreen) | ![8/8](https://img.shields.io/badge/8%2F8-brightgreen) | ✅ |
| Context Management | ![98%](https://img.shields.io/badge/98%25-brightgreen) | ![20/20](https://img.shields.io/badge/20%2F20-brightgreen) | ✅ |
| Service Layer | ![89%](https://img.shields.io/badge/89%25-yellowgreen) | ![10/10](https://img.shields.io/badge/10%2F10-brightgreen) | ✅ |
| Tauri Backend | ![85%](https://img.shields.io/badge/85%25-green) | ![Integration](https://img.shields.io/badge/Integration-blue) | ✅ |

### 🤖 Android Mobile & TV
- **Modern Android Architecture**: MVVM with Hilt DI
- **Jetpack Compose UI**: Material Design 3 theming
- **Offline Support**: Room database with automatic sync
- **Media Playback**: ExoPlayer integration
- **Android TV**: Leanback UI with D-pad navigation

### 🖥️ Desktop Applications
- **Cross-Platform**: Windows, macOS, Linux support
- **Tauri Framework**: Rust backend with React frontend
- **Native Performance**: System integration and file access
- **Auto-Updates**: Built-in update mechanism

### 🌐 Web Interface
- **Responsive Design**: Works on all screen sizes
- **Real-time Updates**: WebSocket synchronization
- **Progressive Web App**: Offline capabilities

## 🚀 Quick Installation

### Option 1: Installation Wizard (Recommended)
Use the graphical installation wizard for easy SMB configuration:

```bash
# Download and run the installation wizard
cd installer-wizard
npm install
npm run tauri:build

# Or use pre-built binaries from releases
# Windows: catalogizer-installer-wizard.exe
# macOS: catalogizer-installer-wizard.app
# Linux: catalogizer-installer-wizard.AppImage
```

**Installation Wizard Features:**
- 🔍 **Network Discovery**: Automatically finds SMB devices
- ⚙️ **Visual Configuration**: Step-by-step setup wizard
- 🧪 **Connection Testing**: Real-time SMB validation
- 💾 **File Management**: Save/load configuration files
- 📊 **Test Coverage**: 93% coverage, 30/30 tests passing

### Option 2: Automated Script
Use the automated installer to set up the complete Catalogizer ecosystem:

```bash
# Download and run the installer
curl -fsSL https://raw.githubusercontent.com/your-repo/Catalogizer/main/install.sh | bash

# Or with custom configuration
curl -fsSL https://raw.githubusercontent.com/your-repo/Catalogizer/main/install.sh | bash -s -- --mode=full --env-file=./my-config.env
```

### Installation Modes

| Mode | Components | Use Case |
|------|------------|----------|
| `full` | Server + Web + Clients | Complete installation |
| `server-only` | API + Database | Server deployment |
| `clients-only` | Android + Desktop | Client development |
| `development` | All + Dev tools | Development environment |

## 🐳 Docker Deployment

### Quick Start
```bash
# Clone repository
git clone <repository-url>
cd Catalogizer

# Create environment configuration
cp deployment/.env.example deployment/.env
# Edit deployment/.env with your settings

# Deploy with Docker Compose
cd deployment
docker-compose up -d

# Monitor deployment
docker-compose logs -f
```

### Production Deployment
```bash
# Deploy to production
./deployment/scripts/deploy-server.sh --env=production --strategy=rolling

# Deploy with monitoring
docker-compose --profile monitoring up -d

# Deploy with backup service
docker-compose --profile backup up -d
```

### Service Architecture

```yaml
services:
  database:        # PostgreSQL 15
  redis:          # Redis 7 for caching
  catalogizer-server:  # Main API server
  transcoder:     # Media transcoding service
  web:           # Nginx + React frontend
  prometheus:    # Metrics collection (optional)
  grafana:       # Monitoring dashboard (optional)
  backup:        # Automated backup service (optional)
```

## 📋 Environment Configuration

### Server Environment (.env)
```env
# Database
DATABASE_NAME=catalogizer
DATABASE_USER=catalogizer
DATABASE_PASSWORD=your_secure_password

# Security
JWT_SECRET=your-super-secret-jwt-key-change-this
SSL_ENABLED=true
SSL_CERT_PATH=/path/to/cert.pem
SSL_KEY_PATH=/path/to/key.pem

# Media Configuration
MEDIA_DIRECTORIES=/mnt/media
TRANSCODE_ENABLED=true
THUMBNAIL_GENERATION=true

# External APIs
TMDB_API_KEY=your_tmdb_key
SPOTIFY_CLIENT_ID=your_spotify_id
SPOTIFY_CLIENT_SECRET=your_spotify_secret
```

### Override Configuration
```bash
# Use custom environment file
docker-compose --env-file ./production.env up -d

# Override specific variables
CATALOGIZER_VERSION=v2.1.0 docker-compose up -d
```

## 🏗️ Build & Deploy Clients

### Android Deployment
```bash
# Build and deploy Android apps
./deployment/scripts/deploy-android.sh

# Deploy to Google Play
./deployment/scripts/deploy-android.sh --target=play-store --env=production

# Deploy to Firebase App Distribution
./deployment/scripts/deploy-android.sh --target=firebase --env=staging

# Build APK for direct distribution
./deployment/scripts/deploy-android.sh --target=apk --build-type=release
```

### Desktop Deployment
```bash
# Build for all platforms
./deployment/scripts/deploy-desktop.sh

# Build for specific platform
./deployment/scripts/deploy-desktop.sh --target=windows
./deployment/scripts/deploy-desktop.sh --target=macos --sign
./deployment/scripts/deploy-desktop.sh --target=linux --format=appimage

# Deploy to GitHub Releases
./deployment/scripts/deploy-desktop.sh --deploy=github --tag=v2.1.0
```

### Cross-Platform API Client
```bash
# Build and publish npm package
cd catalogizer-api-client
npm run build
npm publish

# Install in your project
npm install @catalogizer/api-client
```

## 🔧 Development Setup

### Prerequisites
- Docker & Docker Compose
- Node.js 18+ (for web/desktop clients)
- Android Studio (for Android development)
- Rust & Tauri CLI (for desktop development)

### Development Environment
```bash
# Start development environment
./install.sh --mode=development

# This sets up:
# - Hot-reload enabled services
# - Development databases with test data
# - Debug ports exposed
# - Development tools (pgAdmin, Redis Commander, etc.)
```

### Client Development
```bash
# Android development
cd catalogizer-android
./gradlew assembleDebug

# Desktop development
cd catalogizer-desktop
npm run tauri dev

# Web development
cd catalog-web
npm run dev

# API client library
cd catalogizer-api-client
npm run dev
```

## 📊 Monitoring & Health Checks

### Built-in Health Endpoints
```bash
# Server health
curl http://localhost:8080/health

# Database connectivity
curl http://localhost:8080/health/database

# SMB sources status
curl http://localhost:8080/health/smb

# Overall system status
curl http://localhost:8080/health/system
```

### Monitoring Stack (Optional)
```bash
# Enable monitoring services
docker-compose --profile monitoring up -d

# Access monitoring interfaces
# Prometheus: http://localhost:9090
# Grafana: http://localhost:3000 (admin/admin)
```

### Log Management
```bash
# View service logs
docker-compose logs catalogizer-server
docker-compose logs --tail=100 -f web

# Access log files
docker exec catalogizer-server tail -f /app/logs/catalogizer.log
```

## 🔐 Security Configuration

### SSL/TLS Setup
```bash
# Generate self-signed certificates (development)
./deployment/scripts/generate-ssl.sh

# Use Let's Encrypt (production)
./deployment/scripts/setup-letsencrypt.sh --domain=your-domain.com
```

### Authentication
- JWT-based authentication with refresh tokens
- Role-based access control (Admin, User, Read-only)
- Session management with Redis
- OAuth2 integration support

### Database Security
- SQLCipher encryption for SQLite (standalone)
- PostgreSQL with encrypted connections (Docker)
- Automated backups with encryption
- Point-in-time recovery support

## 🚀 Deployment Strategies

### Rolling Deployment
```bash
# Zero-downtime rolling update
./deployment/scripts/deploy-server.sh --strategy=rolling
```

### Blue-Green Deployment
```bash
# Blue-green deployment with instant rollback
./deployment/scripts/deploy-server.sh --strategy=blue-green
```

### Canary Deployment
```bash
# Gradual traffic shifting
./deployment/scripts/deploy-server.sh --strategy=canary --traffic-split=10
```

## 📚 Documentation

- **[API Documentation](docs/API.md)**: Complete REST API reference
- **[Client Integration](docs/CLIENTS.md)**: Client development guide
- **[Deployment Guide](docs/DEPLOYMENT.md)**: Production deployment
- **[Architecture Overview](docs/ARCHITECTURE.md)**: System design
- **[Security Guide](docs/SECURITY.md)**: Security best practices
- **[Troubleshooting](docs/TROUBLESHOOTING.md)**: Common issues and solutions

## 🆘 Support & Troubleshooting

### Common Issues

#### Docker Issues
```bash
# Clean up Docker resources
docker system prune -a

# Restart services
docker-compose restart

# Check service health
docker-compose ps
```

#### Permission Issues
```bash
# Fix media directory permissions
sudo chown -R $USER:docker /mnt/media
sudo chmod -R 755 /mnt/media
```

#### Database Issues
```bash
# Reset database
docker-compose down database
docker volume rm catalogizer-database-data
docker-compose up -d database
```

### Performance Tuning
```env
# Optimize for large media collections
MAX_CONCURRENT_ANALYSIS=8
ANALYSIS_TIMEOUT_MINUTES=15
SMB_OFFLINE_CACHE_SIZE=5000

# Database performance
POSTGRES_SHARED_BUFFERS=256MB
POSTGRES_EFFECTIVE_CACHE_SIZE=1GB
```

### Getting Help
- 📖 [Wiki](https://github.com/your-repo/Catalogizer/wiki)
- 🐛 [Issue Tracker](https://github.com/your-repo/Catalogizer/issues)
- 💬 [Discussions](https://github.com/your-repo/Catalogizer/discussions)
- 📧 Email: support@catalogizer.dev

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Workflow
1. Fork the repository
2. Create a feature branch
3. Set up development environment: `./install.sh --mode=development`
4. Make your changes
5. Run tests: `./scripts/run-tests.sh`
6. Submit a pull request

### Code Standards
- **Go**: gofmt, golint, go vet
- **TypeScript/React**: ESLint, Prettier
- **Kotlin**: ktlint, detekt
- **Rust**: rustfmt, clippy

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Gin](https://github.com/gin-gonic/gin) - HTTP web framework for Go
- [React](https://reactjs.org/) - JavaScript library for building user interfaces
- [Tauri](https://tauri.app/) - Cross-platform desktop applications
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit for Android
- [TMDB](https://www.themoviedb.org/) - Movie and TV show metadata
- [MusicBrainz](https://musicbrainz.org/) - Open music encyclopedia

---

**Catalogizer** - Organize your media collection with intelligence and style. 🎬📺🎵🎮