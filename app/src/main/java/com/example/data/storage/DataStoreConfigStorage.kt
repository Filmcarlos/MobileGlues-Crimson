package com.example.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.logging.AppLogger
import com.example.core.util.AppResult
import com.example.core.util.DispatcherProvider
import com.example.core.util.runCatchingResult
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppThemeMode
import com.example.domain.model.UserPreferences
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.crimsonDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "crimson_plugin_preferences"
)

/**
 * Jetpack DataStore implementation of [ConfigStorage].
 */
class DataStoreConfigStorage(
    private val dataStore: DataStore<Preferences>,
    private val logger: AppLogger,
    private val dispatchers: DispatcherProvider
) : ConfigStorage {

    constructor(
        context: Context,
        logger: AppLogger,
        dispatchers: DispatcherProvider
    ) : this(context.applicationContext.crimsonDataStore, logger, dispatchers)

    override val preferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                logger.w(TAG, "Failed reading preferences DataStore; emitting defaults", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            UserPreferences(
                themeMode = AppThemeMode.fromKey(prefs[KEY_THEME_MODE]),
                dynamicColorEnabled = prefs[KEY_DYNAMIC_COLOR] ?: false,
                language = AppLanguage.fromCode(prefs[KEY_LANGUAGE])
            )
        }
        .flowOn(dispatchers.io)

    override suspend fun setThemeMode(mode: AppThemeMode): AppResult<Unit> = withContext(dispatchers.io) {
        runCatchingResult("Failed to update theme mode") {
            dataStore.edit { prefs ->
                prefs[KEY_THEME_MODE] = mode.storageKey
            }
            logger.i(TAG, "Theme mode updated to ${mode.name}")
        }
    }

    override suspend fun setDynamicColorEnabled(enabled: Boolean): AppResult<Unit> = withContext(dispatchers.io) {
        runCatchingResult("Failed to update dynamic color preference") {
            dataStore.edit { prefs ->
                prefs[KEY_DYNAMIC_COLOR] = enabled
            }
            logger.i(TAG, "Dynamic color updated to $enabled")
        }
    }

    override suspend fun setLanguage(language: AppLanguage): AppResult<Unit> = withContext(dispatchers.io) {
        runCatchingResult("Failed to update language preference") {
            dataStore.edit { prefs ->
                prefs[KEY_LANGUAGE] = language.code
            }
            logger.i(TAG, "Language updated to ${language.name}")
        }
    }

    override suspend fun resetToDefaults(): AppResult<Unit> = withContext(dispatchers.io) {
        runCatchingResult("Failed to reset preferences") {
            dataStore.edit { prefs ->
                prefs.clear()
            }
            logger.i(TAG, "Preferences reset to Crimson defaults")
        }
    }

    companion object {
        private const val TAG = "ConfigStorage"
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color_enabled")
        private val KEY_LANGUAGE = stringPreferencesKey("app_language")
    }
}
