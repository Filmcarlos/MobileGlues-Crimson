package com.example.domain.usecase

import com.example.core.util.AppResult
import com.example.data.storage.ConfigStorage
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppThemeMode
import com.example.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * UseCase encapsulating user preference observation and updates (Theme, Dynamic Color, Language).
 */
class ManageUserPreferencesUseCase(
    private val configStorage: ConfigStorage
) {
    val preferencesFlow: Flow<UserPreferences> = configStorage.preferencesFlow

    suspend fun updateThemeMode(mode: AppThemeMode): AppResult<Unit> =
        configStorage.setThemeMode(mode)

    suspend fun updateDynamicColor(enabled: Boolean): AppResult<Unit> =
        configStorage.setDynamicColorEnabled(enabled)

    suspend fun updateLanguage(language: AppLanguage): AppResult<Unit> =
        configStorage.setLanguage(language)

    suspend fun resetDefaults(): AppResult<Unit> =
        configStorage.resetToDefaults()
}
