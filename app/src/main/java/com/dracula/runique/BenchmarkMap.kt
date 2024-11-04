package com.dracula.runique

import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun MutableMap<Any, Any>.benchmark(
	operationCount: Long,
): Map<String, Any> {
	val insertTime = measureTimeMillis {
		for (i in 0 until operationCount) {
			this[i] = i
		}
	}
	val readTime = measureTimeMillis {
		for (i in 0 until operationCount) {
			this[i]
		}
	}
	val deleteTime = measureTimeMillis {
		for (i in 0 until operationCount) {
			this.remove(i)
		}
	}

	return mapOf(
		"insertTime" to insertTime.toDuration(DurationUnit.SECONDS),
		"readTime" to readTime.toDuration(DurationUnit.SECONDS),
		"deleteTime" to deleteTime.toDuration(DurationUnit.SECONDS),
		"operationCount" to operationCount,
	)
}

fun main() {
	val operationCount = 1_000_000_000_000
	hashMapOf<Any, Any>().benchmark(operationCount).let(::println)
	linkedMapOf<Any, Any>().benchmark(operationCount).let(::println)
}