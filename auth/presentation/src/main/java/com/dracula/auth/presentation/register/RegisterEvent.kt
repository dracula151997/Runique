package com.dracula.auth.presentation.register

import com.dracula.core.presentation.ui.UiText

sealed interface RegisterEvent {
	data object RegistrationSuccess : RegisterEvent
	data class Error(val error: UiText): RegisterEvent
}