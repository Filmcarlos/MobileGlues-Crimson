package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.domain.model.AppThemeMode

/**
 * Full Material 3 Crimson Dark ColorScheme featuring deep reds, dark ruby containers,
 * and layered dark charcoal/obsidian grays (#0A0A0C - #2A2A34).
 */
val CrimsonDarkColorScheme = darkColorScheme(
    primary = CrimsonDesignTokens.Colors.CrimsonPrimaryDark,
    onPrimary = CrimsonDesignTokens.Colors.CrimsonOnPrimaryDark,
    primaryContainer = CrimsonDesignTokens.Colors.CrimsonContainerDark,
    onPrimaryContainer = CrimsonDesignTokens.Colors.CrimsonOnContainerDark,
    inversePrimary = CrimsonDesignTokens.Colors.CrimsonCoreVivid,
    secondary = CrimsonDesignTokens.Colors.SecondarySilverDark,
    onSecondary = CrimsonDesignTokens.Colors.OnSecondaryDark,
    secondaryContainer = CrimsonDesignTokens.Colors.SecondaryContainerDark,
    onSecondaryContainer = CrimsonDesignTokens.Colors.OnSecondaryContainerDark,
    tertiary = CrimsonDesignTokens.Colors.TertiaryAmberDark,
    onTertiary = CrimsonDesignTokens.Colors.OnTertiaryDark,
    tertiaryContainer = CrimsonDesignTokens.Colors.TertiaryContainerDark,
    onTertiaryContainer = CrimsonDesignTokens.Colors.OnTertiaryContainerDark,
    background = CrimsonDesignTokens.Colors.ObsidianBackground,
    onBackground = CrimsonDesignTokens.Colors.TextPrimaryDark,
    surface = CrimsonDesignTokens.Colors.ObsidianSurface,
    onSurface = CrimsonDesignTokens.Colors.TextPrimaryDark,
    surfaceVariant = CrimsonDesignTokens.Colors.ObsidianSurfaceVariant,
    onSurfaceVariant = CrimsonDesignTokens.Colors.TextSecondaryDark,
    surfaceTint = CrimsonDesignTokens.Colors.CrimsonPrimaryDark,
    surfaceContainerLowest = Color(0xFF070709),
    surfaceContainerLow = Color(0xFF101014),
    surfaceContainer = CrimsonDesignTokens.Colors.ObsidianSurface,
    surfaceContainerHigh = CrimsonDesignTokens.Colors.ObsidianSurfaceElevated,
    surfaceContainerHighest = CrimsonDesignTokens.Colors.ObsidianSurfaceVariant,
    outline = CrimsonDesignTokens.Colors.ObsidianBorder,
    outlineVariant = CrimsonDesignTokens.Colors.ObsidianBorderSubtle,
    error = CrimsonDesignTokens.Colors.StatusErrorCoral,
    errorContainer = CrimsonDesignTokens.Colors.StatusErrorContainerDark
)

val CrimsonLightColorScheme = lightColorScheme(
    primary = CrimsonDesignTokens.Colors.CrimsonPrimaryLight,
    onPrimary = CrimsonDesignTokens.Colors.CrimsonOnPrimaryLight,
    primaryContainer = CrimsonDesignTokens.Colors.CrimsonContainerLight,
    onPrimaryContainer = CrimsonDesignTokens.Colors.CrimsonOnContainerLight,
    secondary = CrimsonDesignTokens.Colors.SecondaryLight,
    onSecondary = CrimsonDesignTokens.Colors.OnSecondaryLight,
    secondaryContainer = CrimsonDesignTokens.Colors.SecondaryContainerLight,
    onSecondaryContainer = CrimsonDesignTokens.Colors.OnSecondaryContainerLight,
    tertiary = CrimsonDesignTokens.Colors.TertiaryLight,
    onTertiary = CrimsonDesignTokens.Colors.OnTertiaryLight,
    tertiaryContainer = CrimsonDesignTokens.Colors.TertiaryContainerLight,
    onTertiaryContainer = CrimsonDesignTokens.Colors.OnTertiaryContainerLight,
    background = CrimsonDesignTokens.Colors.AlabasterBackground,
    onBackground = CrimsonDesignTokens.Colors.TextPrimaryLight,
    surface = CrimsonDesignTokens.Colors.AlabasterSurface,
    onSurface = CrimsonDesignTokens.Colors.TextPrimaryLight,
    surfaceVariant = CrimsonDesignTokens.Colors.AlabasterSurfaceVariant,
    onSurfaceVariant = CrimsonDesignTokens.Colors.TextSecondaryLight,
    surfaceTint = CrimsonDesignTokens.Colors.CrimsonPrimaryLight,
    outline = CrimsonDesignTokens.Colors.AlabasterBorder
)

/**
 * Root Crimson Material 3 Theme for [com.example.MainActivity].
 * Defaults to [AppThemeMode.DARK] (#0A0A0C) and `dynamicColor = false` so the signature
 * deep red & dark gray aesthetic is active out-of-the-box.
 */
@Composable
fun CrimsonTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> CrimsonDarkColorScheme
        else -> CrimsonLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDark
            insetsController.isAppearanceLightNavigationBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CrimsonDesignTokens.TypographyTokens.MaterialTypography,
        shapes = CrimsonDesignTokens.Radius.MaterialShapes,
        content = content
    )
}

@Composable
fun MobileGluesCrimsonTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CrimsonTheme(
        themeMode = themeMode,
        dynamicColor = dynamicColor,
        content = content
    )
}
