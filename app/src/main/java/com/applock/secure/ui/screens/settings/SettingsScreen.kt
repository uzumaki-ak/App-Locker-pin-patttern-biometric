package com.applock.secure.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Settings Screen
 * Manages permissions and app configuration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Refresh permissions when screen appears
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Permissions",
                style = MaterialTheme.typography.titleLarge
            )

            // Accessibility permission
            PermissionItem(
                title = "Accessibility Service",
                description = "Required to monitor app launches",
                isGranted = uiState.isAccessibilityEnabled,
                onClick = { viewModel.openAccessibilitySettings() }
            )

            // Overlay permission
            PermissionItem(
                title = "Display Over Other Apps",
                description = "Required to show lock screen",
                isGranted = uiState.isOverlayEnabled,
                onClick = { viewModel.openOverlaySettings() }
            )

            // Battery optimization
            PermissionItem(
                title = "Battery Optimization",
                description = "Disable for reliable background operation",
                isGranted = uiState.isBatteryOptimizationDisabled,
                onClick = { viewModel.requestBatteryOptimization() }
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