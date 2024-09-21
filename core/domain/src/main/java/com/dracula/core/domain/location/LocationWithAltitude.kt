package com.dracula.core.domain.location

/**
 * Data class representing a location with an associated altitude.
 *
 * @property location The [Location] object representing the geographical location.
 * @property altitude The altitude of the location in meters. Altitude is the height of the location above sea level.
 */
data class LocationWithAltitude(
	val location: Location,
	val altitude: Double,
)