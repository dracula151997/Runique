package com.dracula.analytics.presentation

import com.dracula.analytics.domain.AnalyticsValues
import com.dracula.core.presentation.ui.formatted
import com.dracula.core.presentation.ui.toFormattedKm
import com.dracula.core.presentation.ui.toFormattedKmh
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

/**
 * Extension function to convert a `Duration` object into a human-readable string format.
 *
 * @receiver Duration The duration to be converted.
 * @return String A string representing the duration in the format "Xd, Yh, Zm".
 */
fun Duration.toHumanReadableTime(): String {
    val days = toLong(DurationUnit.DAYS)
    val hours = toLong(DurationUnit.HOURS) % 24
    val minutes = toLong(DurationUnit.MINUTES) % 60
    return "$days d, $hours h, $minutes m"
}

fun AnalyticsValues.toAnalyticsDashboardState(): AnalyticsDashboardState {
	return AnalyticsDashboardState(
		totalDistanceRun = (totalDistanceRun / 1000.0).toFormattedKm(),
		totalTimeRun = totalTimeRun.toHumanReadableTime(),
		fastestEverRun = fastestEverRun.toFormattedKm(),
		avgDistance = avgDistancePerRun.toFormattedKm(),
		avgPace = avgPacePerRun.seconds.formatted()

	)
}