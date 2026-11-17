package com.example.data.gateway

import com.example.core.util.AppResult
import com.example.domain.model.LauncherAppStatus
import com.example.domain.model.ManifestContractInfo
import com.example.domain.model.NativeLibraryStatus

/**
 * Gateway interface isolating all interactions with `libmobileglues.so`, APK native directories,
 * and Android `PackageManager` launcher queries.
 * Designed for graceful execution when `.so` binaries are not yet placed in `jniLibs/`.
 */
interface RendererGateway {
    suspend fun inspectNativeLibrary(): AppResult<NativeLibraryStatus>
    suspend fun detectInstalledLaunchers(): AppResult<List<LauncherAppStatus>>
    suspend fun readManifestContract(): AppResult<ManifestContractInfo>
}
