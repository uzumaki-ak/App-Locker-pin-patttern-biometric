package com.applock.secure.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.security.MessageDigest

/**
 * Extension functions for common operations
 * Makes code cleaner and more readable
 */

// Show toast message
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// Check if accessibility service is enabled
fun Context.isAccessibilityServiceEnabled(serviceName: String): Boolean {
    val enabledServices = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    return enabledServices?.contains(serviceName) == true
}

// Open accessibility settings
fun Context.openAccessibilitySettings() {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

// Open overlay settings
fun Context.openOverlaySettings() {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:$packageName")
    )
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

// Check overlay permission
fun Context.canDrawOverlays(): Boolean {
    return Settings.canDrawOverlays(this)
}

// Open battery optimization settings
fun Context.openBatteryOptimizationSettings() {
    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

// Hash string (for PIN/Pattern)
fun String.hashSHA256(): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(this.toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}

// Get app name from package name
fun Context.getAppNameFromPackage(packageName: String): String {
    return try {
        val pm = packageManager
        val appInfo = pm.getApplicationInfo(packageName, 0)
        pm.getApplicationLabel(appInfo).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        packageName
    }
}

// Composable context helper
@Composable
fun rememberContext(): Context {
    return LocalContext.current
}