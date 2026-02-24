package com.virex.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.virex.app.onboarding.KeyboardEnableScreen
import com.virex.app.fonts.FontsScreen
import com.virex.app.store.FavoritesScreen
import com.virex.app.store.HomeScreen
import com.virex.app.store.PrivacyOnboardingScreen
import com.virex.app.store.ProScreen
import com.virex.app.store.ThemePreviewScreen

sealed class Screen(val route: String) {
    object Privacy : Screen("privacy")
    object KeyboardEnable : Screen("keyboard_enable")
    object Home : Screen("home")
    object Preview : Screen("preview/{themeId}") {
        fun createRoute(themeId: String) = "preview/$themeId"
    }
    object Favorites : Screen("favorites")
    object Pro : Screen("pro")
    object Fonts : Screen("fonts")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Privacy.route) {
            PrivacyOnboardingScreen(
                onContinue = {
                    navController.navigate(Screen.KeyboardEnable.route)
                }
            )
        }

        composable(Screen.KeyboardEnable.route) {
            KeyboardEnableScreen(
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Privacy.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onThemeClick = { themeId ->
                    navController.navigate(Screen.Preview.createRoute(themeId))
                },
                onFavoritesClick = {
                    navController.navigate(Screen.Favorites.route)
                },
                onProClick = {
                    navController.navigate(Screen.Pro.route)
                }
            )
        }

        composable(Screen.Preview.route) { backStackEntry ->
            val themeId = backStackEntry.arguments?.getString("themeId") ?: ""
            ThemePreviewScreen(
                themeId = themeId,
                onBack = { navController.popBackStack() },
                onApply = { navController.popBackStack() }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Pro.route) {
            ProScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Fonts.route) {
            FontsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
