package com.dracula.analytics.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dracula.analytics.presentation.components.AnalyticsCard
import com.dracula.core.presentation.designsystem.RuniqueScaffold
import com.dracula.core.presentation.designsystem.RuniqueTheme
import com.dracula.core.presentation.designsystem.components.RuniqueToolbar
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalyticsDashboardScreenRoot(
	onBackClick: () -> Unit,
	viewModel: AnalyticsDashboardViewModel = koinViewModel(),

	) {
	AnalyticsDashboardScreen(
		state = viewModel.state,
		onAction = {
			when (it) {
				AnalyticsAction.OnBackClick -> onBackClick()
			}
		}
	)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalyticsDashboardScreen(
	state: AnalyticsDashboardState?,
	onAction: (AnalyticsAction) -> Unit,
) {
	RuniqueScaffold(
		topAppBar = {
			RuniqueToolbar(
				title = stringResource(R.string.analytics),
				onBackClick = { onAction(AnalyticsAction.OnBackClick) },
				showBackButton = true
			)
		}
	) { padding ->
		if (state == null) {
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator()
			}
		} else {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(padding),
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp)
				) {
					AnalyticsCard(
						title = stringResource(R.string.total_distance_run),
						value = state.totalDistanceRun,
						modifier = Modifier.weight(1f)
					)
					Spacer(Modifier.width(16.dp))
					AnalyticsCard(
						title = stringResource(R.string.total_time_run),
						value = state.totalTimeRun,
						modifier = Modifier.weight(1f)
					)
				}
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp)
				) {
					AnalyticsCard(
						title = stringResource(R.string.fastest_ever_run),
						value = state.fastestEverRun,
						modifier = Modifier.weight(1f)
					)
					Spacer(Modifier.width(16.dp))
					AnalyticsCard(
						title = stringResource(R.string.avg_distance),
						value = state.avgDistance,
						modifier = Modifier.weight(1f)
					)
				}
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp)
				) {
					AnalyticsCard(
						title = stringResource(R.string.avg_pace),
						value = state.avgPace,
						modifier = Modifier.weight(1f)
					)
					Spacer(Modifier.width(16.dp))
					Box(
						modifier = Modifier.weight(1f)
					)
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun AnalyticsDashboardScreenPreview() {
	RuniqueTheme {
		AnalyticsDashboardScreen(
			state = AnalyticsDashboardState(
				totalDistanceRun = "100 km",
				totalTimeRun = "10 hours",
				fastestEverRun = "5 min",
				avgDistance = "10 km",
				avgPace = "5 min"
			),
			onAction = {}
		)
	}
}