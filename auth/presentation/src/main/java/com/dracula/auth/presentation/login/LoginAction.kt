package com.dracula.auth.presentation.login

interface LoginAction {
	data object OnLoginClick: LoginAction
	data object OnRegisterClick: LoginAction
	data object OnTogglePasswordVisibility: LoginAction
}