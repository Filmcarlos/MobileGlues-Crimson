package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.domain.model.AppThemeMode

private val CrimsonDarkColorScheme = darkColorScheme(
    primary = CrimsonDesignTokens.Colors.CrimsonPrimaryDark,
    onPrimary = CrimsonDesignTokens.Colors.CrimsonOnPrimaryDark,
    primaryContainer = CrimsonDesignTokens.Colors.CrimsonContainerDark,
    onPrimaryContainer = CrimsonDesignTokens.Colors.CrimsonOnContainerDark,
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
    outline = CrimsonDesignTokens.Colors.ObsidianBorder,
    outlineVariant = CrimsonDesignTokens.Colors.ObsidianBorderSubtle,
    error = CrimsonDesignTokens.Colors.StatusErrorCoral,
    errorContainer = CrimsonDesignTokens.Colors.StatusErrorContainerDark
)

private val CrimsonLightColorScheme = lightColorScheme(
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
    outline = CrimsonDesignTokens.Colors.AlabasterBorder
)

/**
 * Root theme for MobileGlues Crimson.
 * Defaults to [AppThemeMode.DARK] (#0A0A0C) and `dynamicColor = false` so the signature
 * Crimson Obsidian aesthetic remains consistent unless the user explicitly toggles them in Settings.
 */
@Composable
fun MobileGluesCrimsonTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CrimsonDesignTokens.TypographyTokens.MaterialTypography,
        shapes = CrimsonDesignTokens.Radius.MaterialShapes,
        content = content
    )
}
