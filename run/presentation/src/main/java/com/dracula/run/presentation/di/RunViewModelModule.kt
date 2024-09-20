package com.dracula.run.presentation.di

import com.dracula.run.presentation.run_overview.RunOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val runViewModelModule = module {
	viewModelOf(::RunOverviewViewModel)
}