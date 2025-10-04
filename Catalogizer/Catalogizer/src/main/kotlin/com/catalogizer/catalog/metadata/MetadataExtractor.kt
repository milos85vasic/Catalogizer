package com.catalogizer.catalog.metadata

import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.sax.BodyContentHandler
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class MetadataExtractor(
    private val threadPoolSize: Int = Runtime.getRuntime().availableProcessors()
) {

    private val logger = LoggerFactory.getLogger(MetadataExtractor::class.java)
    private val executor = Executors.newFixedThreadPool(threadPoolSize)
    private val tika = Tika()
    private val parser = AutoDetectParser()

    fun extractMetadata(
        inputStream: InputStream,
        filename: String,
        options: MetadataExtractionOptions = MetadataExtractionOptions()
    ): CompletableFuture<FileMetadata> {
        return CompletableFuture.supplyAsync({
            try {
                val metadata = Metadata()
                metadata.set("resourceName", filename)

                val contentHandler = BodyContentHandler(-1) // No limit on content length
                val context = org.apache.tika.parser.ParseContext()

                // Extract metadata using Tika
                parser.parse(inputStream, contentHandler, metadata, context)

                // Build file metadata
                buildFileMetadata(metadata, contentHandler.toString(), filename, options)
            } catch (e: Exception) {
                logger.warn("Failed to extract metadata for file '$filename': ${e.message}")
                // Return basic metadata on failure
                FileMetadata(
                    filename = filename,
                    mimeType = detectMimeType(filename),
                    fileType = detectFileType(filename),
                    extractedAt = System.currentTimeMillis(),
                    extractionSuccess = false,
                    errorMessage = e.message
                )
            }
        }, executor).orTimeout(options.timeoutSeconds, TimeUnit.SECONDS)
    }

    private fun buildFileMetadata(
        tikaMetadata: Metadata,
        textContent: String,
        filename: String,
        options: MetadataExtractionOptions
    ): FileMetadata {
        val metadata = FileMetadata(
            filename = filename,
            mimeType = tikaMetadata.get(Metadata.CONTENT_TYPE) ?: detectMimeType(filename),
            fileType = detectFileType(filename),
            extractedAt = System.currentTimeMillis(),
            extractionSuccess = true
        )

        // Extract basic properties
        val mutableProperties = metadata.properties.toMutableMap()
        mutableProperties["title"] = tikaMetadata.get("title")
        mutableProperties["author"] = tikaMetadata.get("author")
        mutableProperties["creator"] = tikaMetadata.get("creator")
        mutableProperties["subject"] = tikaMetadata.get("subject")
        mutableProperties["keywords"] = tikaMetadata.get("keywords")
        mutableProperties["description"] = tikaMetadata.get("description")
        mutableProperties["comments"] = tikaMetadata.get("comments")

        // Date properties
        mutableProperties["creation_date"] = tikaMetadata.get("Creation-Date")
        mutableProperties["modified_date"] = tikaMetadata.get("Last-Modified")
        mutableProperties["last_saved"] = tikaMetadata.get("Last-Save-Date")

        // Document properties
        mutableProperties["page_count"] = tikaMetadata.get("xmpTPg:NPages")
        mutableProperties["word_count"] = tikaMetadata.get("Word-Count")
        mutableProperties["character_count"] = tikaMetadata.get("Character Count")
        mutableProperties["paragraph_count"] = tikaMetadata.get("Paragraph-Count")

        // Media properties (images, videos, audio)
        extractMediaMetadata(tikaMetadata, mutableProperties)

        // Text content for search
        if (options.extractTextContent && textContent.isNotBlank()) {
            val cleanText = textContent.trim()
            if (cleanText.length <= options.maxTextLength) {
                metadata.searchableText = cleanText
            } else {
                metadata.searchableText = cleanText.substring(0, options.maxTextLength) + "..."
            }
        }

        // Custom metadata based on file type
        extractFileTypeSpecificMetadata(tikaMetadata, metadata.fileType, mutableProperties)

        // Clean up null values and assign back
        metadata.properties = mutableProperties.filterValues { it != null }

        return metadata
    }

    private fun extractMediaMetadata(tikaMetadata: Metadata, properties: MutableMap<String, String?>) {
        // Image metadata
        properties["image_width"] = tikaMetadata.get("Image Width")
        properties["image_height"] = tikaMetadata.get("Image Height")
        properties["bits_per_sample"] = tikaMetadata.get("Bits Per Sample")
        properties["color_space"] = tikaMetadata.get("Color Space")
        properties["compression"] = tikaMetadata.get("Compression")

        // Video metadata
        properties["video_width"] = tikaMetadata.get("Video Width")
        properties["video_height"] = tikaMetadata.get("Video Height")
        properties["frame_rate"] = tikaMetadata.get("Frame Rate")
        properties["duration"] = tikaMetadata.get("xmpDM:duration")
        properties["video_codec"] = tikaMetadata.get("Video Codec")

        // Audio metadata
        properties["audio_sample_rate"] = tikaMetadata.get("Audio Sample Rate")
        properties["audio_channels"] = tikaMetadata.get("Audio Channels")
        properties["audio_codec"] = tikaMetadata.get("Audio Codec")
        properties["bitrate"] = tikaMetadata.get("Audio Bitrate")
        properties["album"] = tikaMetadata.get("xmpDM:album")
        properties["artist"] = tikaMetadata.get("xmpDM:artist")
        properties["genre"] = tikaMetadata.get("xmpDM:genre")
        properties["track_number"] = tikaMetadata.get("xmpDM:trackNumber")

        // GPS/Location metadata
        properties["gps_latitude"] = tikaMetadata.get("GPS Latitude")
        properties["gps_longitude"] = tikaMetadata.get("GPS Longitude")
        properties["gps_altitude"] = tikaMetadata.get("GPS Altitude")
    }

    private fun extractFileTypeSpecificMetadata(tikaMetadata: Metadata, fileType: FileType, properties: MutableMap<String, String?>) {
        when (fileType) {
            FileType.OFFICE_DOCUMENT -> {
                properties["application"] = tikaMetadata.get("Application-Name")
                properties["application_version"] = tikaMetadata.get("Application-Version")
                properties["company"] = tikaMetadata.get("Company")
                properties["manager"] = tikaMetadata.get("Manager")
                properties["security"] = tikaMetadata.get("Security")
            }
            FileType.PDF -> {
                properties["pdf_version"] = tikaMetadata.get("PDF Version")
                properties["producer"] = tikaMetadata.get("Producer")
                properties["trapped"] = tikaMetadata.get("Trapped")
                properties["encrypted"] = tikaMetadata.get("Encrypted")
            }
            FileType.ARCHIVE -> {
                properties["compression_method"] = tikaMetadata.get("Compression Method")
                properties["archive_entries"] = tikaMetadata.get("Archive Entries")
            }
            else -> {
                // Generic metadata for other file types
            }
        }
    }

    private fun detectMimeType(filename: String): String {
        return try {
            tika.detect(filename)
        } catch (e: Exception) {
            "application/octet-stream"
        }
    }

    private fun detectFileType(filename: String): FileType {
        val extension = filename.substringAfterLast('.', "").lowercase()
        return when (extension) {
            // Images
            "jpg", "jpeg", "png", "gif", "bmp", "tiff", "tif", "webp", "svg", "ico" -> FileType.IMAGE

            // Videos
            "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v", "3gp", "mpg", "mpeg" -> FileType.VIDEO

            // Audio
            "mp3", "wav", "flac", "aac", "ogg", "wma", "m4a", "opus" -> FileType.AUDIO

            // Documents
            "pdf" -> FileType.PDF
            "doc", "docx", "odt", "rtf" -> FileType.DOCUMENT
            "xls", "xlsx", "ods", "csv" -> FileType.SPREADSHEET
            "ppt", "pptx", "odp" -> FileType.PRESENTATION

            // Code
            "java", "kt", "py", "js", "ts", "cpp", "c", "h", "cs", "go", "rs", "rb", "php" -> FileType.CODE
            "html", "htm", "css", "xml", "json", "yaml", "yml" -> FileType.MARKUP

            // Archives
            "zip", "rar", "7z", "tar", "gz", "bz2", "xz", "tar.gz", "tar.bz2" -> FileType.ARCHIVE

            // Text
            "txt", "md", "log", "ini", "cfg", "conf" -> FileType.TEXT

            // Executables
            "exe", "msi", "deb", "rpm", "dmg", "app", "jar" -> FileType.EXECUTABLE

            else -> FileType.OTHER
        }
    }

    fun shutdown() {
        executor.shutdown()
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}

data class FileMetadata(
    val filename: String,
    val mimeType: String,
    val fileType: FileType,
    val extractedAt: Long,
    val extractionSuccess: Boolean,
    val errorMessage: String? = null,
    var searchableText: String? = null,
    var properties: Map<String, String?> = mutableMapOf()
)

data class MetadataExtractionOptions(
    val extractTextContent: Boolean = true,
    val maxTextLength: Int = 50000, // 50KB of text
    val timeoutSeconds: Long = 30,
    val includeCustomProperties: Boolean = true
)

enum class FileType {
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT,
    SPREADSHEET,
    PRESENTATION,
    PDF,
    ARCHIVE,
    TEXT,
    CODE,
    MARKUP,
    EXECUTABLE,
    OFFICE_DOCUMENT,
    OTHER
}