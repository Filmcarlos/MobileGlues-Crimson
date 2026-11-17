package com.example.domain.usecase

import com.example.core.util.AppResult
import com.example.data.repository.PluginRepository
import com.example.domain.model.PluginOverviewSnapshot

/**
 * UseCase that retrieves the real-time native library status, installed launcher packages,
 * manifest contract values, and local crash report count.
 */
class InspectPluginEnvironmentUseCase(
    private val repository: PluginRepository
) {
    suspend operator fun invoke(): AppResult<PluginOverviewSnapshot> =
        repository.loadPluginOverview()
}
