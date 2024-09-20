package com.dracula.runique

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.dracula.auth.presentation.intro.IntroScreenRoot
import com.dracula.auth.presentation.login.LoginScreenRoot
import com.dracula.auth.presentation.register.RegisterScreenRoot
import com.dracula.core.presentation.ui.NavigationScreen
import com.dracula.run.presentation.run_overview.RunOverviewScreenRoot

@Composable
fun NavigationRoot(
	navController: NavHostController,
	isLoggedIn: Boolean,
) {
	NavHost(
		navController = navController,
		startDestination = if (isLoggedIn) NavigationScreen.RUN_GRAPH else NavigationScreen.AUTH_GRAPH
	) {
		authGraph(navController = navController)
		runGraph(navController = navController)
	}
}

private fun NavGraphBuilder.authGraph(
	navController: NavHostController,
) {
	navigation(
		startDestination = NavigationScreen.Auth.INTRO,
		route = NavigationScreen.AUTH_GRAPH
	) {
		composable(route = NavigationScreen.Auth.INTRO) {
			IntroScreenRoot(
				onSignInClick = {
					navController.navigate(NavigationScreen.Auth.LOGIN) {
						popUpTo(NavigationScreen.Auth.REGISTER) {
							inclusive = true
							saveState = true
						}
						restoreState = true
					}
				},
				onSignUpClick = {
					navController.navigate(NavigationScreen.Auth.REGISTER)
				}
			)
		}
		composable(route = NavigationScreen.Auth.REGISTER) {
			RegisterScreenRoot(
				onSignInClick = {
					navController.navigate(NavigationScreen.Auth.LOGIN) {
						popUpTo(NavigationScreen.Auth.REGISTER) {
							inclusive = true
							saveState = true
						}
						restoreState = true
					}
				},
				onSuccessfulRegistration = {
					navController.navigate(NavigationScreen.Auth.LOGIN)
				},
			)
		}
		composable(route = NavigationScreen.Auth.LOGIN) {
			LoginScreenRoot(
				onSignUpClick = {
					navController.navigate(NavigationScreen.Auth.REGISTER) {
						popUpTo("login") {
							inclusive = true
							saveState = true
						}
						restoreState = true
					}
				},
				onLoginSuccess = {
					navController.navigate(NavigationScreen.Run.RUN) {
						popUpTo(NavigationScreen.AUTH_GRAPH) {
							inclusive = true
							saveState = true
						}
					}
				}
			)
		}
	}
}

private fun NavGraphBuilder.runGraph(
	navController: NavHostController,
) {
	navigation(
		startDestination = NavigationScreen.Run.RUN,
		route = NavigationScreen.RUN_GRAPH
	) {
		composable(route = NavigationScreen.Run.RUN) {
			RunOverviewScreenRoot()
		}
	}
}