package com.applock.secure.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import com.applock.secure.data.local.SecurePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for Onboarding Screen
 * Manages onboarding state and completion
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val securePrefs: SecurePreferences
) : ViewModel() {

    /**
     * Mark onboarding as complete
     */
    fun completeOnboarding() {
        securePrefs.setFirstTime(false)
    }

    /**
     * Check if first time
     */
    fun isFirstTime(): Boolean {
        return securePrefs.isFirstTime()
    }
}