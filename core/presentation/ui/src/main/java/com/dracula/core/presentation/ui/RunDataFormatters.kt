package com.dracula.core.presentation.ui

import android.annotation.SuppressLint
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration

@SuppressLint("DefaultLocale")
		/**
		 * Formats the duration as a string in the format "HH:mm:ss".
		 *
		 * @return A string representing the duration in hours, minutes, and seconds.
		 */
fun Duration.formatted(): String {
	val totalSeconds = inWholeSeconds
	val hours = String.format("%02d", totalSeconds / 3600)
	val minutes = String.format("%02d", (totalSeconds % 3600) / 60)
	val seconds = String.format("%02d", totalSeconds % 60)
	return "$hours:$minutes:$seconds"
}

/**
 * Formats the duration as a pace string in the format "mm:ss min/km".
 *
 * @param distanceKm The distance in kilometers. If the distance is less than or equal to 0.0, or the duration is zero, the function returns "-".
 * @return A string representing the average pace in minutes per kilometer.
 */
@SuppressLint("DefaultLocale")
fun Duration.toFormattedPace(distanceKm: Double): String {
	if (this == Duration.ZERO || distanceKm <= 0.0) {
		return "-"
	}
	val secondsPerKm = (this.inWholeSeconds / distanceKm).roundToInt()
	val avgPaceMinutes = secondsPerKm / 60
	val avgPaceSeconds = String.format("%02d", secondsPerKm % 60)
	return "$avgPaceMinutes:$avgPaceSeconds min/km"
}

/**
 * Extension function to format a `Double` value representing kilometers to a string with one decimal place.
 *
 * @receiver Double The distance in kilometers to be formatted.
 * @return String A string representing the distance in kilometers with one decimal place.
 */
fun Double.toFormattedKm(): String {
	return "${this.roundToDecimals(1)} km"
}

fun Int.toFormattedMeters(): String {
	return "$this m"
}

/**
 * Extension function to format a `Double` value representing speed in kilometers per hour to a string with one decimal place.
 *
 * @receiver Double The speed in kilometers per hour to be formatted.
 * @return String A string representing the speed in kilometers per hour with one decimal place.
 */
fun Double.toFormattedKmh(): String {
    return "${roundToDecimals(1)} km/h"
}

private fun Double.roundToDecimals(decimalCount: Int): Double {
	val factor = 10f.pow(decimalCount)
	return round(this * factor) / factor
}