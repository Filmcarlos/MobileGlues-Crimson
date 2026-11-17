package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeveloperBoard
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.gateway.AndroidRendererGateway
import com.example.domain.model.LauncherAppStatus
import com.example.domain.model.NativeLibraryStatus
import com.example.domain.model.PluginOverviewSnapshot
import com.example.ui.components.AppScaffold
import com.example.ui.components.ErrorState
import com.example.ui.components.LoadingShimmer
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.components.SectionCard
import com.example.ui.components.StatTile
import com.example.ui.components.StatusPill
import com.example.ui.components.StatusPillTone
import com.example.ui.theme.CrimsonDesignTokens

@Composable
fun HomeScreen(
    uiState: CrimsonUiState,
    onEvent: (CrimsonUiEvent) -> Unit,
    onOpenLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppScaffold(
        title = stringResource(R.string.app_name),
        subtitle = stringResource(R.string.app_subtitle),
        modifier = modifier,
        actions = {
            IconButton(
                onClick = { onEvent(CrimsonUiEvent.RefreshInspection) },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("home_refresh_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = stringResource(R.string.action_refresh)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = CrimsonDesignTokens.Spacing.maxReadableContentWidth)
                    .testTag("home_lazy_column"),
                contentPadding = PaddingValues(
                    horizontal = CrimsonDesignTokens.Spacing.md,
                    vertical = CrimsonDesignTokens.Spacing.md
                ),
                verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.md)
            ) {
                when (val state = uiState.overviewState) {
                    OverviewLoadState.Loading -> {
                        item {
                            LoadingShimmer(barCount = 4)
                        }
                    }

                    is OverviewLoadState.Error -> {
                        item {
                            ErrorState(
                                message = state.message,
                                onRetry = { onEvent(CrimsonUiEvent.RefreshInspection) }
                            )
                        }
                    }

                    is OverviewLoadState.Ready -> {
                        val snapshot = state.snapshot
                        item {
                            HeroBannerCard(snapshot = snapshot)
                        }
                        item {
                            QuickTelemetryRow(
                                snapshot = snapshot,
                                logCount = uiState.logEntries.size
                            )
                        }
                        item {
                            NativeLibraryInspectionCard(
                                nativeStatus = snapshot.nativeStatus,
                                onRefresh = { onEvent(CrimsonUiEvent.RefreshInspection) },
                                onOpenLogs = onOpenLogs
                            )
                        }
                        item {
                            InstalledLaunchersCard(
                                launchers = snapshot.launchers
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(CrimsonDesignTokens.Spacing.lg))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroBannerCard(
    snapshot: PluginOverviewSnapshot
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_hero_card"),
        shape = RoundedCornerShape(CrimsonDesignTokens.Radius.xl),
        border = BorderStroke(1.dp, CrimsonDesignTokens.Gradients.CrimsonAccentBorderBrush),
        colors = CardDefaults.cardColors(
            containerColor = CrimsonDesignTokens.Colors.ObsidianSurface
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_crimson_core),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x990A0A0C),
                                Color(0xD90A0A0C),
                                Color(0xFF0A0A0C)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(CrimsonDesignTokens.Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.sm)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusPill(
                        text = stringResource(R.string.app_badge_foundation),
                        tone = StatusPillTone.CRIMSON
                    )
                    StatusPill(
                        text = "v${snapshot.manifestContract.appVersionName}",
                        tone = StatusPillTone.NEUTRAL
                    )
                }
                Spacer(modifier = Modifier.height(CrimsonDesignTokens.Spacing.xs))
                Text(
                    text = stringResource(R.string.home_hero_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = CrimsonDesignTokens.Colors.TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.home_hero_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CrimsonDesignTokens.Colors.TextSecondaryDark
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = CrimsonDesignTokens.Colors.StatusReadyGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.status_privacy_offline),
                        style = MaterialTheme.typography.labelSmall,
                        color = CrimsonDesignTokens.Colors.StatusReadyGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickTelemetryRow(
    snapshot: PluginOverviewSnapshot,
    logCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.sm)
        ) {
            StatTile(
                label = stringResource(R.string.home_stat_mc_range),
                value = stringResource(R.string.home_stat_mc_value),
                icon = Icons.Outlined.SportsEsports,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = stringResource(R.string.home_stat_gles),
                value = stringResource(R.string.home_stat_gles_value),
                icon = Icons.Outlined.DeveloperBoard,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.sm)
        ) {
            StatTile(
                label = stringResource(R.string.home_stat_log_entries),
                value = logCount.toString(),
                icon = Icons.Outlined.Terminal,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = stringResource(R.string.home_stat_crash_reports),
                value = snapshot.crashReportCount.toString(),
                icon = Icons.Outlined.BugReport,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NativeLibraryInspectionCard(
    nativeStatus: NativeLibraryStatus,
    onRefresh: () -> Unit,
    onOpenLogs: () -> Unit
) {
    SectionCard(
        title = stringResource(R.string.home_section_native_title),
        subtitle = stringResource(R.string.home_section_native_subtitle),
        icon = Icons.Outlined.Memory,
        modifier = Modifier.testTag("home_native_status_card"),
        headerTrailing = {
            StatusPill(
                text = if (nativeStatus.isDetected) {
                    stringResource(R.string.status_ready)
                } else {
                    stringResource(R.string.status_missing)
                },
                tone = if (nativeStatus.isDetected) {
                    StatusPillTone.SUCCESS
                } else {
                    StatusPillTone.WARNING
                }
            )
        }
    ) {
        Surface(
            shape = RoundedCornerShape(CrimsonDesignTokens.Radius.md),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(CrimsonDesignTokens.Spacing.md),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.sm)
            ) {
                Icon(
                    imageVector = if (nativeStatus.isDetected) {
                        Icons.Outlined.CheckCircle
                    } else {
                        Icons.Outlined.WarningAmber
                    },
                    contentDescription = null,
                    tint = if (nativeStatus.isDetected) {
                        CrimsonDesignTokens.Colors.StatusReadyGreen
                    } else {
                        CrimsonDesignTokens.Colors.StatusWarningAmber
                    },
                    modifier = Modifier.size(24.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (nativeStatus.isDetected) {
                            stringResource(R.string.home_native_detected_title)
                        } else {
                            stringResource(R.string.home_native_missing_title)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (nativeStatus.isDetected) {
                            nativeStatus.diagnosticNote
                        } else {
                            stringResource(R.string.home_native_missing_hint)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        KeyValueMonoRow(
            label = stringResource(R.string.home_native_label_abi),
            value = nativeStatus.primaryDeviceAbi
        )
        KeyValueMonoRow(
            label = stringResource(R.string.home_native_label_supported_abis),
            value = nativeStatus.supportedAbis.joinToString(", ")
        )
        KeyValueMonoRow(
            label = stringResource(R.string.home_native_label_size),
            value = if (nativeStatus.binarySizeBytes > 0L) {
                "${nativeStatus.binarySizeBytes / 1024} KB (${nativeStatus.binarySizeBytes} B)"
            } else {
                stringResource(R.string.home_native_size_none)
            }
        )
        KeyValueMonoRow(
            label = stringResource(R.string.home_native_label_dir),
            value = nativeStatus.nativeLibraryDirPath
        )

        Spacer(modifier = Modifier.height(CrimsonDesignTokens.Spacing.xxs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.sm)
        ) {
            PrimaryButton(
                text = stringResource(R.string.action_refresh),
                onClick = onRefresh,
                icon = Icons.Outlined.Refresh,
                modifier = Modifier.weight(1f),
                testTag = "home_recheck_native_button"
            )
            SecondaryButton(
                text = stringResource(R.string.action_view_logs),
                onClick = onOpenLogs,
                icon = Icons.Outlined.Terminal,
                modifier = Modifier.weight(1f),
                testTag = "home_open_logs_button"
            )
        }
    }
}

@Composable
private fun InstalledLaunchersCard(
    launchers: List<LauncherAppStatus>
) {
    SectionCard(
        title = stringResource(R.string.home_section_launchers_title),
        subtitle = stringResource(R.string.home_section_launchers_subtitle),
        icon = Icons.Outlined.Extension,
        modifier = Modifier.testTag("home_launchers_card")
    ) {
        launchers.forEachIndexed { index, item ->
            if (index > 0) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            val title = when (item.displayNameResKey) {
                AndroidRendererGateway.LAUNCHER_KEY_FCL -> stringResource(R.string.home_launcher_fcl_name)
                AndroidRendererGateway.LAUNCHER_KEY_ZALITH -> stringResource(R.string.home_launcher_zalith_name)
                else -> item.packageName
            }
            val subtitle = if (item.isInstalled) {
                stringResource(
                    R.string.home_launcher_version_format,
                    item.versionName.orEmpty(),
                    item.versionCode ?: 0L
                )
            } else {
                stringResource(R.string.home_launcher_not_found_desc, item.packageName)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = CrimsonDesignTokens.Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.packageName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusPill(
                    text = if (item.isInstalled) {
                        stringResource(R.string.status_installed)
                    } else {
                        stringResource(R.string.status_not_installed)
                    },
                    tone = if (item.isInstalled) {
                        StatusPillTone.SUCCESS
                    } else {
                        StatusPillTone.NEUTRAL
                    }
                )
            }
        }
    }
}

@Composable
private fun KeyValueMonoRow(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
