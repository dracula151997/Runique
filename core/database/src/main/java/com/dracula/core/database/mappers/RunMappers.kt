package com.dracula.core.database.mappers

import com.dracula.core.database.entity.RunEntity
import com.dracula.core.domain.location.Location
import com.dracula.core.domain.run.Run
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.milliseconds

fun RunEntity.toRun() = Run(
	id = id,
	duration = durationMillis.milliseconds,
	dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneOffset.UTC),
	distanceInMeters = distanceMeters,
	location = Location(latitude = latitude, longitude = longitude),
	maxSpeedKmh = maxSpeedKmh,
	totalElevationMeters = totalElevationMeters,
	mapPictureUrl = mapPictureUrl,
)

fun Run.toRunEntity() = RunEntity(
	id = id ?: ObjectId().toHexString(),
	durationMillis = duration.inWholeMilliseconds,
	distanceMeters = distanceInMeters,
	dateTimeUtc = dateTimeUtc.toInstant().toString(),
	latitude = location.latitude,
	longitude = location.longitude,
	avgSpeedKmh = avgSpeedKmh,
	maxSpeedKmh = maxSpeedKmh,
	totalElevationMeters = totalElevationMeters,
	mapPictureUrl = mapPictureUrl,
)