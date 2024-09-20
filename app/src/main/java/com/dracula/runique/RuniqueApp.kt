package com.dracula.runique

import android.app.Application
import com.dracula.auth.data.authDataModule
import com.dracula.auth.presentation.di.authViewModelModule
import com.dracula.core.di.coreDataModule
import com.dracula.run.presentation.di.runViewModelModule
import com.dracula.runique.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp : Application(){
	override fun onCreate() {
		super.onCreate()
		if (BuildConfig.DEBUG){
			Timber.plant(Timber.DebugTree())
		}
		startKoin {
			androidLogger()
			androidContext(this@RuniqueApp)
			modules(
				appModule,
				authDataModule,
				authViewModelModule,
				coreDataModule,
				runViewModelModule
			)
		}
	}
}