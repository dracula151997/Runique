package com.dracula.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.dracula.core.domain.location.LocationTimestamp
import kotlin.math.abs

object PolylineColorCalculator {
	fun locationToColor(
		location1: LocationTimestamp,
		location2: LocationTimestamp,
	): Color {
		val distanceMeters = location1.location.location.distanceTo(location2.location.location)
		val timeDiff =
			abs((location1.durationTimestamp - location2.durationTimestamp).inWholeSeconds)
		val speedKmh = (distanceMeters / timeDiff) * 3.6
		return interpolateColor(
			speedKmh,
			0.5,
			20.0,
			Color.Green,
			Color.Yellow,
			Color.Red,
		)
	}

	private fun interpolateColor(
		speedKm: Double,
		minSpeed: Double,
		maxSpeed: Double,
		colorStart: Color,
		colorMid: Color,
		colorEnd: Color,
	): Color {
		val ratio = (speedKm - minSpeed) / (maxSpeed - minSpeed).coerceIn(0.0, 1.0)
		val colorInt = when {
			ratio < 0.5 -> {
				val midRatio = ratio / 2
				ColorUtils.blendARGB(colorStart.toArgb(), colorMid.toArgb(), midRatio.toFloat())
			}

			else -> {
				val ratioScaled = (ratio - 0.5) / 0.5
				ColorUtils.blendARGB(colorMid.toArgb(), colorEnd.toArgb(), ratioScaled.toFloat())
			}
		}
		return Color(colorInt)
	}
}