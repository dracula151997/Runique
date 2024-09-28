package com.dracula.run.domain

import com.dracula.core.domain.location.LocationWithAltitude
import kotlinx.coroutines.flow.Flow

interface LocationObserver {
	fun observeLocation(interval: Long): Flow<LocationWithAltitude>
}