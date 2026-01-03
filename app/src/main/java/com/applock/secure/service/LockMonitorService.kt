package com.applock.secure.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.applock.secure.R
import com.applock.secure.util.AppConstants

/**
 * Foreground Service to keep app alive in background
 * Required for Samsung One UI and other aggressive battery optimizers
 * Shows persistent notification while active
 *
 * Why needed:
 * - Prevents service from being killed by system
 * - Required for Android 8.0+ background execution
 * - Ensures accessibility service stays active
 */
class LockMonitorService : Service() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(AppConstants.NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY  // Restart if killed
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Create notification channel (Android 8.0+)
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            AppConstants.CHANNEL_ID,
            AppConstants.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW  // Low importance = no sound
        ).apply {
            description = "App Locker is protecting your apps"
            setShowBadge(false)
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    /**
     * Create persistent notification
     */
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
            .setContentTitle("App Locker Active")
            .setContentText("Your apps are protected")
            .setSmallIcon(R.drawable.ic_lock_notification)
            .setOngoing(true)  // Cannot be dismissed
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}