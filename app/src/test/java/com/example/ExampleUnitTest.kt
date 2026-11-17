package com.example

import com.example.core.crash.LocalCrashHandler
import com.example.core.logging.AppLogger
import com.example.core.logging.LogEntry
import com.example.core.logging.LogLevel
import com.example.core.util.AppResult
import com.example.core.util.DispatcherProvider
import com.example.data.gateway.RendererGateway
import com.example.data.repository.PluginRepository
import com.example.data.storage.ConfigStorage
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppThemeMode
import com.example.domain.model.LauncherAppStatus
import com.example.domain.model.ManifestContractInfo
import com.example.domain.model.NativeLibraryStatus
import com.example.domain.model.UserPreferences
import com.example.domain.usecase.InspectPluginEnvironmentUseCase
import com.example.domain.usecase.ManageUserPreferencesUseCase
import com.example.ui.screens.CrimsonMainViewModel
import com.example.ui.screens. CrimsonUiEvent
import com.example.ui.screens.OverviewLoadState
import java.nio.file.Files
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExampleUnitTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `ConfigStorage updates theme dynamic color and language and resets defaults`() = runTest {
        val fakeStorage = InMemoryFakeConfigStorage()

        // Default values must be DARK theme and Dynamic Color disabled
        val initial = fakeStorage.preferencesFlow.first()
        assertEquals(AppThemeMode.DARK, initial.themeMode)
        assertFalse(initial.dynamicColorEnabled)
        assertEquals(AppLanguage.SYSTEM, initial.language)

        // Update theme & language
        fakeStorage.setThemeMode(AppThemeMode.LIGHT)
        fakeStorage.setDynamicColorEnabled(true)
        fakeStorage.setLanguage(AppLanguage.THAI)

        val updated = fakeStorage.preferencesFlow.first()
        assertEquals(AppThemeMode.LIGHT, updated.themeMode)
        assertTrue(updated.dynamicColorEnabled)
        assertEquals(AppLanguage.THAI, updated.language)

        // Reset to Crimson defaults
        fakeStorage.resetToDefaults()
        val reset = fakeStorage.preferencesFlow.first()
        assertEquals(AppThemeMode.DARK, reset.themeMode)
        assertFalse(reset.dynamicColorEnabled)
        assertEquals(AppLanguage.SYSTEM, reset.language)
    }

    @Test
    fun `CrimsonMainViewModel loads native status and launcher packages into Ready state`() = runTest {
        val fakeStorage = InMemoryFakeConfigStorage()
        val fakeLogger = InMemoryFakeLogger()
        val tempCrashDir = Files.createTempDirectory("crimson_crash_test").toFile()
        val crashHandler = LocalCrashHandler(tempCrashDir, fakeLogger, testDispatcherProvider)
        val fakeGateway = FakeRendererGateway(shouldFail = false)
        val repository = PluginRepository(fakeGateway, crashHandler)

        val viewModel = CrimsonMainViewModel(
            inspectPluginEnvironmentUseCase = InspectPluginEnvironmentUseCase(repository),
            manageUserPreferencesUseCase = ManageUserPreferencesUseCase(fakeStorage),
            pluginRepository = repository,
            logger = fakeLogger
        )

        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect {}
        }

        val state = viewModel.uiState.value
        assertTrue(state.overviewState is OverviewLoadState.Ready)
        val snapshot = (state.overviewState as OverviewLoadState.Ready).snapshot
        assertEquals("libmobileglues.so", snapshot.nativeStatus.libraryFileName)
        assertEquals(2, snapshot.launchers.size)

        // Dispatch theme change event via UDF
        viewModel.onEvent(CrimsonUiEvent.SelectThemeMode(AppThemeMode.LIGHT))
        assertEquals(AppThemeMode.LIGHT, viewModel.uiState.value.preferences.themeMode)

        collectJob.cancel()
        tempCrashDir.deleteRecursively()
    }

    @Test
    fun `CrimsonMainViewModel surfaces explicit Error state when gateway fails`() = runTest {
        val fakeStorage = InMemoryFakeConfigStorage()
        val fakeLogger = InMemoryFakeLogger()
        val tempCrashDir = Files.createTempDirectory("crimson_crash_err_test").toFile()
        val crashHandler = LocalCrashHandler(tempCrashDir, fakeLogger, testDispatcherProvider)
        val failingGateway = FakeRendererGateway(shouldFail = true)
        val repository = PluginRepository(failingGateway, crashHandler)

        val viewModel = CrimsonMainViewModel(
            inspectPluginEnvironmentUseCase = InspectPluginEnvironmentUseCase(repository),
            manageUserPreferencesUseCase = ManageUserPreferencesUseCase(fakeStorage),
            pluginRepository = repository,
            logger = fakeLogger
        )

        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect {}
        }

        val overviewState = viewModel.uiState.value.overviewState
        assertTrue(overviewState is OverviewLoadState.Error)
        assertEquals(
            "Native inspection error: Simulated gateway failure",
            (overviewState as OverviewLoadState.Error).message
        )

        collectJob.cancel()
        tempCrashDir.deleteRecursively()
    }
}

