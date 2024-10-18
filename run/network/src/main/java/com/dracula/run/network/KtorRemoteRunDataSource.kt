package com.dracula.run.network

import com.dracula.core.domain.run.RemoteRunDataSource
import com.dracula.core.domain.run.Run
import com.dracula.core.domain.utils.DataError
import com.dracula.core.domain.utils.EmptyResult
import com.dracula.core.domain.utils.Result
import com.dracula.core.domain.utils.map
import com.dracula.core.networking.constructRoute
import com.dracula.core.networking.delete
import com.dracula.core.networking.get
import com.dracula.core.networking.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KtorRemoteRunDataSource(
	private val httpClient: HttpClient,
) : RemoteRunDataSource {
	override suspend fun getRuns(): Result<List<Run>, DataError.Network> {
		return httpClient.get<List<RunDto>>(
			route = "/runs",
		).map { runs ->
			runs.map { it.toRun() }
		}
	}

	override suspend fun postRun(run: Run, mapPicture: ByteArray): Result<Run, DataError.Network> {
		val requestJson = Json.encodeToString(run.toCreateRunRequest())
		val result = safeCall<RunDto> {
			httpClient.submitFormWithBinaryData(
				url = constructRoute("/run"),
				formData = formData {
					append(
						"MAP_PICTURE", mapPicture,
						headers = Headers.build {
							append(HttpHeaders.ContentType, "image/jpeg")
							append(HttpHeaders.ContentDisposition, "filename=mappicture.jpeg")
						},
					)
					append(
						"RUN_DATA", requestJson,
						headers = Headers.build {
							append(HttpHeaders.ContentType, "text/plain")
							append(HttpHeaders.ContentDisposition, "form-data; name=\"RUN_DATA\"")
						},
					)

				}
			) {
				method = HttpMethod.Post
			}
		}
		return result.map { it.toRun() }
	}

	override suspend fun deleteRun(id: String): EmptyResult<DataError.Network> {
		return httpClient.delete(
			route = "/run", queryParameters = mapOf(
				id to id
			)
		)
	}
}