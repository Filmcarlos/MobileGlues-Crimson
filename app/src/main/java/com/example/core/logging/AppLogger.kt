package com.example.core.logging

import kotlinx.coroutines.flow.StateFlow

enum class LogLevel(val badge: String) {
    DEBUG("DBG"),
    INFO("INF"),
    WARN("WRN"),
    ERROR("ERR")
}

data class LogEntry(
    val id: Long,
    val timestampFormatted: String,
    val level: LogLevel,
    val tag: String,
    val message: String
)

/**
 * Lightweight, 100% offline logger that maintains an observable ring buffer for the in-app
 * Log Viewer screen and persists diagnostic traces to local sandbox storage.
 */
interface AppLogger {
    val entries: StateFlow<List<LogEntry>>
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String, throwable: Throwable? = null)
    fun e(tag: String, message: String, throwable: Throwable? = null)
    suspend fun exportLogsAsText(): String
    suspend fun clearLogs()
}
