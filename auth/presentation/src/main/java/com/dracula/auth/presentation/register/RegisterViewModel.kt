package com.dracula.auth.presentation.register

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dracula.auth.domain.AuthRepository
import com.dracula.auth.domain.UserDataValidator
import com.dracula.auth.presentation.R
import com.dracula.core.domain.utils.DataError
import com.dracula.core.domain.utils.Result
import com.dracula.core.presentation.ui.UiText
import com.dracula.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
class RegisterViewModel(
	private val userDataValidator: UserDataValidator,
	private val repository: AuthRepository,
) : ViewModel() {
	var state by mutableStateOf(RegisterState())
		private set

	private val eventChannel = Channel<RegisterEvent>()
	val events = eventChannel.receiveAsFlow()

	init {
		state.email
			.textAsFlow()
			.onEach { email ->
				val isValidEmail = userDataValidator.isValidEmail(email.toString())
				state = state.copy(
					isEmailValid = isValidEmail,
					canRegister = isValidEmail
							&& state.passwordValidationState.isValidPassword
							&& !state.isRegistering
				)
			}.launchIn(viewModelScope)

		state.password
			.textAsFlow()
			.onEach { password ->
				val passwordValidationState =
					userDataValidator.validatePassword(password.toString())
				state = state.copy(
					canRegister = state.isEmailValid
							&& passwordValidationState.isValidPassword
							&& !state.isRegistering,
					passwordValidationState = passwordValidationState,
				)
			}.launchIn(viewModelScope)

	}

	fun onAction(action: RegisterAction) {
		when (action) {
			RegisterAction.OnLoginClick -> TODO()
			RegisterAction.OnRegisterClick -> register()
			RegisterAction.OnTogglePasswordVisibilityClick -> {
				state = state.copy(isPasswordVisible = !state.isPasswordVisible)
			}
		}
	}

	private fun register() {
		viewModelScope.launch {
			state = state.copy(isRegistering = true)
			val result = repository.register(
				email = state.email.text.toString().trim(),
				password = state.password.text.toString().trim()
			)
			when (result) {
				is Result.Success -> {
					eventChannel.send(RegisterEvent.RegistrationSuccess)
				}

				is Result.Error -> {
					if (result.error == DataError.Network.CONFLICT) {
						eventChannel.send(RegisterEvent.Error(UiText.StringResource(R.string.error_email_exists)))
					} else {
						eventChannel.send(RegisterEvent.Error(result.error.asUiText()))
					}
				}
			}
		}
	}
}