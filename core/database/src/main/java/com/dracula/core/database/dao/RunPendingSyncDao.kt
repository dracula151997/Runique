package com.dracula.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.dracula.core.database.entity.DeletedRunSyncEntity
import com.dracula.core.database.entity.RunPendingSyncEntity
import com.dracula.core.domain.run.Run

@Dao
interface RunPendingSyncDao {

    /**
     * Retrieves all pending sync entities for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of RunPendingSyncEntity objects.
     */
    @Query("SELECT * FROM RunPendingSyncEntity WHERE userId = :userId")
    suspend fun getAllRunPendingSyncEntities(userId: String): List<RunPendingSyncEntity>

    /**
     * Retrieves a specific pending sync entity by run ID.
     *
     * @param runId The ID of the run.
     * @return The RunPendingSyncEntity object, or null if not found.
     */
    @Query("SELECT * FROM RunPendingSyncEntity WHERE runId = :runId")
    suspend fun getRunPendingSyncEntity(runId: String): RunPendingSyncEntity?

    /**
     * Inserts or updates a pending sync entity.
     *
     * @param runPendingSyncEntity The RunPendingSyncEntity object to be upserted.
     */
    @Upsert
    suspend fun upsertRunPendingSyncEntity(runPendingSyncEntity: RunPendingSyncEntity)

    /**
     * Deletes a specific pending sync entity by run ID.
     *
     * @param runId The ID of the run.
     */
    @Query("DELETE FROM RunPendingSyncEntity WHERE runId = :runId")
    suspend fun deleteRunPendingSyncEntity(runId: String)

    /**
     * Retrieves all deleted run sync entities for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of DeletedRunSyncEntity objects.
     */
    @Query("SELECT * FROM DeletedRunSyncEntity WHERE userId = :userId")
    suspend fun getAllDeletedRunSyncEntities(userId: String): List<DeletedRunSyncEntity>

    /**
     * Retrieves a specific deleted run sync entity by run ID.
     *
     * @param runId The ID of the run.
     * @return The DeletedRunSyncEntity object, or null if not found.
     */
    @Query("SELECT * FROM DeletedRunSyncEntity WHERE runId = :runId")
    suspend fun getDeletedRunSyncEntity(runId: String): DeletedRunSyncEntity?

    /**
     * Inserts or updates a deleted run sync entity.
     *
     * @param deletedRunSyncEntity The DeletedRunSyncEntity object to be upserted.
     */
    @Upsert
    suspend fun upsertDeletedRunSyncEntity(deletedRunSyncEntity: DeletedRunSyncEntity)

    /**
     * Deletes a specific deleted run sync entity by run ID.
     *
     * @param runId The ID of the run.
     */
    @Query("DELETE FROM DeletedRunSyncEntity WHERE runId = :runId")
    suspend fun deleteDeletedRunSyncEntity(runId: String)
}