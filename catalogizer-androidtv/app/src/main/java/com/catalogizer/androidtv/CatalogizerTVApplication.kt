package com.catalogizer.androidtv

import android.app.Application
import androidx.core.content.ContextCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CatalogizerTVApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize any TV-specific configurations
        initializeTVSettings()
    }

    private fun initializeTVSettings() {
        // Configure for TV environment
        // Set up media session callbacks
        // Initialize background tasks
    }
}