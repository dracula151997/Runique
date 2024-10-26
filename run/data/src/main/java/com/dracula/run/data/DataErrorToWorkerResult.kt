package com.dracula.run.data

import androidx.work.ListenableWorker
import androidx.work.ListenableWorker.Result
import com.dracula.core.domain.utils.DataError

fun DataError.toWorkerResult(): ListenableWorker.Result {
	return when (this) {
		DataError.Local.DISK_FULL -> Result.failure()
		DataError.Network.REQUEST_TIMEOUT -> Result.retry()
		DataError.Network.UNAUTHORIZED -> Result.retry()
		DataError.Network.CONFLICT -> Result.retry()
		DataError.Network.TOO_MANY_REQUESTS -> Result.retry()
		DataError.Network.NO_INTERNET -> Result.retry()
		DataError.Network.PAYLOAD_TOO_LARGE -> Result.failure()
		DataError.Network.SERVER_ERROR -> Result.retry()
		DataError.Network.SERIALIZATION_ERROR -> Result.failure()
		DataError.Network.UNKNOWN -> Result.failure()
	}
}