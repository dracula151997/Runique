package com.dracula.auth.presentation.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState


@OptIn(ExperimentalFoundationApi::class)
data class LoginState(
	val email: TextFieldState = TextFieldState(),
	val password: TextFieldState = TextFieldState(),
	val isPasswordVisible: Boolean = false,
	val canLogin: Boolean = false,
	val isLoggingIn: Boolean = false,
)