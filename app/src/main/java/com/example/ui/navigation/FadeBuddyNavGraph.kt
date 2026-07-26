package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ClientProfilesScreen
import com.example.ui.screens.CutPlanBuilderScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.GuidedCutScreen
import com.example.ui.screens.HairAnalyzerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

sealed class NavDestination(val route: String, val title: String, val icon: ImageVector) {
    object Home : NavDestination("home", "Dashboard", Icons.Default.Dashboard)
    object CutPlanner : NavDestination("cut_planner", "Fade Plan", Icons.Default.ContentCut)
    object GuidedCut : NavDestination("guided_cut", "Live Coach", Icons.Default.PlayArrow)
    object Profiles : NavDestination("profiles", "Clients", Icons.Default.Group)
    object Diagnostics : NavDestination("diagnostics", "Clipper Health", Icons.Default.SettingsSuggest)
    object HairAnalyzer : NavDestination("hair_analyzer", "AI Advisor", Icons.Default.AutoAwesome)
}

val bottomNavItems = listOf(
    NavDestination.Home,
    NavDestination.CutPlanner,
    NavDestination.GuidedCut,
    NavDestination.Profiles,
    NavDestination.Diagnostics,
    NavDestination.HairAnalyzer
)

@Composable
fun FadeBuddyAppContent(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavDestination.Home.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DarkNavySurface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (selected) CyanAccent else TextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                color = if (selected) CyanAccent else TextSecondary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = SurfaceVariantDark
                        ),
                        modifier = Modifier.testTag("nav_item_${item.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestination.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavDestination.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onStartPlanClick = { navController.navigate(NavDestination.CutPlanner.route) },
                    onStartLiveCutClick = { navController.navigate(NavDestination.GuidedCut.route) },
                    onOpenDiagnosticsClick = { navController.navigate(NavDestination.Diagnostics.route) },
                    onOpenAiAdvisorClick = { navController.navigate(NavDestination.HairAnalyzer.route) }
                )
            }
            composable(NavDestination.CutPlanner.route) {
                CutPlanBuilderScreen(
                    viewModel = viewModel,
                    onStartGuidedCut = { navController.navigate(NavDestination.GuidedCut.route) }
                )
            }
            composable(NavDestination.GuidedCut.route) {
                GuidedCutScreen(viewModel = viewModel)
            }
            composable(NavDestination.Profiles.route) {
                ClientProfilesScreen(
                    viewModel = viewModel,
                    onSelectClientForPlan = { client ->
                        navController.navigate(NavDestination.CutPlanner.route)
                    }
                )
            }
            composable(NavDestination.Diagnostics.route) {
                DiagnosticsScreen(viewModel = viewModel)
            }
            composable(NavDestination.HairAnalyzer.route) {
                HairAnalyzerScreen(
                    viewModel = viewModel,
                    onApplyAiRecipeToPlan = {
                        navController.navigate(NavDestination.CutPlanner.route)
                    }
                )
            }
        }
    }
}
