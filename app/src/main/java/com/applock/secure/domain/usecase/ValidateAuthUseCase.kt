package com.applock.secure.domain.usecase

import com.applock.secure.data.model.AuthMethod
import com.applock.secure.data.repository.SecurityRepository
import javax.inject.Inject

/**
 * Use case for validating authentication
 * Handles PIN, Pattern validation logic
 */
class ValidateAuthUseCase @Inject constructor(
    private val securityRepository: SecurityRepository
) {

    /**
     * Validate PIN
     */
    fun validatePin(pin: String): Boolean {
        return securityRepository.verifyPin(pin)
    }

    /**
     * Validate Pattern
     */
    fun validatePattern(pattern: String): Boolean {
        return securityRepository.verifyPattern(pattern)
    }

    /**
     * Get current authentication method
     */
    fun getAuthMethod(): AuthMethod {
        return securityRepository.getAuthMethod()
    }

    /**
     * Check if authentication is set up
     */
    fun isAuthSetup(): Boolean {
        return securityRepository.isAuthSetup()
    }

    /**
     * Validate recovery PIN
     */
    fun validateRecoveryPin(pin: String): Boolean {
        return securityRepository.verifyRecoveryPin(pin)
    }
}