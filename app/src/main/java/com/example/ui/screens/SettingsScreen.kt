package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppThemeMode
import com.example.ui.components.AppScaffold
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.EmptyState
import com.example.ui.components.SecondaryButton
import com.example.ui.components.SectionCard
import com.example.ui.components.SettingRow
import com.example.ui.theme.CrimsonDesignTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    uiState: CrimsonUiState,
    onEvent: (CrimsonUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isResetDialogVisible) {
        ConfirmDialog(
            title = stringResource(R.string.settings_reset_dialog_title),
            message = stringResource(R.string.settings_reset_dialog_message),
            confirmText = stringResource(R.string.action_confirm),
            cancelText = stringResource(R.string.action_cancel),
            onConfirm = { onEvent(CrimsonUiEvent.ConfirmResetPreferences) },
            onDismiss = { onEvent(CrimsonUiEvent.DismissResetPreferences) }
        )
    }

    AppScaffold(
        title = stringResource(R.string.settings_title),
        subtitle = stringResource(R.string.settings_Appearance_subtitle),
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
                    .testTag("settings_lazy_column"),
                contentPadding = PaddingValues(CrimsonDesignTokens.Spacing.md),
                verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.md)
            ) {
                item {
                    SectionCard(
                        title = stringResource(R.string.settings_Appearance_section),
                        subtitle = stringResource(R.string.settings_Appearance_subtitle),
                        icon = Icons.Outlined.ColorLens
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.xs)) {
                            SettingRow(
                                title = stringResource(R.string.settings_theme_label),
                                icon = Icons.Outlined.DarkMode
                            )
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.xs),
                                verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.xs)
                            ) {
                                ThemeFilterChip(
                                    label = stringResource(R.string.settings_theme_dark),
                                    selected = uiState.preferences.themeMode == AppThemeMode.DARK,
                                    onClick = { onEvent(CrimsonUiEvent.SelectThemeMode(AppThemeMode.DARK)) },
                                    testTag = "theme_chip_dark"
                                )
                                ThemeFilterChip(
                                    label = stringResource(R.string.settings_theme_light),
                                    selected = uiState.preferences.themeMode == AppThemeMode.LIGHT,
                                    onClick = { onEvent(CrimsonUiEvent.SelectThemeMode(AppThemeMode.LIGHT)) },
                                    testTag = "theme_chip_light"
                                )
                                ThemeFilterChip(
                                    label = stringResource(R.string.settings_theme_system),
                                    selected = uiState.preferences.themeMode == AppThemeMode.SYSTEM,
                                    onClick = { onEvent(CrimsonUiEvent.SelectThemeMode(AppThemeMode.SYSTEM)) },
                                    testTag = "theme_chip_system"
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        SettingRow(
                            title = stringResource(R.string.settings_dynamic_color_title),
                            subtitle = stringResource(R.string.settings_dynamic_color_subtitle),
                            icon = Icons.Outlined.ColorLens,
                            onClick = {
                                onEvent(CrimsonUiEvent.ToggleDynamicColor(!uiState.preferences.dynamicColorEnabled))
                            },
                            testTag = "settings_dynamic_color_row",
                            trailing = {
                                Switch(
                                    checked = uiState.preferences.dynamicColorEnabled,
                                    onCheckedChange = { enabled ->
                                        onEvent(CrimsonUiEvent.ToggleDynamicColor(enabled))
                                    },
                                    modifier = Modifier.testTag("settings_dynamic_color_switch")
                                )
                            }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        Column(verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.xs)) {
                            SettingRow(
                                title = stringResource(R.string.settings_language_label),
                                icon = Icons.Outlined.Language
                            )
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.xs),
                                verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.xs)
                            ) {
                                ThemeFilterChip(
                                    label = stringResource(R.string.settings_language_system),
                                    selected = uiState.preferences.language == AppLanguage.SYSTEM,
                                    onClick = { onEvent(CrimsonUiEvent.SelectLanguage(AppLanguage.SYSTEM)) },
                                    testTag = "lang_chip_system"
                                )
                                ThemeFilterChip(
                                    label = stringResource(R.string.settings_language_th),
                                    selected = uiState.preferences.language == AppLanguage.THAI,
                                    onClick = { onEvent(CrimsonUiEvent.SelectLanguage(AppLanguage.THAI)) },
                                    testTag = "lang_chip_th"
                                )
                                ThemeFilterChip(
                                    label = stringResource(R.string.settings_language_en),
                                    selected = uiState.preferences.language == AppLanguage.ENGLISH,
                                    onClick = { onEvent(CrimsonUiEvent.SelectLanguage(AppLanguage.ENGLISH)) },
                                    testTag = "lang_chip_en"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(CrimsonDesignTokens.Spacing.xs))
                        SecondaryButton(
                            text = stringResource(R.string.action_reset_defaults),
                            onClick = { onEvent(CrimsonUiEvent.RequestResetPreferences) },
                            icon = Icons.Outlined.RestartAlt,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "settings_reset_defaults_button"
                        )
                    }
                }

                item {
                    EmptyState(
                        title = stringResource(R.string.settings_renderer_placeholder_title),
                        description = stringResource(R.string.settings_renderer_placeholder_desc),
                        icon = Icons.Outlined.Tune
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
        },
        modifier = Modifier
            .heightIn(min = CrimsonDesignTokens.Spacing.minTouchTarget)
            .testTag(testTag),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
