package com.dracula.auth.presentation.login

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
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
import com.dracula.auth.presentation.R
import com.dracula.core.presentation.designsystem.EmailIcon
import com.dracula.core.presentation.designsystem.Poppins
import com.dracula.core.presentation.designsystem.RuniqueTheme
import com.dracula.core.presentation.designsystem.components.GradientBackground
import com.dracula.core.presentation.designsystem.components.RuniqueActionButton
import com.dracula.core.presentation.designsystem.components.RuniquePasswordTextField
import com.dracula.core.presentation.designsystem.components.RuniqueTextField
import com.dracula.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenRoot(
	viewModel: LoginViewModel = koinViewModel(),
	onLoginSuccess: () -> Unit,
	onSignUpClick: () -> Unit,
) {
	val keyboardController = LocalSoftwareKeyboardController.current
	val context = LocalContext.current
	ObserveAsEvents(flow = viewModel.events) { event ->
		when(event){
			 is LoginEvent.Error -> {
				 keyboardController?.hide()
				 Toast.makeText(
					 context,
					 event.error.asString(context),
					 Toast.LENGTH_LONG
				 ).show()
			 }
			is LoginEvent.LoginSuccess -> {
				keyboardController?.hide()
				Toast.makeText(
					context,
					R.string.your_logged_in,
					Toast.LENGTH_LONG
				).show()
				onLoginSuccess()
			}
		}
	}
	LoginScreen(
		state = viewModel.state,
		onAction = { action ->
			when(action){
				is LoginAction.OnRegisterClick -> onSignUpClick()
				else -> Unit
			}
			viewModel.onAction(action)
		}
	)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LoginScreen(
	state: LoginState,
	onAction: (LoginAction) -> Unit,
) {
	GradientBackground {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 16.dp, vertical = 32.dp)
				.padding(top = 16.dp)
		) {
			Text(
				text = stringResource(R.string.hi_there),
				fontWeight = FontWeight.SemiBold,
				style = MaterialTheme.typography.headlineMedium
			)
			Text(
				text = stringResource(R.string.welcome_text),
				fontSize = 12.sp,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(Modifier.height(48.dp))
			RuniqueTextField(
				state = state.email,
				startIcon = EmailIcon,
				keyboardType = KeyboardType.Email,
				endIcon = null,
				hint = stringResource(R.string.example_email),
				title = stringResource(R.string.email),
			)
			Spacer(Modifier.height(16.dp))
			RuniquePasswordTextField(
				state = state.password,
				hint = stringResource(R.string.password),
				title = stringResource(R.string.password),
				isPasswordVisible = state.isPasswordVisible,
				onTogglePasswordVisibility = { onAction(LoginAction.OnTogglePasswordVisibility) },
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(Modifier.height(32.dp))
			RuniqueActionButton(
				text = stringResource(R.string.login),
				isLoading = state.isLoggingIn,
				onClick = { onAction(LoginAction.OnLoginClick) },
				enabled = state.canLogin && !state.isLoggingIn
			)
			val annotatedString = buildAnnotatedString {
				withStyle(
					style = SpanStyle(
						fontFamily = Poppins,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				) {
					append(stringResource(R.string.dont_have_an_account) + " ")
				}
				pushStringAnnotation(
					tag = "clickable_text",
					annotation = stringResource(R.string.sign_up)
				)
				withStyle(
					style = SpanStyle(
						fontFamily = Poppins,
						color = MaterialTheme.colorScheme.primary,
						fontWeight = FontWeight.Medium
					)
				) {
					append(stringResource(R.string.sign_up))
				}
			}
			Box(
				modifier = Modifier
					.weight(1f)
					.align(Alignment.CenterHorizontally)
					.navigationBarsPadding(),
				contentAlignment = Alignment.BottomCenter
			) {
				ClickableText(
					text = annotatedString,
					onClick = { offset ->
						annotatedString.getStringAnnotations(
							tag = "clickable_text",
							start = offset,
							end = offset
						).firstOrNull()?.let { _ ->
							onAction(LoginAction.OnRegisterClick)
						}
					}
				)
			}


		}
	}
}

@Preview
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun LoginScreenPreview() {
	RuniqueTheme {
		LoginScreen(
			state = LoginState(),
			onAction = {}
		)
	}
}