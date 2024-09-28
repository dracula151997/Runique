package com.dracula.run.location.di

import com.dracula.run.domain.LocationObserver
import com.dracula.run.location.AndroidLocationObserver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val locationModule = module {
	singleOf(::AndroidLocationObserver).bind<LocationObserver>()
}