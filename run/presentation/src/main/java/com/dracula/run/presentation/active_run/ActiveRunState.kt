package com.dracula.run.presentation.active_run

import com.dracula.core.domain.location.Location
import com.dracula.run.domain.RunData
import kotlin.time.Duration

data class ActiveRunState(
	val elapsedTime: Duration = Duration.ZERO,
	val shouldTrack: Boolean = false,
	val hasStartedRunning: Boolean = false,
	val currentLocation: Location? = null,
	val isRunFinished: Boolean = false,
	val isSavingRun: Boolean = false,
	val runData: RunData = RunData()

)