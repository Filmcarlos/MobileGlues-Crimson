package com.example.domain.model

/**
 * Real-time inspection report of the `libmobileglues.so` native library and host device ABIs.
 */
data class NativeLibraryStatus(
    val libraryFileName: String,
    val isDetected: Boolean,
    val isLoadable: Boolean,
    val primaryDeviceAbi: String,
    val supportedAbis: List<String>,
    val nativeLibraryDirPath: String,
    val binarySizeBytes: Long,
    val diagnosticNote: String
)

/**
 * Detection status for a target Minecraft Java launcher queried via Android PackageManager.
 */
data class LauncherAppStatus(
    val packageName: String,
    val displayNameResKey: String,
    val isInstalled: Boolean,
    val versionName: String?,
    val versionCode: Long?
)

/**
 * Contract values injected via `manifestPlaceholders` in `app/build.gradle.kts` and read from
 * `<meta-data>` in `AndroidManifest.xml`.
 */
data class ManifestContractInfo(
    val appVersionName: String,
    val appVersionCode: Long,
    val applicationId: String,
    val fclPlugin: String,
    val description: String,
    val rendererSpec: String,
    val boatEnv: String,
    val pojavEnv: String,
    val minMcVer: String,
    val maxMcVer: String
)

/**
 * Aggregated snapshot for the Home, Tools, Info, and About screens.
 */
data class PluginOverviewSnapshot(
    val nativeStatus: NativeLibraryStatus,
    val launchers: List<LauncherAppStatus>,
    val manifestContract: ManifestContractInfo,
    val crashReportCount: Int,
    val latestCrashSummary: String?
)

data class OpenSourceLicenseItem(
    val id: String,
    val nameResId: Int,
    val spdxResId: Int,
    val summaryResId: Int,
    val fullNoticeText: String
)
