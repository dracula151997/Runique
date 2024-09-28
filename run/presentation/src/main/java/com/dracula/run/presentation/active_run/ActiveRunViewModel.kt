package com.dracula.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dracula.run.domain.RunningTracker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber

class ActiveRunViewModel(
	private val runningTracker: RunningTracker,
) : ViewModel() {
	var state by mutableStateOf(ActiveRunState())
		private set

	private val eventChannel = Channel<ActiveRunEvent>()
	val events = eventChannel.receiveAsFlow()

	private val _hasLocationPermission = MutableStateFlow(false)
	val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission

	init {
		_hasLocationPermission.onEach { hasPermission ->
			if (hasPermission) {
				runningTracker.startObservingLocation()
			} else {
				runningTracker.stopObservingLocation()
			}

		}.launchIn(viewModelScope)

		runningTracker.currentLocation
			.onEach { newLocation ->
				Timber.d("Location: $newLocation")
			}.launchIn(viewModelScope)
	}


	fun onAction(action: ActiveRunAction) {
		when (action) {
			ActiveRunAction.OnBackClick -> TODO()
			ActiveRunAction.OnFinishRunClick -> TODO()
			ActiveRunAction.OnResumeRunClick -> TODO()
			ActiveRunAction.OnToggleRunClick -> TODO()
			is ActiveRunAction.SubmitLocationPermissionInfo -> {
				_hasLocationPermission.value = action.acceptedLocationPermission
				state = state.copy(
					showLocationRotational = action.shouldShowLocationPermissionRotational
				)
			}

			is ActiveRunAction.SubmitNotificationPermissionInfo -> state = state.copy(
				showNotificationRotational = action.shouldShowNotificationPermissionRotational
			)

			ActiveRunAction.DismissPermissionRotationalDialog -> state = state.copy(
				showLocationRotational = false,
				showNotificationRotational = false
			)
		}
	}
}