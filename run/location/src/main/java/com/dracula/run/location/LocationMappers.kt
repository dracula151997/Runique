package com.dracula.run.location

import android.location.Location
import com.dracula.core.domain.location.LocationWithAltitude

fun Location.toLocationWithAltitude(): LocationWithAltitude {
	return LocationWithAltitude(
		location = com.dracula.core.domain.location.Location(
			latitude = latitude,
			longitude = longitude
		),
		altitude = altitude
	)
}