private class InMemoryFakeConfigStorage : ConfigStorage {
    private val state = MutableStateFlow(UserPreferences())
    override val preferencesFlow: Flow<UserPreferences> = state

    override suspend fun setThemeMode(mode: AppThemeMode): AppResult<Unit> {
        state.update { it.copy(themeMode = mode) }
        return AppResult.Success(Unit)
    }

    override suspend fun setDynamicColorEnabled(enabled: Boolean): AppResult<Unit> {
        state.update { it.copy(dynamicColorEnabled = enabled) }
        return AppResult.Success(Unit)
    }

    override suspend fun setLanguage(language: AppLanguage): AppResult<Unit> {
        state.update { it.copy(language = language) }
        return AppResult.Success(Unit)
    }

    override suspend fun resetToDefaults(): AppResult<Unit> {
        state.value = UserPreferences()
        return AppResult.Success(Unit)
    }
}

private class InMemoryFakeLogger : AppLogger {
    override val entries: StateFlow<List<LogEntry>> = MutableStateFlow(emptyList())
    override fun d(tag: String, message: String) = Unit
    override fun i(tag: String, message: String) = Unit
    override fun w(tag: String, message: String, throwable: Throwable?) = Unit
    override fun e(tag: String, message: String, throwable: Throwable?) = Unit
    override suspend fun exportLogsAsText(): String = ""
    override suspend fun clearLogs() = Unit
}

private class FakeRendererGateway(private val shouldFail: Boolean) : RendererGateway {
    override suspend fun inspectNativeLibrary(): AppResult<NativeLibraryStatus> {
        if (shouldFail) {
            return AppResult.Error("Native inspection error: Simulated gateway failure")
        }
        return AppResult.Success(
            NativeLibraryStatus(
                libraryFileName = "libmobileglues.so",
                isDetected = false,
                isLoadable = false,
                primaryDeviceAbi = "arm64-v8a",
                supportedAbis = listOf("arm64-v8a"),
                nativeLibraryDirPath = "/data/app/lib/arm64",
                binarySizeBytes = 0L,
                diagnosticNote = "Awaiting .so binary"
            )
        )
    }

    override suspend fun detectInstalledLaunchers(): AppResult<List<LauncherAppStatus>> =
        AppResult.Success(
            listOf(
                LauncherAppStatus("com.tungsten.fcl", "fcl", true, "1.2.0", 120L),
                LauncherAppStatus("com.movtery.zalithlauncher.v2", "zalith", false, null, null)
            )
        )

    override suspend fun readManifestContract(): AppResult<ManifestContractInfo> =
        AppResult.Success(
            ManifestContractInfo(
                appVersionName = "1.0.0-crimson",
                appVersionCode = 100L,
                applicationId = "com.crimsondev.glplugin",
                fclPlugin = "true",
                description = "MobileGlues Crimson",
                rendererSpec = "MobileGlues Crimson:libmobileglues.so:libmobileglues.so",
                boatEnv = "LIBGL_ES=3",
                pojavEnv = "LIBGL_ES=3:POJAV_RENDERER=opengles3:POJAVEXEC_EGL=libmobileglues.so:LIBGL_EGL=libmobileglues.so",
                minMcVer = "1.17",
                maxMcVer = ""
            )
        )
}
