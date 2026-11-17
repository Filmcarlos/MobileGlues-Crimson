package com.example.core.crash

import com.example.core.logging.AppLogger
import com.example.core.util.DispatcherProvider
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.withContext

/**
 * Local-only crash handler that captures uncaught thread exceptions into the app's private
 * sandbox (`files/crash_reports/`) without any network transmission.
 */
class LocalCrashHandler(
    private val crashDirectory: File,
    private val logger: AppLogger,
    private val dispatchers: DispatcherProvider
) {
    private val fileTimestampFormatter = DateTimeFormatter.ofPattern(CRASH_FILE_TS_PATTERN, Locale.US)
        .withZone(ZoneId.systemDefault())

    fun install() {
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            saveCrashSync(thread.name, throwable)
            previousHandler?.uncaughtException(thread, throwable)
        }
        logger.i(TAG, "Local zero-network CrashHandler installed")
    }

    private fun saveCrashSync(threadName: String, throwable: Throwable) {
        runCatching {
            if (!crashDirectory.exists()) {
                crashDirectory.mkdirs()
            }
            val timestamp = fileTimestampFormatter.format(Instant.now())
            val reportFile = File(crashDirectory, "${CRASH_FILE_PREFIX}${timestamp}${CRASH_FILE_EXT}")
            val stackTrace = StringWriter().also { sw ->
                PrintWriter(sw).use { pw ->
                    throwable.printStackTrace(pw)
                }
            }.toString()

            val reportContent = buildString {
                appendLine("=== MobileGlues Crimson Local Crash Report ===")
                appendLine("Timestamp: $timestamp")
                appendLine("Thread: $threadName")
                appendLine("Exception: ${throwable::class.java.name}: ${throwable.message}")
                appendLine("----------------------------------------------")
                appendLine(stackTrace)
            }
            reportFile.writeText(reportContent)
            logger.e(TAG, "Uncaught exception saved to ${reportFile.name}", throwable)
        }
    }

    suspend fun getCrashReportCount(): Int = withContext(dispatchers.io) {
        if (!crashDirectory.exists()) return@withContext 0
        crashDirectory.listFiles { file ->
            file.isFile && file.name.startsWith(CRASH_FILE_PREFIX) && file.name.endsWith(CRASH_FILE_EXT)
        }?.size ?: 0
    }

    suspend fun getLatestCrashSummary(): String? = withContext(dispatchers.io) {
        if (!crashDirectory.exists()) return@withContext null
        val latest = crashDirectory.listFiles { file ->
            file.isFile && file.name.startsWith(CRASH_FILE_PREFIX) && file.name.endsWith(CRASH_FILE_EXT)
        }?.maxByOrNull { it.lastModified() } ?: return@withContext null
        runCatching { latest.readLines().take(MAX_SUMMARY_LINES).joinToString("\n") }.getOrNull()
    }

    suspend fun clearCrashReports() = withContext(dispatchers.io) {
        if (!crashDirectory.exists()) return@withContext
        crashDirectory.listFiles()?.forEach { file ->
            if (file.isFile) file.delete()
        }
        logger.i(TAG, "Cleared local crash reports")
    }

    companion object {
        private const val TAG = "CrashHandler"
        private const val CRASH_FILE_PREFIX = "crash_"
        private const val CRASH_FILE_EXT = ".txt"
        private const val CRASH_FILE_TS_PATTERN = "yyyyMMdd_HHmmss"
        private const val MAX_SUMMARY_LINES = 8
    }
}
