package com.dracula.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dracula.core.domain.SessionStorage
import com.dracula.core.domain.SyncRunScheduler
import com.dracula.core.domain.run.RunRepository
import com.dracula.run.presentation.run_overview.mapper.toRunUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class RunOverviewViewModel(
	private val runRepository: RunRepository,
	private val syncRunScheduler: SyncRunScheduler,
	private val applicationScope: CoroutineScope,
	private val sessionStorage: SessionStorage,
) : ViewModel() {
	var state by mutableStateOf(RunOverviewState())
		private set

	init {
		viewModelScope.launch {
			syncRunScheduler.scheduleSync(
				syncType = SyncRunScheduler.SyncType.FetchRuns(interval = 30.minutes)
			)
		}
		runRepository.getRuns()
			.onEach { runs ->
				val runsUi = runs.map { it.toRunUi() }
				state = state.copy(runs = runsUi)
			}.launchIn(viewModelScope)

		viewModelScope.launch {
			runRepository.syncPendingRuns()
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

			RunOverviewAction.OnLogoutClick -> logout()
			RunOverviewAction.OnStartClick -> Unit
			else -> {}
		}
	}

	private fun logout() {
		applicationScope.launch {
			syncRunScheduler.cancelAllSyncs()
			runRepository.deleteAllRuns()
			runRepository.logout()
			sessionStorage.set(null)
		}
	}
}