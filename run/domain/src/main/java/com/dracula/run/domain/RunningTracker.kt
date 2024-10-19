package com.dracula.run.domain

import com.dracula.core.domain.Timer
import com.dracula.core.domain.location.LocationTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class RunningTracker(
	private val locationObserver: LocationObserver,
	applicationScope: CoroutineScope,
) {
	private val _runData = MutableStateFlow(RunData())
	val runData: StateFlow<RunData> = _runData

	private val _isTracking = MutableStateFlow(false)
	val isTracking: StateFlow<Boolean> = _isTracking
	private val isObservingLocation = MutableStateFlow(false)

	private val _elapsedTime = MutableStateFlow(ZERO)
	val elapsedTime: StateFlow<Duration> = _elapsedTime

	/**
	 * A `StateFlow` that emits the current location if location observation is active.
	 *
	 * This flow observes the `isObservingLocation` state. When `isObservingLocation` is `true`,
	 * it starts observing the location updates from `locationObserver` with a specified interval.
	 * When `isObservingLocation` is `false`, it emits an empty flow.
	 *
	 * The resulting flow is converted to a `StateFlow` with lazy sharing, meaning it will start
	 * collecting only when there is an active collector, and it will stop when there are no collectors.
	 *
	 * @property currentLocation The `StateFlow` emitting the current location or `null` if not observing.
	 */
	val currentLocation = isObservingLocation
		.flatMapLatest { isObserving ->
			if (isObserving) {
				locationObserver.observeLocation(1000)
			} else {
				flowOf()
			}
		}.stateIn(
			scope = applicationScope,
			started = SharingStarted.Lazily,
			initialValue = null
		)

	init {
		/**
		 * Observes the `isTracking` state and updates the elapsed time accordingly.
		 *
		 * This flow observes the `isTracking` state. When `isTracking` is `true`, it starts a timer that emits
		 * time intervals. These intervals are added to the `_elapsedTime` state flow, which represents the total
		 * elapsed time since tracking started. When `isTracking` is `false`, the flow emits no values.
		 *
		 * The resulting flow is launched in the provided `applicationScope`, ensuring it runs as long as the
		 * application is active.
		 */
		_isTracking
			.onEach { isTracking ->
				if (!isTracking) {
					val newList = buildList {
						addAll(runData.value.locations)
						add(emptyList<LocationTimestamp>())
					}.toList()
					_runData.update {
						it.copy(locations = newList)
					}
				}
			}
			.flatMapLatest { isTracking ->
				if (isTracking) {
					Timer.timeAndEmit()
				} else emptyFlow()
			}.onEach {
				_elapsedTime.value += it
			}.launchIn(applicationScope)

		/**
		 * Observes the current location and updates the run data accordingly.
		 *
		 * This flow combines the `currentLocation` and `isTracking` state flows. When `isTracking` is `true`,
		 * it emits the current location and creates a `LocationTimestamp` with the elapsed time. The resulting
		 * `LocationTimestamp` is then used to update the run data, including the total distance traveled and
		 * the average pace.
		 *
		 * The flow is launched in the provided `applicationScope`, ensuring it runs as long as the application is active.
		 */
		currentLocation
			.filterNotNull()
			.combineTransform(_isTracking) { location, isTracking ->
				if (isTracking) {
					emit(location)
				}
			}.zip(_elapsedTime) { location, elapsedTime ->
				LocationTimestamp(location = location, durationTimestamp = elapsedTime)
			}.onEach { location ->
				val currentLocations = runData.value.locations
				val lastLocationList = if (currentLocations.isNotEmpty()) {
					currentLocations.last() + location
				} else {
					listOf(location)
				}
				val newLocationsList = currentLocations.replaceLast(lastLocationList)
				val distanceMeter =
					LocationDataCalculator.getTotalDistanceInMeters(locations = newLocationsList)
				val distanceInKm = distanceMeter / 1000.0
				val currentDuration = location.durationTimestamp
				val avgSecondsPerKm = if (distanceInKm > 0) {
					(currentDuration.inWholeSeconds / distanceInKm).roundToInt()
				} else {
					0
				}
				_runData.update {
					RunData(
						distanceMeters = distanceMeter,
						pace = avgSecondsPerKm.seconds,
						locations = newLocationsList
					)
				}
			}.launchIn(applicationScope)
	}

	fun startObservingLocation() {
		isObservingLocation.value = true
	}

	fun startTracking() {
		_isTracking.value = true
	}

	fun stopTracking() {
		_isTracking.value = false
	}

	fun stopObservingLocation() {
		isObservingLocation.value = false
	}

	fun finishRun() {
		stopObservingLocation()
		stopTracking()
		_elapsedTime.value = ZERO
		_runData.value = RunData()
	}
}

/**
 * Replaces the last list in a list of lists with the provided replacement list.
 *
 * This function takes a list of lists and replaces the last list in the outer list
 * with the provided replacement list. If the outer list is empty, it returns a new
 * list containing only the replacement list.
 *
 * @param replacement The list to replace the last list in the outer list.
 * @return A new list of lists with the last list replaced by the replacement list.
 *
 * @sample
 * val originalList = listOf(listOf(1, 2), listOf(3, 4))
 * val replacementList = listOf(5, 6)
 * val result = originalList.replaceLast(replacementList)
 * // result: [[1, 2], [5, 6]]
 *
 * @sample
 * val emptyList = emptyList<List<Int>>()
 * val replacementList = listOf(5, 6)
 * val result = emptyList.replaceLast(replacementList)
 * // result: [[5, 6]]
 */
private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>> {
	if (isEmpty()) {
		return listOf(replacement)
	}
	return dropLast(1) + listOf(replacement)
}
