package com.dracula.core.database.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dracula.core.database.entity.DeletedRunSyncEntity
import com.dracula.core.database.entity.RunEntity
import com.dracula.core.database.entity.RunPendingSyncEntity

@Database(
	entities = [RunEntity::class, RunPendingSyncEntity::class, DeletedRunSyncEntity::class],
	version = 1
)
abstract class RunDatabase : RoomDatabase() {
	abstract val runDao: RunDao
	abstract val runPendingSyncDao: RunPendingSyncDao
	abstract val analyticsDao: AnalyticsDao

	companion object {
		const val DATABASE_NAME = "run_db"
	}

}