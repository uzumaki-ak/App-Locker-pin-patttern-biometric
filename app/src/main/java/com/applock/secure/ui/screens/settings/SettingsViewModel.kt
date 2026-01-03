package com.applock.secure.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.applock.secure.data.repository.SecurityRepository
import com.applock.secure.util.PermissionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for Settings Screen
 * Manages app settings and permissions
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        checkPermissions()
    }

    /**
     * Check all permissions
     */
    fun checkPermissions() {
        _uiState.value = _uiState.value.copy(
            isAccessibilityEnabled = PermissionHelper.isAccessibilityServiceEnabled(context),
            isOverlayEnabled = PermissionHelper.canDrawOverlays(context),
            isBatteryOptimizationDisabled = PermissionHelper.isBatteryOptimizationDisabled(context)
        )
    }

    /**
     * Open accessibility settings
     */
    fun openAccessibilitySettings() {
        PermissionHelper.openAccessibilitySettings(context)
    }

    /**
     * Open overlay settings
     */
    fun openOverlaySettings() {
        PermissionHelper.openOverlaySettings(context)
    }

    /**
     * Request battery optimization
     */
    fun requestBatteryOptimization() {
        PermissionHelper.requestDisableBatteryOptimization(context)
    }
}

/**
 * UI State for Settings
 */
data class SettingsUiState(
    val isAccessibilityEnabled: Boolean = false,
    val isOverlayEnabled: Boolean = false,
    val isBatteryOptimizationDisabled: Boolean = false
)