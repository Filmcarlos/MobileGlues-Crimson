package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.BuildConfig
import com.example.R
import com.example.domain.model.OpenSourceLicenseItem
import com.example.ui.components.AppScaffold
import com.example.ui.components.SectionCard
import com.example.ui.components.SettingRow
import com.example.ui.components.StatusPill
import com.example.ui.components.StatusPillTone
import com.example.ui.theme.CrimsonDesignTokens

@Composable
fun AboutScreen(
    uiState: CrimsonUiState,
    onEvent: (CrimsonUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedLicense = uiState.selectedLicenseDialog
    if (selectedLicense != null) {
        LicenseNoticeDialog(
            item = selectedLicense,
            onDismiss = { onEvent(CrimsonUiEvent.DismissLicenseDetail) }
        )
    }

    AppScaffold(
        title = stringResource(R.string.about_title),
        subtitle = stringResource(
            R.string.about_version_format,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        ),
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
                    .testTag("about_lazy_column"),
                contentPadding = PaddingValues(CrimsonDesignTokens.Spacing.md),
                verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.md)
            ) {
                item {
                    SectionCard(
                        title = stringResource(R.string.about_plugin_card_title),
                        subtitle = stringResource(R.string.about_sdk_format),
                        icon = Icons.Outlined.Info,
                        headerTrailing = {
                            StatusPill(
                                text = "v${BuildConfig.VERSION_NAME}",
                                tone = StatusPillTone.CRIMSON
                            )
                        }
                    ) {
                        Text(
                            text = stringResource(
                                R.string.about_version_format,
                                BuildConfig.VERSION_NAME,
                                BuildConfig.VERSION_CODE
                            ),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(
                                R.string.about_package_format,
                                BuildConfig.APPLICATION_ID
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                item {
                    SectionCard(
                        title = stringResource(R.string.about_privacy_title),
                        icon = Icons.Outlined.VerifiedUser,
                        headerTrailing = {
                            StatusPill(
                                text = stringResource(R.string.status_ready),
                                tone = StatusPillTone.SUCCESS
                            )
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.about_privacy_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    SectionCard(
                        title = stringResource(R.string.about_licenses_section),
                        subtitle = stringResource(R.string.about_licenses_subtitle),
                        icon = Icons.Outlined.Gavel,
                        modifier = Modifier.testTag("about_licenses_card")
                    ) {
                        uiState.licenses.forEachIndexed { index, item ->
                            if (index > 0) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                            SettingRow(
                                title = stringResource(item.nameResId),
                                subtitle = "${stringResource(item.spdxResId)}\n${stringResource(item.summaryResId)}",
                                onClick = { onEvent(CrimsonUiEvent.OpenLicenseDetail(item)) },
                                testTag = "license_row_${item.id}",
                                trailing = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LicenseNoticeDialog(
    item: OpenSourceLicenseItem,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = stringResource(item.nameResId),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(item.spdxResId),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.sm)
            ) {
                Text(
                    text = item.fullNoticeText.trim(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("license_dialog_close")
            ) {
                Text(
                    text = stringResource(R.string.action_confirm),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}
