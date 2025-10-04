package com.catalogizer.samba

import jcifs.smb.SmbFile
import org.slf4j.LoggerFactory
import java.io.*
import java.util.*

class SmbFileOperations(private val client: SmbClient) {

    private val logger = LoggerFactory.getLogger(SmbFileOperations::class.java)

    @Throws(IOException::class)
    fun listFiles(path: String = ""): List<SmbFileInfo> {
        val smbFile = client.createSmbFile(path)

        if (!smbFile.exists()) {
            throw FileNotFoundException("Path does not exist: $path")
        }

        if (!smbFile.isDirectory) {
            throw IllegalArgumentException("Path is not a directory: $path")
        }

        return smbFile.listFiles()?.map { file ->
            SmbFileInfo(
                name = file.name.removeSuffix("/"),
                path = file.path,
                isDirectory = file.isDirectory,
                size = if (file.isDirectory) 0L else file.length(),
                lastModified = Date(file.lastModified)
            )
        } ?: emptyList()
    }

    @Throws(IOException::class)
    fun readFile(filePath: String): ByteArray {
        val smbFile = client.createSmbFile(filePath)

        if (!smbFile.exists()) {
            throw FileNotFoundException("File does not exist: $filePath")
        }

        if (smbFile.isDirectory) {
            throw IllegalArgumentException("Path is a directory, not a file: $filePath")
        }

        return smbFile.inputStream.use { it.readAllBytes() }
    }

    @Throws(IOException::class)
    fun readFileAsText(filePath: String, charset: String = "UTF-8"): String {
        return String(readFile(filePath), charset(charset))
    }

    @Throws(IOException::class)
    fun writeFile(filePath: String, data: ByteArray) {
        val smbFile = client.createSmbFile(filePath)

        smbFile.outputStream.use { outputStream ->
            outputStream.write(data)
        }

        logger.info("File written successfully: $filePath")
    }

    @Throws(IOException::class)
    fun writeTextFile(filePath: String, content: String, charset: String = "UTF-8") {
        writeFile(filePath, content.toByteArray(charset(charset)))
    }

    @Throws(IOException::class)
    fun deleteFile(filePath: String): Boolean {
        val smbFile = client.createSmbFile(filePath)

        if (!smbFile.exists()) {
            return false
        }

        if (smbFile.isDirectory) {
            throw IllegalArgumentException("Use deleteDirectory() for directories: $filePath")
        }

        smbFile.delete()
        logger.info("File deleted successfully: $filePath")
        return true
    }

    @Throws(IOException::class)
    fun copyFile(sourcePath: String, destinationPath: String) {
        val sourceFile = client.createSmbFile(sourcePath)
        val destFile = client.createSmbFile(destinationPath)

        if (!sourceFile.exists()) {
            throw FileNotFoundException("Source file does not exist: $sourcePath")
        }

        if (sourceFile.isDirectory) {
            throw IllegalArgumentException("Source is a directory, not a file: $sourcePath")
        }

        sourceFile.inputStream.use { input ->
            destFile.outputStream.use { output ->
                input.copyTo(output)
            }
        }

        logger.info("File copied from $sourcePath to $destinationPath")
    }

    @Throws(IOException::class)
    fun fileExists(filePath: String): Boolean {
        return client.createSmbFile(filePath).exists()
    }

    @Throws(IOException::class)
    fun getFileInfo(filePath: String): SmbFileInfo? {
        val smbFile = client.createSmbFile(filePath)

        if (!smbFile.exists()) {
            return null
        }

        return SmbFileInfo(
            name = smbFile.name.removeSuffix("/"),
            path = smbFile.path,
            isDirectory = smbFile.isDirectory,
            size = if (smbFile.isDirectory) 0L else smbFile.length(),
            lastModified = Date(smbFile.lastModified)
        )
    }
}