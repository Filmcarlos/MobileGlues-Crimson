package com.example.domain.model

enum class AppThemeMode(val storageKey: String) {
    SYSTEM("system"),
    DARK("dark"),
    LIGHT("light");

    companion object {
        fun fromKey(key: String?): AppThemeMode =
            entries.firstOrNull { it.storageKey == key } ?: DARK
    }
}

enum class AppLanguage(val code: String) {
    SYSTEM("system"),
    THAI("th"),
    ENGLISH("en");

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: SYSTEM
    }
}

/**
 * User preferences stored via Jetpack DataStore.
 * Note: [themeMode] defaults to [AppThemeMode.DARK] (#0A0A0C) and [dynamicColorEnabled]
 * defaults to `false` to preserve the signature Crimson Obsidian brand identity.
 */
data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val dynamicColorEnabled: Boolean = false,
    val language: AppLanguage = AppLanguage.SYSTEM
)
