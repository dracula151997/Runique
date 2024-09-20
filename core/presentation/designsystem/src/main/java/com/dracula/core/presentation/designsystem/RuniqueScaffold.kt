package com.dracula.core.presentation.designsystem

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dracula.core.presentation.designsystem.components.GradientBackground

@Composable
fun RuniqueScaffold(
	modifier: Modifier = Modifier,
	withGradient: Boolean = true,
	topAppBar: @Composable () -> Unit = {},
	floatingActionButton: @Composable () -> Unit = {},
	content: @Composable (PaddingValues) -> Unit,
) {
	Scaffold(
		topBar = topAppBar,
		floatingActionButton = floatingActionButton,
		floatingActionButtonPosition = FabPosition.Center,
		content = { paddingValues ->
			if (withGradient) {
				GradientBackground {
					content(paddingValues)
				}
			} else {
				content(paddingValues)
			}
		},
	)
}