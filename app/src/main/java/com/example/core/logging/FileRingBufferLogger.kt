package com.example.core.logging

import com.example.core.util.DispatcherProvider
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Thread-safe local ring-buffer + file-backed implementation of [AppLogger].
 * Never transmits any data over the network.
 */
class FileRingBufferLogger(
    private val logDirectory: File,
    private val dispatchers: DispatcherProvider,
    private val maxEntries: Int = MAX_RING_BUFFER_ENTRIES
) : AppLogger {

    private val scope = CoroutineScope(SupervisorJob() + dispatchers.io)
    private val fileMutex = Mutex()
    private val idCounter = AtomicLong(1L)
    private val timeFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN, Locale.US)
        .withZone(ZoneId.systemDefault())

    private val _entries = MutableStateFlow<List<LogEntry>>(emptyList())
    override val entries: StateFlow<List<LogEntry>> = _entries.asStateFlow()

    private val logFile: File
        get() = File(logDirectory, LOG_FILE_NAME)

    init {
        scope.launch {
            loadPersistedEntries()
        }
    }

    override fun d(tag: String, message: String) {
        record(LogLevel.DEBUG, tag, message, null)
    }

    override fun i(tag: String, message: String) {
        record(LogLevel.INFO, tag, message, null)
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        record(LogLevel.WARN, tag, message, throwable)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        record(LogLevel.ERROR, tag, message, throwable)
    }

    override suspend fun exportLogsAsText(): String = withContext(dispatchers.default) {
        val current = _entries.value
        if (current.isEmpty()) {
            return@withContext ""
        }
        buildString {
            appendLine("=== MobileGlues Crimson Diagnostic Log ===")
            current.forEach { entry ->
                append('[')
                append(entry.timestampFormatted)
                append("] [")
                append(entry.level.badge)
                append("] [")
                append(entry.tag)
                append("] ")
                appendLine(entry.message)
            }
        }
    }

    override suspend fun clearLogs() {
        _entries.value = emptyList()
        withContext(dispatchers.io) {
            fileMutex.withLock {
                if (logFile.exists()) {
                    logFile.delete()
                }
            }
        }
    }

    private fun record(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        val fullMessage = if (throwable != null) {
            "$message | ${throwable::class.java.simpleName}: ${throwable.message.orEmpty()}"
        } else {
            message
        }
        val entry = LogEntry(
            id = idCounter.getAndIncrement(),
            timestampFormatted = timeFormatter.format(Instant.now()),
            level = level,
            tag = tag,
            message = fullMessage
        )
        _entries.update { current ->
            val updated = current + entry
            if (updated.size > maxEntries) updated.takeLast(maxEntries) else updated
        }
        scope.launch {
            appendEntryToDisk(entry)
        }
    }

    private suspend fun appendEntryToDisk(entry: LogEntry) {
        fileMutex.withLock {
            runCatching {
                if (!logDirectory.exists()) {
                    logDirectory.mkdirs()
                }
                val serialized = "${entry.timestampFormatted}|${entry.level.name}|${entry.tag}|${
                    entry.message.replace('\n', ' ')
                }\n"
                logFile.appendText(serialized)
            }
        }
    }

    private suspend fun loadPersistedEntries() {
        fileMutex.withLock {
            runCatching {
                if (!logFile.exists()) return@withLock
                val lines = logFile.readLines().takeLast(maxEntries)
                val restored = lines.mapNotNull { line ->
                    val parts = line.split('|', limit = LOG_COLUMN_COUNT)
                    if (parts.size < LOG_COLUMN_COUNT) return@mapNotNull null
                    val level = runCatching { LogLevel.valueOf(parts[1]) }.getOrDefault(LogLevel.INFO)
                    LogEntry(
                        id = idCounter.getAndIncrement(),
                        timestampFormatted = parts[0],
                        level = level,
                        tag = parts[2],
                        message = parts[3]
                    )
                }
                if (restored.isNotEmpty()) {
                    _entries.update { current ->
                        (restored + current).takeLast(maxEntries)
                    }
                }
            }
        }
    }

    companion object {
        private const val MAX_RING_BUFFER_ENTRIES = 250
        private const val LOG_FILE_NAME = "crimson_diagnostics.log"
        private const val TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS"
        private const val LOG_COLUMN_COUNT = 4
    }
}
