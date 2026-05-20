package cn.anc.dashcam.core.common

sealed interface AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>
    data class Error(val throwable: Throwable) : AppResult<Nothing>
}

fun normalizeBrandCode(value: String): String {
    return value.trim().lowercase()
}
