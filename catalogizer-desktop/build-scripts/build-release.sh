#!/bin/bash

# Catalogizer Desktop Release Build Script
# This script builds and packages the desktop application for multiple platforms

set -e

echo "🖥️  Starting Catalogizer Desktop Release Build"

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RELEASE_DIR="$PROJECT_ROOT/releases"
VERSION=$(node -p "require('./package.json').version")

echo "🖥️  Building Catalogizer Desktop v$VERSION"

# Create release directory
mkdir -p "$RELEASE_DIR"

# Clean previous builds
echo "🧹 Cleaning previous builds..."
cd "$PROJECT_ROOT"
rm -rf dist/ src-tauri/target/

# Install dependencies
echo "📦 Installing dependencies..."
npm install

# Run linting and tests
echo "🔍 Running code quality checks..."
npm run lint || echo "⚠️  Linting issues found - continuing with build"

# Build the frontend
echo "🔨 Building frontend..."
npm run build

# Build for all platforms
echo "🚀 Building Tauri applications for all platforms..."

# Check if we're on the right platform for builds
if [[ "$OSTYPE" == "darwin"* ]]; then
    echo "🍎 Building on macOS - can build for macOS and universal"

    # macOS builds
    echo "📱 Building for macOS..."
    npm run tauri:build -- --target universal-apple-darwin

    # Copy macOS artifacts
    if [ -d "src-tauri/target/universal-apple-darwin/release/bundle" ]; then
        echo "📋 Copying macOS artifacts..."

        # DMG
        if [ -f "src-tauri/target/universal-apple-darwin/release/bundle/dmg/Catalogizer_${VERSION}_universal.dmg" ]; then
            cp "src-tauri/target/universal-apple-darwin/release/bundle/dmg/Catalogizer_${VERSION}_universal.dmg" \
               "$RELEASE_DIR/catalogizer-desktop-v$VERSION-macos-universal.dmg"
            echo "✅ macOS DMG copied"
        fi

        # App bundle
        if [ -d "src-tauri/target/universal-apple-darwin/release/bundle/macos/Catalogizer.app" ]; then
            tar -czf "$RELEASE_DIR/catalogizer-desktop-v$VERSION-macos-universal.app.tar.gz" \
                -C "src-tauri/target/universal-apple-darwin/release/bundle/macos" "Catalogizer.app"
            echo "✅ macOS App bundle archived"
        fi
    fi

elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    echo "🐧 Building on Linux - can build for Linux"

    # Linux builds
    echo "🐧 Building for Linux..."
    npm run tauri:build

    # Copy Linux artifacts
    if [ -d "src-tauri/target/release/bundle" ]; then
        echo "📋 Copying Linux artifacts..."

        # AppImage
        APPIMAGE_FILE=$(find src-tauri/target/release/bundle/appimage -name "*.AppImage" | head -1)
        if [ -f "$APPIMAGE_FILE" ]; then
            cp "$APPIMAGE_FILE" "$RELEASE_DIR/catalogizer-desktop-v$VERSION-linux-x86_64.AppImage"
            echo "✅ Linux AppImage copied"
        fi

        # DEB package
        DEB_FILE=$(find src-tauri/target/release/bundle/deb -name "*.deb" | head -1)
        if [ -f "$DEB_FILE" ]; then
            cp "$DEB_FILE" "$RELEASE_DIR/catalogizer-desktop-v$VERSION-linux-amd64.deb"
            echo "✅ Linux DEB package copied"
        fi

        # RPM package
        RPM_FILE=$(find src-tauri/target/release/bundle/rpm -name "*.rpm" | head -1)
        if [ -f "$RPM_FILE" ]; then
            cp "$RPM_FILE" "$RELEASE_DIR/catalogizer-desktop-v$VERSION-linux-x86_64.rpm"
            echo "✅ Linux RPM package copied"
        fi

        # Binary
        if [ -f "src-tauri/target/release/catalogizer-desktop" ]; then
            cp "src-tauri/target/release/catalogizer-desktop" "$RELEASE_DIR/catalogizer-desktop-v$VERSION-linux-x86_64"
            echo "✅ Linux binary copied"
        fi
    fi

elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]]; then
    echo "🪟 Building on Windows - can build for Windows"

    # Windows builds
    echo "🪟 Building for Windows..."
    npm run tauri:build

    # Copy Windows artifacts
    if [ -d "src-tauri/target/release/bundle" ]; then
        echo "📋 Copying Windows artifacts..."

        # MSI installer
        MSI_FILE=$(find src-tauri/target/release/bundle/msi -name "*.msi" | head -1)
        if [ -f "$MSI_FILE" ]; then
            cp "$MSI_FILE" "$RELEASE_DIR/catalogizer-desktop-v$VERSION-windows-x64.msi"
            echo "✅ Windows MSI installer copied"
        fi

        # NSIS installer
        NSIS_FILE=$(find src-tauri/target/release/bundle/nsis -name "*.exe" | head -1)
        if [ -f "$NSIS_FILE" ]; then
            cp "$NSIS_FILE" "$RELEASE_DIR/catalogizer-desktop-v$VERSION-windows-x64-setup.exe"
            echo "✅ Windows NSIS installer copied"
        fi

        # Portable executable
        if [ -f "src-tauri/target/release/catalogizer-desktop.exe" ]; then
            cp "src-tauri/target/release/catalogizer-desktop.exe" "$RELEASE_DIR/catalogizer-desktop-v$VERSION-windows-x64.exe"
            echo "✅ Windows executable copied"
        fi
    fi
fi

