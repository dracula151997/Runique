package com.dracula.core.database

import androidx.room.Room
import com.dracula.core.database.dao.RunDatabase
import com.dracula.core.domain.run.LocalRunDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
	single {
		Room.databaseBuilder(
			androidApplication(),
			RunDatabase::class.java,
			RunDatabase.DATABASE_NAME
		).build()
	}
	single { get<RunDatabase>().runDao }
	singleOf(::RoomLocalRunDataSource).bind<LocalRunDataSource>()
}