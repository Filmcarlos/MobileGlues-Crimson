package com.example.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Single source of truth for all Design System tokens in MobileGlues Crimson:
 * Colors (WCAG AA compliant on #0A0A0C and Light surfaces), Gradients, Typography Scale
 * (Thai + English via Prompt & JetBrains Mono), 4dp Spacing Grid, Corner Radii, Elevation,
 * and Motion Durations/Easing curves.
 */
object CrimsonDesignTokens {

    object Colors {
        // Primary Dark Obsidian Palette (#0A0A0C base)
        val ObsidianBackground = Color(0xFF0A0A0C)
        val ObsidianSurface = Color(0xFF121216)
        val ObsidianSurfaceElevated = Color(0xFF191920)
        val ObsidianSurfaceVariant = Color(0xFF22222B)
        val ObsidianBorder = Color(0xFF2E2E3A)
        val ObsidianBorderSubtle = Color(0xFF1F1F29)

        // Crimson Brand Accents (WCAG AA Checked)
        val CrimsonPrimaryDark = Color(0xFFFF4D63)        // Contrast on #0A0A0C = 6.5:1 (AA+ Pass)
        val CrimsonOnPrimaryDark = Color(0xFF280006)
        val CrimsonContainerDark = Color(0xFF520915)      // Deep ruby container
        val CrimsonOnContainerDark = Color(0xFFFFD9DC)    // Contrast on #520915 = 9.8:1 (AAA Pass)
        val CrimsonCoreVivid = Color(0xFFDC143C)          // Classic Crimson
        val CrimsonRubyDeep = Color(0xFF8B001D)

        // Secondary & Status Semantic Colors (Dark)
        val SecondarySilverDark = Color(0xFFE5BDC0)
        val OnSecondaryDark = Color(0xFF43292C)
        val SecondaryContainerDark = Color(0xFF362225)
        val OnSecondaryContainerDark = Color(0xFFFFD9DC)

        val TertiaryAmberDark = Color(0xFFFFB961)
        val OnTertiaryDark = Color(0xFF462A00)
        val TertiaryContainerDark = Color(0xFF643F00)
        val OnTertiaryContainerDark = Color(0xFFFFDDB7)

        val StatusReadyGreen = Color(0xFF34D399)          // Contrast on #0A0A0C = 9.9:1
        val StatusReadyContainerDark = Color(0xFF063E2B)
        val StatusWarningAmber = Color(0xFFFBBF24)        // Contrast on #0A0A0C = 11.4:1
        val StatusWarningContainerDark = Color(0xFF422D05)
        val StatusErrorCoral = Color(0xFFFF6B6B)          // Contrast on #0A0A0C = 7.4:1
        val StatusErrorContainerDark = Color(0xFF4D1217)

        // Text Hierarchy (Dark)
        val TextPrimaryDark = Color(0xFFF5F3F7)           // 17.6:1 on #0A0A0C
        val TextSecondaryDark = Color(0xFFB8B4C0)         // 9.7:1 on #0A0A0C
        val TextMutedDark = Color(0xFF8E8A96)             // 5.8:1 on #0A0A0C

        // Light Alabaster Palette (WCAG AA Checked)
        val AlabasterBackground = Color(0xFFFAF8F9)
        val AlabasterSurface = Color(0xFFFFFFFF)
        val AlabasterSurfaceElevated = Color(0xFFF4EFF1)
        val AlabasterSurfaceVariant = Color(0xFFECE4E7)
        val AlabasterBorder = Color(0xFFD6C9CD)

        val CrimsonPrimaryLight = Color(0xFFB00B29)       // Contrast on #FFFFFF = 7.1:1 (AAA Pass)
        val CrimsonOnPrimaryLight = Color(0xFFFFFFFF)
        val CrimsonContainerLight = Color(0xFFFFD9DC)
        val CrimsonOnContainerLight = Color(0xFF3F000A)

        val SecondaryLight = Color(0xFF755659)
        val OnSecondaryLight = Color(0xFFFFFFFF)
        val SecondaryContainerLight = Color(0xFFFFD9DC)
        val OnSecondaryContainerLight = Color(0xFF2C1518)

        val TertiaryLight = Color(0xFF7E5700)
        val OnTertiaryLight = Color(0xFFFFFFFF)
        val TertiaryContainerLight = Color(0xFFFFDEA6)
        val OnTertiaryContainerLight = Color(0xFF281900)

        val TextPrimaryLight = Color(0xFF1A1618)
        val TextSecondaryLight = Color(0xFF52474A)
    }

    object Gradients {
        val CrimsonHeroBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF3A0610),
                Color(0xFF1A070B),
                Color(0xFF0A0A0C)
            )
        )

        val CrimsonAccentBorderBrush = Brush.linearGradient(
            colors = listOf(
                Colors.CrimsonPrimaryDark.copy(alpha = 0.65f),
                Colors.CrimsonRubyDeep.copy(alpha = 0.25f),
                Colors.ObsidianBorder
            )
        )

        val CrimsonPrimaryButtonBrush = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFE11D48),
                Color(0xFFBE123C)
            )
        )
    }

    object Spacing {
        val xxs: Dp = 4.dp
        val xs: Dp = 8.dp
        val sm: Dp = 12.dp
        val md: Dp = 16.dp
        val lg: Dp = 20.dp
        val xl: Dp = 24.dp
        val xxl: Dp = 32.dp
        val xxxl: Dp = 48.dp
        val minTouchTarget: Dp = 48.dp
        val maxReadableContentWidth: Dp = 840.dp
    }

    object Radius {
        val xs: Dp = 6.dp
        val sm: Dp = 10.dp
        val md: Dp = 14.dp
        val lg: Dp = 20.dp
        val xl: Dp = 28.dp
        val pill: Dp = 999.dp

        val MaterialShapes = Shapes(
            extraSmall = RoundedCornerShape(xs),
            small = RoundedCornerShape(sm),
            medium = RoundedCornerShape(md),
            large = RoundedCornerShape(lg),
            extraLarge = RoundedCornerShape(xl)
        )
    }

    object Elevation {
        val none: Dp = 0.dp
        val low: Dp = 2.dp
        val medium: Dp = 6.dp
        val high: Dp = 12.dp
    }

    object Motion {
        const val DURATION_FAST_MS: Int = 150
        const val DURATION_MEDIUM_MS: Int = 240
        const val DURATION_SLOW_MS: Int = 360
        const val SHIMMER_CYCLE_MS: Int = 1100

        val StandardEasing: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
        val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    }

    object TypographyTokens {
        val PromptFamily = FontFamily(
            Font(R.font.prompt, FontWeight.Normal),
            Font(R.font.prompt, FontWeight.Medium),
            Font(R.font.prompt, FontWeight.SemiBold),
            Font(R.font.prompt, FontWeight.Bold)
        )

        val MonospaceFamily = FontFamily(
            Font(R.font.jetbrains_mono, FontWeight.Normal),
            Font(R.font.jetbrains_mono, FontWeight.Medium)
        )

        val MaterialTypography = Typography(
            displayLarge = TextStyle(
                fontFamily = PromptFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                lineHeight = 44.sp,
                letterSpacing = (-0.25).sp
            ),
            headlineLarge = TextStyle(
                fontFamily = PromptFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 36.sp
            ),
            headlineMedium = TextStyle(
                fontFamily = PromptFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                lineHeight = 30.sp
            ),
            titleLarge = TextStyle(
                fontFamily = PromptFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp,
                lineHeight = 26.sp
            ),
            titleMedium = TextStyle(
                fontFamily = PromptFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.1.sp
            ),
            titleSmall = TextStyle(
                fontFamily = PromptFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp
            ),
            bodyLarge = TextStyle(
                fontFamily = PromptFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 23.sp
            ),
            bodyMedium = TextStyle(
                fontFamily = PromptFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 20.sp
            ),
            bodySmall = TextStyle(
                fontFamily = PromptFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 18.sp
            ),
            labelLarge = TextStyle(
                fontFamily = PromptFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.2.sp
            ),
            labelMedium = TextStyle(
                fontFamily = MonospaceFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.3.sp
            ),
            labelSmall = TextStyle(
                fontFamily = MonospaceFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                letterSpacing = 0.4.sp
            )
        )
    }
}
