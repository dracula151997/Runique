package com.dracula.run.data.di

import androidx.work.Worker
import com.dracula.core.domain.run.LocalRunDataSource
import com.dracula.core.domain.run.RunRepository
import com.dracula.run.data.CreateRunWorker
import com.dracula.run.data.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val runDataModule = module {
	workerOf(::CreateRunWorker)
	workerOf(::FetchRunsWorker)
}