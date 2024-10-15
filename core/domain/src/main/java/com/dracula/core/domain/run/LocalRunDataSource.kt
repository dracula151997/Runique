package com.dracula.core.domain.run

import com.dracula.core.domain.utils.DataError
import com.dracula.core.domain.utils.Error
import com.dracula.core.domain.utils.Result
import kotlinx.coroutines.flow.Flow

typealias RunId = String

interface LocalRunDataSource {
	fun getRuns(): Flow<List<Run>>
	suspend fun upsertRun(run: Run): Result<RunId, DataError.Local>
	suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, DataError.Local>
	suspend fun deleteRun(id: String)
	suspend fun deleteAllRuns()
}