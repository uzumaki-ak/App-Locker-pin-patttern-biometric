package com.applock.secure.ui.components

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Biometric Prompt Helper
 * Handles fingerprint and face authentication
 * Uses Android BiometricPrompt API
 */
class BiometricPromptHelper(
    private val activity: FragmentActivity,
    private val onSuccess: () -> Unit,
    private val onError: (String) -> Unit
) {

    private val biometricPrompt: BiometricPrompt
    private val promptInfo: BiometricPrompt.PromptInfo

    init {
        val executor = ContextCompat.getMainExecutor(activity)

        // Create biometric prompt
        biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Authentication failed")
                }
            }
        )

        // Build prompt info
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock App")
            .setSubtitle("Use biometric to unlock")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            .build()
    }

    /**
     * Show biometric prompt
     */
    fun showBiometricPrompt() {
        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Check if biometric is available
     */
    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(activity)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
}