package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.ui.components.AppScaffold
import com.example.ui.components.EmptyState
import com.example.ui.components.SecondaryButton
import com.example.ui.components.SectionCard
import com.example.ui.components.SettingRow
import com.example.ui.components.StatusPill
import com.example.ui.components.StatusPillTone
import com.example.ui.theme.CrimsonDesignTokens

@Composable
fun ToolsScreen(
    uiState: CrimsonUiState,
    onEvent: (CrimsonUiEvent) -> Unit,
    onOpenLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snapshot = (uiState.overviewState as? OverviewLoadState.Ready)?.snapshot
    val crashCount = snapshot?.crashReportCount ?: 0

    AppScaffold(
        title = stringResource(R.string.tools_title),
        subtitle = stringResource(R.string.tools_diagnostics_subtitle),
        modifier = modifier
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
                    .testTag("tools_lazy_column"),
                contentPadding = PaddingValues(CrimsonDesignTokens.Spacing.md),
                verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.md)
            ) {
                item {
                    SectionCard(
                        title = stringResource(R.string.tools_diagnostics_section),
                        subtitle = stringResource(R.string.tools_diagnostics_subtitle),
                        icon = Icons.Outlined.Science
                    ) {
                        SettingRow(
                            title = stringResource(R.string.tools_open_logs_title),
                            subtitle = stringResource(R.string.tools_open_logs_subtitle),
                            icon = Icons.Outlined.Terminal,
                            onClick = onOpenLogs,
                            testTag = "tools_open_logs_row",
                            trailing = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        SettingRow(
                            title = if (crashCount == 0) {
                                stringResource(R.string.tools_crash_status_clean)
                            } else {
                                stringResource(R.string.tools_crash_status_found, crashCount)
                            },
                            subtitle = snapshot?.latestCrashSummary,
                            icon = Icons.Outlined.BugReport,
                            trailing = {
                                StatusPill(
                                    text = crashCount.toString(),
                                    tone = if (crashCount == 0) StatusPillTone.SUCCESS else StatusPillTone.WARNING
                                )
                            }
                        )

                        if (crashCount > 0) {
                            Spacer(modifier = Modifier.height(CrimsonDesignTokens.Spacing.xxs))
                            SecondaryButton(
                                text = stringResource(R.string.tools_crash_clear_action),
                                onClick = { onEvent(CrimsonUiEvent.ClearCrashReports) },
                                icon = Icons.Outlined.DeleteSweep,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "tools_clear_crashes_button"
                            )
                        }
                    }
                }

                item {
                    EmptyState(
                        title = stringResource(R.string.tools_placeholder_title),
                        description = stringResource(R.string.tools_placeholder_desc),
                        icon = Icons.Outlined.Speed
                    )
                }
            }
        }
    }
}
