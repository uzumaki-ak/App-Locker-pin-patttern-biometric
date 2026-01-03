package com.applock.secure.data.model

/**
 * Data class for security settings
 * Stores user preferences for authentication
 */
data class SecuritySettings(
    val authMethod: AuthMethod = AuthMethod.NONE,
    val pinHash: String? = null,
    val patternHash: String? = null,
    val recoveryPinHash: String? = null,
    val biometricEnabled: Boolean = false,
    val autoLockDelay: Long = 0L  // Milliseconds
)