package com.applock.secure.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.applock.secure.ui.components.PatternLockView
import com.applock.secure.ui.components.PinInputView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAuthMethodDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main Settings Content
            if (!uiState.showPinSetup && !uiState.showPatternSetup) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Permissions",
                        style = MaterialTheme.typography.titleLarge
                    )

                    PermissionItem(
                        title = "Accessibility Service",
                        description = "Required to monitor app launches",
                        isGranted = uiState.isAccessibilityEnabled,
                        onClick = { viewModel.openAccessibilitySettings() }
                    )

                    PermissionItem(
                        title = "Display Over Other Apps",
                        description = "Required to show lock screen",
                        isGranted = uiState.isOverlayEnabled,
                        onClick = { viewModel.openOverlaySettings() }
                    )

                    PermissionItem(
                        title = "Battery Optimization",
                        description = "Disable for reliable operation",
                        isGranted = uiState.isBatteryOptimizationDisabled,
                        onClick = { viewModel.requestBatteryOptimization() }
                    )

                    Divider()

                    Text(
                        text = "Security",
                        style = MaterialTheme.typography.titleLarge
                    )

                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "Change Authentication Method",
                        description = "Current: ${uiState.currentAuthMethod}",
                        onClick = { showAuthMethodDialog = true }
                    )

                    SettingsItem(
                        icon = Icons.Default.Fingerprint,
                        title = "Biometric Authentication",
                        description = if (uiState.biometricEnabled) "Enabled" else "Disabled",
                        onClick = { viewModel.toggleBiometric() }
                    )

                    SettingsItem(
                        icon = Icons.Default.Warning,
                        title = "Reset Security",
                        description = "Clear all locked apps and reset password",
                        onClick = { showResetDialog = true }
                    )

                    Divider()

                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "App Locker v1.0.0",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Secure your apps with privacy-first approach",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // PIN Setup Screen
            if (uiState.showPinSetup) {
                SetupPinScreen(
                    onComplete = { pin ->
                        viewModel.savePinAndDismiss(pin)
                    },
                    onCancel = {
                        viewModel.cancelSetup()
                    }
                )
            }

            // Pattern Setup Screen
            if (uiState.showPatternSetup) {
                SetupPatternScreen(
                    onComplete = { pattern ->
                        viewModel.savePatternAndDismiss(pattern)
                    },
                    onCancel = {
                        viewModel.cancelSetup()
                    }
                )
            }
        }
    }

    // Auth Method Selection Dialog
    if (showAuthMethodDialog) {
        AlertDialog(
            onDismissRequest = { showAuthMethodDialog = false },
            title = { Text("Change Authentication") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select new authentication method:")

                    OutlinedButton(
                        onClick = {
                            viewModel.changeToPin()
                            showAuthMethodDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Pin, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Change to PIN")
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.changeToPattern()
                            showAuthMethodDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Pattern, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Change to Pattern")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAuthMethodDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Security?") },
            text = {
                Text("This will:\n• Clear all locked apps\n• Reset your password\n• Disable biometric\n\nThis cannot be undone!")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllSecurity()
                        showResetDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reset Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// PIN Setup Component
@Composable
private fun SetupPinScreen(
    onComplete: (String) -> Unit,
    onCancel: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1 = enter, 2 = confirm
    var error by remember { mutableStateOf<String?>(null) }

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
                imageVector = Icons.Default.Pin,
                contentDescription = "PIN",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (step == 1) "Enter New PIN" else "Confirm PIN",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "4-8 digits",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            PinInputView(
                onPinComplete = { enteredPin ->
                    when (step) {
                        1 -> {
                            if (enteredPin.length >= 4) {
                                pin = enteredPin
                                step = 2
                                error = null
                            } else {
                                error = "PIN must be at least 4 digits"
                            }
                        }
                        2 -> {
                            if (enteredPin == pin) {
                                onComplete(pin)
                            } else {
                                error = "PINs don't match. Try again."
                                step = 1
                                pin = ""
                            }
                        }
                    }
                },
                onPinChange = {
                    if (error != null) error = null
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}

// Pattern Setup Component
@Composable
private fun SetupPatternScreen(
    onComplete: (String) -> Unit,
    onCancel: () -> Unit
) {
    var pattern by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1 = enter, 2 = confirm
    var error by remember { mutableStateOf<String?>(null) }

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
                imageVector = Icons.Default.Pattern,
                contentDescription = "Pattern",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (step == 1) "Draw New Pattern" else "Confirm Pattern",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Connect at least 4 dots",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            PatternLockView(
                onPatternComplete = { drawnPattern ->
                    when (step) {
                        1 -> {
                            if (drawnPattern.length >= 4) {
                                pattern = drawnPattern
                                step = 2
                                error = null
                            } else {
                                error = "Pattern must connect at least 4 dots"
                            }
                        }
                        2 -> {
                            if (drawnPattern == pattern) {
                                onComplete(pattern)
                            } else {
                                error = "Patterns don't match. Try again."
                                step = 1
                                pattern = ""
                            }
                        }
                    }
                },
                onPatternChange = {
                    if (error != null) error = null
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun PermissionItem(
    title: String,
    description: String,
    isGranted: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isGranted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Granted",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Button(onClick = onClick) {
                    Text("Grant")
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}