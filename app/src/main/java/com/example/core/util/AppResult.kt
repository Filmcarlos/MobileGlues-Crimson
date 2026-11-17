package com.example.core.util

/**
 * Explicit sealed Result wrapper for all domain, gateway, and storage operations.
 * Prevents silent exception swallowing and forces UI/ViewModel layers to surface errors.
 */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : AppResult<Nothing>
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Error -> this
}

inline fun <T> runCatchingResult(
    errorPrefix: String,
    block: () -> T
): AppResult<T> = try {
    AppResult.Success(block())
} catch (t: Throwable) {
    val detail = t.message?.takeIf { it.isNotBlank() } ?: t::class.java.simpleName
    AppResult.Error(message = "$errorPrefix: $detail", cause = t)
}
