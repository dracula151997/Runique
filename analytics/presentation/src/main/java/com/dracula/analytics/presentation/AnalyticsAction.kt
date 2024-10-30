package com.dracula.analytics.presentation

sealed interface AnalyticsAction {
	data object OnBackClick: AnalyticsAction
}