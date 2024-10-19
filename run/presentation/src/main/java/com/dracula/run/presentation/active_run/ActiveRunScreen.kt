package com.dracula.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dracula.core.presentation.designsystem.RuniqueScaffold
import com.dracula.core.presentation.designsystem.RuniqueTheme
import com.dracula.core.presentation.designsystem.StartIcon
import com.dracula.core.presentation.designsystem.StopIcon
import com.dracula.core.presentation.designsystem.components.RuniqueActionButton
import com.dracula.core.presentation.designsystem.components.RuniqueDialog
import com.dracula.core.presentation.designsystem.components.RuniqueFloatingActionButton
import com.dracula.core.presentation.designsystem.components.RuniqueOutlinedActionButton
import com.dracula.core.presentation.designsystem.components.RuniqueToolbar
import com.dracula.core.presentation.ui.ObserveAsEvents
import com.dracula.run.presentation.R
import com.dracula.run.presentation.active_run.components.RunDataCard
import com.dracula.run.presentation.active_run.maps.TrackerMap
import com.dracula.run.presentation.active_run.service.ActiveRunService
import com.dracula.run.presentation.utils.hasLocationPermission
import com.dracula.run.presentation.utils.hasNotificationPermission
import com.dracula.run.presentation.utils.shouldShowLocationPermissionRotational
import com.dracula.run.presentation.utils.shouldShowNotificationPermissionRotational
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream

