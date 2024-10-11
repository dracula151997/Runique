package com.dracula.run.presentation.active_run.maps

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dracula.core.domain.location.Location
import com.dracula.core.domain.location.LocationTimestamp
import com.dracula.core.presentation.designsystem.RunIcon
import com.dracula.run.presentation.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import timber.log.Timber

@Composable
fun TrackerMap(
	isRunFinished: Boolean,
	currentLocation: Location?,
	locations: List<List<LocationTimestamp>>,
	onSnapshot: (Bitmap) -> Unit,
	modifier: Modifier = Modifier,
) {
	val context = LocalContext.current
	val mapStyle = remember {
		MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
	}
	val cameraPositionState = rememberCameraPositionState()
	val markerState = MarkerState()

	val marketPositionLat by animateFloatAsState(
		targetValue = currentLocation?.latitude?.toFloat() ?: 0f,
		label = "marketPositionLatAnimation",
		animationSpec = tween(durationMillis = 500)
	)
	val marketPositionLong by animateFloatAsState(
		targetValue = currentLocation?.longitude?.toFloat() ?: 0f,
		label = "marketPositionLanAnimation",
		animationSpec = tween(durationMillis = 500)
	)
	val marketPosition =
		remember(marketPositionLat, marketPositionLong) { LatLng(marketPositionLat.toDouble(), marketPositionLong.toDouble()) }

	LaunchedEffect(marketPosition, isRunFinished) {
		if (!isRunFinished) {
			markerState.position = marketPosition
		}
	}

	LaunchedEffect(currentLocation, isRunFinished) {
		if (currentLocation != null && !isRunFinished) {
			val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
			cameraPositionState.animate(
				CameraUpdateFactory.newLatLngZoom(latLng, 17f)
			)
		}
	}

	GoogleMap(
		properties = MapProperties(
			mapStyleOptions = mapStyle,
		),
		uiSettings = MapUiSettings(
			zoomControlsEnabled = false
		),
		cameraPositionState = cameraPositionState,
	) {
		RuniquePolylines(locations = locations)
		if (!isRunFinished && currentLocation != null) {
			MarkerComposable(
				currentLocation,
				state = markerState,
			) {
				Box(
					modifier = Modifier
						.size(35.dp)
						.clip(CircleShape)
						.background(MaterialTheme.colorScheme.primary),
					contentAlignment = Alignment.Center
				) {
					Icon(
						imageVector = RunIcon,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.onPrimary,
						modifier = Modifier.size(20.dp)
					)
				}
			}

		}
	}
}