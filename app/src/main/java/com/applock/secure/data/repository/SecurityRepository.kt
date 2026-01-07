package com.applock.secure.data.repository

import com.applock.secure.data.local.SecurePreferences
import com.applock.secure.data.model.AuthMethod
import com.applock.secure.data.model.SecuritySettings
import com.applock.secure.util.hashSHA256
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityRepository @Inject constructor(
    private val securePrefs: SecurePreferences
) {

    fun getSecuritySettings(): SecuritySettings {
        return SecuritySettings(
            authMethod = securePrefs.getAuthMethod(),
            pinHash = securePrefs.getPinHash(),
            patternHash = securePrefs.getPatternHash(),
            recoveryPinHash = securePrefs.getRecoveryPinHash(),
            securityQuestion = securePrefs.getSecurityQuestion(),
            securityAnswerHash = securePrefs.getSecurityAnswerHash(),
            biometricEnabled = securePrefs.isBiometricEnabled()
        )
    }

    fun isAuthSetup(): Boolean {
        val method = securePrefs.getAuthMethod()
        return when (method) {
            AuthMethod.PIN -> securePrefs.getPinHash() != null
            AuthMethod.PATTERN -> securePrefs.getPatternHash() != null
            AuthMethod.BIOMETRIC -> securePrefs.isBiometricEnabled()
            AuthMethod.NONE -> false
        }
    }

    fun setupPin(pin: String) {
        val hash = pin.hashSHA256()
        securePrefs.setPinHash(hash)
        securePrefs.setAuthMethod(AuthMethod.PIN)
    }

    fun verifyPin(pin: String): Boolean {
        val storedHash = securePrefs.getPinHash() ?: return false
        val inputHash = pin.hashSHA256()
        return storedHash == inputHash
    }

    fun setupPattern(pattern: String) {
        val hash = pattern.hashSHA256()
        securePrefs.setPatternHash(hash)
        securePrefs.setAuthMethod(AuthMethod.PATTERN)
    }

    fun verifyPattern(pattern: String): Boolean {
        val storedHash = securePrefs.getPatternHash() ?: return false
        val inputHash = pattern.hashSHA256()
        return storedHash == inputHash
    }

    // Recovery PIN methods
    fun setupRecoveryPin(pin: String) {
        val hash = pin.hashSHA256()
        securePrefs.setRecoveryPinHash(hash)
    }

    fun verifyRecoveryPin(pin: String): Boolean {
        val storedHash = securePrefs.getRecoveryPinHash() ?: return false
        val inputHash = pin.hashSHA256()
        return storedHash == inputHash
    }

    fun hasRecoveryPin(): Boolean {
        return securePrefs.getRecoveryPinHash() != null
    }

    // Security Question methods
    fun setupSecurityQuestion(question: String, answer: String) {
        val answerHash = answer.lowercase().trim().hashSHA256()
        securePrefs.setSecurityQuestion(question)
        securePrefs.setSecurityAnswerHash(answerHash)
    }

    fun verifySecurityAnswer(answer: String): Boolean {
        val storedHash = securePrefs.getSecurityAnswerHash() ?: return false
        val inputHash = answer.lowercase().trim().hashSHA256()
        return storedHash == inputHash
    }

    fun getSecurityQuestion(): String? {
        return securePrefs.getSecurityQuestion()
    }

    fun hasSecurityQuestion(): Boolean {
        return securePrefs.getSecurityQuestion() != null
    }

    // Reset password using recovery
    fun resetPasswordWithRecovery(newPin: String? = null, newPattern: String? = null) {
        if (newPin != null) {
            setupPin(newPin)
        }
        if (newPattern != null) {
            setupPattern(newPattern)
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        securePrefs.setBiometricEnabled(enabled)
        if (enabled) {
            securePrefs.setAuthMethod(AuthMethod.BIOMETRIC)
        }
    }

    fun isBiometricEnabled(): Boolean {
        return securePrefs.isBiometricEnabled()
    }

    fun getAuthMethod(): AuthMethod {
        return securePrefs.getAuthMethod()
    }

    fun resetSecurity() {
        securePrefs.clearAll()
    }

    fun changePin(newPin: String) {
        setupPin(newPin)
    }

    fun changePattern(newPattern: String) {
        setupPattern(newPattern)
    }
}