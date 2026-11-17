package com.example.data.gateway

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.example.BuildConfig
import com.example.core.logging.AppLogger
import com.example.core.util.AppResult
import com.example.core.util.DispatcherProvider
import com.example.core.util.runCatchingResult
import com.example.domain.model.LauncherAppStatus
import com.example.domain.model.ManifestContractInfo
import com.example.domain.model.NativeLibraryStatus
import java.io.File
import java.util.zip.ZipFile
import kotlinx.coroutines.withContext

/**
 * Production Android implementation of [RendererGateway].
 * Inspects real filesystem paths (`applicationInfo.nativeLibraryDir`) and APK zip entries
 * for `libmobileglues.so`, and queries `PackageManager` for FCL & Zalith Launcher.
 */
class AndroidRendererGateway(
    private val context: Context,
    private val logger: AppLogger,
    private val dispatchers: DispatcherProvider
) : RendererGateway {

    override suspend fun inspectNativeLibrary(): AppResult<NativeLibraryStatus> = withContext(dispatchers.io) {
        runCatchingResult("Native library inspection failed") {
            val appInfo = context.applicationInfo
            val nativeDir = appInfo.nativeLibraryDir.orEmpty()
            val libFileName = BuildConfig.NATIVE_LIB_FILENAME
            val supportedAbis = Build.SUPPORTED_ABIS?.toList().orEmpty()
            val primaryAbi = supportedAbis.firstOrNull() ?: UNKNOWN_ABI

            val extractedFile = File(nativeDir, libFileName)
            val existsOnDisk = extractedFile.exists() && extractedFile.isFile && extractedFile.length() > 0L

            // Also check inside base APK zip in case extractNativeLibs behavior varies on host OS
            val apkEntrySize = if (!existsOnDisk) {
                findNativeLibSizeInApk(appInfo.sourceDir, primaryAbi, supportedAbis, libFileName)
            } else {
                extractedFile.length()
            }

            val isDetected = existsOnDisk || apkEntrySize > 0L
            val binarySize = if (existsOnDisk) extractedFile.length() else apkEntrySize

            val isLoadable = if (isDetected) {
                tryLoadLibraryGracefully()
            } else {
                false
            }

            val note = when {
                existsOnDisk && isLoadable ->
                    "Verified at ${extractedFile.absolutePath} (${binarySize} bytes, dlopen OK)"
                existsOnDisk ->
                    "Found at ${extractedFile.absolutePath} (${binarySize} bytes)"
                apkEntrySize > 0L ->
                    "Bundled inside APK (${apkEntrySize} bytes) for ABI $primaryAbi"
                else ->
                    "No $libFileName found in $nativeDir. Place binary in app/src/main/jniLibs/$primaryAbi/$libFileName"
            }

            logger.i(TAG, "Native inspection: detected=$isDetected, abi=$primaryAbi, note=$note")

            NativeLibraryStatus(
                libraryFileName = libFileName,
                isDetected = isDetected,
                isLoadable = isLoadable,
                primaryDeviceAbi = primaryAbi,
                supportedAbis = supportedAbis,
                nativeLibraryDirPath = nativeDir,
                binarySizeBytes = binarySize,
                diagnosticNote = note
            )
        }
    }

    private fun findNativeLibSizeInApk(
        sourceApkPath: String?,
        primaryAbi: String,
        supportedAbis: List<String>,
        libFileName: String
    ): Long {
        if (sourceApkPath.isNullOrBlank()) return 0L
        val apkFile = File(sourceApkPath)
        if (!apkFile.exists()) return 0L
        return runCatching {
            ZipFile(apkFile).use { zip ->
                val candidateAbis = (listOf(primaryAbi) + supportedAbis).distinct()
                for (abi in candidateAbis) {
                    val entry = zip.getEntry("lib/$abi/$libFileName")
                    if (entry != null && entry.size > 0L) {
                        return@use entry.size
                    }
                }
                0L
            }
        }.getOrDefault(0L)
    }

    private fun tryLoadLibraryGracefully(): Boolean = try {
        System.loadLibrary(NATIVE_MODULE_SHORT_NAME)
        true
    } catch (e: UnsatisfiedLinkError) {
        logger.w(TAG, "libmobileglues.so present or checked, dlopen note: ${e.message}")
        false
    } catch (t: Throwable) {
        logger.w(TAG, "Unexpected error probing native library load", t)
        false
    }

    override suspend fun detectInstalledLaunchers(): AppResult<List<LauncherAppStatus>> = withContext(dispatchers.io) {
        runCatchingResult("Launcher package query failed") {
            val pm = context.packageManager
            TARGET_LAUNCHERS.map { (pkg, key) ->
                queryPackageStatus(pm, pkg, key)
            }
        }
    }

    private fun queryPackageStatus(
        pm: PackageManager,
        packageName: String,
        displayKey: String
    ): LauncherAppStatus {
        return try {
            val pkgInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, 0)
            }
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pkgInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pkgInfo.versionCode.toLong()
            }
            logger.i(TAG, "Detected launcher $packageName v${pkgInfo.versionName} ($versionCode)")
            LauncherAppStatus(
                packageName = packageName,
                displayNameResKey = displayKey,
                isInstalled = true,
                versionName = pkgInfo.versionName ?: UNKNOWN_VERSION,
                versionCode = versionCode
            )
        } catch (_: PackageManager.NameNotFoundException) {
            logger.d(TAG, "Launcher package not installed: $packageName")
            LauncherAppStatus(
                packageName = packageName,
                displayNameResKey = displayKey,
                isInstalled = false,
                versionName = null,
                versionCode = null
            )
        }
    }

    override suspend fun readManifestContract(): AppResult<ManifestContractInfo> = withContext(dispatchers.io) {
        runCatchingResult("Failed reading AndroidManifest meta-data contract") {
            val pm = context.packageManager
            val pkgName = context.packageName
            val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getApplicationInfo(
                    pkgName,
                    PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)
            }
            val meta = appInfo.metaData

            fun readMetaString(key: String, fallback: String): String {
                if (meta == null || !meta.containsKey(key)) return fallback
                meta.getString(key)?.let { return it }
                if (meta.getBoolean(key, false) || !meta.getBoolean(key, true)) {
                    return meta.getBoolean(key).toString()
                }
                val floatVal = meta.getFloat(key, Float.NaN)
                if (!floatVal.isNaN()) {
                    return floatVal.toString()
                }
                return fallback
            }

            ManifestContractInfo(
                appVersionName = BuildConfig.VERSION_NAME,
                appVersionCode = BuildConfig.VERSION_CODE.toLong(),
                applicationId = BuildConfig.APPLICATION_ID,
                fclPlugin = readMetaString(META_KEY_FCL_PLUGIN, "true"),
                description = readMetaString(META_KEY_DES, BuildConfig.RENDERER_DISPLAY_NAME),
                rendererSpec = readMetaString(META_KEY_RENDERER, BuildConfig.RENDERER_META_SPEC),
                boatEnv = readMetaString(META_KEY_BOAT_ENV, BuildConfig.BOAT_ENV_SPEC),
                pojavEnv = readMetaString(META_KEY_POJAV_ENV, BuildConfig.POJAV_ENV_SPEC),
                minMcVer = readMetaString(META_KEY_MIN_MC_VER, BuildConfig.MIN_MC_VER),
                maxMcVer = readMetaString(META_KEY_MAX_MC_VER, BuildConfig.MAX_MC_VER)
            )
        }
    }

    companion object {
        private const val TAG = "RendererGateway"
        private const val NATIVE_MODULE_SHORT_NAME = "mobileglues"
        private const val UNKNOWN_ABI = "unknown"
        private const val UNKNOWN_VERSION = "unknown"

        const val PACKAGE_FCL = "com.tungsten.fcl"
        const val PACKAGE_ZALITH = "com.movtery.zalithlauncher.v2"

        const val LAUNCHER_KEY_FCL = "fcl"
        const val LAUNCHER_KEY_ZALITH = "zalith"

        private val TARGET_LAUNCHERS = listOf(
            PACKAGE_FCL to LAUNCHER_KEY_FCL,
            PACKAGE_ZALITH to LAUNCHER_KEY_ZALITH
        )

        private const val META_KEY_FCL_PLUGIN = "fclPlugin"
        private const val META_KEY_DES = "des"
        private const val META_KEY_RENDERER = "renderer"
        private const val META_KEY_BOAT_ENV = "boatEnv"
        private const val META_KEY_POJAV_ENV = "pojavEnv"
        private const val META_KEY_MIN_MC_VER = "minMCVer"
        private const val META_KEY_MAX_MC_VER = "maxMCVer"
    }
}
