package com.dracula.analytics.data.di

import com.dracula.analytics.data.RoomAnalyticsRepository
import com.dracula.analytics.domain.AnalyticsRepository
import com.dracula.core.database.dao.RunDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsDataModule = module {
	singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
	single { get<RunDatabase>().analyticsDao }
}