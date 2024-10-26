package com.dracula.run.network

import com.dracula.core.domain.location.Location
import com.dracula.core.domain.run.Run
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.milliseconds

fun RunDto.toRun(): Run {
	return Run(
		id = id,
		duration = durationMillis.milliseconds,
		distanceInMeters = distanceMeters,
		location = Location(latitude = latitude, longitude = longitude),
		totalElevationMeters = totalElevation,
		dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneOffset.UTC),
		maxSpeedKmh = maxSpeedKmh,
		mapPictureUrl = mapPictureUrl,
	)
}

fun Run.toRunDto(): RunDto {
	return RunDto(
		id = id!!,
		dateTimeUtc = dateTimeUtc.toString(),
		durationMillis = duration.inWholeMilliseconds,
		latitude = location.latitude,
		longitude = location.longitude,
		avgSpeedKmh = avgSpeedKmh,
		maxSpeedKmh = maxSpeedKmh,
		totalElevation = totalElevationMeters,
		distanceMeters = distanceInMeters,
		mapPictureUrl = mapPictureUrl,
	)
}

fun Run.toCreateRunRequest(): CreateRunRequest {
	return CreateRunRequest(
		durationMillis = duration.inWholeMilliseconds,
		distanceMeters = distanceInMeters,
		epochMillis = dateTimeUtc.toEpochSecond() * 1000L,
		latitude = location.latitude,
		longitude = location.longitude,
		avgSpeedKmh = avgSpeedKmh,
		maxSpeedKmh = maxSpeedKmh,
		totalElevationMeters = totalElevationMeters,
		id = id!!
	)
}