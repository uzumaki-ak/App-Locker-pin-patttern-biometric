package com.applock.secure.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applock.secure.data.model.AuthMethod
import com.applock.secure.data.repository.AppRepository
import com.applock.secure.data.repository.SecurityRepository
import com.applock.secure.util.PermissionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val securityRepository: SecurityRepository,
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        checkPermissions()
        loadCurrentAuth()
    }

    private fun loadCurrentAuth() {
        val authMethod = securityRepository.getAuthMethod()
        val biometricEnabled = securityRepository.isBiometricEnabled()
        _uiState.value = _uiState.value.copy(
            currentAuthMethod = authMethod.name,
            biometricEnabled = biometricEnabled
        )
    }

    fun checkPermissions() {
        _uiState.value = _uiState.value.copy(
            isAccessibilityEnabled = PermissionHelper.isAccessibilityServiceEnabled(context),
            isOverlayEnabled = PermissionHelper.canDrawOverlays(context),
            isBatteryOptimizationDisabled = PermissionHelper.isBatteryOptimizationDisabled(context)
        )
    }

    fun openAccessibilitySettings() {
        PermissionHelper.openAccessibilitySettings(context)
    }

    fun openOverlaySettings() {
        PermissionHelper.openOverlaySettings(context)
    }

    fun requestBatteryOptimization() {
        PermissionHelper.requestDisableBatteryOptimization(context)
    }

    fun changeToPin() {
        _uiState.value = _uiState.value.copy(
            showPinSetup = true,
            showPatternSetup = false
        )
    }

    fun changeToPattern() {
        _uiState.value = _uiState.value.copy(
            showPatternSetup = true,
            showPinSetup = false
        )
    }

    fun savePinAndDismiss(pin: String) {
        securityRepository.setupPin(pin)
        _uiState.value = _uiState.value.copy(
            showPinSetup = false,
            currentAuthMethod = AuthMethod.PIN.name
        )
    }

    fun savePatternAndDismiss(pattern: String) {
        securityRepository.setupPattern(pattern)
        _uiState.value = _uiState.value.copy(
            showPatternSetup = false,
            currentAuthMethod = AuthMethod.PATTERN.name
        )
    }

    fun cancelSetup() {
        _uiState.value = _uiState.value.copy(
            showPinSetup = false,
            showPatternSetup = false
        )
    }

    fun toggleBiometric() {
        val currentState = securityRepository.isBiometricEnabled()
        securityRepository.setBiometricEnabled(!currentState)
        _uiState.value = _uiState.value.copy(
            biometricEnabled = !currentState
        )
    }

    fun resetAllSecurity() {
        viewModelScope.launch {
            securityRepository.resetSecurity()
            appRepository.clearAllApps()
        }
    }
}

data class SettingsUiState(
    val isAccessibilityEnabled: Boolean = false,
    val isOverlayEnabled: Boolean = false,
    val isBatteryOptimizationDisabled: Boolean = false,
    val showPinSetup: Boolean = false,
    val showPatternSetup: Boolean = false,
    val currentAuthMethod: String = "NONE",
    val biometricEnabled: Boolean = false
)