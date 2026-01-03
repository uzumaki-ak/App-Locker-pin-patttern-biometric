package com.applock.secure.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.applock.secure.ui.screens.home.HomeScreen
import com.applock.secure.ui.screens.onboarding.OnboardingScreen
import com.applock.secure.ui.screens.setup.SetupAuthScreen
import com.applock.secure.ui.screens.settings.SettingsScreen
import com.applock.secure.ui.screens.applist.AppListScreen

/**
 * Navigation routes
 */
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object SetupAuth : Screen("setup_auth")
    object Home : Screen("home")
    object AppList : Screen("app_list")
    object Settings : Screen("settings")
}

/**
 * Main navigation graph
 * Handles navigation between screens
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding screen
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.SetupAuth.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Setup authentication screen
        composable(Screen.SetupAuth.route) {
            SetupAuthScreen(
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SetupAuth.route) { inclusive = true }
                    }
                }
            )
        }

        // Home screen
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAppList = {
                    navController.navigate(Screen.AppList.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // App list screen
        composable(Screen.AppList.route) {
            AppListScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Settings screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}