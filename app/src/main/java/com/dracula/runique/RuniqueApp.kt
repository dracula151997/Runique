package com.dracula.runique

import android.app.Application
import android.content.Context
import com.dracula.auth.data.authDataModule
import com.dracula.auth.presentation.di.authViewModelModule
import com.dracula.core.database.databaseModule
import com.dracula.core.di.coreDataModule
import com.dracula.run.data.di.runDataModule
import com.dracula.run.location.di.locationModule
import com.dracula.run.network.di.networkModule
import com.dracula.run.presentation.di.runPresentationModule
import com.dracula.runique.di.appModule
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
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
			workManagerFactory()
			modules(
				appModule,
				authDataModule,
				authViewModelModule,
				coreDataModule,
				runPresentationModule,
				locationModule,
				databaseModule,
				coreDataModule,
				networkModule,
				runDataModule,
			)
		}
	}

	override fun attachBaseContext(base: Context?) {
		super.attachBaseContext(base)
		SplitCompat.install(this)
	}
}