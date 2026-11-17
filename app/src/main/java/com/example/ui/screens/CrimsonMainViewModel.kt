package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.di.AppContainer
import com.example.core.logging.AppLogger
import com.example.core.logging.LogEntry
import com.example.core.util.AppResult
import com.example.data.repository.PluginRepository
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppThemeMode
import com.example.domain.model.OpenSourceLicenseItem
import com.example.domain.model.PluginOverviewSnapshot
import com.example.domain.model.UserPreferences
import com.example.domain.usecase.InspectPluginEnvironmentUseCase
import com.example.domain.usecase.ManageUserPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface OverviewLoadState {
    data object Loading : OverviewLoadState
    data class Ready(val snapshot: PluginOverviewSnapshot) : OverviewLoadState
    data class Error(val message: String) : OverviewLoadState
}

/**
 * Single immutable UI State holder following strict Unidirectional Data Flow (UDF).
 */
data class CrimsonUiState(
    val preferences: UserPreferences = UserPreferences(),
    val overviewState: OverviewLoadState = OverviewLoadState.Loading,
    val logEntries: List<LogEntry> = emptyList(),
    val licenses: List<OpenSourceLicenseItem> = emptyList(),
    val selectedLicenseDialog: OpenSourceLicenseItem? = null,
    val isResetDialogVisible: Boolean = false,
    val isClearLogsDialogVisible: Boolean = false,
    val userFeedbackMessage: String? = null
)

/**
 * Explicit UI Event/Intent contract dispatched from Composables back to [CrimsonMainViewModel].
 */
sealed interface CrimsonUiEvent {
    data object RefreshInspection : CrimsonUiEvent
    data class SelectThemeMode(val mode: AppThemeMode) : CrimsonUiEvent
    data class ToggleDynamicColor(val enabled: Boolean) : CrimsonUiEvent
    data class SelectLanguage(val language: AppLanguage) : CrimsonUiEvent
    data object RequestResetPreferences : CrimsonUiEvent
    data object ConfirmResetPreferences : CrimsonUiEvent
    data object DismissResetPreferences : CrimsonUiEvent
    data object RequestClearLogs : CrimsonUiEvent
    data object ConfirmClearLogs : CrimsonUiEvent
    data object DismissClearLogs : CrimsonUiEvent
    data object ClearCrashReports : CrimsonUiEvent
    data class OpenLicenseDetail(val item: OpenSourceLicenseItem) : CrimsonUiEvent
    data object DismissLicenseDetail : CrimsonUiEvent
    data object ConsumeFeedbackMessage : CrimsonUiEvent
}

private data class DialogAndFeedbackState(
    val selectedLicense: OpenSourceLicenseItem? = null,
    val showResetDialog: Boolean = false,
    val showClearLogsDialog: Boolean = false,
    val feedbackMessage: String? = null
)

