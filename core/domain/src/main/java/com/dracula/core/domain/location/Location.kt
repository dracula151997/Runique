package com.dracula.core.domain.location

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Location(
	val latitude: Double,
	val longitude: Double,
) {
	/**
	 * Calculates the distance in meters between this location and the specified location.
	 *
	 * This function uses the Haversine formula to calculate the great-circle distance between
	 * two points on the Earth's surface, given their latitude and longitude in degrees.
	 *
	 * The Haversine formula is:
	 * a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
	 * c = 2 ⋅ atan2( √a, √(1−a) )
	 * d = R ⋅ c
	 * where:
	 * - φ1 and φ2 are the latitudes of the two points in radians,
	 * - Δφ is the difference between the latitudes,
	 * - Δλ is the difference between the longitudes,
	 * - R is the Earth's radius (mean radius = 6,371,000 meters).
	 *
	 * @param other The other location to which the distance is calculated.
	 * @return The distance in meters between this location and the specified location.
	 *
	 * @sample
	 * val loc1 = Location(52.5200, 13.4050) // Berlin
	 * val loc2 = Location(48.8566, 2.3522)  // Paris
	 * val distance = loc1.distanceTo(loc2)
	 * // distance: 878,455.5 meters (approximately)
	 *
	 * @sample
	 * val loc1 = Location(34.0522, -118.2437) // Los Angeles
	 * val loc2 = Location(36.1699, -115.1398) // Las Vegas
	 * val distance = loc1.distanceTo(loc2)
	 * // distance: 367,603.2 meters (approximately)
	 */
	fun distanceTo(other: Location): Float {
		val lat1 = Math.toRadians(latitude)
		val lon1 = Math.toRadians(longitude)
		val lat2 = Math.toRadians(other.latitude)
		val lon2 = Math.toRadians(other.longitude)

		val dLat = lat2 - lat1
		val dLon = lon2 - lon1

		val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
		val c = 2 * atan2(sqrt(a), sqrt(1 - a))

		return (EARTH_RADIUS * c).toFloat()
	}

	companion object {
		private const val EARTH_RADIUS = 6_371_000.0
	}
}