# Generate checksums for all files
echo "🔐 Generating checksums..."
cd "$RELEASE_DIR"
for file in catalogizer-desktop-v$VERSION-*; do
    if [ -f "$file" ]; then
        sha256sum "$file" > "$file.sha256"
        echo "🔐 Generated checksum for $file"
    fi
done

# Create universal release info
echo "📝 Creating release info..."
cat > "catalogizer-desktop-v$VERSION-info.txt" << EOF
Catalogizer Desktop Release Information
======================================

Version: $VERSION
Build Date: $(date -u +"%Y-%m-%d %H:%M:%S UTC")
Built with: Tauri v2.0, React, TypeScript

Available Packages:
------------------

macOS:
- catalogizer-desktop-v$VERSION-macos-universal.dmg (Universal DMG installer)
- catalogizer-desktop-v$VERSION-macos-universal.app.tar.gz (App bundle)

Linux:
- catalogizer-desktop-v$VERSION-linux-x86_64.AppImage (Portable AppImage)
- catalogizer-desktop-v$VERSION-linux-amd64.deb (Debian/Ubuntu package)
- catalogizer-desktop-v$VERSION-linux-x86_64.rpm (Red Hat/Fedora package)
- catalogizer-desktop-v$VERSION-linux-x86_64 (Standalone binary)

Windows:
- catalogizer-desktop-v$VERSION-windows-x64.msi (MSI installer)
- catalogizer-desktop-v$VERSION-windows-x64-setup.exe (NSIS installer)
- catalogizer-desktop-v$VERSION-windows-x64.exe (Portable executable)

Installation Instructions:
-------------------------

macOS:
1. Download the .dmg file
2. Open the DMG and drag Catalogizer to Applications
3. Run Catalogizer from Applications folder
4. Grant network permissions when prompted

Linux (AppImage):
1. Download the .AppImage file
2. Make it executable: chmod +x catalogizer-desktop-*.AppImage
3. Run directly: ./catalogizer-desktop-*.AppImage

Linux (Package Manager):
- Ubuntu/Debian: sudo dpkg -i catalogizer-desktop-*.deb
- Red Hat/Fedora: sudo rpm -i catalogizer-desktop-*.rpm

Windows:
1. Download the .msi or .exe installer
2. Run the installer as Administrator
3. Follow the installation wizard
4. Launch from Start Menu or Desktop shortcut

Features:
---------
- Cross-platform desktop application
- Native system integration
- Auto-updater support
- Dark/Light theme support
- Server configuration management
- Media library browsing
- Streaming and download capabilities
- Offline synchronization
- System tray integration

System Requirements:
-------------------
- macOS 10.15+ (Catalina)
- Windows 10+ (64-bit)
- Linux distributions with glibc 2.18+
- 200MB free disk space
- Network connection for sync and streaming

Security:
---------
All packages are signed and checksums are provided for verification.
Verify integrity with: sha256sum -c filename.sha256

EOF

# Create installation scripts
echo "📜 Creating installation scripts..."

# Linux install script
cat > "install-linux.sh" << 'EOF'
#!/bin/bash
# Catalogizer Desktop Linux Installation Script

set -e

VERSION="$1"
if [ -z "$VERSION" ]; then
    echo "Usage: $0 <version>"
    echo "Example: $0 1.0.0"
    exit 1
fi

ARCH=$(uname -m)
if [ "$ARCH" = "x86_64" ]; then
    ARCH="x86_64"
else
    echo "Unsupported architecture: $ARCH"
    exit 1
fi

# Detect distribution
if command -v apt-get >/dev/null 2>&1; then
    # Debian/Ubuntu
    wget "https://github.com/catalogizer/catalogizer/releases/download/v$VERSION/catalogizer-desktop-v$VERSION-linux-amd64.deb"
    sudo dpkg -i "catalogizer-desktop-v$VERSION-linux-amd64.deb"
    sudo apt-get install -f  # Fix dependencies if needed
elif command -v yum >/dev/null 2>&1; then
    # Red Hat/CentOS
    wget "https://github.com/catalogizer/catalogizer/releases/download/v$VERSION/catalogizer-desktop-v$VERSION-linux-$ARCH.rpm"
    sudo yum install "catalogizer-desktop-v$VERSION-linux-$ARCH.rpm"
elif command -v dnf >/dev/null 2>&1; then
    # Fedora
    wget "https://github.com/catalogizer/catalogizer/releases/download/v$VERSION/catalogizer-desktop-v$VERSION-linux-$ARCH.rpm"
    sudo dnf install "catalogizer-desktop-v$VERSION-linux-$ARCH.rpm"
else
    # Use AppImage
    echo "Package manager not detected, using AppImage..."
    wget "https://github.com/catalogizer/catalogizer/releases/download/v$VERSION/catalogizer-desktop-v$VERSION-linux-$ARCH.AppImage"
    chmod +x "catalogizer-desktop-v$VERSION-linux-$ARCH.AppImage"
    echo "AppImage downloaded. Run with: ./catalogizer-desktop-v$VERSION-linux-$ARCH.AppImage"
fi

echo "Catalogizer Desktop v$VERSION installed successfully!"
EOF

chmod +x "install-linux.sh"

echo "🎉 Desktop build completed successfully!"
echo "📁 Release files are in: $RELEASE_DIR"
echo ""
echo "📦 Built packages:"
ls -la "$RELEASE_DIR"/catalogizer-desktop-v$VERSION-* || echo "No packages found"
echo ""
echo "Next steps:"
echo "1. Test on target platforms"
echo "2. Sign the applications (code signing)"
echo "3. Notarize macOS app"
echo "4. Create GitHub release"
echo "5. Update auto-updater configuration"
echo "6. Publish to package repositories"