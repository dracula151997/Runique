package com.dracula.run.domain

import com.dracula.core.domain.location.Location
import com.dracula.core.domain.location.LocationTimestamp
import kotlin.time.Duration

/**
 * Data class representing the data of a run.
 *
 * @property distanceMeters The distance of the run in meters. Defaults to 0.
 * @property pace The pace of the run as a [Duration]. Defaults to [Duration.ZERO]. Pace is the time taken to cover a certain distance, typically measured in minutes per kilometer or mile.
 * @property locations A list of lists of [Location] objects representing the locations of the run. Defaults to an empty list.
 */
data class RunData(
	val distanceMeters: Int = 0,
	val pace: Duration = Duration.ZERO,
	val locations: List<List<LocationTimestamp>> = emptyList(),
)