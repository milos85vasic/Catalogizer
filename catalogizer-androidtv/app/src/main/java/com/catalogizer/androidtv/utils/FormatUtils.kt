package com.catalogizer.androidtv.utils

import kotlin.math.ln
import kotlin.math.pow

/**
 * Format duration in seconds to human readable string
 */
fun formatDuration(durationSeconds: Long): String {
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60
    val seconds = durationSeconds % 60

    return when {
        hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
        else -> String.format("%d:%02d", minutes, seconds)
    }
}

/**
 * Format file size in bytes to human readable string
 */
fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (ln(bytes.toDouble()) / ln(1024.0)).toInt()

    return String.format(
        "%.1f %s",
        bytes / 1024.0.pow(digitGroups.toDouble()),
        units[digitGroups]
    )
}

/**
 * Format bitrate to human readable string
 */
fun formatBitrate(bitrate: Long): String {
    return when {
        bitrate >= 1_000_000 -> String.format("%.1f Mbps", bitrate / 1_000_000.0)
        bitrate >= 1_000 -> String.format("%.0f Kbps", bitrate / 1_000.0)
        else -> "$bitrate bps"
    }
}

/**
 * Format resolution to string
 */
fun formatResolution(width: Int?, height: Int?): String? {
    return if (width != null && height != null) {
        "${width}x${height}"
    } else null
}

/**
 * Get quality description from resolution
 */
fun getQualityFromResolution(width: Int?, height: Int?): String {
    if (width == null || height == null) return "Unknown"

    return when {
        height >= 2160 -> "4K UHD"
        height >= 1440 -> "2K QHD"
        height >= 1080 -> "Full HD"
        height >= 720 -> "HD"
        height >= 480 -> "SD"
        else -> "Low Quality"
    }
}

/**
 * Format media type for display
 */
fun formatMediaType(mediaType: String): String {
    return when (mediaType.lowercase()) {
        "movie" -> "Movie"
        "tv" -> "TV Show"
        "music" -> "Music"
        "document" -> "Document"
        "video" -> "Video"
        "audio" -> "Audio"
        "image" -> "Image"
        else -> mediaType.capitalize()
    }
}

/**
 * Format codec name
 */
fun formatCodec(codec: String): String {
    return when (codec.lowercase()) {
        "h264", "avc" -> "H.264"
        "h265", "hevc" -> "H.265"
        "vp9" -> "VP9"
        "av1" -> "AV1"
        "mp3" -> "MP3"
        "aac" -> "AAC"
        "flac" -> "FLAC"
        "opus" -> "Opus"
        else -> codec.uppercase()
    }
}

/**
 * Format frame rate
 */
fun formatFrameRate(frameRate: Double?): String? {
    return frameRate?.let {
        if (it % 1.0 == 0.0) {
            "${it.toInt()} fps"
        } else {
            String.format("%.1f fps", it)
        }
    }
}

/**
 * Format audio channels
 */
fun formatAudioChannels(channels: Int?): String? {
    return channels?.let {
        when (it) {
            1 -> "Mono"
            2 -> "Stereo"
            6 -> "5.1"
            8 -> "7.1"
            else -> "$it channels"
        }
    }
}

/**
 * Format sample rate
 */
fun formatSampleRate(sampleRate: Int?): String? {
    return sampleRate?.let {
        when {
            it >= 1000 -> "${it / 1000} kHz"
            else -> "$it Hz"
        }
    }
}

/**
 * Format date to relative time
 */
fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7
    val months = days / 30
    val years = days / 365

    return when {
        years > 0 -> if (years == 1L) "1 year ago" else "$years years ago"
        months > 0 -> if (months == 1L) "1 month ago" else "$months months ago"
        weeks > 0 -> if (weeks == 1L) "1 week ago" else "$weeks weeks ago"
        days > 0 -> if (days == 1L) "1 day ago" else "$days days ago"
        hours > 0 -> if (hours == 1L) "1 hour ago" else "$hours hours ago"
        minutes > 0 -> if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
        else -> "Just now"
    }
}

/**
 * Format progress percentage
 */
fun formatProgress(current: Long, total: Long): String {
    if (total <= 0) return "0%"
    val percentage = (current * 100) / total
    return "$percentage%"
}

/**
 * Format progress time
 */
fun formatProgressTime(current: Long, total: Long): String {
    val currentFormatted = formatDuration(current)
    val totalFormatted = formatDuration(total)
    return "$currentFormatted / $totalFormatted"
}