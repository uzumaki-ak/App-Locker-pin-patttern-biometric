package com.applock.secure.domain.usecase

import com.applock.secure.data.model.LockedApp
import com.applock.secure.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting locked apps
 * Separates business logic from UI and repository
 * Follows clean architecture principles
 */
class GetLockedAppsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {

    /**
     * Execute use case - returns Flow of locked apps
     */
    operator fun invoke(): Flow<List<LockedApp>> {
        return appRepository.getLockedApps()
    }

    /**
     * Get all installed apps
     */
    suspend fun getInstalledApps(): List<LockedApp> {
        return appRepository.getInstalledApps()
    }

    /**
     * Check if app is locked
     */
    suspend fun isAppLocked(packageName: String): Boolean {
        return appRepository.isAppLocked(packageName)
    }
}