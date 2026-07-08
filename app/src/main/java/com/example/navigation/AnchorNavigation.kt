@file:OptIn(ExperimentalSharedTransitionApi::class)
package com.example.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.AppContainer
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.onboarding.LoginScreen
import com.example.ui.screens.onboarding.PledgeScreen
import com.example.ui.screens.dashboard.DashboardScreen

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AnchorNavigation(appContainer: AppContainer) {
    val navController = rememberNavController()

    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHost(
                navController = navController, 
                startDestination = "onboarding",
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) {
                composable("onboarding") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        OnboardingScreen(
                            onFinish = {
                                navController.navigate("login")
                            }
                        )
                    }
                }
                composable("login") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        LoginScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onFinish = {
                                navController.navigate("pledge") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        )
                    }
                }
                composable("pledge") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        PledgeScreen(
                            onFinish = {
                                navController.navigate("dashboard") {
                                    popUpTo("pledge") { inclusive = true }
                                }
                            },
                            onSkip = {
                                navController.navigate("dashboard") {
                                    popUpTo("pledge") { inclusive = true }
                                }
                            }
                        )
                    }
                }
                composable("dashboard") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        DashboardScreen(
                            onNavigateToPlanner = { navController.navigate("planner") { popUpTo("dashboard") } },
                            onNavigateToAnalytics = { navController.navigate("analytics") { popUpTo("dashboard") } },
                            onNavigateToAIStats = { navController.navigate("insights") { popUpTo("dashboard") } }
                        )
                    }
                }
                composable("planner") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        com.example.ui.screens.planner.PlannerScreen(
                            onNavigateToFocus = { navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = true } } },
                            onNavigateToAnalytics = { navController.navigate("analytics") { popUpTo("dashboard") } },
                            onNavigateToAIStats = { navController.navigate("insights") { popUpTo("dashboard") } }
                        )
                    }
                }
                composable("analytics") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        androidx.compose.material3.Scaffold(
                            bottomBar = {
                                com.example.ui.components.BottomNavigationBar(
                                    currentRoute = "analytics",
                                    onNavigateToFocus = { navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = true } } },
                                    onNavigateToPlanner = { navController.navigate("planner") { popUpTo("dashboard") } },
                                    onNavigateToAnalytics = { },
                                    onNavigateToAIStats = { navController.navigate("insights") { popUpTo("dashboard") } }
                                )
                            }
                        ) { padding ->
                            androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(padding)) {
                                com.example.ui.screens.analytics.AnalyticsScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
                composable("insights") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        androidx.compose.material3.Scaffold(
                            bottomBar = {
                                com.example.ui.components.BottomNavigationBar(
                                    currentRoute = "insights",
                                    onNavigateToFocus = { navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = true } } },
                                    onNavigateToPlanner = { navController.navigate("planner") { popUpTo("dashboard") } },
                                    onNavigateToAnalytics = { navController.navigate("analytics") { popUpTo("dashboard") } },
                                    onNavigateToAIStats = { }
                                )
                            }
                        ) { padding ->
                            androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(padding)) {
                                com.example.ui.screens.insights.InsightsScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
                composable("settings") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        com.example.ui.screens.settings.SettingsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
                composable("block") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        com.example.ui.screens.block.BlockScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
