package com.applock.secure.data.local

import androidx.room.*
import com.applock.secure.data.model.LockedApp
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for LockedApp entity
 * Provides database operations for locked apps
 * Uses Flow for reactive updates
 */
@Dao
interface LockedAppDao {

    /**
     * Get all locked apps as Flow (live updates)
     */
    @Query("SELECT * FROM locked_apps WHERE isLocked = 1 ORDER BY appName ASC")
    fun getAllLockedApps(): Flow<List<LockedApp>>

    /**
     * Get all apps (locked and unlocked)
     */
    @Query("SELECT * FROM locked_apps ORDER BY appName ASC")
    fun getAllApps(): Flow<List<LockedApp>>

    /**
     * Check if app is locked
     */
    @Query("SELECT isLocked FROM locked_apps WHERE packageName = :packageName")
    suspend fun isAppLocked(packageName: String): Boolean?

    /**
     * Insert or update app
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: LockedApp)

    /**
     * Insert multiple apps
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<LockedApp>)

    /**
     * Update app lock status
     */
    @Query("UPDATE locked_apps SET isLocked = :isLocked WHERE packageName = :packageName")
    suspend fun updateLockStatus(packageName: String, isLocked: Boolean)

    /**
     * Delete app from database
     */
    @Delete
    suspend fun deleteApp(app: LockedApp)

    /**
     * Clear all apps
     */
    @Query("DELETE FROM locked_apps")
    suspend fun clearAll()

    /**
     * Get single app by package name
     */
    @Query("SELECT * FROM locked_apps WHERE packageName = :packageName")
    suspend fun getApp(packageName: String): LockedApp?
}