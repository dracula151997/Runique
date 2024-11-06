package com.dracula.auth.presentation.login

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.Flow


data class LoginState(
	val email: TextFieldState = TextFieldState(),
	val password: TextFieldState = TextFieldState(),
	val isPasswordVisible: Boolean = false,
	val canLogin: Boolean = false,
	val isLoggingIn: Boolean = false,
)

fun TextFieldState.textAsFlow(): Flow<CharSequence>{
	return snapshotFlow { text }
}