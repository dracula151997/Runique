package com.dracula.run.network.di

import com.dracula.core.domain.run.RemoteRunDataSource
import com.dracula.run.network.KtorRemoteRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
	singleOf(::KtorRemoteRunDataSource).bind<RemoteRunDataSource>()

}