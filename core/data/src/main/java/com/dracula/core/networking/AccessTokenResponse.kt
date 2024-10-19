package com.dracula.core.networking

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenResponse(
	val accessToken: String,
	val expirationTimestamp: Long,
)