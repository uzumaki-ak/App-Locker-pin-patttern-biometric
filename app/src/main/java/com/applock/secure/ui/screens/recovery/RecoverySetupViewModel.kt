package com.applock.secure.ui.screens.recovery



import androidx.lifecycle.ViewModel
import com.applock.secure.data.repository.SecurityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecoverySetupViewModel @Inject constructor(
    private val securityRepository: SecurityRepository
) : ViewModel() {

    fun saveRecovery(question: String, answer: String, recoveryPin: String) {
        securityRepository.setupSecurityQuestion(question, answer)
        securityRepository.setupRecoveryPin(recoveryPin)
    }
}