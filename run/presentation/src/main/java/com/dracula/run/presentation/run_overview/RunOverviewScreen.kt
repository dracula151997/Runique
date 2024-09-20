package com.dracula.run.presentation.run_overview

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dracula.core.presentation.designsystem.AnalyticsIcon
import com.dracula.core.presentation.designsystem.LogoIcon
import com.dracula.core.presentation.designsystem.LogoutIcon
import com.dracula.core.presentation.designsystem.RunIcon
import com.dracula.core.presentation.designsystem.RuniqueScaffold
import com.dracula.core.presentation.designsystem.components.RuniqueFloatingActionButton
import com.dracula.core.presentation.designsystem.components.RuniqueToolbar
import com.dracula.core.presentation.designsystem.components.util.DropDownItem
import com.dracula.run.presentation.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
	viewModel: RunOverviewViewModel = koinViewModel(),
) {
	RunOverviewScreen(
		onAction = viewModel::onAction
	)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RunOverviewScreen(
	onAction: (RunOverviewAction) -> Unit,
) {
	val topAppBarState = rememberTopAppBarState()
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
		state = topAppBarState
	)
	RuniqueScaffold(
		topAppBar = {
			RuniqueToolbar(
				title = stringResource(R.string.app_name),
				showBackButton = false,
				scrollBehavior = scrollBehavior,
				startContent = {
					Icon(
						imageVector = LogoIcon,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.primary,
						modifier = Modifier.size(30.dp)
					)
				},
				menuItems = listOf(
					DropDownItem(
						icon = AnalyticsIcon,
						title = stringResource(R.string.analytics)
					),
					DropDownItem(
						icon = LogoutIcon,
						title = stringResource(R.string.logout)
					)
				),
				onMenuItemClick = { index ->
					when (index) {
						0 -> onAction(RunOverviewAction.OnAnalyticsClick)
						1 -> onAction(RunOverviewAction.OnLogoutClick)
					}
				},
			)
		},
		floatingActionButton = {
			RuniqueFloatingActionButton(
				icon = RunIcon,
				onClick = {
					onAction(RunOverviewAction.OnStartClick)
				},
			)
		}
	) { padding ->

	}
}