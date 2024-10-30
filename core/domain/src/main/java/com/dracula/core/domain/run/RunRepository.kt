package com.dracula.core.domain.run

import com.dracula.core.domain.utils.DataError
import com.dracula.core.domain.utils.EmptyResult
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a repository for managing runs.
 */
interface RunRepository {

    /**
     * Retrieves a flow of all runs from local database.
     *
     * @return A [Flow] emitting a list of [Run] objects.
     */
    fun getRuns(): Flow<List<Run>>

    /**
     * Fetches runs from a remote source.
     *
     * @return An [EmptyResult] indicating success or a [DataError].
     */
    suspend fun fetchRuns(): EmptyResult<DataError>

    /**
     * Inserts or updates a run along with its map picture.
     *
     * @param run The [Run] object to be upserted.
     * @param mapPicture The map picture associated with the run.
     * @return An [EmptyResult] indicating success or a [DataError].
     */
    suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError>

    /**
     * Deletes a run by its ID.
     *
     * @param id The ID of the run to be deleted.
     */
    suspend fun deleteRun(id: RunId)

    /**
     * Synchronizes pending runs with a remote source.
     */
    suspend fun syncPendingRuns()

    /**
     * Logs out the user and performs necessary cleanup.
     *
     * @return An [EmptyResult] indicating success or a [DataError.Network].
     */
    suspend fun logout(): EmptyResult<DataError.Network>

    /**
     * Deletes all runs from the repository.
     */
    suspend fun deleteAllRuns()
}