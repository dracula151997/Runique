package com.dracula.core.domain.run

import com.dracula.core.domain.utils.DataError
import com.dracula.core.domain.utils.EmptyResult
import com.dracula.core.domain.utils.Result

interface RemoteRunDataSource {
	suspend fun getRuns(): Result<List<Run>, DataError.Network>
	suspend fun postRun(run: Run, mapPicture: ByteArray): Result<Run, DataError.Network>
	suspend fun deleteRun(id: String): EmptyResult<DataError.Network>




}