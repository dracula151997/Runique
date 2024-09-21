package com.dracula.run.presentation.run_overview

import androidx.lifecycle.ViewModel

class RunOverviewViewModel : ViewModel() {
	fun onAction(action: RunOverviewAction) {
		when (action) {
			RunOverviewAction.OnLogoutClick -> TODO()
			RunOverviewAction.OnAnalyticsClick -> TODO()
			else -> Unit
		}
	}

	private fun onStartClick() {

	}
}