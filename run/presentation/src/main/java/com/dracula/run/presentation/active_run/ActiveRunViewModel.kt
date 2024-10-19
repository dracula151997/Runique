package com.dracula.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dracula.core.domain.location.Location
import com.dracula.core.domain.run.Run
import com.dracula.core.domain.run.RunRepository
import com.dracula.core.domain.utils.Result
import com.dracula.core.presentation.ui.asUiText
import com.dracula.run.domain.LocationDataCalculator
import com.dracula.run.domain.RunningTracker
import com.dracula.run.presentation.active_run.service.ActiveRunService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime

class ActiveRunViewModel(
	private val runningTracker: RunningTracker,
	private val runRepository: RunRepository,
) : ViewModel() {
	var state by mutableStateOf(
		ActiveRunState(
			shouldTrack = ActiveRunService.isServiceRunning && runningTracker.isTracking.value,
			hasStartedRunning = ActiveRunService.isServiceRunning
		)
	)
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
					isRunFinished = true,
					isSavingRun = true
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

			is ActiveRunAction.OnRunProcessed -> finishRun(action.mapPictureByteArray)
		}
	}

	private fun finishRun(mapPictureBytes: ByteArray) {
		val locations = state.runData.locations
		if (locations.isEmpty() || locations.first().size <= 1) {
			state = state.copy(
				isSavingRun = false
			)
			return
		}
		viewModelScope.launch {
			val run = Run(
				id = null,
				duration = state.elapsedTime,
				dateTimeUtc = ZonedDateTime.now()
					.withZoneSameInstant(ZoneId.of("UTC")),
				distanceInMeters = state.runData.distanceMeters,
				location = state.currentLocation ?: Location(0.0, 0.0),
				maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(locations = locations),
				totalElevationMeters = LocationDataCalculator.getTotalElevationMeters(locations = locations),
				mapPictureUrl = null
			)
			runningTracker.finishRun()
			//TODO: save run in the repository
			when (val result = runRepository.upsertRun(run, mapPictureBytes)) {
				is Result.Error -> eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
				is Result.Success -> {
					eventChannel.send(ActiveRunEvent.RunSaved)
				}
			}


			state = state.copy(
				isSavingRun = false
			)

		}
	}

	override fun onCleared() {
		super.onCleared()
		if (!ActiveRunService.isServiceRunning) {
			runningTracker.stopObservingLocation()
		}
	}
}