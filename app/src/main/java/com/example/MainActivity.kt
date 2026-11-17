package com.example

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.model.AppLanguage
import com.example.ui.navigation.CrimsonAppShell
import com.example.ui.screens.CrimsonMainViewModel
import com.example.ui.theme.MobileGluesCrimsonTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = (applicationContext as CrimsonApplication).container

        setContent {
            val viewModel: CrimsonMainViewModel = viewModel(
                factory = CrimsonMainViewModel.factory(appContainer)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val windowSizeClass = calculateWindowSizeClass(this)

            LocalizedContextWrapper(language = uiState.preferences.language) {
                MobileGluesCrimsonTheme(
                    themeMode = uiState.preferences.themeMode,
                    dynamicColor = uiState.preferences.dynamicColorEnabled
                ) {
                    CrimsonAppShell(
                        widthSizeClass = windowSizeClass.widthSizeClass,
                        uiState = uiState,
                        onEvent = viewModel::onEvent,
                        onExportLogsText = viewModel::exportDiagnosticLogsText
                    )
                }
            }
        }
    }
}

/**
 * Dynamically overrides [LocalContext] configuration when the user selects Thai (`th`) or
 * English (`en`) inside the app, enabling immediate runtime bilingual switching without restarting.
 */
@Composable
private fun LocalizedContextWrapper(
    language: AppLanguage,
    content: @Composable () -> Unit
) {
    val baseContext = LocalContext.current
    val localizedContext = remember(baseContext, language) {
        baseContext.withLanguageOverride(language)
    }
    CompositionLocalProvider(LocalContext provides localizedContext) {
        content()
    }
}

private fun Context.withLanguageOverride(language: AppLanguage): Context {
    if (language == AppLanguage.SYSTEM) return this
    val targetLocale = Locale.forLanguageTag(language.code)
    Locale.setDefault(targetLocale)
    val config = Configuration(resources.configuration)
    config.setLocale(targetLocale)
    return createConfigurationContext(config)
}
