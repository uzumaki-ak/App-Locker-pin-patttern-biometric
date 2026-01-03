package com.applock.secure.ui.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applock.secure.data.model.AuthMethod
import com.applock.secure.data.repository.SecurityRepository
import com.applock.secure.util.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Setup Authentication Screen
 * Handles PIN and Pattern setup logic
 */
@HiltViewModel
class SetupAuthViewModel @Inject constructor(
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupAuthUiState())
    val uiState: StateFlow<SetupAuthUiState> = _uiState.asStateFlow()

    /**
     * Select authentication method
     */
    fun selectAuthMethod(method: AuthMethod) {
        _uiState.value = _uiState.value.copy(
            selectedMethod = method,
            step = SetupStep.ENTER_CREDENTIAL
        )
    }

    /**
     * Set PIN (first entry)
     */
    fun setPin(pin: String) {
        if (pin.length < AppConstants.MIN_PIN_LENGTH) {
            _uiState.value = _uiState.value.copy(
                error = "PIN must be at least ${AppConstants.MIN_PIN_LENGTH} digits"
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            credential = pin,
            step = SetupStep.CONFIRM_CREDENTIAL,
            error = null
        )
    }

    /**
     * Confirm PIN (second entry)
     */
    fun confirmPin(pin: String) {
        if (pin != _uiState.value.credential) {
            _uiState.value = _uiState.value.copy(
                error = "PINs don't match"
            )
            return
        }

        viewModelScope.launch {
            securityRepository.setupPin(pin)
            _uiState.value = _uiState.value.copy(
                isComplete = true,
                error = null
            )
        }
    }

    /**
     * Set Pattern (first entry)
     */
    fun setPattern(pattern: String) {
        if (pattern.length < AppConstants.MIN_PATTERN_LENGTH) {
            _uiState.value = _uiState.value.copy(
                error = "Pattern must connect at least ${AppConstants.MIN_PATTERN_LENGTH} dots"
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            credential = pattern,
            step = SetupStep.CONFIRM_CREDENTIAL,
            error = null
        )
    }

    /**
     * Confirm Pattern (second entry)
     */
    fun confirmPattern(pattern: String) {
        if (pattern != _uiState.value.credential) {
            _uiState.value = _uiState.value.copy(
                error = "Patterns don't match"
            )
            return
        }

        viewModelScope.launch {
            securityRepository.setupPattern(pattern)
            _uiState.value = _uiState.value.copy(
                isComplete = true,
                error = null
            )
        }
    }

    /**
     * Clear error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * UI State for Setup Screen
 */
data class SetupAuthUiState(
    val selectedMethod: AuthMethod = AuthMethod.NONE,
    val step: SetupStep = SetupStep.SELECT_METHOD,
    val credential: String = "",
    val error: String? = null,
    val isComplete: Boolean = false
)

/**
 * Setup steps
 */
enum class SetupStep {
    SELECT_METHOD,
    ENTER_CREDENTIAL,
    CONFIRM_CREDENTIAL
}