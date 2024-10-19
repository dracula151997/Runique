package com.dracula.core.data.run

import com.dracula.core.domain.run.LocalRunDataSource
import com.dracula.core.domain.run.RemoteRunDataSource
import com.dracula.core.domain.run.Run
import com.dracula.core.domain.run.RunId
import com.dracula.core.domain.run.RunRepository
import com.dracula.core.domain.utils.DataError
import com.dracula.core.domain.utils.EmptyResult
import com.dracula.core.domain.utils.Result
import com.dracula.core.domain.utils.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

class OfflineFirstRunRepository(
	private val localRunDataSource: LocalRunDataSource,
	private val remoteRunDataSource: RemoteRunDataSource,
	private val applicationScope: CoroutineScope,
) : RunRepository {
	override fun getRuns(): Flow<List<Run>> {
		return localRunDataSource.getRuns()
	}

	override suspend fun fetchRuns(): EmptyResult<DataError> {
		return when (val result = remoteRunDataSource.getRuns()) {
			is Result.Success -> {
				applicationScope.async {
					localRunDataSource.upsertRuns(result.data).asEmptyDataResult()
				}.await()
			}

			is Result.Error -> result.asEmptyDataResult()
		}
	}

	override suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
		val localResult = localRunDataSource.upsertRun(run)
		if (localResult !is Result.Success) {
			return localResult.asEmptyDataResult()
		}
		val runId = run.copy(id = localResult.data)
		return when (val remoteResult =
			remoteRunDataSource.postRun(run = runId, mapPicture = mapPicture)) {
			is Result.Success -> {
				applicationScope.async {
					localRunDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
				}.await()
			}

			is Result.Error -> {
				Result.Success(Unit)
			}
		}
	}

	override suspend fun deleteRun(id: RunId) {
		localRunDataSource.deleteRun(id)
		val remoteResult = applicationScope.async {
			remoteRunDataSource.deleteRun(id)
		}.await()

	}

}