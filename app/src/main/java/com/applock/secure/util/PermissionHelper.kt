package com.applock.secure.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings

/**
 * Helper class for handling Android permissions
 * Manages accessibility, overlay, and battery optimization permissions
 */
object PermissionHelper {

    /**
     * Check if accessibility service is enabled
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val serviceName = "${context.packageName}/${context.packageName}.service.AppLockAccessibilityService"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""
        return enabledServices.contains(serviceName)
    }

    /**
     * Open accessibility settings
     */
    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    /**
     * Check if overlay permission is granted
     */
    fun canDrawOverlays(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    /**
     * Open overlay permission settings
     */
    fun openOverlaySettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    /**
     * Check if battery optimization is disabled (recommended for background service)
     */
    fun isBatteryOptimizationDisabled(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    /**
     * Request to disable battery optimization
     */
    fun requestDisableBatteryOptimization(context: Context) {
        val intent = Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Uri.parse("package:${context.packageName}")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to settings page
            openBatteryOptimizationSettings(context)
        }
    }

    /**
     * Open battery optimization settings page
     */
    fun openBatteryOptimizationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    /**
     * Check all required permissions
     */
    fun areAllPermissionsGranted(context: Context): Boolean {
        return isAccessibilityServiceEnabled(context) && canDrawOverlays(context)
    }
}