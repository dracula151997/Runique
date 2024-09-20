package com.dracula.core.domain.utils

/**
 * A sealed interface representing a result that can either be a success or an error.
 *
 * @param D The type of the data in case of success.
 * @param E The type of the error in case of failure, which must extend [Error].
 */
sealed interface Result<out D, out E : Error> {
	/**
	 * Represents a successful result containing data.
	 *
	 * @param D The type of the data.
	 * @property data The data of the successful result.
	 */
	data class Success<out D>(val data: D) : Result<D, Nothing>

	/**
	 * Represents an error result containing an error.
	 *
	 * @param E The type of the error, which must extend [com.dracula.core.domain.utils.Error].
	 * @property error The error of the result.
	 */
	data class Error<out E : com.dracula.core.domain.utils.Error>(val error: E) : Result<Nothing, E>
}

/**
 * Transforms the data of a [Result] if it is a success.
 *
 * @param D The type of the original data.
 * @param E The type of the error.
 * @param R The type of the transformed data.
 * @param transform A function that transforms the original data to the new type.
 * @return A [Result] containing the transformed data if the original result was a success, or the original error if it was a failure.
 */
inline fun <D, E : Error, R> Result<D, E>.map(transform: (D) -> R): Result<R, E> {
	return when (this) {
		is Result.Success -> Result.Success(transform(data))
		is Result.Error -> Result.Error(error)
	}
}

/**
 * Converts a [Result] to an [EmptyResult], which contains no data in case of success.
 *
 * @param D The type of the original data.
 * @param E The type of the error.
 * @return An [EmptyResult] containing no data if the original result was a success, or the original error if it was a failure.
 */
fun <D, E : Error> Result<D, E>.asEmptyDataResult(): EmptyResult<E> {
	return map { }
}

/**
 * A type alias for a [Result] that contains no data in case of success.
 *
 * @param E The type of the error.
 */
typealias EmptyResult<E> = Result<Unit, E>