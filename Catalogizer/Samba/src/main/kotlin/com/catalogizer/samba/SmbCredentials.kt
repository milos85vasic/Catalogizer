package com.catalogizer.samba

import kotlinx.serialization.Serializable

@Serializable
data class SmbCredentials(
    val username: String,
    val password: String,
    val domain: String = ""
)