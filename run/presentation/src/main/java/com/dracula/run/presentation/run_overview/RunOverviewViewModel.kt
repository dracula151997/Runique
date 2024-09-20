package com.dracula.run.presentation.run_overview

import android.view.View
import androidx.lifecycle.ViewModel

class RunOverviewViewModel : ViewModel(){
	fun onAction(action: RunOverviewAction){
		when(action){
			RunOverviewAction.OnStartClick -> TODO()
			RunOverviewAction.OnLogoutClick -> TODO()
			RunOverviewAction.OnAnalyticsClick -> TODO()
		}
	}

	private fun onStartClick(){

	}
}