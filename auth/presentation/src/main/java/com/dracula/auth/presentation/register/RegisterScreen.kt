package com.dracula.auth.presentation.register

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dracula.auth.domain.PasswordValidationState
import com.dracula.auth.domain.UserDataValidator
import com.dracula.auth.presentation.R
import com.dracula.core.presentation.designsystem.CheckIcon
import com.dracula.core.presentation.designsystem.CrossIcon
import com.dracula.core.presentation.designsystem.EmailIcon
import com.dracula.core.presentation.designsystem.Poppins
import com.dracula.core.presentation.designsystem.RuniqueDarkRed
import com.dracula.core.presentation.designsystem.RuniqueGray
import com.dracula.core.presentation.designsystem.RuniqueGreen
import com.dracula.core.presentation.designsystem.RuniqueTheme
import com.dracula.core.presentation.designsystem.components.GradientBackground
import com.dracula.core.presentation.designsystem.components.RuniqueActionButton
import com.dracula.core.presentation.designsystem.components.RuniquePasswordTextField
import com.dracula.core.presentation.designsystem.components.RuniqueTextField
import com.dracula.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreenRoot(
	onSignInClick: () -> Unit,
	onSuccessfulRegistration: () -> Unit,
	viewModel: RegisterViewModel = koinViewModel(),
) {
	val context = LocalContext.current
	val keyboardController = LocalSoftwareKeyboardController.current
	ObserveAsEvents(viewModel.events) { event ->
		when (event) {
			is RegisterEvent.Error -> {
				keyboardController?.hide()
				Toast.makeText(context, event.error.asString(context), Toast.LENGTH_SHORT).show()
			}

			RegisterEvent.RegistrationSuccess -> {
				Toast.makeText(context, R.string.registration_successful, Toast.LENGTH_SHORT).show()
				onSuccessfulRegistration()
			}
		}
	}
	RegisterScreen(
		state = viewModel.state,
		onAction = viewModel::onAction
	)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RegisterScreen(
	state: RegisterState,
	modifier: Modifier = Modifier,
	onAction: (RegisterAction) -> Unit,
) {
	GradientBackground {
		Column(
			modifier = modifier
				.verticalScroll(state = rememberScrollState())
				.fillMaxSize()
				.padding(horizontal = 16.dp, vertical = 32.dp)
				.padding(top = 16.dp)
		) {
			Text(
				text = stringResource(R.string.create_account),
				style = MaterialTheme.typography.headlineMedium
			)
			val annotatedString = buildAnnotatedString {
				withStyle(
					style = SpanStyle(
						fontFamily = Poppins,
						color = RuniqueGray
					)
				) {
					append(stringResource(R.string.already_have_an_account) + " ")
				}
				pushStringAnnotation(
					tag = "clickable_text",
					annotation = stringResource(R.string.login)
				)
				withStyle(
					style = SpanStyle(
						fontFamily = Poppins,
						color = MaterialTheme.colorScheme.primary,
						fontWeight = FontWeight.Medium
					)
				) {
					append(stringResource(R.string.login))
				}
			}
			ClickableText(
				text = annotatedString,
				onClick = { offset ->
					annotatedString.getStringAnnotations(
						tag = "clickable_text",
						start = offset,
						end = offset
					).firstOrNull()?.let { _ ->
						onAction(RegisterAction.OnLoginClick)
					}
				}
			)
			Spacer(Modifier.height(48.dp))
			RuniqueTextField(
				state = state.email,
				hint = stringResource(R.string.example_email),
				startIcon = EmailIcon,
				endIcon = if (state.isEmailValid) CheckIcon else null,
				title = stringResource(R.string.email),
				additionalInfo = stringResource(R.string.must_be_valid_email),
				keyboardType = KeyboardType.Email,
			)
			Spacer(Modifier.height(16.dp))
			RuniquePasswordTextField(
				state = state.password,
				onTogglePasswordVisibility = {
					onAction(RegisterAction.OnTogglePasswordVisibilityClick)
				},
				isPasswordVisible = state.isPasswordVisible,
				hint = stringResource(R.string.password),
				title = stringResource(R.string.password),
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(Modifier.height(16.dp))
			PasswordRequirement(
				text = stringResource(
					R.string.at_least_x_characters,
					UserDataValidator.MIN_PASSWORD_LENGTH
				),
				isValid = state.passwordValidationState.hasMinLength
			)
			Spacer(Modifier.height(4.dp))
			PasswordRequirement(
				text = stringResource(
					R.string.at_least_one_number,
				),
				isValid = state.passwordValidationState.hasNumber
			)
			Spacer(Modifier.height(4.dp))
			PasswordRequirement(
				text = stringResource(
					R.string.contains_lowercase_character,
				),
				isValid = state.passwordValidationState.hasLowerCaseCharacter
			)
			Spacer(Modifier.height(4.dp))
			PasswordRequirement(
				text = stringResource(
					R.string.contains_uppercase_character,
				),
				isValid = state.passwordValidationState.hasUpperCaseCharacter
			)
			Spacer(Modifier.height(32.dp))
			RuniqueActionButton(
				text = stringResource(R.string.register),
				isLoading = state.isRegistering,
				enabled = state.canRegister && !state.isRegistering,
				onClick = {
					onAction(RegisterAction.OnRegisterClick)
				}
			)
		}
	}
}

@Composable
fun PasswordRequirement(
	text: String,
	isValid: Boolean,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			imageVector = if (isValid) CheckIcon else CrossIcon,
			contentDescription = null,
			tint = if (isValid) RuniqueGreen else RuniqueDarkRed
		)
		Spacer(Modifier.width(16.dp))
		Text(
			text = text,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontSize = 14.sp
		)
	}
}

@Preview
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun RegisterScreenPreview() {
	RuniqueTheme {
		RegisterScreen(
			state = RegisterState(
				passwordValidationState = PasswordValidationState(
					hasNumber = true,
					hasUpperCaseCharacter = true,
				),
			),
		) { }
	}
}