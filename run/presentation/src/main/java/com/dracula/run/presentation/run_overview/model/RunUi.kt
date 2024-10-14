package com.dracula.run.presentation.run_overview.model

data class RunUi(
	val id: String,
	val duration: String,
	val dateTime: String,
	val distance: String,
	val avgSpeed: String,
	val maxSpeedInKm: String,
	val pace: String,
	val tonalElevation: String,
	val mapPictureUrl: String?
)
