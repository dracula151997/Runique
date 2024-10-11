package com.dracula.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.dracula.core.domain.location.LocationTimestamp
import kotlin.math.abs

object PolylineColorCalculator {
	/**
	 * Determines the color of a polyline based on the speed between two locations.
	 *
	 * The color is determined by interpolating between three colors:
	 * - Green: Represents a speed of 0.5 km/h or less.
	 * - Yellow: Represents a speed of 10 km/h.
	 * - Red: Represents a speed of 20 km/h or more.
	 *
	 * @param location1 The first location.
	 * @param location2 The second location.
	 * @return The color representing the speed between the two locations.
	 */
	fun locationToColor(
		location1: LocationTimestamp,
		location2: LocationTimestamp,
	): Color {
		val distanceMeters = location1.location.location.distanceTo(location2.location.location)
		val timeDiff =
			abs((location1.durationTimestamp - location2.durationTimestamp).inWholeSeconds)
		val speedKmh = (distanceMeters / timeDiff) * 3.6
		return interpolateColor(
			speedKm = speedKmh,
			minSpeed = 0.5,
			maxSpeed = 20.0,
			colorStart = Color.Green,
			colorMid = Color.Yellow,
			colorEnd = Color.Red,
		)
	}

	/**
	 * Interpolates a color based on the speed between two points.
	 *
	 * This function determines the color to represent a speed value by interpolating between three colors:
	 * - `colorStart`: The color representing the minimum speed.
	 * - `colorMid`: The color representing the midpoint speed.
	 * - `colorEnd`: The color representing the maximum speed.
	 *
	 * The interpolation is done in two stages:
	 * 1. If the speed is less than the midpoint speed, the color is interpolated between `colorStart` and `colorMid`.
	 * 2. If the speed is greater than or equal to the midpoint speed, the color is interpolated between `colorMid` and `colorEnd`.
	 *
	 * @param speedKm The speed in km/h.
	 * @param minSpeed The minimum speed for the color range.
	 * @param maxSpeed The maximum speed for the color range.
	 * @param colorStart The color representing the minimum speed.
	 * @param colorMid The color representing the midpoint speed.
	 * @param colorEnd The color representing the maximum speed.
	 * @return The interpolated color based on the speed.
	 */
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