package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.CrimsonDesignTokens

enum class StatusPillTone {
    CRIMSON,
    SUCCESS,
    WARNING,
    NEUTRAL
}

/**
 * 1. AppScaffold — Shared screen scaffold with edge-to-edge WindowInsets, TopAppBar, and SnackbarHost.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onNavigateBack: (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            if (snackbarHostState != null) {
                SnackbarHost(hostState = snackbarHostState)
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("nav_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = bottomBar,
        content = content
    )
}

/**
 * 2. SectionCard — Layered Obsidian/Alabaster card with subtle border and smooth animateContentSize.
 */
@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    headerTrailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = CrimsonDesignTokens.Motion.DURATION_MEDIUM_MS,
                    easing = CrimsonDesignTokens.Motion.StandardEasing
                )
            ),
        shape = RoundedCornerShape(CrimsonDesignTokens.Radius.lg),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CrimsonDesignTokens.Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.sm)
                ) {
                    if (icon != null) {
                        Surface(
                            shape = RoundedCornerShape(CrimsonDesignTokens.Radius.sm),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                if (headerTrailing != null) {
                    Spacer(modifier = Modifier.width(CrimsonDesignTokens.Spacing.xs))
                    headerTrailing()
                }
            }
            content()
        }
    }
}

/**
 * 3. SettingRow — Interactive row with guaranteed ≥ 48dp touch target, icon, title, subtitle, and trailing slot.
 */
@Composable
fun SettingRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    val rowModifier = modifier
        .fillMaxWidth()
        .heightIn(min = CrimsonDesignTokens.Spacing.minTouchTarget)
        .clip(RoundedCornerShape(CrimsonDesignTokens.Radius.md))
        .let { base ->
            if (onClick != null) {
                base.clickable(onClick = onClick)
            } else {
                base
            }
        }
        .let { base ->
            if (testTag != null) base.testTag(testTag) else base
        }
        .padding(
            horizontal = CrimsonDesignTokens.Spacing.xs,
            vertical = CrimsonDesignTokens.Spacing.sm
        )

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.md)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(CrimsonDesignTokens.Spacing.sm))
            trailing()
        }
    }
}

/**
 * 4. StatTile — Compact telemetry tile displaying a key metric, label, and icon.
 */
@Composable
fun StatTile(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(CrimsonDesignTokens.Radius.md),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(CrimsonDesignTokens.Spacing.md),
            verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.xs)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.xs)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 5. PrimaryButton — High-contrast Crimson action button with ≥ 48dp touch height.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .heightIn(min = CrimsonDesignTokens.Spacing.minTouchTarget)
            .let { if (testTag != null) it.testTag(testTag) else it },
        shape = RoundedCornerShape(CrimsonDesignTokens.Radius.md),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(
            horizontal = CrimsonDesignTokens.Spacing.lg,
            vertical = CrimsonDesignTokens.Spacing.sm
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(CrimsonDesignTokens.Spacing.xs))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * 6. SecondaryButton — Outlined obsidian/crimson button with ≥ 48dp touch height.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .heightIn(min = CrimsonDesignTokens.Spacing.minTouchTarget)
            .let { if (testTag != null) it.testTag(testTag) else it },
        shape = RoundedCornerShape(CrimsonDesignTokens.Radius.md),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        contentPadding = PaddingValues(
            horizontal = CrimsonDesignTokens.Spacing.lg,
            vertical = CrimsonDesignTokens.Spacing.sm
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(CrimsonDesignTokens.Spacing.xs))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * 7. StatusPill — Monospace badge pill for statuses (READY, MISSING, COMING SOON, INSTALLED).
 */
@Composable
fun StatusPill(
    text: String,
    modifier: Modifier = Modifier,
    tone: StatusPillTone = StatusPillTone.CRIMSON
) {
    val (bgColor, fgColor) = when (tone) {
        StatusPillTone.CRIMSON -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        StatusPillTone.SUCCESS -> CrimsonDesignTokens.Colors.StatusReadyContainerDark to CrimsonDesignTokens.Colors.StatusReadyGreen
        StatusPillTone.WARNING -> CrimsonDesignTokens.Colors.StatusWarningContainerDark to CrimsonDesignTokens.Colors.StatusWarningAmber
        StatusPillTone.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(CrimsonDesignTokens.Radius.pill),
        color = bgColor,
        border = BorderStroke(1.dp, fgColor.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = CrimsonDesignTokens.Spacing.sm,
                vertical = CrimsonDesignTokens.Spacing.xxs
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(fgColor)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = fgColor
            )
        }
    }
}

/**
 * 8. EmptyState — Crafted architectural placeholder ("เร็วๆ นี้" / Coming Soon) that looks intentional and premium.
 */
@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.AutoAwesome,
    badgeText: String = stringResource(R.string.status_coming_soon_badge),
    actionButton: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CrimsonDesignTokens.Radius.lg),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, CrimsonDesignTokens.Gradients.CrimsonAccentBorderBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CrimsonDesignTokens.Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.sm)
        ) {
            StatusPill(
                text = badgeText,
                tone = StatusPillTone.CRIMSON
            )
            Spacer(modifier = Modifier.height(CrimsonDesignTokens.Spacing.xxs))
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (actionButton != null) {
                Spacer(modifier = Modifier.height(CrimsonDesignTokens.Spacing.xs))
                actionButton()
            }
        }
    }
}

/**
 * 9. ErrorState — Explicit error card with retry action so no failure is silently swallowed.
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("error_state_card"),
        shape = RoundedCornerShape(CrimsonDesignTokens.Radius.lg),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CrimsonDesignTokens.Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.sm)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            SecondaryButton(
                text = stringResource(R.string.action_retry),
                onClick = onRetry,
                testTag = "error_retry_button"
            )
        }
    }
}

/**
 * 10. LoadingShimmer — Lightweight animated shimmer placeholder for asynchronous inspection states.
 */
@Composable
fun LoadingShimmer(
    modifier: Modifier = Modifier,
    barCount: Int = 3
) {
    val transition = rememberInfiniteTransition(label = "crimson_shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = CrimsonDesignTokens.Motion.SHIMMER_CYCLE_MS,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val baseColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    val highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(progress - 300f, 0f),
        end = Offset(progress, 200f)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("loading_shimmer"),
        verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.sm)
    ) {
        repeat(barCount) { idx ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (idx == barCount - 1) 0.7f else 1f)
                    .height(68.dp)
                    .clip(RoundedCornerShape(CrimsonDesignTokens.Radius.md))
                    .background(brush)
            )
        }
    }
}

/**
 * 11. ConfirmDialog — Accessible confirmation dialog for destructive or reset operations.
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("confirm_dialog_confirm")
            ) {
                Text(
                    text = confirmText,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("confirm_dialog_cancel")
            ) {
                Text(
                    text = cancelText,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}
