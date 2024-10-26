package com.dracula.run.data.di

import com.dracula.core.domain.SyncRunScheduler
import com.dracula.run.data.CreateRunWorker
import com.dracula.run.data.DeleteRunWorker
import com.dracula.run.data.FetchRunsWorker
import com.dracula.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
	workerOf(::CreateRunWorker)
	workerOf(::FetchRunsWorker)
	workerOf(::DeleteRunWorker)

	singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}