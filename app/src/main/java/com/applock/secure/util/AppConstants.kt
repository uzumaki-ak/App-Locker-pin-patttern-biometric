package com.applock.secure.util

/**
 * Application constants
 * Centralized location for all constant values used throughout the app
 */
object AppConstants {

    // Shared Preferences
    const val PREFS_NAME = "app_locker_prefs"
    const val KEY_FIRST_TIME = "first_time"
    const val KEY_AUTH_METHOD = "auth_method"
    const val KEY_PIN_HASH = "pin_hash"
    const val KEY_PATTERN_HASH = "pattern_hash"
    const val KEY_RECOVERY_PIN_HASH = "recovery_pin_hash"
    const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    const val KEY_SERVICE_ENABLED = "service_enabled"

    // PIN Configuration
    const val MIN_PIN_LENGTH = 4
    const val MAX_PIN_LENGTH = 8

    // Pattern Configuration
    const val PATTERN_GRID_SIZE = 3
    const val MIN_PATTERN_LENGTH = 4

    // Service
    const val NOTIFICATION_ID = 1001
    const val CHANNEL_ID = "app_locker_channel"
    const val CHANNEL_NAME = "App Locker Service"

    // Auth Methods
    const val AUTH_PIN = "pin"
    const val AUTH_PATTERN = "pattern"
    const val AUTH_BIOMETRIC = "biometric"

    // Intent Actions
    const val ACTION_UNLOCK_APP = "com.applock.secure.UNLOCK_APP"
    const val EXTRA_PACKAGE_NAME = "package_name"
    const val EXTRA_APP_NAME = "app_name"

    // Delays
    const val LOCK_DELAY_MS = 100L
    const val UNLOCK_DURATION_MS = 5000L
}