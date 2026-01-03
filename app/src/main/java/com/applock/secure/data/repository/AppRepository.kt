package com.applock.secure.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.applock.secure.data.local.LockedAppDao
import com.applock.secure.data.model.LockedApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val lockedAppDao: LockedAppDao
) {

    companion object {
        private const val TAG = "AppRepository"
    }

    fun getLockedApps(): Flow<List<LockedApp>> {
        return lockedAppDao.getAllLockedApps()
    }

    fun getAllApps(): Flow<List<LockedApp>> {
        return lockedAppDao.getAllApps()
    }

    suspend fun isAppLocked(packageName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val isLocked = lockedAppDao.isAppLocked(packageName) ?: false
                Log.d(TAG, "Checking if $packageName is locked: $isLocked")
                isLocked
            } catch (e: Exception) {
                Log.e(TAG, "Error checking lock status", e)
                false
            }
        }
    }

    suspend fun lockApp(app: LockedApp) {
        withContext(Dispatchers.IO) {
            try {
                val lockedApp = app.copy(isLocked = true)
                lockedAppDao.insertApp(lockedApp)
                Log.d(TAG, "Locked app: ${app.appName}")

                // Verify it was saved
                val saved = lockedAppDao.getApp(app.packageName)
                Log.d(TAG, "Verification - App saved: ${saved?.appName}, isLocked: ${saved?.isLocked}")
            } catch (e: Exception) {
                Log.e(TAG, "Error locking app", e)
            }
        }
    }

    suspend fun unlockApp(packageName: String) {
        withContext(Dispatchers.IO) {
            try {
                lockedAppDao.updateLockStatus(packageName, false)
                Log.d(TAG, "Unlocked app: $packageName")
            } catch (e: Exception) {
                Log.e(TAG, "Error unlocking app", e)
            }
        }
    }

    suspend fun toggleAppLock(packageName: String) {
        withContext(Dispatchers.IO) {
            val app = lockedAppDao.getApp(packageName)
            if (app != null) {
                lockedAppDao.updateLockStatus(packageName, !app.isLocked)
            }
        }
    }

    suspend fun getInstalledApps(): List<LockedApp> {
        return withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

            val existingApps = lockedAppDao.getAllApps().first()
            val existingPackages = existingApps.map { it.packageName }.toSet()

            packages
                .filter { appInfo ->
                    val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    val isUpdatedSystemApp = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

                    appInfo.packageName != context.packageName &&
                            (!isSystemApp || isUpdatedSystemApp) &&
                            !existingPackages.contains(appInfo.packageName)
                }
                .map { appInfo ->
                    LockedApp(
                        packageName = appInfo.packageName,
                        appName = pm.getApplicationLabel(appInfo).toString(),
                        isLocked = false
                    )
                }
                .sortedBy { it.appName }
        }
    }

    suspend fun removeApp(packageName: String) {
        withContext(Dispatchers.IO) {
            val app = lockedAppDao.getApp(packageName)
            if (app != null) {
                lockedAppDao.deleteApp(app)
            }
        }
    }

    suspend fun clearAllApps() {
        withContext(Dispatchers.IO) {
            lockedAppDao.clearAll()
        }
    }
}