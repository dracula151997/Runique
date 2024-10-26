package com.dracula.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dracula.core.database.dao.RunPendingSyncDao
import com.dracula.core.database.mappers.toRun
import com.dracula.core.domain.run.RemoteRunDataSource
import com.dracula.core.domain.utils.Result

class CreateRunWorker(
	context: Context,
	private val parameters: WorkerParameters,
	private val remoteRunDataSource: RemoteRunDataSource,
	private val pendingSyncDao: RunPendingSyncDao,
) : CoroutineWorker(context, parameters) {

	override suspend fun doWork(): Result {
		if (runAttemptCount >= 5) {
			return Result.failure()
		}

		val pendingRunId = parameters.inputData.getString(RUN_ID) ?: return Result.failure()
		val pendingRunEntity =
			pendingSyncDao.getRunPendingSyncEntity(pendingRunId) ?: return Result.failure()

		val run = pendingRunEntity.run.toRun()
		return when(val result = remoteRunDataSource.postRun(run, pendingRunEntity.mapPicture)){
			is com.dracula.core.domain.utils.Result.Error -> {
				result.error.toWorkerResult()
			}
			is com.dracula.core.domain.utils.Result.Success -> {
				pendingSyncDao.deleteRunPendingSyncEntity(pendingRunId)
				Result.success()
			}
		}
	}

	companion object {
		const val RUN_ID = "run_id"
		const val USER_ID = "user_id"
	}
}