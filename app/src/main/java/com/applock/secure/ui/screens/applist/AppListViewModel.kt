package com.applock.secure.ui.screens.applist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applock.secure.data.model.LockedApp
import com.applock.secure.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for App List Screen
 * Manages installed apps and locking
 */
@HiltViewModel
class AppListViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppListUiState>(AppListUiState.Loading)
    val uiState: StateFlow<AppListUiState> = _uiState.asStateFlow()

    init {
        loadInstalledApps()
    }

    /**
     * Load all installed apps
     */
    private fun loadInstalledApps() {
        viewModelScope.launch {
            try {
                _uiState.value = AppListUiState.Loading
                val apps = appRepository.getInstalledApps()
                _uiState.value = AppListUiState.Success(apps)
            } catch (e: Exception) {
                _uiState.value = AppListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Lock an app
     */
    fun lockApp(app: LockedApp) {
        viewModelScope.launch {
            appRepository.lockApp(app)
            loadInstalledApps() // Refresh list
        }
    }

    /**
     * Search apps
     */
    fun searchApps(query: String) {
        val currentState = _uiState.value
        if (currentState is AppListUiState.Success) {
            val filtered = if (query.isEmpty()) {
                currentState.allApps
            } else {
                currentState.allApps.filter {
                    it.appName.contains(query, ignoreCase = true)
                }
            }
            _uiState.value = AppListUiState.Success(
                apps = filtered,
                allApps = currentState.allApps
            )
        }
    }
}

/**
 * UI State for App List
 */
sealed class AppListUiState {
    object Loading : AppListUiState()
    data class Success(
        val apps: List<LockedApp>,
        val allApps: List<LockedApp> = apps
    ) : AppListUiState()
    data class Error(val message: String) : AppListUiState()
}