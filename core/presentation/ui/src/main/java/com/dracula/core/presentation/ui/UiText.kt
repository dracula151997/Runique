package com.dracula.core.presentation.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
	data class DynamicString(val value: String) : UiText
	class StringResource(
		@StringRes val resId: Int,
		val args: Array<Any> = emptyArray(),
	) : UiText

	@Composable
	fun asString(): String = when (this) {
		is DynamicString -> value
		is StringResource -> stringResource(id = resId, *args)
	}

	fun asString(context: Context) = when (this) {
		is DynamicString -> value
		is StringResource -> context.getString(resId, *args)
	}

	companion object {
		fun from(value: String): UiText = DynamicString(value)
		fun from(@StringRes resId: Int, args: Array<Any> = emptyArray()): UiText =
			StringResource(resId, args)
	}


}