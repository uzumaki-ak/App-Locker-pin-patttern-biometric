package com.applock.secure.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applock.secure.data.model.LockedApp
import com.applock.secure.data.repository.AppRepository
import com.applock.secure.util.PermissionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home Screen
 * Manages locked apps list and permissions
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    /**
     * Locked apps list (reactive)
     */
    val lockedApps: StateFlow<List<LockedApp>> = appRepository.getLockedApps()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Remove app from locked list
     */
    fun removeLockedApp(packageName: String) {
        viewModelScope.launch {
            appRepository.unlockApp(packageName)
        }
    }

    /**
     * Clear all locked apps
     */
    fun clearAllLockedApps() {
        viewModelScope.launch {
            appRepository.clearAllApps()
        }
    }
}