#!/bin/bash

# Catalogizer Android TV Release Build Script
# This script builds and packages the Android TV application for release

set -e

echo "📺 Starting Catalogizer Android TV Release Build"

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="$PROJECT_ROOT/build"
RELEASE_DIR="$PROJECT_ROOT/releases"
VERSION=$(grep "versionName" app/build.gradle.kts | cut -d'"' -f2)
BUILD_NUMBER=$(grep "versionCode" app/build.gradle.kts | cut -d'=' -f2 | tr -d ' ')

echo "📺 Building Catalogizer Android TV v$VERSION ($BUILD_NUMBER)"

# Create release directory
mkdir -p "$RELEASE_DIR"

# Clean previous builds
echo "🧹 Cleaning previous builds..."
cd "$PROJECT_ROOT"
./gradlew clean

# Check code quality
echo "🔍 Running code quality checks..."
./gradlew lint
./gradlew ktlintCheck

# Run tests
echo "🧪 Running tests..."
./gradlew test

# Build release APK
echo "🔨 Building release APK..."
./gradlew assembleRelease

# Build release AAB (Android App Bundle)
echo "📦 Building release AAB..."
./gradlew bundleRelease

# Copy artifacts to release directory
echo "📋 Copying release artifacts..."
APK_FILE="app/build/outputs/apk/release/app-release.apk"
AAB_FILE="app/build/outputs/bundle/release/app-release.aab"
MAPPING_FILE="app/build/outputs/mapping/release/mapping.txt"

if [ -f "$APK_FILE" ]; then
    cp "$APK_FILE" "$RELEASE_DIR/catalogizer-androidtv-v$VERSION-$BUILD_NUMBER.apk"
    echo "✅ APK copied to releases/"
else
    echo "❌ APK file not found!"
    exit 1
fi

if [ -f "$AAB_FILE" ]; then
    cp "$AAB_FILE" "$RELEASE_DIR/catalogizer-androidtv-v$VERSION-$BUILD_NUMBER.aab"
    echo "✅ AAB copied to releases/"
else
    echo "❌ AAB file not found!"
    exit 1
fi

if [ -f "$MAPPING_FILE" ]; then
    cp "$MAPPING_FILE" "$RELEASE_DIR/catalogizer-androidtv-v$VERSION-$BUILD_NUMBER-mapping.txt"
    echo "✅ ProGuard mapping file copied to releases/"
fi

# Generate checksums
echo "🔐 Generating checksums..."
cd "$RELEASE_DIR"
sha256sum "catalogizer-androidtv-v$VERSION-$BUILD_NUMBER.apk" > "catalogizer-androidtv-v$VERSION-$BUILD_NUMBER.apk.sha256"
sha256sum "catalogizer-androidtv-v$VERSION-$BUILD_NUMBER.aab" > "catalogizer-androidtv-v$VERSION-$BUILD_NUMBER.aab.sha256"

# Create release info
echo "📝 Creating release info..."
cat > "catalogizer-androidtv-v$VERSION-$BUILD_NUMBER-info.txt" << EOF
Catalogizer Android TV Release Information
=========================================

Version: $VERSION
Build Number: $BUILD_NUMBER
Build Date: $(date -u +"%Y-%m-%d %H:%M:%S UTC")
Minimum SDK: 26 (Android 8.0)
Target SDK: 34 (Android 14)

Files:
- catalogizer-androidtv-v$VERSION-$BUILD_NUMBER.apk (APK for sideloading)
- catalogizer-androidtv-v$VERSION-$BUILD_NUMBER.aab (Android App Bundle for Play Store)
- catalogizer-androidtv-v$VERSION-$BUILD_NUMBER-mapping.txt (ProGuard mapping for debugging)

Installation on Android TV:
1. Enable "Unknown sources" in Android TV settings
2. Use a file manager app or ADB to install the APK
3. Alternative: Use apps like "Downloader" to download and install

Sideloading via ADB:
1. Enable Developer options on Android TV
2. Enable ADB debugging
3. Connect to your Android TV: adb connect [TV_IP_ADDRESS]
4. Install APK: adb install catalogizer-androidtv-v$VERSION-$BUILD_NUMBER.apk

Features:
- TV-optimized UI with D-pad navigation
- 10-foot interface design
- Media browsing with poster grids
- Video playback with Media3 ExoPlayer
- Background services for sync
- Leanback launcher integration
- Remote control support

System Requirements:
- Android TV 8.0 (API level 26) or higher
- 200MB free storage space
- Network connection for streaming and sync
- Remote control or gamepad for navigation

Compatible Devices:
- Android TV devices
- Google TV (Chromecast with Google TV)
- NVIDIA Shield TV
- Sony Android TV
- TCL Android TV
- Hisense Android TV
- And other Android TV certified devices

EOF

echo "🎉 Android TV build completed successfully!"
echo "📁 Release files are in: $RELEASE_DIR"
echo ""
echo "📺 APK: catalogizer-androidtv-v$VERSION-$BUILD_NUMBER.apk"
echo "📦 AAB: catalogizer-androidtv-v$VERSION-$BUILD_NUMBER.aab"
echo ""
echo "Next steps:"
echo "1. Test on Android TV devices or emulator"
echo "2. Submit to Google Play Store (TV apps section)"
echo "3. Create promotional materials for TV"
echo "4. Test with various remote controls"
echo "5. Tag the release in Git"