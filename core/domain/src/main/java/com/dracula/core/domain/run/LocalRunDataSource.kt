package com.dracula.core.domain.run

import com.dracula.core.domain.utils.DataError
import com.dracula.core.domain.utils.Result
import kotlinx.coroutines.flow.Flow

typealias RunId = String

/**
 * Interface for local data source operations related to runs.
 */
interface LocalRunDataSource {

    /**
     * Retrieves a flow of all runs.
     *
     * @return A [Flow] emitting a list of [Run] objects.
     */
    fun getRuns(): Flow<List<Run>>

    /**
     * Inserts or updates a run.
     *
     * @param run The [Run] object to be upserted.
     * @return A [Result] containing the [RunId] of the upserted run or a [DataError.Local] on failure.
     */
    suspend fun upsertRun(run: Run): Result<RunId, DataError.Local>

    /**
     * Inserts or updates multiple runs.
     *
     * @param runs A list of [Run] objects to be upserted.
     * @return A [Result] containing a list of [RunId]s of the upserted runs or a [DataError.Local] on failure.
     */
    suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, DataError.Local>

    /**
     * Deletes a run by its ID.
     *
     * @param id The ID of the run to be deleted.
     */
    suspend fun deleteRun(id: String)

    /**
     * Deletes all runs.
     */
    suspend fun deleteAllRuns()
}