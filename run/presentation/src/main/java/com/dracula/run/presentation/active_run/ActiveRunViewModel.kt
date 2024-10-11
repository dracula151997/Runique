package com.dracula.run.presentation.active_run

import androidx.compose.material3.TimeInput
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dracula.run.domain.RunningTracker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
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

	/**
	 * A `StateFlow` that represents whether the run should be tracked.
	 *
	 * This flow is derived from the `state.shouldTrack` property using `snapshotFlow`, which captures
	 * the state of `shouldTrack` whenever it changes. The resulting flow is then converted to a `StateFlow`
	 * using `stateIn`, which ensures it has a consistent initial value and is scoped to the `viewModelScope`.
	 *
	 * The `SharingStarted.Lazily` parameter ensures that the flow starts collecting only when it is needed,
	 * and stops when it is no longer in use, conserving resources.
	 */
	private val shouldTrack = snapshotFlow { state.shouldTrack }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.Lazily,
			initialValue = state.shouldTrack
		)

	/**
	 * A `StateFlow` that represents whether the run is currently being tracked.
	 *
	 * This flow combines the `shouldTrack` and `hasLocationPermission` state flows. It emits `true` if both
	 * `shouldTrack` and `hasLocationPermission` are `true`, indicating that tracking should be active.
	 * The resulting flow is converted to a `StateFlow` using `stateIn`, ensuring it has a consistent initial
	 * value and is scoped to the `viewModelScope`.
	 *
	 * The `SharingStarted.Lazily` parameter ensures that the flow starts collecting only when it is needed,
	 * and stops when it is no longer in use, conserving resources.
	 */
	private val isTracking =
		combine(shouldTrack, hasLocationPermission) { shouldTrack, hasPermission ->
			shouldTrack && hasPermission
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.Lazily,
			initialValue = false
		)

	init {
		_hasLocationPermission.onEach { hasPermission ->
			if (hasPermission) {
				runningTracker.startObservingLocation()
			} else {
				runningTracker.stopObservingLocation()
			}
		}.launchIn(viewModelScope)

		isTracking
			.onEach { isTracking ->
				if (isTracking) {
					runningTracker.startTracking()
				} else {
					runningTracker.stopTracking()
				}
			}.launchIn(viewModelScope)

		runningTracker
			.currentLocation
			.onEach {
				state = state.copy(currentLocation = it?.location)
			}.launchIn(viewModelScope)

		runningTracker
			.runData
			.onEach {
				state = state.copy(runData = it)
			}.launchIn(viewModelScope)

		runningTracker
			.elapsedTime
			.onEach {
				state = state.copy(elapsedTime = it)
			}.launchIn(viewModelScope)


	}


	fun onAction(action: ActiveRunAction) {
		when (action) {
			ActiveRunAction.OnBackClick -> {
				state = state.copy(shouldTrack = false)
			}

			ActiveRunAction.OnFinishRunClick -> {
				state = state.copy(
					shouldTrack = false,
					hasStartedRunning = false
				)
			}


			ActiveRunAction.OnResumeRunClick -> {
				state = state.copy(
					shouldTrack = true
				)
			}

			ActiveRunAction.OnToggleRunClick -> {
				state = state.copy(
					hasStartedRunning = true,
					shouldTrack = !state.shouldTrack
				)
			}

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