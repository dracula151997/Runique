package com.dracula.auth.data

import com.dracula.auth.domain.AuthRepository
import com.dracula.core.domain.AuthInfo
import com.dracula.core.domain.SessionStorage
import com.dracula.core.domain.utils.DataError
import com.dracula.core.domain.utils.EmptyResult
import com.dracula.core.domain.utils.Result
import com.dracula.core.domain.utils.asEmptyDataResult
import com.dracula.core.networking.post
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
	private val httpClient: HttpClient,
	private val sessionStorage: SessionStorage,
) : AuthRepository {
	override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
		val result = httpClient.post<LoginRequest, LoginResponse>(
			route = "/login",
			body = LoginRequest(
				email = email,
				password = password
			)
		)
		if (result is Result.Success) {
			sessionStorage.set(
				AuthInfo(
					accessToken = result.data.accessToken,
					refreshToken = result.data.refreshToken,
					userId = result.data.userId,
				)
			)
		}
		return result.asEmptyDataResult()
	}

	override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> {
		return httpClient.post<RegisterRequest, Unit>(
			route = "/register",
			body = RegisterRequest(
				email = email,
				password = password
			)
		)
	}

}