# Samba Module

A Kotlin JVM library for working with SMB/CIFS protocol and performing common file operations.

## Features

- **Connection Management**: Secure SMB connections with authentication
- **File Operations**: Read, write, delete, copy files
- **Directory Operations**: Create, delete, list directories
- **Serializable Configuration**: JSON serializable connection settings
- **Comprehensive Testing**: Unit tests for all major functionalities

## Usage

### Basic Setup

```kotlin
import com.catalogizer.samba.*

// Create SambaUtils instance
val sambaUtils = SambaUtils.create(
    host = "192.168.1.100",
    share = "shared",
    username = "user",
    password = "password",
    domain = "WORKGROUP"
)

// Test connection
if (sambaUtils.testConnection()) {
    println("Connection successful!")
}
```

### File Operations

```kotlin
// List files in a directory
val files = sambaUtils.fileOperations.listFiles("/documents")

// Read a file
val content = sambaUtils.fileOperations.readFileAsText("/documents/file.txt")

// Write a file
sambaUtils.fileOperations.writeTextFile("/documents/newfile.txt", "Hello World!")

// Delete a file
sambaUtils.fileOperations.deleteFile("/documents/oldfile.txt")

// Copy a file
sambaUtils.fileOperations.copyFile("/source.txt", "/destination.txt")
```

### Directory Operations

```kotlin
// Create a directory
sambaUtils.directoryOperations.createDirectory("/newdir")

// List directories
val directories = sambaUtils.directoryOperations.listDirectories("/")

// Delete directory (recursive)
sambaUtils.directoryOperations.deleteDirectory("/olddir", recursive = true)

// Get directory size
val size = sambaUtils.directoryOperations.getDirectorySize("/documents")
```

### Configuration with JSON

```kotlin
import kotlinx.serialization.json.Json

// Create configuration
val config = SmbConnectionConfig(
    host = "192.168.1.100",
    port = 445,
    share = "shared",
    credentials = SmbCredentials("user", "password", "WORKGROUP"),
    timeout = 30000
)

// Serialize to JSON
val json = Json.encodeToString(config)

// Create SambaUtils from config
val sambaUtils = SambaUtils.create(config)
```

## Dependencies

- **JCIFS-NG**: Modern SMB/CIFS library for Java
- **Kotlinx Serialization**: JSON serialization support
- **SLF4J + Logback**: Logging framework

## Error Handling

All operations throw `IOException` for network/file system errors. Handle appropriately:

```kotlin
try {
    val files = sambaUtils.fileOperations.listFiles("/documents")
    // Process files
} catch (e: IOException) {
    println("Error accessing SMB share: ${e.message}")
}
```