class CrimsonMainViewModel(
    private val inspectPluginEnvironmentUseCase: InspectPluginEnvironmentUseCase,
    private val manageUserPreferencesUseCase: ManageUserPreferencesUseCase,
    private val pluginRepository: PluginRepository,
    private val logger: AppLogger
) : ViewModel() {

    private val overviewStateFlow = MutableStateFlow<OverviewLoadState>(OverviewLoadState.Loading)
    private val dialogStateFlow = MutableStateFlow(DialogAndFeedbackState())
    private val licensesList = pluginRepository.getOpenSourceLicenses()

    val uiState: StateFlow<CrimsonUiState> = combine(
        manageUserPreferencesUseCase.preferencesFlow,
        overviewStateFlow,
        logger.entries,
        dialogStateFlow
    ) { prefs, overview, logs, dialogs ->
        CrimsonUiState(
            preferences = prefs,
            overviewState = overview,
            logEntries = logs,
            licenses = licensesList,
            selectedLicenseDialog = dialogs.selectedLicense,
            isResetDialogVisible = dialogs.showResetDialog,
            isClearLogsDialogVisible = dialogs.showClearLogsDialog,
            userFeedbackMessage = dialogs.feedbackMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = CrimsonUiState(licenses = licensesList)
    )

    init {
        refreshEnvironmentInspection()
    }

    fun onEvent(event: CrimsonUiEvent) {
        when (event) {
            CrimsonUiEvent.RefreshInspection -> refreshEnvironmentInspection()
            is CrimsonUiEvent.SelectThemeMode -> updateTheme(event.mode)
            is CrimsonUiEvent.ToggleDynamicColor -> updateDynamicColor(event.enabled)
            is CrimsonUiEvent.SelectLanguage -> updateLanguage(event.language)
            CrimsonUiEvent.RequestResetPreferences -> {
                dialogStateFlow.update { it.copy(showResetDialog = true) }
            }
            CrimsonUiEvent.ConfirmResetPreferences -> confirmResetPreferences()
            CrimsonUiEvent.DismissResetPreferences -> {
                dialogStateFlow.update { it.copy(showResetDialog = false) }
            }
            CrimsonUiEvent.RequestClearLogs -> {
                dialogStateFlow.update { it.copy(showClearLogsDialog = true) }
            }
            CrimsonUiEvent.ConfirmClearLogs -> confirmClearLogs()
            CrimsonUiEvent.DismissClearLogs -> {
                dialogStateFlow.update { it.copy(showClearLogsDialog = false) }
            }
            CrimsonUiEvent.ClearCrashReports -> clearSavedCrashes()
            is CrimsonUiEvent.OpenLicenseDetail -> {
                dialogStateFlow.update { it.copy(selectedLicense = event.item) }
            }
            CrimsonUiEvent.DismissLicenseDetail -> {
                dialogStateFlow.update { it.copy(selectedLicense = null) }
            }
            CrimsonUiEvent.ConsumeFeedbackMessage -> {
                dialogStateFlow.update { it.copy(feedbackMessage = null) }
            }
        }
    }

    suspend fun exportDiagnosticLogsText(): String = logger.exportLogsAsText()

    private fun refreshEnvironmentInspection() {
        viewModelScope.launch {
            overviewStateFlow.value = OverviewLoadState.Loading
            when (val result = inspectPluginEnvironmentUseCase()) {
                is AppResult.Success -> {
                    overviewStateFlow.value = OverviewLoadState.Ready(result.data)
                }
                is AppResult.Error -> {
                    logger.e(TAG, result.message, result.cause)
                    overviewStateFlow.value = OverviewLoadState.Error(result.message)
                }
            }
        }
    }

    private fun updateTheme(mode: AppThemeMode) {
        viewModelScope.launch {
            val result = manageUserPreferencesUseCase.updateThemeMode(mode)
            if (result is AppResult.Error) {
                postFeedback(result.message)
            }
        }
    }

    private fun updateDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            val result = manageUserPreferencesUseCase.updateDynamicColor(enabled)
            if (result is AppResult.Error) {
                postFeedback(result.message)
            }
        }
    }

    private fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch {
            val result = manageUserPreferencesUseCase.updateLanguage(language)
            if (result is AppResult.Error) {
                postFeedback(result.message)
            }
        }
    }

    private fun confirmResetPreferences() {
        viewModelScope.launch {
            dialogStateFlow.update { it.copy(showResetDialog = false) }
            when (val result = manageUserPreferencesUseCase.resetDefaults()) {
                is AppResult.Success -> Unit
                is AppResult.Error -> postFeedback(result.message)
            }
        }
    }

    private fun confirmClearLogs() {
        viewModelScope.launch {
            dialogStateFlow.update { it.copy(showClearLogsDialog = false) }
            logger.clearLogs()
            pluginRepository.clearCrashReports()
            refreshEnvironmentInspection()
        }
    }

    private fun clearSavedCrashes() {
        viewModelScope.launch {
            pluginRepository.clearCrashReports()
            refreshEnvironmentInspection()
        }
    }

    private fun postFeedback(message: String) {
        dialogStateFlow.update { it.copy(feedbackMessage = message) }
    }

    companion object {
        private const val TAG = "CrimsonViewModel"
        private const val STOP_TIMEOUT_MS = 5_000L

        fun factory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CrimsonMainViewModel(
                        inspectPluginEnvironmentUseCase = container.inspectPluginEnvironmentUseCase,
                        manageUserPreferencesUseCase = container.manageUserPreferencesUseCase,
                        pluginRepository = container.pluginRepository,
                        logger = container.logger
                    ) as T
                }
            }
    }
}
