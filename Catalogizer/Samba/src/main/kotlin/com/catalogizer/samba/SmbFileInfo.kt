package com.catalogizer.samba

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class SmbFileInfo(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    @Serializable(with = DateSerializer::class)
    val lastModified: Date
)