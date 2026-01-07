package com.applock.secure.ui.screens.recovery

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.applock.secure.data.model.SecurityQuestions
import com.applock.secure.ui.components.PinInputView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverySetupScreen(
    viewModel: RecoverySetupViewModel = hiltViewModel(),
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1=question, 2=answer, 3=recovery PIN
    var selectedQuestion by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = "Recovery",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Set Up Recovery",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "In case you forget your password",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (step) {
            1 -> {
                // Security Question Selection
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedQuestion,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Security Question") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        SecurityQuestions.questions.forEach { question ->
                            DropdownMenuItem(
                                text = { Text(question) },
                                onClick = {
                                    selectedQuestion = question
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedQuestion.isNotEmpty()) {
                            step = 2
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedQuestion.isNotEmpty()
                ) {
                    Text("Next")
                }
            }

            2 -> {
                // Security Answer
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Your Answer") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (answer.isNotEmpty()) {
                            step = 3
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = answer.isNotEmpty()
                ) {
                    Text("Next")
                }
            }

            3 -> {
                // Recovery PIN
                Text(
                    text = "Set Recovery PIN",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Different from your main PIN",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                PinInputView(
                    onPinComplete = { recoveryPin ->
                        viewModel.saveRecovery(
                            question = selectedQuestion,
                            answer = answer,
                            recoveryPin = recoveryPin
                        )
                        onComplete()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onSkip) {
            Text("Skip (Not Recommended)")
        }
    }
}