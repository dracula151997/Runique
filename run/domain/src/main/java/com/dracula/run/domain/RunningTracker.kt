package com.dracula.run.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class RunningTracker(
	private val locationObserver: LocationObserver,
	private val applicationScope: CoroutineScope,
) {
	private val isObservingLocation = MutableStateFlow(false)
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

	fun startObservingLocation() {
		isObservingLocation.value = true
	}

	fun stopObservingLocation() {
		isObservingLocation.value = false
	}
}