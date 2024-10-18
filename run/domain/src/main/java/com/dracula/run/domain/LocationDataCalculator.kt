package com.dracula.run.domain

import com.dracula.core.domain.location.LocationTimestamp
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

object LocationDataCalculator {
	/**
	 * Calculates the total distance in meters for a given list of location timestamps.
	 *
	 * This function takes a list of lists of `LocationTimestamp` objects, where each inner list represents
	 * a sequence of locations over time. It calculates the total distance traveled by summing up the distances
	 * between consecutive locations in each inner list.
	 *
	 * The distance between two locations is calculated using the `distanceTo` method of the `Location` class,
	 * which uses the Haversine formula to compute the great-circle distance between two points on the Earth's surface.
	 *
	 * @param locations A list of lists of `LocationTimestamp` objects, where each inner list represents a sequence of locations.
	 * @return The total distance in meters, rounded to the nearest integer.
	 *
	 * @sample
	 * val loc1 = Location(52.5200, 13.4050) // Berlin
	 * val loc2 = Location(48.8566, 2.3522)  // Paris
	 * val loc3 = Location(51.5074, -0.1278) // London
	 * val timestamp1 = LocationTimestamp(LocationWithAltitude(loc1, 34.0), Duration.ZERO)
	 * val timestamp2 = LocationTimestamp(LocationWithAltitude(loc2, 35.0), Duration.seconds(3600))
	 * val timestamp3 = LocationTimestamp(LocationWithAltitude(loc3, 36.0), Duration.seconds(7200))
	 * val totalDistance = LocationDataCalculator.getTotalDistanceInMeters(listOf(listOf(timestamp1, timestamp2, timestamp3)))
	 * // totalDistance: 1,316,000 meters (approximately)
	 */
	fun getTotalDistanceInMeters(locations: List<List<LocationTimestamp>>): Int {
		return locations
			.sumOf { timestampsPerLine ->
				timestampsPerLine.zipWithNext { location1, location2 ->
					location1.location.location.distanceTo(location2.location.location)
				}.sum().roundToInt()
			}
	}

	fun getMaxSpeedKmh(locations: List<List<LocationTimestamp>>): Double {
		return locations.maxOf { locationSet ->
			locationSet.zipWithNext { location1, location2 ->
				val distance = location1.location.location.distanceTo(
					other = location2.location.location
				)
				val hoursDifference = (location1.durationTimestamp - location2.durationTimestamp)
					.toDouble(DurationUnit.HOURS)

				if (hoursDifference == 0.0) {
					0.0
				} else {
					(distance / 1000.0) / hoursDifference
				}

			}.maxOrNull() ?: 0.0
		}
	}

	fun getTotalElevationMeters(locations: List<List<LocationTimestamp>>): Int {
		return locations.sumOf { locationSet ->
			locationSet.zipWithNext { location1, location2 ->
				val altitude1 = location1.location.altitude
				val altitude2 = location2.location.altitude
				(altitude2 - altitude1).coerceAtLeast(0.0)
			}.sum().roundToInt()
		}
	}

}