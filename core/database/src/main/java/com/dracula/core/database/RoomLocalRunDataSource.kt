package com.dracula.core.database

import android.database.sqlite.SQLiteFullException
import com.dracula.core.database.dao.RunDao
import com.dracula.core.database.mappers.toRun
import com.dracula.core.database.mappers.toRunEntity
import com.dracula.core.domain.run.LocalRunDataSource
import com.dracula.core.domain.run.Run
import com.dracula.core.domain.run.RunId
import com.dracula.core.domain.utils.DataError
import com.dracula.core.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalRunDataSource(
	private val runDao: RunDao,
) : LocalRunDataSource {
	override fun getRuns(): Flow<List<Run>> {
		return runDao.getRuns()
			.map { runEntities ->
				runEntities.map { entity ->
					entity.toRun()
				}
			}
	}

	override suspend fun upsertRun(run: Run): Result<RunId, DataError.Local> {
		return try {
			val entity = run.toRunEntity()
			runDao.upsertRun(entity)
			Result.Success(entity.id)
		} catch (e: SQLiteFullException) {
			return Result.Error(DataError.Local.DISK_FULL)
		}
	}

	override suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, DataError.Local> {
		return try {
			val entities = runs.map { it.toRunEntity() }
			runDao.upsertRuns(entities)
			Result.Success(entities.map { it.id })
		} catch (e: SQLiteFullException) {
			return Result.Error(DataError.Local.DISK_FULL)
		}
	}

	override suspend fun deleteRun(id: String) {
		runDao.deleteRun(id)
	}

	override suspend fun deleteAllRuns() {
		runDao.deleteAllRuns()
	}
}