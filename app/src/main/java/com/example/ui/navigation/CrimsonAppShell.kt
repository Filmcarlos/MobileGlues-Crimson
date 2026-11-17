package com.example.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.CrimsonUiEvent
import com.example.ui.screens.CrimsonUiState
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InfoScreen
import com.example.ui.screens.LogViewerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.theme.CrimsonDesignTokens

/**
 * Adaptive navigation shell supporting both `NavigationBar` (Compact handhelds) and
 * `NavigationRail` (Medium / Expanded tablets & landscape) with smooth fade motion transitions.
 */
@Composable
fun CrimsonAppShell(
    widthSizeClass: WindowWidthSizeClass,
    uiState: CrimsonUiState,
    onEvent: (CrimsonUiEvent) -> Unit,
    onExportLogsText: suspend () -> String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isLogViewerActive = currentDestination?.hasRoute<CrimsonRoute.LogViewer>() == true
    val useNavigationRail = widthSizeClass != WindowWidthSizeClass.Compact

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (!useNavigationRail && !isLogViewerActive) {
                CrimsonBottomNavigationBar(
                    currentDestination = currentDestination,
                    onSelectDestination = { destination ->
                        navigateTopLevel(navController, destination.route)
                    }
                )
            }
        }
    ) { outerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPadding)
        ) {
            if (useNavigationRail && !isLogViewerActive) {
                CrimsonSideNavigationRail(
                    currentDestination = currentDestination,
                    onSelectDestination = { destination ->
                        navigateTopLevel(navController, destination.route)
                    }
                )
            }

            NavHost(
                navController = navController,
                startDestination = CrimsonRoute.Home,
                modifier = Modifier.weight(1f),
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            durationMillis = CrimsonDesignTokens.Motion.DURATION_MEDIUM_MS,
                            easing = CrimsonDesignTokens.Motion.StandardEasing
                        )
                    )
                },
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(
                            durationMillis = CrimsonDesignTokens.Motion.DURATION_FAST_MS,
                            easing = CrimsonDesignTokens.Motion.StandardEasing
                        )
                    )
                }
            ) {
                composable<CrimsonRoute.Home> {
                    HomeScreen(
                        uiState = uiState,
                        onEvent = onEvent,
                        onOpenLogs = { navController.navigate(CrimsonRoute.LogViewer) }
                    )
                }
                composable<CrimsonRoute.Settings> {
                    SettingsScreen(
                        uiState = uiState,
                        onEvent = onEvent
                    )
                }
                composable<CrimsonRoute.Tools> {
                    ToolsScreen(
                        uiState = uiState,
                        onEvent = onEvent,
                        onOpenLogs = { navController.navigate(CrimsonRoute.LogViewer) }
                    )
                }
                composable<CrimsonRoute.Info> {
                    InfoScreen(
                        uiState = uiState,
                        onEvent = onEvent
                    )
                }
                composable<CrimsonRoute.About> {
                    AboutScreen(
                        uiState = uiState,
                        onEvent = onEvent
                    )
                }
                composable<CrimsonRoute.LogViewer> {
                    LogViewerScreen(
                        uiState = uiState,
                        onEvent = onEvent,
                        onExportLogsText = onExportLogsText,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun CrimsonBottomNavigationBar(
    currentDestination: NavDestination?,
    onSelectDestination: (TopLevelDestination) -> Unit
) {
    NavigationBar(
        windowInsets = WindowInsets.navigationBars,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("crimson_bottom_nav_bar")
    ) {
        TopLevelDestination.entries.forEach { destination ->
            val selected = currentDestination.isOnDestination(destination)
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectDestination(destination) },
                modifier = Modifier.testTag(destination.testTag),
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = stringResource(destination.labelResId)
                    )
                },
                label = {
                    Text(
                        text = stringResource(destination.labelResId),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun CrimsonSideNavigationRail(
    currentDestination: NavDestination?,
    onSelectDestination: (TopLevelDestination) -> Unit
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxHeight()
            .testTag("crimson_nav_rail")
    ) {
        Spacer(modifier = Modifier.height(CrimsonDesignTokens.Spacing.md))
        TopLevelDestination.entries.forEach { destination ->
            val selected = currentDestination.isOnDestination(destination)
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectDestination(destination) },
                modifier = Modifier
                    .padding(vertical = CrimsonDesignTokens.Spacing.xxs)
                    .testTag(destination.testTag),
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = stringResource(destination.labelResId)
                    )
                },
                label = {
                    Text(
                        text = stringResource(destination.labelResId),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

private fun NavDestination?.isOnDestination(destination: TopLevelDestination): Boolean {
    if (this == null) return false
    return when (destination.route) {
        CrimsonRoute.Home -> hasRoute<CrimsonRoute.Home>()
        CrimsonRoute.Settings -> hasRoute<CrimsonRoute.Settings>()
        CrimsonRoute.Tools -> hasRoute<CrimsonRoute.Tools>()
        CrimsonRoute.Info -> hasRoute<CrimsonRoute.Info>()
        CrimsonRoute.About -> hasRoute<CrimsonRoute.About>()
        CrimsonRoute.LogViewer -> hasRoute<CrimsonRoute.LogViewer>()
    }
}

private fun navigateTopLevel(
    navController: NavHostController,
    route: CrimsonRoute
) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
