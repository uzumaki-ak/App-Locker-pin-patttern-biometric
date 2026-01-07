package com.applock.secure.ui.screens.lock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.applock.secure.data.model.AuthMethod
import com.applock.secure.ui.components.BiometricPromptHelper
import com.applock.secure.ui.components.PatternLockView
import com.applock.secure.ui.components.PinInputView
import com.applock.secure.ui.theme.AppLockerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockScreenActivity : FragmentActivity() {

    private val viewModel: LockViewModel by viewModels()
    private lateinit var biometricHelper: BiometricPromptHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        biometricHelper = BiometricPromptHelper(
            activity = this,
            onSuccess = {
                viewModel.onBiometricSuccess()
            },
            onError = { error ->
                viewModel.onBiometricFailure(error)
            }
        )

        val packageName = intent.getStringExtra("package_name") ?: ""

        setContent {
            AppLockerTheme {
                LockScreenContent(
                    viewModel = viewModel,
                    packageName = packageName,
                    onUnlocked = { finish() },
                    onShowBiometric = {
                        if (biometricHelper.isBiometricAvailable()) {
                            biometricHelper.showBiometricPrompt()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LockScreenContent(
    viewModel: LockViewModel,
    packageName: String,
    onUnlocked: () -> Unit,
    onShowBiometric: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showForgotPassword by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isUnlocked) {
        if (uiState.isUnlocked) {
            onUnlocked()
        }
    }

    LaunchedEffect(Unit) {
        if (uiState.biometricEnabled) {
            onShowBiometric()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "App is Locked",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your credentials to unlock",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            val errorMessage = uiState.error
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Show "Forgot Password" after 3 failed attempts
            if (uiState.attemptCount >= 3) {
                TextButton(onClick = { showForgotPassword = true }) {
                    Text(
                        "Forgot Password?",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            when (uiState.authMethod) {
                AuthMethod.PIN -> {
                    PinInputView(
                        onPinComplete = { pin ->
                            viewModel.validatePin(pin)
                        },
                        onPinChange = {
                            if (uiState.error != null) viewModel.clearError()
                        }
                    )
                }

                AuthMethod.PATTERN -> {
                    PatternLockView(
                        onPatternComplete = { pattern ->
                            viewModel.validatePattern(pattern)
                        },
                        onPatternChange = {
                            if (uiState.error != null) viewModel.clearError()
                        }
                    )
                }

                AuthMethod.BIOMETRIC -> {
                    Button(
                        onClick = onShowBiometric,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Use Biometric")
                    }
                }

                AuthMethod.NONE -> {
                    Text("No authentication set")
                }
            }

            if (uiState.biometricEnabled && uiState.authMethod != AuthMethod.BIOMETRIC) {
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onShowBiometric) {
                    Text("Use Biometric Authentication")
                }
            }
        }
    }

    // Forgot Password Dialog with Recovery
    if (showForgotPassword) {
        ForgotPasswordDialog(
            viewModel = viewModel,
            onDismiss = { showForgotPassword = false },
            onSuccess = onUnlocked
        )
    }
}

/**
 * Forgot Password Dialog
 * Shows recovery flow: Security Question -> Recovery PIN -> Reset Password
 */
@Composable
private fun ForgotPasswordDialog(
    viewModel: LockViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1=question, 2=recovery PIN, 3=reset
    var userAnswer by remember { mutableStateOf("") }
    var recoveryPinInput by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val securityQuestion = viewModel.getSecurityQuestion()
    val hasRecovery = viewModel.hasRecoverySetup()
    val authMethod = viewModel.getAuthMethod()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Forgot Password") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when {
                    !hasRecovery -> {
                        Text(
                            "No recovery method set up.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "To reset:\n1. Go to Settings\n2. Apps → App Locker\n3. Clear Data",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    step == 1 && securityQuestion != null -> {
                        Text(
                            "Step 1: Security Question",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            securityQuestion,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        OutlinedTextField(
                            value = userAnswer,
                            onValueChange = {
                                userAnswer = it
                                error = null
                            },
                            label = { Text("Your Answer") },
                            singleLine = true,
                            isError = error != null,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (error != null) {
                            Text(
                                error!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    step == 2 -> {
                        Text(
                            "Step 2: Recovery PIN",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            "Enter your recovery PIN",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        OutlinedTextField(
                            value = recoveryPinInput,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() } && it.length <= 8) {
                                    recoveryPinInput = it
                                    error = null
                                }
                            },
                            label = { Text("Recovery PIN") },
                            singleLine = true,
                            isError = error != null,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (error != null) {
                            Text(
                                error!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    step == 3 -> {
                        Text(
                            "Step 3: Set New Password",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            "Enter your new ${authMethod.name}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = {
                                if (authMethod == AuthMethod.PIN) {
                                    if (it.all { char -> char.isDigit() } && it.length <= 8) {
                                        newPassword = it
                                        error = null
                                    }
                                } else {
                                    newPassword = it
                                    error = null
                                }
                            },
                            label = { Text("New ${authMethod.name}") },
                            singleLine = true,
                            isError = error != null,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (error != null) {
                            Text(
                                error!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (hasRecovery) {
                when (step) {
                    1 -> {
                        Button(
                            onClick = {
                                val isCorrect = viewModel.verifySecurityAnswer(userAnswer)
                                if (isCorrect) {
                                    step = 2
                                    error = null
                                } else {
                                    error = "Incorrect answer. Try again."
                                }
                            },
                            enabled = userAnswer.isNotEmpty()
                        ) {
                            Text("Verify")
                        }
                    }

                    2 -> {
                        Button(
                            onClick = {
                                val isCorrect = viewModel.verifyRecoveryPin(recoveryPinInput)
                                if (isCorrect) {
                                    step = 3
                                    error = null
                                } else {
                                    error = "Incorrect recovery PIN."
                                }
                            },
                            enabled = recoveryPinInput.length >= 4
                        ) {
                            Text("Verify")
                        }
                    }

                    3 -> {
                        Button(
                            onClick = {
                                if (newPassword.length >= 4) {
                                    viewModel.resetPassword(newPassword)
                                    onSuccess()
                                    onDismiss()
                                } else {
                                    error = "Password must be at least 4 characters"
                                }
                            },
                            enabled = newPassword.length >= 4
                        ) {
                            Text("Reset Password")
                        }
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}