package com.catalogizer.samba

import kotlinx.serialization.Serializable

@Serializable
data class SmbConnectionConfig(
    val host: String,
    val port: Int = 445,
    val share: String,
    val credentials: SmbCredentials,
    val timeout: Int = 30000 // 30 seconds
) {
    fun toSmbUrl(): String {
        return "smb://$host:$port/$share/"
    }
}