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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.ManifestContractInfo
import com.example.ui.components.AppScaffold
import com.example.ui.components.EmptyState
import com.example.ui.components.ErrorState
import com.example.ui.components.LoadingShimmer
import com.example.ui.components.SectionCard
import com.example.ui.theme.CrimsonDesignTokens

@Composable
fun InfoScreen(
    uiState: CrimsonUiState,
    onEvent: (CrimsonUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AppScaffold(
        title = stringResource(R.string.info_title),
        subtitle = stringResource(R.string.info_manifest_subtitle),
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
                    .testTag("info_lazy_column"),
                contentPadding = PaddingValues(CrimsonDesignTokens.Spacing.md),
                verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.md)
            ) {
                when (val state = uiState.overviewState) {
                    OverviewLoadState.Loading -> {
                        item { LoadingShimmer(barCount = 3) }
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
                        item {
                            ManifestContractCard(contract = state.snapshot.manifestContract)
                        }
                    }
                }

                item {
                    EmptyState(
                        title = stringResource(R.string.info_placeholder_title),
                        description = stringResource(R.string.info_placeholder_desc),
                        icon = Icons.Outlined.Memory
                    )
                }
            }
        }
    }
}

@Composable
private fun ManifestContractCard(
    contract: ManifestContractInfo
) {
    SectionCard(
        title = stringResource(R.string.info_manifest_section),
        subtitle = stringResource(R.string.info_manifest_subtitle),
        icon = Icons.Outlined.Code,
        modifier = Modifier.testTag("info_manifest_card")
    ) {
        val entries = listOf(
            stringResource(R.string.info_key_fcl_plugin) to contract.fclPlugin,
            stringResource(R.string.info_key_des) to contract.description,
            stringResource(R.string.info_key_renderer) to contract.rendererSpec,
            stringResource(R.string.info_key_boat_env) to contract.boatEnv,
            stringResource(R.string.info_key_pojav_env) to contract.pojavEnv,
            stringResource(R.string.info_key_min_mc) to contract.minMcVer,
            stringResource(R.string.info_key_max_mc) to contract.maxMcVer.ifEmpty { "\"\" (Unlimited)" }
        )

        entries.forEachIndexed { index, (key, value) ->
            if (index > 0) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = CrimsonDesignTokens.Spacing.xxs),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = key,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
