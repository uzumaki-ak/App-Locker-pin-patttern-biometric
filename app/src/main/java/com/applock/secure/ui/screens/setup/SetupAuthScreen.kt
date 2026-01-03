package com.applock.secure.ui.screens.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Pattern
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.applock.secure.data.model.AuthMethod
import com.applock.secure.ui.components.PinInputView
import com.applock.secure.ui.components.PatternLockView

/**
 * Setup Authentication Screen
 * Allows user to configure PIN or Pattern
 * Two-step process: Enter -> Confirm
 */
@Composable
fun SetupAuthScreen(
    viewModel: SetupAuthViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate when setup is complete
    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (uiState.step) {
            SetupStep.SELECT_METHOD -> {
                SelectMethodContent(
                    onPinSelected = { viewModel.selectAuthMethod(AuthMethod.PIN) },
                    onPatternSelected = { viewModel.selectAuthMethod(AuthMethod.PATTERN) }
                )
            }

            SetupStep.ENTER_CREDENTIAL -> {
                when (uiState.selectedMethod) {
                    AuthMethod.PIN -> {
                        EnterPinContent(
                            onPinEntered = { viewModel.setPin(it) },
                            error = uiState.error,
                            onErrorDismiss = { viewModel.clearError() }
                        )
                    }
                    AuthMethod.PATTERN -> {
                        EnterPatternContent(
                            onPatternDrawn = { viewModel.setPattern(it) },
                            error = uiState.error,
                            onErrorDismiss = { viewModel.clearError() }
                        )
                    }
                    else -> {}
                }
            }

            SetupStep.CONFIRM_CREDENTIAL -> {
                when (uiState.selectedMethod) {
                    AuthMethod.PIN -> {
                        ConfirmPinContent(
                            onPinEntered = { viewModel.confirmPin(it) },
                            error = uiState.error,
                            onErrorDismiss = { viewModel.clearError() }
                        )
                    }
                    AuthMethod.PATTERN -> {
                        ConfirmPatternContent(
                            onPatternDrawn = { viewModel.confirmPattern(it) },
                            error = uiState.error,
                            onErrorDismiss = { viewModel.clearError() }
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun SelectMethodContent(
    onPinSelected: () -> Unit,
    onPatternSelected: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Choose Authentication Method",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(32.dp))

        // PIN option
        OutlinedCard(
            onClick = onPinSelected,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pin,
                    contentDescription = "PIN",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("PIN", style = MaterialTheme.typography.titleMedium)
            }
        }

        // Pattern option
        OutlinedCard(
            onClick = onPatternSelected,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pattern,
                    contentDescription = "Pattern",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Pattern", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun EnterPinContent(
    onPinEntered: (String) -> Unit,
    error: String?,
    onErrorDismiss: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Enter PIN",
            style = MaterialTheme.typography.headlineSmall
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PinInputView(
            onPinComplete = onPinEntered,
            onPinChange = { if (error != null) onErrorDismiss() }
        )
    }
}

@Composable
private fun ConfirmPinContent(
    onPinEntered: (String) -> Unit,
    error: String?,
    onErrorDismiss: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Confirm PIN",
            style = MaterialTheme.typography.headlineSmall
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PinInputView(
            onPinComplete = onPinEntered,
            onPinChange = { if (error != null) onErrorDismiss() }
        )
    }
}

@Composable
private fun EnterPatternContent(
    onPatternDrawn: (String) -> Unit,
    error: String?,
    onErrorDismiss: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Draw Pattern",
            style = MaterialTheme.typography.headlineSmall
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PatternLockView(
            onPatternComplete = onPatternDrawn,
            onPatternChange = { if (error != null) onErrorDismiss() }
        )
    }
}

@Composable
private fun ConfirmPatternContent(
    onPatternDrawn: (String) -> Unit,
    error: String?,
    onErrorDismiss: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Confirm Pattern",
            style = MaterialTheme.typography.headlineSmall
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PatternLockView(
            onPatternComplete = onPatternDrawn,
            onPatternChange = { if (error != null) onErrorDismiss() }
        )
    }
}