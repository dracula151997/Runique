package com.dracula.run.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.dracula.core.database.dao.RunPendingSyncDao
import com.dracula.core.database.entity.DeletedRunSyncEntity
import com.dracula.core.database.entity.RunPendingSyncEntity
import com.dracula.core.database.mappers.toRunEntity
import com.dracula.core.domain.SessionStorage
import com.dracula.core.domain.run.Run
import com.dracula.core.domain.run.RunId
import com.dracula.core.domain.SyncRunScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncRunWorkerScheduler(
	private val context: Context,
	private val pendingSyncDao: RunPendingSyncDao,
	private val sessionStorage: SessionStorage,
	private val applicationScope: CoroutineScope,
) : SyncRunScheduler {
	private val workManager = WorkManager.getInstance(context)
	override suspend fun scheduleSync(syncType: SyncRunScheduler.SyncType) {
		when (syncType) {
			is SyncRunScheduler.SyncType.CreateRun -> scheduleCreateRunWorker(
				run = syncType.run,
				mapPictureBytes = syncType.mapPictureBytes
			)

			is SyncRunScheduler.SyncType.DeleteRun -> scheduleDeleteRunWorker(runId = syncType.runId)
			is SyncRunScheduler.SyncType.FetchRuns -> scheduleFetchRunsWorker(interval = syncType.interval)
		}
	}

	private suspend fun scheduleDeleteRunWorker(runId: RunId) {
		val userId = sessionStorage.get()?.userId ?: return
		val entity = DeletedRunSyncEntity(
			runId = runId,
			userId = userId
		)
		pendingSyncDao.upsertDeletedRunSyncEntity(entity)
		val workRequest = OneTimeWorkRequestBuilder<DeleteRunWorker>()
			.addTag("delete_work")
			.setConstraints(
				Constraints.Builder()
					.setRequiredNetworkType(NetworkType.CONNECTED)
					.build()
			).setBackoffCriteria(
				backoffPolicy = BackoffPolicy.EXPONENTIAL,
				backoffDelay = 2000L,
				timeUnit = TimeUnit.MILLISECONDS
			).setInputData(
				Data.Builder()
					.putString(DeleteRunWorker.RUN_ID, entity.runId)
					.build()
			).build()
		applicationScope.launch { workManager.enqueue(workRequest) }.join()

	}

	private suspend fun scheduleCreateRunWorker(
		run: Run,
		mapPictureBytes: ByteArray,
	) {
		val userId = sessionStorage.get()?.userId ?: return
		val pendingRunEntity = RunPendingSyncEntity(
			run = run.toRunEntity(),
			mapPicture = mapPictureBytes,
			userId = userId
		)
		pendingSyncDao.upsertRunPendingSyncEntity(pendingRunEntity)
		val workRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
			.addTag("create_work")
			.setConstraints(
				Constraints.Builder()
					.setRequiredNetworkType(NetworkType.CONNECTED)
					.build()
			).setBackoffCriteria(
				backoffPolicy = BackoffPolicy.EXPONENTIAL,
				backoffDelay = 2000L,
				timeUnit = TimeUnit.MILLISECONDS
			).setInputData(
				Data.Builder()
					.putString(CreateRunWorker.RUN_ID, pendingRunEntity.runId)
					.build()
			).build()
		applicationScope.launch { workManager.enqueue(workRequest).await() }.join()
	}

	private suspend fun scheduleFetchRunsWorker(interval: Duration) {
		val isSyncScheduled = withContext(Dispatchers.IO) {
			workManager.getWorkInfosByTag("sync_work")
				.get()
				.isNotEmpty()
		}
		if (isSyncScheduled) {
			return
		}

		val workRequest = PeriodicWorkRequestBuilder<FetchRunsWorker>(
			repeatInterval = interval.toJavaDuration(),

			).setConstraints(
			Constraints.Builder()
				.setRequiredNetworkType(NetworkType.CONNECTED)
				.build()

		).setBackoffCriteria(
			backoffPolicy = BackoffPolicy.EXPONENTIAL,
			backoffDelay = 2000L,
			timeUnit = TimeUnit.MILLISECONDS
		).setInitialDelay(
			duration = 30,
			timeUnit = TimeUnit.MINUTES
		).addTag("sync_work")
			.build()
		workManager.enqueue(workRequest).await()
	}

	override suspend fun cancelAllSyncs() {
		WorkManager
			.getInstance(context)
			.cancelAllWork()
			.await()
	}

}