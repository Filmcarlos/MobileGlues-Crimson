package com.example.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.R
import kotlinx.serialization.Serializable

/**
 * Type-safe `@Serializable` routes for Jetpack Navigation Compose.
 */
sealed interface CrimsonRoute {
    @Serializable
    data object Home : CrimsonRoute

    @Serializable
    data object Settings : CrimsonRoute

    @Serializable
    data object Tools : CrimsonRoute

    @Serializable
    data object Info : CrimsonRoute

    @Serializable
    data object About : CrimsonRoute

    @Serializable
    data object LogViewer : CrimsonRoute
}

/**
 * Metadata for the 5 primary navigation destinations shown in the Bottom NavigationBar (Compact)
 * or NavigationRail (Medium / Expanded).
 */
enum class TopLevelDestination(
    val route: CrimsonRoute,
    @StringRes val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME(
        route = CrimsonRoute.Home,
        labelResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_tab_home"
    ),
    SETTINGS(
        route = CrimsonRoute.Settings,
        labelResId = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        testTag = "nav_tab_settings"
    ),
    TOOLS(
        route = CrimsonRoute.Tools,
        labelResId = R.string.nav_tools,
        selectedIcon = Icons.Filled.Build,
        unselectedIcon = Icons.Outlined.Build,
        testTag = "nav_tab_tools"
    ),
    INFO(
        route = CrimsonRoute.Info,
        labelResId = R.string.nav_info,
        selectedIcon = Icons.Filled.Description,
        unselectedIcon = Icons.Outlined.Description,
        testTag = "nav_tab_info"
    ),
    ABOUT(
        route = CrimsonRoute.About,
        labelResId = R.string.nav_about,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info,
        testTag = "nav_tab_about"
    )
}
