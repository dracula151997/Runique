package com.dracula.core.networking

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenRequest(
	val refreshToken: String,
	val userId: String
)