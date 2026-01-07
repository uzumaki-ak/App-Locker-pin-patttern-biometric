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

    private fun loadAuthMethod() {
        val authMethod = validateAuthUseCase.getAuthMethod()
        val biometricEnabled = securityRepository.isBiometricEnabled()
        _uiState.value = _uiState.value.copy(
            authMethod = authMethod,
            biometricEnabled = biometricEnabled
        )
    }

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

    fun onBiometricSuccess() {
        _uiState.value = _uiState.value.copy(
            isUnlocked = true,
            error = null
        )
    }

    fun onBiometricFailure(error: String) {
        _uiState.value = _uiState.value.copy(
            error = error,
            attemptCount = _uiState.value.attemptCount + 1
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // Recovery methods
    fun getSecurityQuestion(): String? {
        return securityRepository.getSecurityQuestion()
    }

    fun hasRecoverySetup(): Boolean {
        return securityRepository.hasSecurityQuestion() && securityRepository.hasRecoveryPin()
    }

    fun verifySecurityAnswer(answer: String): Boolean {
        return securityRepository.verifySecurityAnswer(answer)
    }

    fun verifyRecoveryPin(pin: String): Boolean {
        return securityRepository.verifyRecoveryPin(pin)
    }

    fun getAuthMethod(): AuthMethod {
        return securityRepository.getAuthMethod()
    }

    fun resetPassword(newPassword: String) {
        val authMethod = securityRepository.getAuthMethod()
        when (authMethod) {
            AuthMethod.PIN -> {
                securityRepository.setupPin(newPassword)
            }
            AuthMethod.PATTERN -> {
                securityRepository.setupPattern(newPassword)
            }
            else -> {}
        }
    }
}

data class LockUiState(
    val authMethod: AuthMethod = AuthMethod.NONE,
    val biometricEnabled: Boolean = false,
    val isUnlocked: Boolean = false,
    val error: String? = null,
    val attemptCount: Int = 0
)