package com.sebsmedia.smediaviewer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PinScreen(
    isPinSet: Boolean,
    onPinVerified: (String) -> Boolean,
    onPinSet: (String) -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSettingPin by remember { mutableStateOf(!isPinSet) }
    var step by remember { mutableStateOf(0) } // 0 = enter PIN, 1 = confirm PIN

    LaunchedEffect(isPinSet) {
        isSettingPin = !isPinSet
        if (isPinSet) {
            step = 0
            pin = ""
            confirmPin = ""
        }
    }

    LaunchedEffect(pin, confirmPin) {
        errorMessage = null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when {
                isSettingPin && step == 0 -> "Set Your PIN"
                isSettingPin && step == 1 -> "Confirm Your PIN"
                else -> "Enter PIN"
            },
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        val currentPin = if (isSettingPin && step == 1) confirmPin else pin

        OutlinedTextField(
            value = currentPin,
            onValueChange = { value ->
                if (value.length <= 4 && value.all { it.isDigit() }) {
                    if (isSettingPin) {
                        if (step == 0) {
                            pin = value
                        } else {
                            confirmPin = value
                        }
                    } else {
                        pin = value
                    }
                }
            },
            label = { Text(if (isSettingPin && step == 1) "Confirm 4-digit PIN" else "Enter 4-digit PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isSettingPin && step == 0 && pin.isNotEmpty()) {
            Text(
                text = "Enter a 4-digit PIN",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isSettingPin && step == 1 && confirmPin.isNotEmpty()) {
            Text(
                text = "Re-enter to confirm",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    !isSettingPin -> {
                        if (pin.length == 4) {
                            val isValid = onPinVerified(pin)
                            if (isValid) {
                                onSuccess()
                            } else {
                                errorMessage = "Incorrect PIN"
                                pin = ""
                            }
                        } else {
                            errorMessage = "PIN must be 4 digits"
                        }
                    }
                    step == 0 -> {
                        if (pin.length == 4) {
                            confirmPin = ""  // Clear confirm PIN field
                            step = 1
                        } else {
                            errorMessage = "PIN must be 4 digits"
                        }
                    }
                    step == 1 -> {
                        if (confirmPin.length == 4) {
                            if (pin == confirmPin) {
                                onPinSet(pin)
                                onSuccess()
                            } else {
                                errorMessage = "PINs do not match"
                                pin = ""
                                confirmPin = ""
                                step = 0
                            }
                        } else {
                            errorMessage = "PIN must be 4 digits"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(
                text = when {
                    !isSettingPin -> "Verify"
                    step == 0 -> "Continue"
                    else -> "Set PIN"
                }
            )
        }

        if (isPinSet && !isSettingPin) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    isSettingPin = true
                    step = 0
                    pin = ""
                    confirmPin = ""
                    errorMessage = null
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Set New PIN")
            }
        }
    }
}
