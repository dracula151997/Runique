package com.dracula.run.presentation.active_run

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dracula.core.presentation.designsystem.RuniqueScaffold
import com.dracula.core.presentation.designsystem.RuniqueTheme
import com.dracula.core.presentation.designsystem.StartIcon
import com.dracula.core.presentation.designsystem.StopIcon
import com.dracula.core.presentation.designsystem.components.RuniqueFloatingActionButton
import com.dracula.core.presentation.designsystem.components.RuniqueToolbar
import com.dracula.run.presentation.R
import com.dracula.run.presentation.active_run.components.RunDataCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActiveRunScreenRoot(
	viewModel: ActiveRunViewModel = koinViewModel(),
) {
	ActiveRunScreen(
		state = viewModel.state,
		onAction = viewModel::onAction
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActiveRunScreen(
	state: ActiveRunState,
	onAction: (ActiveRunAction) -> Unit,
) {
	RuniqueScaffold(
		withGradient = false,
		topAppBar = {
			RuniqueToolbar(
				title = stringResource(R.string.active_run),
				showBackButton = true,
				onBackClick = {
					onAction(ActiveRunAction.OnBackClick)
				}
			)
		},
		floatingActionButton = {
			RuniqueFloatingActionButton(
				icon = if (state.shouldTrack) StopIcon else StartIcon,
				onClick = {
					onAction(ActiveRunAction.OnToggleRunClick)
				},
				iconSize = 20.dp,
				contentDescription = if (state.shouldTrack)
					stringResource(R.string.pause_run)
				else
					stringResource(R.string.start_run)
			)
		}
	) { padding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(color = MaterialTheme.colorScheme.surface)
		) {
			RunDataCard(
				elapsedTime = state.elapsedTime,
				runData = state.runData,
				modifier = Modifier
					.padding(16.dp)
					.padding(padding)
			)
		}

	}
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
	RuniqueTheme {
		ActiveRunScreen(
			state = ActiveRunState(),
			onAction = {}
		)
	}
}
