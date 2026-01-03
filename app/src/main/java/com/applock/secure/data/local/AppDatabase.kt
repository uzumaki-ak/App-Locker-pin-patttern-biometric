package com.applock.secure.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.applock.secure.data.model.LockedApp

/**
 * Room Database for app locker
 * Stores locked apps information
 * Version 1: Initial database schema
 */
@Database(
    entities = [LockedApp::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to LockedAppDao
     */
    abstract fun lockedAppDao(): LockedAppDao

    companion object {
        const val DATABASE_NAME = "app_locker_db"
    }
}