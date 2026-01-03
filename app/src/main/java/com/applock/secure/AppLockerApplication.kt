package com.applock.secure

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class
 * Initializes Hilt dependency injection
 * This is the entry point of the app
 */
@HiltAndroidApp
class AppLockerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Application initialization
        // Hilt automatically sets up dependency injection
    }
}