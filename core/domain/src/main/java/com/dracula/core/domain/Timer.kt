package com.dracula.core.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Timer {
/**
 * Creates a flow that emits the elapsed time in milliseconds at regular intervals.
 *
 * This function starts a coroutine that continuously calculates the elapsed time
 * since the last emission and emits this duration as a `Flow<Duration>`.
 *
 * @return A `Flow<Duration>` that emits the elapsed time in milliseconds.
 */
fun timeAndEmit(): Flow<Duration> {
    return flow {
        var lastEmitTime = System.currentTimeMillis()
        while (true) {
            delay(200) // Delay for 200 milliseconds
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - lastEmitTime
            emit(elapsedTime.milliseconds) // Emit the elapsed time in milliseconds
            lastEmitTime = currentTime
        }
    }
}
}