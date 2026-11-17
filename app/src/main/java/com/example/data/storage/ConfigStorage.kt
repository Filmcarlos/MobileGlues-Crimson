package com.example.data.storage

import com.example.core.util.AppResult
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppThemeMode
import com.example.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for persistent user preferences storage.
 * Allows substituting an in-memory fake during unit/Robolectric tests.
 */
interface ConfigStorage {
    val preferencesFlow: Flow<UserPreferences>
    suspend fun setThemeMode(mode: AppThemeMode): AppResult<Unit>
    suspend fun setDynamicColorEnabled(enabled: Boolean): AppResult<Unit>
    suspend fun setLanguage(language: AppLanguage): AppResult<Unit>
    suspend fun resetToDefaults(): AppResult<Unit>
}
