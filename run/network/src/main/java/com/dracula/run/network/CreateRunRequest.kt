package com.dracula.run.network

import kotlinx.serialization.Serializable

@Serializable
data class CreateRunRequest(
	val durationMillis: Long,
	val distanceMeters: Int,
	val epochMillis: Long,
	val latitude: Double,
	val longitude: Double,
	val avgSpeedKmh: Double,
	val maxSpeedKmh: Double,
	val totalElevationMeters: Int,
	val id: String,
)