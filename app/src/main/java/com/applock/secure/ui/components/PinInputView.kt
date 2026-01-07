package com.applock.secure.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.applock.secure.util.AppConstants

/**
 * PIN Input Component
 * Shows dynamic dots based on PIN length (4-8 digits)
 * Auto-submits when user stops typing
 */
@Composable
fun PinInputView(
    onPinComplete: (String) -> Unit,
    onPinChange: (String) -> Unit = {}
) {
    var pin by remember { mutableStateOf("") }
    var showSubmitButton by remember { mutableStateOf(false) }

    // Show submit button after 1 second of no input
    LaunchedEffect(pin) {
        if (pin.length >= AppConstants.MIN_PIN_LENGTH) {
            showSubmitButton = false
            kotlinx.coroutines.delay(1000)
            showSubmitButton = true
        } else {
            showSubmitButton = false
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dynamic PIN display (shows only entered digits)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Show dots for entered digits
            repeat(maxOf(pin.length, AppConstants.MIN_PIN_LENGTH)) { index ->
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(16.dp),
                        shape = MaterialTheme.shapes.small,
                        color = if (index < pin.length) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {}
                }
            }
        }

        Text(
            text = "${pin.length}/${AppConstants.MAX_PIN_LENGTH} digits",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Number pad
        NumberPad(
            onNumberClick = { number ->
                if (pin.length < AppConstants.MAX_PIN_LENGTH) {
                    val newPin = pin + number
                    pin = newPin
                    onPinChange(newPin)
                }
            },
            onDeleteClick = {
                if (pin.isNotEmpty()) {
                    pin = pin.dropLast(1)
                    onPinChange(pin)
                }
            }
        )

        // Submit button (appears after user stops typing)
        if (showSubmitButton && pin.length >= AppConstants.MIN_PIN_LENGTH) {
            Button(
                onClick = {
                    onPinComplete(pin)
                    pin = ""
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Unlock")
            }
        }
    }
}

@Composable
private fun NumberPad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9")
        ).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { number ->
                    Button(
                        onClick = { onNumberClick(number) },
                        modifier = Modifier.size(72.dp)
                    ) {
                        Text(number, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.size(72.dp))

            Button(
                onClick = { onNumberClick("0") },
                modifier = Modifier.size(72.dp)
            ) {
                Text("0", style = MaterialTheme.typography.titleLarge)
            }

            Button(
                onClick = onDeleteClick,
                modifier = Modifier.size(72.dp)
            ) {
                Text("⌫", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}