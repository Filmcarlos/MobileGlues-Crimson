package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.core.logging.LogEntry
import com.example.core.logging.LogLevel
import com.example.ui.components.AppScaffold
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.EmptyState
import com.example.ui.components.StatusPill
import com.example.ui.components.StatusPillTone
import com.example.ui.theme.CrimsonDesignTokens
import kotlinx.coroutines.launch

@Composable
fun LogViewerScreen(
    uiState: CrimsonUiState,
    onEvent: (CrimsonUiEvent) -> Unit,
    onExportLogsText: suspend () -> String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val copiedMessage = stringResource(R.string.msg_logs_copied)

    if (uiState.isClearLogsDialogVisible) {
        ConfirmDialog(
            title = stringResource(R.string.logs_clear_confirm_title),
            message = stringResource(R.string.logs_clear_confirm_desc),
            confirmText = stringResource(R.string.action_confirm),
            cancelText = stringResource(R.string.action_cancel),
            onConfirm = { onEvent(CrimsonUiEvent.ConfirmClearLogs) },
            onDismiss = { onEvent(CrimsonUiEvent.DismissClearLogs) }
        )
    }

    AppScaffold(
        title = stringResource(R.string.logs_title),
        subtitle = stringResource(R.string.logs_subtitle),
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        actions = {
            IconButton(
                onClick = {
                    scope.launch {
                        val exported = onExportLogsText()
                        copyTextToClipboard(context, exported)
                        snackbarHostState.showSnackbar(copiedMessage)
                    }
                },
                enabled = uiState.logEntries.isNotEmpty(),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("logs_copy_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = stringResource(R.string.action_copy_logs)
                )
            }
            IconButton(
                onClick = { onEvent(CrimsonUiEvent.RequestClearLogs) },
                enabled = uiState.logEntries.isNotEmpty(),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("logs_clear_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = stringResource(R.string.action_clear_logs)
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
            if (uiState.logEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = CrimsonDesignTokens.Spacing.maxReadableContentWidth)
                        .padding(CrimsonDesignTokens.Spacing.md)
                ) {
                    EmptyState(
                        title = stringResource(R.string.logs_empty_title),
                        description = stringResource(R.string.logs_empty_desc),
                        icon = Icons.Outlined.Terminal,
                        badgeText = "RING BUFFER"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = CrimsonDesignTokens.Spacing.maxReadableContentWidth)
                        .testTag("logs_lazy_column"),
                    contentPadding = PaddingValues(CrimsonDesignTokens.Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.xs)
                ) {
                    items(
                        items = uiState.logEntries.asReversed(),
                        key = { it.id }
                    ) { entry ->
                        LogEntryCard(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun LogEntryCard(entry: LogEntry) {
    val tone = when (entry.level) {
        LogLevel.DEBUG -> StatusPillTone.NEUTRAL
        LogLevel.INFO -> StatusPillTone.SUCCESS
        LogLevel.WARN -> StatusPillTone.WARNING
        LogLevel.ERROR -> StatusPillTone.CRIMSON
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CrimsonDesignTokens.Radius.sm),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CrimsonDesignTokens.Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(CrimsonDesignTokens.Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusPill(text = entry.level.badge, tone = tone)
                    Text(
                        text = entry.tag,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = entry.timestampFormatted,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = entry.message,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun copyTextToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
    val clip = ClipData.newPlainText("MobileGlues Crimson Logs", text)
    clipboard.setPrimaryClip(clip)
}
