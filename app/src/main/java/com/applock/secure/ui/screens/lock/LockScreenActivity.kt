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
}