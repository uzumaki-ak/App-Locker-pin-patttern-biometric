package com.applock.secure.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.applock.secure.util.AppConstants

/**
 * PIN Input Component
 * Custom PIN entry with dots for security
 * Auto-triggers callback when PIN is complete
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinInputView(
    onPinComplete: (String) -> Unit,
    onPinChange: (String) -> Unit = {}
) {
    var pin by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // PIN display (dots)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(AppConstants.MAX_PIN_LENGTH) { index ->
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

        // Hidden text field for input
        TextField(
            value = pin,
            onValueChange = { newPin ->
                if (newPin.length <= AppConstants.MAX_PIN_LENGTH && newPin.all { it.isDigit() }) {
                    pin = newPin
                    onPinChange(newPin)

                    // Auto-submit when minimum length reached
                    if (newPin.length >= AppConstants.MIN_PIN_LENGTH) {
                        onPinComplete(newPin)
                        pin = "" // Clear for next attempt
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.width(0.dp).height(0.dp) // Hidden but functional
        )

        // Number pad
        NumberPad(
            onNumberClick = { number ->
                if (pin.length < AppConstants.MAX_PIN_LENGTH) {
                    val newPin = pin + number
                    pin = newPin
                    onPinChange(newPin)

                    if (newPin.length >= AppConstants.MIN_PIN_LENGTH && newPin.length <= AppConstants.MAX_PIN_LENGTH) {
                        onPinComplete(newPin)
                        pin = ""
                    }
                }
            },
            onDeleteClick = {
                if (pin.isNotEmpty()) {
                    pin = pin.dropLast(1)
                    onPinChange(pin)
                }
            }
        )
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
        // Rows 1-3
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

        // Bottom row (0 and delete)
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