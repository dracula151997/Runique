package com.dracula.core.di

import com.dracula.core.data.auth.EncryptedSessionStorage
import com.dracula.core.domain.SessionStorage
import com.dracula.core.networking.HttpClientFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
	single {
		HttpClientFactory(get()).build()
	}
	singleOf(::EncryptedSessionStorage).bind<SessionStorage>()

}