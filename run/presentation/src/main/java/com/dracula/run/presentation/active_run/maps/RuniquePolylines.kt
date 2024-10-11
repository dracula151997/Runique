package com.dracula.run.presentation.active_run.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dracula.core.domain.location.LocationTimestamp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline

@Composable
fun RuniquePolylines(
	locations: List<List<LocationTimestamp>>,
	modifier: Modifier = Modifier,
) {
	val polylines = remember(locations) {
		locations.map { locations ->
			locations.zipWithNext { location1, location2 ->
				PolylineUi(
					location1 = location1.location.location,
					location2 = location2.location.location,
					color = PolylineColorCalculator.locationToColor(location1, location2),
				)
			}
		}
	}
	polylines.forEach { polylinesUi ->
		polylinesUi.forEach { polylineUi ->
			Polyline(
				points = listOf(
					LatLng(polylineUi.location1.latitude, polylineUi.location1.longitude),
					LatLng(polylineUi.location2.latitude, polylineUi.location2.longitude),
				),
				color = polylineUi.color,

			)
		}
	}
}