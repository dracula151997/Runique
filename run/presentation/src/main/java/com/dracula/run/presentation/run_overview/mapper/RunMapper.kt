package com.dracula.run.presentation.run_overview.mapper

import com.dracula.core.domain.run.Run
import com.dracula.core.presentation.ui.formatted
import com.dracula.core.presentation.ui.toFormattedKm
import com.dracula.core.presentation.ui.toFormattedKmh
import com.dracula.core.presentation.ui.toFormattedMeters
import com.dracula.core.presentation.ui.toFormattedPace
import com.dracula.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toRunUi(): RunUi {
	val dateTimeLocalTime = dateTimeUtc.withZoneSameInstant(ZoneId.systemDefault())
	val formattedDateTime =
		dateTimeLocalTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mma"))
	val distanceKm = distanceInMeters / 1000.0
	return RunUi(
		id = id!!,
		duration = duration.formatted(),
		dateTime = formattedDateTime,
		distance = distanceKm.toFormattedKm(),
		avgSpeed = avgSpeedKmh.toFormattedKmh(),
		maxSpeedInKm = maxSpeedKmh.toFormattedKmh(),
		pace = duration.toFormattedPace(distanceKm),
		tonalElevation = totalElevationMeters.toFormattedMeters(),
		mapPictureUrl = mapPictureUrl
	)
}