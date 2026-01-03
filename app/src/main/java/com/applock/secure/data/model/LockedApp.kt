package com.applock.secure.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a locked app in the database
 * Each locked app has a package name, app name, and lock status
 */
@Entity(tableName = "locked_apps")
data class LockedApp(
    @PrimaryKey
    val packageName: String,      // Unique identifier (e.g., com.whatsapp)
    val appName: String,           // Display name (e.g., WhatsApp)
    val isLocked: Boolean = true,  // Lock status
    val iconPath: String? = null,  // Optional icon path
    val addedTimestamp: Long = System.currentTimeMillis()  // When app was added
)