package com.dracula.auth.domain

data class PasswordValidationState(
	val hasMinLength: Boolean = false,
	val hasNumber: Boolean = false,
	val hasLowerCaseCharacter: Boolean = false,
	val hasUpperCaseCharacter: Boolean = false,
) {
	val isValidPassword: Boolean =
		hasMinLength && hasNumber && hasLowerCaseCharacter && hasUpperCaseCharacter
}