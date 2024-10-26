package com.dracula.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RunPendingSyncEntity(
	@Embedded val run: RunEntity,
	val mapPicture: ByteArray,
	val userId: String,
	@PrimaryKey(autoGenerate = false)
	val runId: String = run.id,
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as RunPendingSyncEntity

		if (run != other.run) return false
		if (!mapPicture.contentEquals(other.mapPicture)) return false
		if (userId != other.userId) return false
		if (runId != other.runId) return false

		return true
	}

	override fun hashCode(): Int {
		var result = run.hashCode()
		result = 31 * result + mapPicture.contentHashCode()
		result = 31 * result + userId.hashCode()
		result = 31 * result + runId.hashCode()
		return result
	}
}