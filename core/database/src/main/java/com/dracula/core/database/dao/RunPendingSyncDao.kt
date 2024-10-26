package com.dracula.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.dracula.core.database.entity.DeletedRunSyncEntity
import com.dracula.core.database.entity.RunPendingSyncEntity
import com.dracula.core.domain.run.Run

@Dao
interface RunPendingSyncDao {
	@Query("SELECT * FROM RunPendingSyncEntity WHERE userId = :userId")
	suspend fun getAllRunPendingSyncEntities(userId: String): List<RunPendingSyncEntity>

	@Query("SELECT * FROM RunPendingSyncEntity WHERE runId = :runId")
	suspend fun getRunPendingSyncEntity(runId: String): RunPendingSyncEntity?

	@Upsert
	suspend fun upsertRunPendingSyncEntity(runPendingSyncEntity: RunPendingSyncEntity)

	@Query("DELETE FROM RunPendingSyncEntity WHERE runId = :runId")
	suspend fun deleteRunPendingSyncEntity(runId: String)

	@Query("SELECT * FROM DeletedRunSyncEntity WHERE userId = :userId")
	suspend fun getAllDeletedRunSyncEntities(userId: String): List<DeletedRunSyncEntity>

	@Query("SELECT * FROM DeletedRunSyncEntity WHERE runId = :runId")
	suspend fun getDeletedRunSyncEntity(runId: String): DeletedRunSyncEntity?

	@Upsert
	suspend fun upsertDeletedRunSyncEntity(deletedRunSyncEntity: DeletedRunSyncEntity)

	@Query("DELETE FROM DeletedRunSyncEntity WHERE runId = :runId")
	suspend fun deleteDeletedRunSyncEntity(runId: String)
}