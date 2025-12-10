package com.catalogizer.androidtv.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.catalogizer.androidtv.data.models.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("catalogizer_settings", Context.MODE_PRIVATE)
    
    private var currentSettings: Settings = Settings(
        enableNotifications = true,
        enableAutoPlay = false,
        streamingQuality = "Auto",
        enableSubtitles = true,
        subtitleLanguage = "English"
    )

    init {
        loadSettings()
    }

    fun getSettings(): Settings {
        return currentSettings
    }

    suspend fun saveSettings(settings: Settings) = withContext(Dispatchers.IO) {
        currentSettings = settings
        
        // Save to SharedPreferences
        with(sharedPreferences.edit()) {
            putBoolean("enable_notifications", settings.enableNotifications)
            putBoolean("enable_auto_play", settings.enableAutoPlay)
            putString("streaming_quality", settings.streamingQuality)
            putBoolean("enable_subtitles", settings.enableSubtitles)
            putString("subtitle_language", settings.subtitleLanguage)
            apply()
        }
    }
    
    private fun loadSettings() {
        currentSettings = Settings(
            enableNotifications = sharedPreferences.getBoolean("enable_notifications", true),
            enableAutoPlay = sharedPreferences.getBoolean("enable_auto_play", false),
            streamingQuality = sharedPreferences.getString("streaming_quality", "Auto") ?: "Auto",
            enableSubtitles = sharedPreferences.getBoolean("enable_subtitles", true),
            subtitleLanguage = sharedPreferences.getString("subtitle_language", "English") ?: "English"
        )
    }
}