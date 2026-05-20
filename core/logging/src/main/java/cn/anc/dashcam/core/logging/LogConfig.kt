package cn.anc.dashcam.core.logging

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

data class LogConfig(
    val packageName: String,
    val logDirectoryName: String,
    val enableConsoleLogging: Boolean,
    val includeLocation: Boolean,
    val enableFileLogging: Boolean,
    val consoleMinLevel: LogLevel,
    val fileMinLevel: LogLevel,
    val maxFileSizeBytes: Long,
    val maxFileCount: Int,
    val retentionDays: Int,
) {

    companion object {
        fun createDefault(
            context: Context,
            logDirectoryName: String = DEFAULT_LOG_DIRECTORY_NAME,
        ): LogConfig {
            val appContext = context.applicationContext
            val isDebuggable = appContext.isDebuggable()

            return LogConfig(
                packageName = appContext.packageName,
                logDirectoryName = logDirectoryName,
                enableConsoleLogging = isDebuggable,
                includeLocation = true,
                enableFileLogging = true,
                consoleMinLevel = if (isDebuggable) LogLevel.VERBOSE else LogLevel.WARN,
                fileMinLevel = if (isDebuggable) LogLevel.DEBUG else LogLevel.WARN,
                maxFileSizeBytes = if (isDebuggable) 5L * 1024L * 1024L else 2L * 1024L * 1024L,
                maxFileCount = if (isDebuggable) 10 else 5,
                retentionDays = if (isDebuggable) 3 else 7,
            )
        }

        private fun Context.isDebuggable(): Boolean {
            return try {
                val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                (appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            } catch (error: PackageManager.NameNotFoundException) {
                false
            }
        }

        const val DEFAULT_LOG_DIRECTORY_NAME = "app_logs"
    }
}
