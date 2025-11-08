package com.example.movieapp.ui.auth

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.movieapp.ui.theme.ExtraBlack
import com.example.movieapp.ui.theme.Night
import com.example.movieapp.ui.theme.RedPrimary
import com.example.movieapp.ui.theme.Silver

/**
 * Authentication screen used for both login and registration
 * The UI dynamically changes based on the current AuthMode Login or Register
 * It uses reactive Compose state management to reflect input and loading states
 */
@Composable

fun AuthScreen(
    state: AuthUiState,
    onUsername: (String) -> Unit,
    onEmail: (String) -> Unit,
    onPassword: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleMode: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Night, ExtraBlack)))
            .padding(24.dp)
            .systemBarsPadding()
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text(
                text = if (state.mode == AuthMode.Login) "" else "",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(6.dp))
            Text(text = "CineShare", style = MaterialTheme.typography.titleMedium, color = Silver)
        }
        /**
         * Main authentication card
         * Contains all form fields, buttons, and feedback messages
         */
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ExtraBlack),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(Modifier.padding(20.dp)) {

                if (state.mode == AuthMode.Register) {
                    OutlinedTextField(
                        value = state.username,
                        onValueChange = onUsername,
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                }
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmail,
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPassword,
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.error != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(state.error, color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(16.dp))

                /**
                 * Submit button login or register
                 * Uses Crossfade to animate between loading state and normal state
                 */
                Button(
                    onClick = onSubmit,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                ) {
                    Crossfade(state.isLoading, label = "btn") { loading ->
                        if (loading) CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        ) else Text(if (state.mode == AuthMode.Login) "Log in" else "Create account")
                    }
                }
                // Toggle text between Login and Register modes
                TextButton(
                    onClick = onToggleMode,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        if (state.mode == AuthMode.Login)
                            "New here? Create an account"
                        else
                            "Already have an account? Log in"
                    )
                }
            }
        }
    }
}
