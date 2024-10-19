package com.dracula.run.presentation.run_overview

import com.dracula.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
	val runs: List<RunUi> = emptyList()
)