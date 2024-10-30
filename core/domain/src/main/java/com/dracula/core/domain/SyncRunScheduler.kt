package com.dracula.core.domain

import com.dracula.core.domain.run.Run
import com.dracula.core.domain.run.RunId
import kotlin.time.Duration

/**
 * Interface for scheduling and managing synchronization tasks.
 */
interface SyncRunScheduler {

    /**
     * Schedules a synchronization task.
     *
     * @param syncType The type of synchronization task to schedule.
     */
    suspend fun scheduleSync(syncType: SyncType)

    /**
     * Cancels all scheduled synchronization tasks.
     */
    suspend fun cancelAllSyncs()

    /**
     * Sealed interface representing different types of synchronization tasks.
     */
    sealed interface SyncType {
        /**
         * Synchronization task for fetching runs at a specified interval.
         *
         * @param interval The interval at which to fetch runs.
         */
        data class FetchRuns(val interval: Duration) : SyncType

        /**
         * Synchronization task for deleting a run by its ID.
         *
         * @param runId The ID of the run to delete.
         */
        data class DeleteRun(val runId: RunId) : SyncType

        /**
         * Synchronization task for creating a run with its associated map picture.
         *
         * @param run The run to create.
         * @param mapPictureBytes The map picture associated with the run.
         */
        class CreateRun(val run: Run, val mapPictureBytes: ByteArray) : SyncType
    }
}