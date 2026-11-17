package com.example.core.di

import android.content.Context
import com.example.core.crash.LocalCrashHandler
import com.example.core.logging.AppLogger
import com.example.core.logging.FileRingBufferLogger
import com.example.core.util.DefaultDispatcherProvider
import com.example.core.util.DispatcherProvider
import com.example.data.gateway.AndroidRendererGateway
import com.example.data.gateway.RendererGateway
import com.example.data.repository.PluginRepository
import com.example.data.storage.ConfigStorage
import com.example.data.storage.DataStoreConfigStorage
import com.example.domain.usecase.InspectPluginEnvironmentUseCase
import com.example.domain.usecase.ManageUserPreferencesUseCase
import java.io.File

/**
 * Lightweight manual Dependency Injection container scoped to [com.example.CrimsonApplication].
 * Avoids Hilt overhead while keeping all gateways, repositories, and use cases swappable for tests.
 */
interface AppContainer {
    val dispatchers: DispatcherProvider
    val logger: AppLogger
    val crashHandler: LocalCrashHandler
    val configStorage: ConfigStorage
    val rendererGateway: RendererGateway
    val pluginRepository: PluginRepository
    val inspectPluginEnvironmentUseCase: InspectPluginEnvironmentUseCase
    val manageUserPreferencesUseCase: ManageUserPreferencesUseCase
}

class DefaultAppContainer(
    applicationContext: Context
) : AppContainer {

    override val dispatchers: DispatcherProvider = DefaultDispatcherProvider()

    override val logger: AppLogger = FileRingBufferLogger(
        logDirectory = File(applicationContext.filesDir, LOG_SUBDIR),
        dispatchers = dispatchers
    )

    override val crashHandler: LocalCrashHandler = LocalCrashHandler(
        crashDirectory = File(applicationContext.filesDir, CRASH_SUBDIR),
        logger = logger,
        dispatchers = dispatchers
    )

    override val configStorage: ConfigStorage = DataStoreConfigStorage(
        context = applicationContext,
        logger = logger,
        dispatchers = dispatchers
    )

    override val rendererGateway: RendererGateway = AndroidRendererGateway(
        context = applicationContext,
        logger = logger,
        dispatchers = dispatchers
    )

    override val pluginRepository: PluginRepository = PluginRepository(
        rendererGateway = rendererGateway,
        crashHandler = crashHandler
    )

    override val inspectPluginEnvironmentUseCase: InspectPluginEnvironmentUseCase =
        InspectPluginEnvironmentUseCase(pluginRepository)

    override val manageUserPreferencesUseCase: ManageUserPreferencesUseCase =
        ManageUserPreferencesUseCase(configStorage)

    companion object {
        private const val LOG_SUBDIR = "diagnostics_logs"
        private const val CRASH_SUBDIR = "crash_reports"
    }
}
