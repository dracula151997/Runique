package com.dracula.runique.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.dracula.runique.MainViewModel
import com.dracula.runique.RuniqueApp
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
	single<SharedPreferences> {
		EncryptedSharedPreferences(
			context = androidApplication(),
			fileName = "auth_info",
			masterKey = MasterKey(androidApplication()),
			prefKeyEncryptionScheme = EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			prefValueEncryptionScheme = EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		)
	}
	viewModelOf(::MainViewModel)
	single<CoroutineScope> {
		(androidApplication() as RuniqueApp).applicationScope
	}
}