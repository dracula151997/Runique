package com.dracula.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dracula.core.domain.run.RunRepository
import com.dracula.run.presentation.run_overview.mapper.toRunUi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RunOverviewViewModel(
	private val runRepository: RunRepository,
) : ViewModel() {
	var state by mutableStateOf(RunOverviewState())
		private set

	init {
		runRepository.getRuns()
			.onEach { runs ->
				val runUi = runs.map { it.toRunUi() }
				state = state.copy(runs = runUi)
			}.launchIn(viewModelScope)

		viewModelScope.launch {
			runRepository.fetchRuns()
		}
	}

	fun onAction(action: RunOverviewAction) {
		when (action) {
			is RunOverviewAction.DeleteRun -> {
				viewModelScope.launch {
					runRepository.deleteRun(action.runUi.id)
				}
			}
			RunOverviewAction.OnLogoutClick -> Unit
			RunOverviewAction.OnStartClick -> Unit
			else -> {}
		}
	}

	private fun onStartClick() {

	}
}