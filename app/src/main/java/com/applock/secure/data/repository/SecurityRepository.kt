package com.applock.secure.data.repository

import com.applock.secure.data.local.SecurePreferences
import com.applock.secure.data.model.AuthMethod
import com.applock.secure.data.model.SecuritySettings
import com.applock.secure.util.hashSHA256
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for security and authentication
 * Manages PIN, Pattern, and Biometric settings
 * All sensitive data is hashed before storage
 */
@Singleton
class SecurityRepository @Inject constructor(
    private val securePrefs: SecurePreferences
) {

    /**
     * Get current security settings
     */
    fun getSecuritySettings(): SecuritySettings {
        return SecuritySettings(
            authMethod = securePrefs.getAuthMethod(),
            pinHash = securePrefs.getPinHash(),
            patternHash = securePrefs.getPatternHash(),
            recoveryPinHash = securePrefs.getRecoveryPinHash(),
            biometricEnabled = securePrefs.isBiometricEnabled()
        )
    }

    /**
     * Check if authentication is set up
     */
    fun isAuthSetup(): Boolean {
        val method = securePrefs.getAuthMethod()
        return when (method) {
            AuthMethod.PIN -> securePrefs.getPinHash() != null
            AuthMethod.PATTERN -> securePrefs.getPatternHash() != null
            AuthMethod.BIOMETRIC -> securePrefs.isBiometricEnabled()
            AuthMethod.NONE -> false
        }
    }

    /**
     * Set up PIN authentication
     */
    fun setupPin(pin: String) {
        val hash = pin.hashSHA256()
        securePrefs.setPinHash(hash)
        securePrefs.setAuthMethod(AuthMethod.PIN)
    }

    /**
     * Verify PIN
     */
    fun verifyPin(pin: String): Boolean {
        val storedHash = securePrefs.getPinHash() ?: return false
        val inputHash = pin.hashSHA256()
        return storedHash == inputHash
    }

    /**
     * Set up Pattern authentication
     */
    fun setupPattern(pattern: String) {
        val hash = pattern.hashSHA256()
        securePrefs.setPatternHash(hash)
        securePrefs.setAuthMethod(AuthMethod.PATTERN)
    }

    /**
     * Verify Pattern
     */
    fun verifyPattern(pattern: String): Boolean {
        val storedHash = securePrefs.getPatternHash() ?: return false
        val inputHash = pattern.hashSHA256()
        return storedHash == inputHash
    }

    /**
     * Set up recovery PIN
     */
    fun setupRecoveryPin(pin: String) {
        val hash = pin.hashSHA256()
        securePrefs.setRecoveryPinHash(hash)
    }

    /**
     * Verify recovery PIN
     */
    fun verifyRecoveryPin(pin: String): Boolean {
        val storedHash = securePrefs.getRecoveryPinHash() ?: return false
        val inputHash = pin.hashSHA256()
        return storedHash == inputHash
    }

    /**
     * Enable/disable biometric authentication
     */
    fun setBiometricEnabled(enabled: Boolean) {
        securePrefs.setBiometricEnabled(enabled)
        if (enabled) {
            securePrefs.setAuthMethod(AuthMethod.BIOMETRIC)
        }
    }

    /**
     * Check if biometric is enabled
     */
    fun isBiometricEnabled(): Boolean {
        return securePrefs.isBiometricEnabled()
    }

    /**
     * Get current auth method
     */
    fun getAuthMethod(): AuthMethod {
        return securePrefs.getAuthMethod()
    }

    /**
     * Reset all security settings
     */
    fun resetSecurity() {
        securePrefs.clearAll()
    }

    /**
     * Change PIN
     */
    fun changePin(newPin: String) {
        setupPin(newPin)
    }

    /**
     * Change Pattern
     */
    fun changePattern(newPattern: String) {
        setupPattern(newPattern)
    }
}