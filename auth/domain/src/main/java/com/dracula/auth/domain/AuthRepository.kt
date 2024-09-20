package com.dracula.auth.domain

import com.dracula.core.domain.utils.DataError
import com.dracula.core.domain.utils.EmptyResult

interface AuthRepository {
	suspend fun login(email: String, password: String) : EmptyResult<DataError.Network>
	suspend fun register(email: String, password: String) : EmptyResult<DataError.Network>
}