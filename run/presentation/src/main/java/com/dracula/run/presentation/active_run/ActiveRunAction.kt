package com.dracula.run.presentation.active_run

sealed interface ActiveRunAction {
	data object OnToggleRunClick : ActiveRunAction
	data object OnFinishRunClick : ActiveRunAction
	data object OnResumeRunClick : ActiveRunAction
	data object OnBackClick : ActiveRunAction
	data class SubmitLocationPermissionInfo(
		val acceptedLocationPermission: Boolean,
		val shouldShowLocationPermissionRotational: Boolean,
	) : ActiveRunAction

	data class SubmitNotificationPermissionInfo(
		val acceptedNotificationPermission: Boolean,
		val shouldShowNotificationPermissionRotational: Boolean,
	) : ActiveRunAction

	data object DismissPermissionRotationalDialog : ActiveRunAction
	class OnRunProcessed(val mapPictureByteArray: ByteArray) : ActiveRunAction
}