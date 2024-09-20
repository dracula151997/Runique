package com.dracula.auth.data

import com.dracula.auth.domain.AuthRepository
import com.dracula.auth.domain.PatternValidator
import com.dracula.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
	single<PatternValidator> { EmailPatternValidator }
	singleOf(::UserDataValidator)
	singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}