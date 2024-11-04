package com.dracula.runique

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.dracula.core.presentation.designsystem.RuniqueTheme
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
	private val viewModel by viewModel<MainViewModel>()
	private lateinit var splitInstallManager: SplitInstallManager
	private val splitInstallListener = SplitInstallStateUpdatedListener { state ->
		when (state.status()) {
			SplitInstallSessionStatus.DOWNLOADING -> {
				viewModel.setAnalyticsInstallDialogVisibility(true)
			}

			SplitInstallSessionStatus.INSTALLED -> {
				viewModel.setAnalyticsInstallDialogVisibility(false)
				Toast.makeText(
					applicationContext,
					getString(R.string.analytics_feature_installed),
					Toast.LENGTH_SHORT
				).show()
			}

			SplitInstallSessionStatus.INSTALLING -> {
				viewModel.setAnalyticsInstallDialogVisibility(true)
			}


			SplitInstallSessionStatus.CANCELED -> {
				viewModel.setAnalyticsInstallDialogVisibility(false)
				Toast.makeText(
					applicationContext,
					getString(R.string.failed_to_install_analytics_feature),
					Toast.LENGTH_SHORT
				).show()
			}

			SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
				splitInstallManager.startConfirmationDialogForResult(
					state,
					this,
					0
				)
				Toast.makeText(
					applicationContext,
					getString(R.string.failed_to_install_analytics_feature),
					Toast.LENGTH_SHORT
				).show()
			}

			SplitInstallSessionStatus.FAILED -> {
				viewModel.setAnalyticsInstallDialogVisibility(false)
				Toast.makeText(
					applicationContext,
					getString(R.string.failed_to_install_analytics_feature),
					Toast.LENGTH_SHORT
				).show()
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		installSplashScreen().apply {
			setKeepOnScreenCondition { viewModel.state.isCheckingAuth }
		}
		enableEdgeToEdge()
		splitInstallManager = SplitInstallManagerFactory.create(this)
		setContent {
			RuniqueTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
					val navController = rememberNavController()
					if (!viewModel.state.isCheckingAuth)
						NavigationRoot(
							navController = navController,
							isLoggedIn = viewModel.state.isLoggedIn,
							onAnalyticsClick = {
								installOrStartAnalyticsFeature()
							}
						)
				}
				if (viewModel.state.showAnalyticsInstallDialog) {
					Dialog(
						onDismissRequest = {}
					) {
						Column(
							modifier = Modifier
								.clip(RoundedCornerShape(16.dp))
								.background(color = MaterialTheme.colorScheme.surface)
								.padding(16.dp),
							verticalArrangement = Arrangement.Center,
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							CircularProgressIndicator()
							Spacer(Modifier.height(16.dp))
							Text(
								text = stringResource(R.string.installing_analytics_feature),
								color = MaterialTheme.colorScheme.onSurface,

							)
						}
					}
				}
			}
		}
	}

	override fun onResume() {
		super.onResume()
		splitInstallManager.registerListener(splitInstallListener)
	}

	override fun onPause() {
		super.onPause()
		splitInstallManager.unregisterListener(splitInstallListener)
	}

	private fun installOrStartAnalyticsFeature() {
		if (splitInstallManager.installedModules.contains("analytics_feature")) {
			Intent().setClassName(
				packageName,
				"com.dracula.analytics.analytics_feature.AnalyticsActivity"
			).also(::startActivity)
		} else {
			val request = SplitInstallRequest.newBuilder()
				.addModule("analytics_feature")
				.build()
			splitInstallManager.startInstall(request)
				.addOnFailureListener {
					it.printStackTrace()
					Toast.makeText(
						applicationContext,
						getString(R.string.failed_to_install_analytics_feature),
						Toast.LENGTH_SHORT
					).show()
				}
		}
	}
}