package com.dracula.auth.presentation.login

import com.dracula.core.presentation.ui.UiText

interface LoginEvent {
	data class Error(val error: UiText): LoginEvent
	data object LoginSuccess: LoginEvent
}