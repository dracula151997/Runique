package com.dracula.core.networking

import com.dracula.core.data.BuildConfig
import com.dracula.core.domain.utils.DataError
import com.dracula.core.domain.utils.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

/**
 * Extension function for making a GET request using Ktor HttpClient.
 *
 * @param route The endpoint route for the GET request.
 * @param queryParameters A map of query parameters to be included in the request. Defaults to an empty map.
 * @return A [Result] containing either the response of type [Response] or a [DataError.Network] error.
 */
suspend inline fun <reified Response : Any> HttpClient.get(
    route: String,
    queryParameters: Map<String, Any?> = emptyMap(),
): Result<Response, DataError.Network> {
    return safeCall {
        get {
            url(constructRoute(route = route))
            queryParameters.forEach { (key, value) ->
                if (value != null) {
                    parameter(key, value.toString())
                }
            }
        }
    }
}
/**
 * Extension function for making a POST request using Ktor HttpClient.
 *
 * @param route The endpoint route for the POST request.
 * @param body The request body to be sent with the POST request.
 * @return A [Result] containing either the response of type [Response] or a [DataError.Network] error.
 */
suspend inline fun <reified Request : Any, reified Response : Any> HttpClient.post(
    route: String,
    body: Request,
): Result<Response, DataError.Network> {
    return safeCall {
        post {
            url(urlString = constructRoute(route = route))
            setBody(body = body)
        }
    }
}

/**
 * Extension function for making a DELETE request using Ktor HttpClient.
 *
 * @param route The endpoint route for the DELETE request.
 * @param queryParameters A map of query parameters to be included in the request. Defaults to an empty map.
 * @return A [Result] containing either the response of type [Response] or a [DataError.Network] error.
 */
suspend inline fun <reified Response : Any> HttpClient.delete(
    route: String,
    queryParameters: Map<String, Any?> = emptyMap(),
): Result<Response, DataError.Network> {
    return safeCall {
        delete {
            url(constructRoute(route = route))
            queryParameters.forEach { (key, value) ->
                if (value != null) {
                    parameter(key, value.toString())
                }
            }
        }
    }
}


/**
 * Executes a network call safely, catching and handling various exceptions.
 *
 * @param T The type of the expected response body.
 * @param execute A lambda function that performs the network call and returns an [HttpResponse].
 * @return A [Result] object containing either the response body of type [T] or a [DataError.Network] error.
 */
suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse,
): Result<T, DataError.Network> {
    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        e.printStackTrace()
        return Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: SerializationException) {
        e.printStackTrace()
        return Result.Error(DataError.Network.SERIALIZATION_ERROR)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        e.printStackTrace()
        return Result.Error(DataError.Network.UNKNOWN)
    }
    return responseToResult(response = response)
}

/**
 * Converts an [HttpResponse] to a [Result] object.
 *
 * @param T The type of the expected response body.
 * @param response The [HttpResponse] to be converted.
 * @return A [Result] object containing either the response body of type [T] or a [DataError.Network] error.
 */
suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Network> {
    return when (response.status.value) {
        in 200..299 -> {
            val data = response.body<T>()
            Result.Success(data)
        }
        401 -> {
            Result.Error(DataError.Network.UNAUTHORIZED)
        }
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}

/**
 * Constructs a complete URL route based on the given endpoint route.
 *
 * @param route The endpoint route to be constructed.
 * @return A complete URL string.
 */
fun constructRoute(route: String): String {
    return when {
        route.contains(BuildConfig.BASE_URL) -> route
        route.startsWith("/") -> BuildConfig.BASE_URL + route
        else -> BuildConfig.BASE_URL + "/" + route
    }
}