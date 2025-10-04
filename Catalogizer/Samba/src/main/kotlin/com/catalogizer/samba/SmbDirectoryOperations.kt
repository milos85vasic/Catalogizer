package com.catalogizer.samba

import org.slf4j.LoggerFactory
import java.io.FileNotFoundException
import java.io.IOException

class SmbDirectoryOperations(private val client: SmbClient) {

    private val logger = LoggerFactory.getLogger(SmbDirectoryOperations::class.java)

    @Throws(IOException::class)
    fun createDirectory(path: String): Boolean {
        val smbFile = client.createSmbFile(path)

        if (smbFile.exists()) {
            if (smbFile.isDirectory) {
                logger.info("Directory already exists: $path")
                return true
            } else {
                throw IllegalArgumentException("Path exists but is not a directory: $path")
            }
        }

        smbFile.mkdirs()
        logger.info("Directory created successfully: $path")
        return true
    }

    @Throws(IOException::class)
    fun deleteDirectory(path: String, recursive: Boolean = false): Boolean {
        val smbFile = client.createSmbFile(path)

        if (!smbFile.exists()) {
            return false
        }

        if (!smbFile.isDirectory) {
            throw IllegalArgumentException("Path is not a directory: $path")
        }

        if (recursive) {
            deleteDirectoryRecursive(smbFile)
        } else {
            val files = smbFile.listFiles()
            if (files != null && files.isNotEmpty()) {
                throw IllegalArgumentException("Directory is not empty. Use recursive=true to delete non-empty directories: $path")
            }
            smbFile.delete()
        }

        logger.info("Directory deleted successfully: $path")
        return true
    }

    @Throws(IOException::class)
    private fun deleteDirectoryRecursive(smbFile: jcifs.smb.SmbFile) {
        if (smbFile.isDirectory) {
            val files = smbFile.listFiles()
            if (files != null) {
                for (file in files) {
                    deleteDirectoryRecursive(file)
                }
            }
        }
        smbFile.delete()
    }

    @Throws(IOException::class)
    fun directoryExists(path: String): Boolean {
        val smbFile = client.createSmbFile(path)
        return smbFile.exists() && smbFile.isDirectory
    }

    @Throws(IOException::class)
    fun listDirectories(path: String = ""): List<SmbFileInfo> {
        val smbFile = client.createSmbFile(path)

        if (!smbFile.exists()) {
            throw FileNotFoundException("Path does not exist: $path")
        }

        if (!smbFile.isDirectory) {
            throw IllegalArgumentException("Path is not a directory: $path")
        }

        return smbFile.listFiles()?.filter { it.isDirectory }?.map { file ->
            SmbFileInfo(
                name = file.name.removeSuffix("/"),
                path = file.path,
                isDirectory = true,
                size = 0L,
                lastModified = java.util.Date(file.lastModified)
            )
        } ?: emptyList()
    }

    @Throws(IOException::class)
    fun copyDirectory(sourcePath: String, destinationPath: String) {
        val sourceDir = client.createSmbFile(sourcePath)
        val destDir = client.createSmbFile(destinationPath)

        if (!sourceDir.exists()) {
            throw FileNotFoundException("Source directory does not exist: $sourcePath")
        }

        if (!sourceDir.isDirectory) {
            throw IllegalArgumentException("Source is not a directory: $sourcePath")
        }

        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        copyDirectoryRecursive(sourceDir, destDir)
        logger.info("Directory copied from $sourcePath to $destinationPath")
    }

    @Throws(IOException::class)
    private fun copyDirectoryRecursive(sourceDir: jcifs.smb.SmbFile, destDir: jcifs.smb.SmbFile) {
        val files = sourceDir.listFiles()
        if (files != null) {
            for (file in files) {
                val destFile = client.createSmbFile(destDir.path + file.name)

                if (file.isDirectory) {
                    if (!destFile.exists()) {
                        destFile.mkdirs()
                    }
                    copyDirectoryRecursive(file, destFile)
                } else {
                    file.inputStream.use { input ->
                        destFile.outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    fun getDirectorySize(path: String): Long {
        val smbFile = client.createSmbFile(path)

        if (!smbFile.exists()) {
            throw FileNotFoundException("Directory does not exist: $path")
        }

        if (!smbFile.isDirectory) {
            throw IllegalArgumentException("Path is not a directory: $path")
        }

        return calculateDirectorySize(smbFile)
    }

    @Throws(IOException::class)
    private fun calculateDirectorySize(smbFile: jcifs.smb.SmbFile): Long {
        var size = 0L
        val files = smbFile.listFiles()

        if (files != null) {
            for (file in files) {
                size += if (file.isDirectory) {
                    calculateDirectorySize(file)
                } else {
                    file.length()
                }
            }
        }

        return size
    }
}