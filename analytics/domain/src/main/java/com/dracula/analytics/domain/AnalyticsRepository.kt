package com.dracula.analytics.domain

interface AnalyticsRepository {
	suspend fun getAnalyticsValues(): AnalyticsValues
}