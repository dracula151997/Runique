package com.dracula.runique

import android.app.Application
import com.dracula.auth.data.authDataModule
import com.dracula.auth.presentation.di.authViewModelModule
import com.dracula.core.di.coreDataModule
import com.dracula.run.location.di.locationModule
import com.dracula.run.presentation.di.runPresentationModule
import com.dracula.runique.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp : Application(){
	val applicationScope = CoroutineScope(SupervisorJob())
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
				runPresentationModule,
				locationModule
			)
		}
	}
}