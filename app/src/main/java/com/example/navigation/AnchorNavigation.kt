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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

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
fun AnchorNavigation(appContainer: AppContainer, initialBlockedApp: String? = null, onClearBlockedApp: () -> Unit = {}) {
    val navController = rememberNavController()

var currentBlockedApp by remember { mutableStateOf(initialBlockedApp) }
    
    if (currentBlockedApp != null) {
        com.example.ui.screens.block.BlockedOverlayScreen(
            packageName = currentBlockedApp ?: "",
            onGoHome = {
                currentBlockedApp = null
                onClearBlockedApp()
            }
        )
    } else {
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
                            onNavigateToBlocks = { navController.navigate("block") { popUpTo("dashboard") } },
                            
                            onNavigateToAnalytics = { navController.navigate("analytics") { popUpTo("dashboard") } },
                            onNavigateToAIStats = { navController.navigate("insights") { popUpTo("dashboard") } },
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }
                }
                composable("planner") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        com.example.ui.screens.planner.PlannerScreen(
                            onNavigateToFocus = { navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = true } } },
                            onNavigateToBlocks = { navController.navigate("block") { popUpTo("dashboard") } },
                            onNavigateToAddSchedule = { date -> navController.navigate("add_schedule/$date") },
                            onNavigateToEditSchedule = { date, id -> navController.navigate("add_schedule/$date?scheduleId=$id") },
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
                                    onNavigateToBlocks = { navController.navigate("block") { popUpTo("dashboard") } },
                            
                                    onNavigateToAnalytics = { },
                                    onNavigateToAIStats = { navController.navigate("insights") { popUpTo("dashboard") } },

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
                                    onNavigateToBlocks = { navController.navigate("block") { popUpTo("dashboard") } },
                            
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
                composable(
                    "add_schedule/{date}?scheduleId={scheduleId}",
                    arguments = listOf(
                        androidx.navigation.navArgument("date") { type = androidx.navigation.NavType.StringType },
                        androidx.navigation.navArgument("scheduleId") { type = androidx.navigation.NavType.IntType; defaultValue = -1 }
                    )
                ) { backStackEntry ->
                    val dateStr = backStackEntry.arguments?.getString("date") ?: ""
                    val scheduleId = backStackEntry.arguments?.getInt("scheduleId") ?: -1
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        com.example.ui.screens.planner.AddScheduleScreen(
                            dateStr = dateStr,
                            scheduleId = scheduleId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
                composable(
                    route = "blocked_overlay?pkg={pkg}",
                    arguments = listOf(androidx.navigation.navArgument("pkg") { type = androidx.navigation.NavType.StringType; nullable = true })
                ) { backStackEntry ->
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        val pkg = backStackEntry.arguments?.getString("pkg") ?: ""
                        com.example.ui.screens.block.BlockedOverlayScreen(
                            packageName = pkg,
                            onGoHome = {
                                navController.navigate("dashboard") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
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
}
