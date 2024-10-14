package com.dracula.run.presentation.run_overview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.dracula.core.domain.location.Location
import com.dracula.core.domain.run.Run
import com.dracula.core.presentation.designsystem.CalendarIcon
import com.dracula.core.presentation.designsystem.RunOutlinedIcon
import com.dracula.core.presentation.designsystem.RuniqueTheme
import com.dracula.run.presentation.R
import com.dracula.run.presentation.run_overview.mapper.toRunUi
import com.dracula.run.presentation.run_overview.model.RunDataUi
import com.dracula.run.presentation.run_overview.model.RunUi
import java.time.ZonedDateTime
import kotlin.math.max
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RunListItem(
	runUi: RunUi,
	onDeleteClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var showDropDown by remember { mutableStateOf(false) }
	Box {
		Column(
			modifier = modifier
				.clip(RoundedCornerShape(16.dp))
				.background(MaterialTheme.colorScheme.surface)
				.combinedClickable(
					onClick = {},
					onDoubleClick = { showDropDown = true }
				)
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			MapImage(imageUrl = runUi.mapPictureUrl)
			RunningTimeSection(duration = runUi.duration, modifier = Modifier.fillMaxWidth())
			HorizontalDivider(
				color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
			)
			RunningDateSection(dateTime = runUi.dateTime)
			DataGrid(run = runUi, modifier = Modifier.fillMaxWidth())
		}
		DropdownMenu(expanded = showDropDown, onDismissRequest = {
			showDropDown = false
		}) {
			DropdownMenuItem(
				text = {
					Text(
						text = stringResource(R.string.delete),
						color = MaterialTheme.colorScheme.onSurface
					)
				},
				onClick = {
					onDeleteClick()
					showDropDown = false
				}
			)
		}
	}
}

@Composable
private fun RunningDateSection(
	dateTime: String,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			imageVector = CalendarIcon,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Spacer(modifier = Modifier.width(16.dp))
		Text(
			text = dateTime,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
	}
}

@Composable
private fun MapImage(imageUrl: String?, modifier: Modifier = Modifier) {
	SubcomposeAsyncImage(
		model = imageUrl,
		contentDescription = stringResource(R.string.run_map),
		modifier = modifier
			.fillMaxWidth()
			.aspectRatio(16f / 9f)
			.clip(RoundedCornerShape(16.dp)),
		loading = {
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator(
					modifier = Modifier.size(20.dp),
					strokeWidth = 2.dp,
					color = MaterialTheme.colorScheme.onSurface
				)
			}
		},
		error = {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(MaterialTheme.colorScheme.errorContainer),
				contentAlignment = Alignment.Center
			) {
				Text(
					text = stringResource(R.string.error_could_not_load_image),
					color = MaterialTheme.colorScheme.onError
				)
			}
		}
	)

}

@Composable
fun RunningTimeSection(duration: String, modifier: Modifier = Modifier) {
	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically
	) {
		Box(
			modifier = Modifier
				.size(40.dp)
				.clip(RoundedCornerShape(10.dp))
				.background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
				.border(
					width = 1.dp,
					color = MaterialTheme.colorScheme.primary,
					shape = RoundedCornerShape(10.dp)
				)
				.padding(4.dp),
			contentAlignment = Alignment.Center
		) {
			Icon(
				imageVector = RunOutlinedIcon,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary
			)
		}
		Spacer(modifier = Modifier.width(16.dp))
		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.Center
		) {
			Text(
				text = stringResource(R.string.total_running_time),
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = duration,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DataGrid(
	run: RunUi,
	modifier: Modifier = Modifier,
) {
	val runDataUiList = listOf(
		RunDataUi(
			name = stringResource(R.string.distance),
			value = run.distance
		),
		RunDataUi(
			name = stringResource(R.string.pace),
			value = run.pace
		),
		RunDataUi(
			name = stringResource(R.string.avg_speed),
			value = run.avgSpeed
		),
		RunDataUi(
			name = stringResource(R.string.max_speed),
			value = run.maxSpeedInKm
		),
		RunDataUi(
			name = stringResource(R.string.total_elevation),
			value = run.tonalElevation
		)
	)
	var maxWidth by remember { mutableIntStateOf(0) }
	val maxWithDp = with(LocalDensity.current) { maxWidth.toDp() }
	FlowRow(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
		maxItemsInEachRow = 3
	) {
		runDataUiList.forEach { runData ->
			DataGridCell(runData = runData, modifier = Modifier
				.defaultMinSize(minWidth = maxWithDp)
				.onSizeChanged {
					maxWidth = max(maxWidth, it.width)
				})
		}
	}
}

@Composable
private fun DataGridCell(
	runData: RunDataUi,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
	) {
		Text(
			text = runData.name,
			fontSize = 12.sp,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = runData.value,
			color = MaterialTheme.colorScheme.onSurface
		)
	}
}

@Composable
@Preview
private fun RunListItemPreview() {
	RuniqueTheme {
		RunListItem(
			runUi = Run(
				id = "1",
				duration = 10.minutes + 30.seconds,
				mapPictureUrl = null,
				dateTimeUtc = ZonedDateTime.now(),
				distanceInMeters = 8581,
				location = Location(
					latitude = 12.13,
					longitude = 14.15
				),
				maxSpeedKmh = 16.17,
				totalElevationMeters = 3035

			).toRunUi(),
			onDeleteClick = {}
		)
	}
}