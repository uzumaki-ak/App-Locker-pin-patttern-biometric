package com.applock.secure.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.applock.secure.data.repository.AppRepository
import com.applock.secure.ui.screens.lock.LockScreenActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppLockAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var appRepository: AppRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var lastPackageName: String? = null
    private val unlockedApps = mutableSetOf<String>()

    companion object {
        private const val TAG = "AppLockService"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val packageName = event.packageName?.toString() ?: return

        Log.d(TAG, "App opened: $packageName")

        // Ignore our own package and lock screen
        if (packageName == this.packageName ||
            packageName.contains("LockScreenActivity")) {
            return
        }

        // Prevent duplicate checks for same app
        if (packageName == lastPackageName) {
            return
        }

        lastPackageName = packageName

        // Check if recently unlocked
        if (unlockedApps.contains(packageName)) {
            Log.d(TAG, "App was recently unlocked: $packageName")
            return
        }

        // Check if app is locked in database
        serviceScope.launch {
            try {
                val isLocked = appRepository.isAppLocked(packageName)
                Log.d(TAG, "Is $packageName locked? $isLocked")

                if (isLocked) {
                    showLockScreen(packageName)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking lock status", e)
            }
        }
    }

    private fun showLockScreen(packageName: String) {
        Log.d(TAG, "Showing lock screen for: $packageName")

        val intent = Intent(this, LockScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NO_HISTORY
            putExtra("package_name", packageName)
        }
        startActivity(intent)

        // Mark as unlocked temporarily
        unlockedApps.add(packageName)

        // Remove from unlocked list after 5 seconds
        serviceScope.launch {
            kotlinx.coroutines.delay(5000)
            unlockedApps.remove(packageName)
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        lastPackageName = null
        unlockedApps.clear()
        Log.d(TAG, "Service destroyed")
    }
}