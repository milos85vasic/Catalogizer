package com.catalogizer.catalog.hash

import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class HashingEngine(
    private val threadPoolSize: Int = Runtime.getRuntime().availableProcessors()
) {

    private val logger = LoggerFactory.getLogger(HashingEngine::class.java)
    private val executor = Executors.newFixedThreadPool(threadPoolSize)

    fun computeHashes(
        inputStream: InputStream,
        fileSize: Long,
        options: HashingOptions = HashingOptions()
    ): CompletableFuture<FileHashes> {
        return CompletableFuture.supplyAsync({
            try {
                val bufferedStream = BufferedInputStream(inputStream, options.bufferSize)
                val hashes = FileHashes()

                if (options.enableQuickHash && fileSize > options.quickHashThreshold) {
                    // For large files, compute quick hash (first + last blocks + size)
                    hashes.quickHash = computeQuickHash(bufferedStream, fileSize, options)

                    // Reset stream for full hashing if needed
                    if (options.enableFullHashing && fileSize <= options.maxFileSizeForFullHashing) {
                        bufferedStream.reset()
                        computeFullHashes(bufferedStream, hashes, options)
                    }
                } else {
                    // For smaller files, always compute full hashes
                    computeFullHashes(bufferedStream, hashes, options)
                }

                logger.debug("Computed hashes for file of size $fileSize bytes")
                hashes
            } catch (e: Exception) {
                logger.error("Failed to compute hashes: ${e.message}", e)
                throw IOException("Hash computation failed", e)
            }
        }, executor)
    }

    private fun computeQuickHash(
        inputStream: InputStream,
        fileSize: Long,
        options: HashingOptions
    ): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(options.quickHashBlockSize)

        // Hash file size first
        digest.update(fileSize.toString().toByteArray())

        // Hash first block
        val firstBlockSize = inputStream.read(buffer)
        if (firstBlockSize > 0) {
            digest.update(buffer, 0, firstBlockSize)
        }

        // For large files, skip to the end and hash last block
        if (fileSize > options.quickHashBlockSize * 2) {
            // Skip to near the end
            val skipBytes = fileSize - options.quickHashBlockSize
            var totalSkipped = 0L
            while (totalSkipped < skipBytes) {
                val skipped = inputStream.skip(skipBytes - totalSkipped)
                if (skipped <= 0) break
                totalSkipped += skipped
            }

            // Hash last block
            val lastBlockSize = inputStream.read(buffer)
            if (lastBlockSize > 0) {
                digest.update(buffer, 0, lastBlockSize)
            }
        }

        return bytesToHex(digest.digest())
    }

    private fun computeFullHashes(
        inputStream: InputStream,
        hashes: FileHashes,
        options: HashingOptions
    ) {
        val md5Digest = if (options.enableMD5) MessageDigest.getInstance("MD5") else null
        val sha256Digest = if (options.enableSHA256) MessageDigest.getInstance("SHA-256") else null
        val blake3Digest = if (options.enableBLAKE3) MessageDigest.getInstance("SHA-256") else null // Fallback to SHA-256

        val buffer = ByteArray(options.bufferSize)
        var bytesRead: Int

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            md5Digest?.update(buffer, 0, bytesRead)
            sha256Digest?.update(buffer, 0, bytesRead)
            blake3Digest?.update(buffer, 0, bytesRead)
        }

        hashes.md5Hash = md5Digest?.let { bytesToHex(it.digest()) }
        hashes.sha256Hash = sha256Digest?.let { bytesToHex(it.digest()) }
        hashes.blake3Hash = blake3Digest?.let { bytesToHex(it.digest()) } // Using SHA-256 as fallback

        // Set primary content hash
        hashes.contentHash = hashes.sha256Hash ?: hashes.md5Hash ?: hashes.blake3Hash
    }

    fun computeMD5(inputStream: InputStream): String {
        return DigestUtils.md5Hex(inputStream)
    }

    fun computeSHA256(inputStream: InputStream): String {
        return DigestUtils.sha256Hex(inputStream)
    }

    fun verifyHash(inputStream: InputStream, expectedHash: String, algorithm: HashAlgorithm): Boolean {
        return try {
            val computedHash = when (algorithm) {
                HashAlgorithm.MD5 -> computeMD5(inputStream)
                HashAlgorithm.SHA256 -> computeSHA256(inputStream)
                HashAlgorithm.BLAKE3 -> computeSHA256(inputStream) // Fallback
            }
            computedHash.equals(expectedHash, ignoreCase = true)
        } catch (e: Exception) {
            logger.error("Hash verification failed: ${e.message}", e)
            false
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
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

data class FileHashes(
    var md5Hash: String? = null,
    var sha256Hash: String? = null,
    var blake3Hash: String? = null,
    var contentHash: String? = null, // Primary hash for duplicate detection
    var quickHash: String? = null // Fast hash for large files
)

data class HashingOptions(
    val enableMD5: Boolean = true,
    val enableSHA256: Boolean = true,
    val enableBLAKE3: Boolean = false, // Would need proper BLAKE3 implementation
    val enableQuickHash: Boolean = true,
    val enableFullHashing: Boolean = true,
    val bufferSize: Int = 64 * 1024, // 64KB
    val quickHashBlockSize: Int = 1024 * 1024, // 1MB
    val quickHashThreshold: Long = 10 * 1024 * 1024, // 10MB
    val maxFileSizeForFullHashing: Long = 1024 * 1024 * 1024 // 1GB
)

enum class HashAlgorithm {
    MD5,
    SHA256,
    BLAKE3
}