@Composable
fun ActiveRunScreenRoot(
	onFinish: () -> Unit,
	onBack: () -> Unit,
	onServiceToggle: (shouldServiceRun: Boolean) -> Unit,
	viewModel: ActiveRunViewModel = koinViewModel(),
) {
	val context = LocalContext.current
	ObserveAsEvents(
		flow = viewModel.events
	) { event ->
		when (event) {
			is ActiveRunEvent.Error -> {
				Toast.makeText(context, event.error.asString(context = context), Toast.LENGTH_SHORT)
					.show()
			}

			ActiveRunEvent.RunSaved -> {
				onFinish()
			}
		}
	}
	ActiveRunScreen(
		state = viewModel.state,
		onAction = { action ->
			when(action){
				is ActiveRunAction.OnBackClick -> {
					if (!viewModel.state.hasStartedRunning){
						onBack()
					}
				}
				else -> Unit
			}
			viewModel.onAction(action)

		},
		onServiceToggle = onServiceToggle
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActiveRunScreen(
	state: ActiveRunState,
	onServiceToggle: (isServiceRunning: Boolean) -> Unit,
	onAction: (ActiveRunAction) -> Unit,
) {
	val context = LocalContext.current
	val permissionLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestMultiplePermissions()
	) { permissions ->
		val hasNotificationPermission = if (Build.VERSION.SDK_INT >= 33) {
			permissions[Manifest.permission.POST_NOTIFICATIONS] == true
		} else {
			true
		}
		val hasFineLocationPermission =
			permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
		val hasCoarseLocationPermission =
			permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
		val activity = context as ComponentActivity
		val showLocationRotational = activity.shouldShowLocationPermissionRotational()
		val showNotificationRotational = activity.shouldShowNotificationPermissionRotational()
		onAction(
			ActiveRunAction.SubmitLocationPermissionInfo(
				acceptedLocationPermission = hasCoarseLocationPermission && hasFineLocationPermission,
				shouldShowLocationPermissionRotational = showNotificationRotational
			)
		)
		onAction(
			ActiveRunAction.SubmitNotificationPermissionInfo(
				acceptedNotificationPermission = hasNotificationPermission,
				shouldShowNotificationPermissionRotational = showLocationRotational
			)
		)
	}
	LaunchedEffect(key1 = true) {
		val activity = context as ComponentActivity
		val showLocationRotational = activity.shouldShowLocationPermissionRotational()
		val showNotificationRotational = activity.shouldShowNotificationPermissionRotational()
		onAction(
			ActiveRunAction.SubmitLocationPermissionInfo(
				acceptedLocationPermission = context.hasLocationPermission(),
				shouldShowLocationPermissionRotational = showNotificationRotational
			)
		)
		onAction(
			ActiveRunAction.SubmitNotificationPermissionInfo(
				acceptedNotificationPermission = context.hasNotificationPermission(),
				shouldShowNotificationPermissionRotational = showLocationRotational
			)
		)
		if (!showLocationRotational && !showNotificationRotational) {
			permissionLauncher.requestRuniquePermissions(context)
		}
	}
	LaunchedEffect(key1 = state.shouldTrack) {
		if (context.hasLocationPermission() && state.shouldTrack && !ActiveRunService.isServiceRunning) {
			onServiceToggle(true)
		}
	}
	LaunchedEffect(key1 = state.isRunFinished) {
		if (state.isRunFinished) {
			onServiceToggle(false)
		}
	}
	RuniqueScaffold(
		withGradient = false,
		topAppBar = {
			RuniqueToolbar(
				title = stringResource(R.string.active_run),
				showBackButton = true,
				onBackClick = {
					onAction(ActiveRunAction.OnBackClick)
				}
			)
		},
		floatingActionButton = {
			RuniqueFloatingActionButton(
				icon = if (state.shouldTrack) StopIcon else StartIcon,
				onClick = {
					onAction(ActiveRunAction.OnToggleRunClick)
				},
				iconSize = 20.dp,
				contentDescription = if (state.shouldTrack)
					stringResource(R.string.pause_run)
				else
					stringResource(R.string.start_run)
			)
		}
	) { padding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(color = MaterialTheme.colorScheme.surface)
		) {
			TrackerMap(
				isRunFinished = state.isRunFinished,
				currentLocation = state.currentLocation,
				locations = state.runData.locations,
				onSnapshot = { bitmap ->
					val stream = ByteArrayOutputStream()
					stream.use {
						bitmap.compress(
							/* format = */ Bitmap.CompressFormat.JPEG,
							/* quality = */ 80,
							/* stream = */ it
						)
					}
					onAction(ActiveRunAction.OnRunProcessed(stream.toByteArray()))

				},
				modifier = Modifier.fillMaxSize()
			)
			RunDataCard(
				elapsedTime = state.elapsedTime,
				runData = state.runData,
				modifier = Modifier
					.padding(16.dp)
					.padding(padding)
			)
		}

	}
	if (!state.shouldTrack && state.hasStartedRunning) {
		RuniqueDialog(
			title = stringResource(R.string.running_is_paused),
			onDismiss = {
				onAction(ActiveRunAction.OnResumeRunClick)
			},
			description = stringResource(R.string.resume_or_finish_run),
			primaryButton = {
				RuniqueActionButton(
					text = stringResource(R.string.resume),
					isLoading = false,
					onClick = {
						onAction(ActiveRunAction.OnResumeRunClick)
					},
					modifier = Modifier.weight(1f)
				)
			},
			secondaryButton = {
				RuniqueOutlinedActionButton(
					text = stringResource(R.string.finish),
					isLoading = state.isSavingRun,
					onClick = {
						onAction(ActiveRunAction.OnFinishRunClick)
					},
					modifier = Modifier.weight(1f)
				)
			}
		)
	}
	if (state.showLocationRotational || state.showNotificationRotational) {
		RuniqueDialog(
			title = stringResource(R.string.permission_required),
			description = when {
				state.showLocationRotational && state.showNotificationRotational -> stringResource(R.string.location_and_notification_permission_required)
				state.showLocationRotational -> stringResource(R.string.location_permission_required)
				else -> stringResource(R.string.notification_permission_required)
			},
			onDismiss = {},
			primaryButton = {
				RuniqueOutlinedActionButton(
					text = stringResource(R.string.okay),
					isLoading = false,
					onClick = {
						onAction(ActiveRunAction.DismissPermissionRotationalDialog)
					}
				)
			}
		)
	}
}

/**
 * Extension function for `ActivityResultLauncher` to request location and notification permissions.
 *
 * @param context The context from which the permissions are requested.
 */
private fun ActivityResultLauncher<Array<String>>.requestRuniquePermissions(
	context: Context,
) {
	val hasLocationPermission = context.hasLocationPermission()
	val hasNotificationPermission = context.hasNotificationPermission()
	val locationPermissions = arrayOf(
		Manifest.permission.ACCESS_FINE_LOCATION,
		Manifest.permission.ACCESS_COARSE_LOCATION
	)
	val notificationPermission = if (Build.VERSION.SDK_INT >= 33) {
		arrayOf(Manifest.permission.POST_NOTIFICATIONS)
	} else arrayOf()

	when {
		!hasLocationPermission && !hasNotificationPermission -> launch(locationPermissions + notificationPermission)
		!hasLocationPermission -> launch(locationPermissions)
		!hasNotificationPermission -> launch(notificationPermission)
	}
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
	RuniqueTheme {
		ActiveRunScreen(
			state = ActiveRunState(),
			onAction = {},
			onServiceToggle = {}
		)
	}
}
