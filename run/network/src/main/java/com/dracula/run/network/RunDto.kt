package com.dracula.run.network

import kotlinx.serialization.Serializable

@Serializable
data class RunDto(
	val id: String,
	val dateTimeUtc: String,
	val durationMillis: Long,
	val distanceMeters: Int,
	val latitude: Double,
	val longitude: Double,
	val avgSpeedKmh: Double,
	val maxSpeedKmh: Double,
	val totalElevation: Int,
	val mapPictureUrl: String?,

	)