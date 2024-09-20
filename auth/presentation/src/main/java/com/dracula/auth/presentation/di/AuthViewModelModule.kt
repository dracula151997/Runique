package com.dracula.auth.presentation.di

import com.dracula.auth.presentation.login.LoginViewModel
import com.dracula.auth.presentation.register.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authViewModelModule = module {
	viewModelOf(::RegisterViewModel)
	viewModelOf(::LoginViewModel)
}