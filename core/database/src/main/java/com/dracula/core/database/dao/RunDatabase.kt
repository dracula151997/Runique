package com.dracula.core.database.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dracula.core.database.entity.RunEntity

@Database(
	entities = [RunEntity::class],
	version = 1
)
abstract class RunDatabase: RoomDatabase(){
	abstract val runDao: RunDao

	companion object{
		const val DATABASE_NAME = "run_db"
	}

}