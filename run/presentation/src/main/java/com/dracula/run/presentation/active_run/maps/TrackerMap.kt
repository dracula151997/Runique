package com.dracula.run.presentation.active_run.maps

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dracula.core.domain.location.Location
import com.dracula.core.domain.location.LocationTimestamp
import com.dracula.core.presentation.designsystem.RunIcon
import com.dracula.run.presentation.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.ktx.awaitSnapshot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(MapsComposeExperimentalApi::class)
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
		remember(marketPositionLat, marketPositionLong) {
			LatLng(
				marketPositionLat.toDouble(),
				marketPositionLong.toDouble()
			)
		}
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
	var triggerCapture by remember { mutableStateOf(false) }
	var createdSnapshotJob: Job? = remember { null }
	GoogleMap(
		properties = MapProperties(
			mapStyleOptions = mapStyle,
		),
		uiSettings = MapUiSettings(
			zoomControlsEnabled = false
		),
		cameraPositionState = cameraPositionState,
		modifier = if (isRunFinished) modifier
			.width(300.dp)
			.aspectRatio(16f / 9f)
			.alpha(0f)
			.onSizeChanged {
				if (it.width >= 300) {
					triggerCapture = true
				}
			} else modifier
	) {
		RuniquePolylines(locations = locations)
		MapEffect(locations, isRunFinished, triggerCapture) { map ->
			if (isRunFinished && triggerCapture && createdSnapshotJob == null) {
				triggerCapture = false

				val boundsBuilder = LatLngBounds.builder()
				locations.flatten().forEach { locations ->
					boundsBuilder.include(
						LatLng(
							locations.location.location.latitude,
							locations.location.location.longitude
						)
					)
				}
				map.moveCamera(
					CameraUpdateFactory.newLatLngBounds(
						/* bounds = */ boundsBuilder.build(),
						/* padding = */ 100
					)
				)
				map.setOnCameraIdleListener {
					createdSnapshotJob?.cancel()
					createdSnapshotJob = GlobalScope.launch {
						delay(500)
						map.awaitSnapshot()?.let(onSnapshot)
					}
				}
			}


		}
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