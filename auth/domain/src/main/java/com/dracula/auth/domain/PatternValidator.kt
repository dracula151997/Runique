package com.dracula.auth.domain

interface PatternValidator {
	fun matches(value: String): Boolean
}