package com.dracula.analytics.data

import com.dracula.analytics.domain.AnalyticsRepository
import com.dracula.analytics.domain.AnalyticsValues
import com.dracula.core.database.dao.AnalyticsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class RoomAnalyticsRepository(
	private val dao: AnalyticsDao,
) : AnalyticsRepository {
	override suspend fun getAnalyticsValues(): AnalyticsValues {
		return withContext(Dispatchers.IO) {
			val totalDistance = async { dao.getTotalDistance() }
			val totalTime = async { dao.getTotalTimeRun() }
			val maxSpeed = async { dao.getMaxRunSpeed() }
			val avgDistance = async { dao.getAvgDistancePerRun() }
			val avgPace = async { dao.getAvgPacePerRun() }
			AnalyticsValues(
				totalDistanceRun = totalDistance.await(),
				totalTimeRun = totalTime.await().milliseconds,
				fastestEverRun = maxSpeed.await(),
				avgPacePerRun = avgPace.await(),
				avgDistancePerRun = avgDistance.await()
			)
		}
	}

}