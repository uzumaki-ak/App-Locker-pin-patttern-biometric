package com.applock.secure.ui.screens.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applock.secure.data.model.AuthMethod
import com.applock.secure.data.repository.SecurityRepository
import com.applock.secure.domain.usecase.ValidateAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Lock Screen
 * Handles authentication validation when unlocking apps
 */
@HiltViewModel
class LockViewModel @Inject constructor(
    private val validateAuthUseCase: ValidateAuthUseCase,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LockUiState())
    val uiState: StateFlow<LockUiState> = _uiState.asStateFlow()

    init {
        loadAuthMethod()
    }

    /**
     * Load current authentication method
     */
    private fun loadAuthMethod() {
        val authMethod = validateAuthUseCase.getAuthMethod()
        val biometricEnabled = securityRepository.isBiometricEnabled()
        _uiState.value = _uiState.value.copy(
            authMethod = authMethod,
            biometricEnabled = biometricEnabled
        )
    }

    /**
     * Validate PIN
     */
    fun validatePin(pin: String) {
        viewModelScope.launch {
            val isValid = validateAuthUseCase.validatePin(pin)
            if (isValid) {
                _uiState.value = _uiState.value.copy(
                    isUnlocked = true,
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "Incorrect PIN",
                    attemptCount = _uiState.value.attemptCount + 1
                )
            }
        }
    }

    /**
     * Validate Pattern
     */
    fun validatePattern(pattern: String) {
        viewModelScope.launch {
            val isValid = validateAuthUseCase.validatePattern(pattern)
            if (isValid) {
                _uiState.value = _uiState.value.copy(
                    isUnlocked = true,
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "Incorrect Pattern",
                    attemptCount = _uiState.value.attemptCount + 1
                )
            }
        }
    }

    /**
     * Handle biometric success
     */
    fun onBiometricSuccess() {
        _uiState.value = _uiState.value.copy(
            isUnlocked = true,
            error = null
        )
    }

    /**
     * Handle biometric failure
     */
    fun onBiometricFailure(error: String) {
        _uiState.value = _uiState.value.copy(
            error = error,
            attemptCount = _uiState.value.attemptCount + 1
        )
    }

    /**
     * Clear error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * UI State for Lock Screen
 */
data class LockUiState(
    val authMethod: AuthMethod = AuthMethod.NONE,
    val biometricEnabled: Boolean = false,
    val isUnlocked: Boolean = false,
    val error: String? = null,
    val attemptCount: Int = 0
)