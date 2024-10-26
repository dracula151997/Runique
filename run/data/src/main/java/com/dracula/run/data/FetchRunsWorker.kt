package com.dracula.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dracula.core.domain.run.RunRepository
import com.dracula.core.domain.utils.Result

class FetchRunsWorker(
	context: Context,
	params: WorkerParameters,
	private val runRepository: RunRepository,
) : CoroutineWorker(context, params) {

	override suspend fun doWork(): Result {
		if (runAttemptCount >= 5) {
			return Result.failure()
		}
		return when (val result = runRepository.fetchRuns()) {
			is com.dracula.core.domain.utils.Result.Error -> {
				result.error.toWorkerResult()
			}

			is com.dracula.core.domain.utils.Result.Success -> Result.success()
		}
	}

}