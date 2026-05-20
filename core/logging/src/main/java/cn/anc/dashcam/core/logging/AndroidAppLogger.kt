package cn.anc.dashcam.core.logging

import android.util.Log
import java.io.File

class AndroidAppLogger(
    private var config: LogConfig,
    private val fileManager: LogFileManager,
) : AppLogger {

    override fun log(level: LogLevel, message: String, throwable: Throwable?, tag: String?) {
        val logTag = if (tag.isNullOrBlank()) DEFAULT_TAG else "$DEFAULT_TAG.$tag"
        val sanitizedMessage = LogSanitizer.sanitize(message)
        val messageWithLocation = sanitizedMessage.withLocation()

        if (config.enableConsoleLogging && level.allows(config.consoleMinLevel)) {
            writeConsoleLog(
                level = level,
                tag = logTag,
                message = messageWithLocation,
                throwable = throwable,
            )
        }

        fileManager.write(level, logTag, messageWithLocation, throwable)
    }

    override fun setLogLevel(level: LogLevel) {
        config = config.copy(consoleMinLevel = level, fileMinLevel = level)
        fileManager.updateConfig(config)
    }

    override fun getLogLevel(): LogLevel {
        return config.consoleMinLevel
    }

    override fun getLogFiles(): List<File> {
        return fileManager.files()
    }

    override fun getLogDirectory(): File {
        return fileManager.logDirectory()
    }

    override fun clearLogFiles() {
        fileManager.clear()
    }

    private fun writeConsoleLog(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        val consoleMessage = message.withSanitizedThrowable(throwable)
        when (level) {
            LogLevel.VERBOSE -> Log.v(tag, consoleMessage)
            LogLevel.DEBUG -> Log.d(tag, consoleMessage)
            LogLevel.INFO -> Log.i(tag, consoleMessage)
            LogLevel.WARN -> Log.w(tag, consoleMessage)
            LogLevel.ERROR,
            LogLevel.FATAL,
            -> Log.e(tag, consoleMessage)
        }
    }

    private fun String.withLocation(): String {
        if (!config.includeLocation) {
            return this
        }

        val location = callerLocation() ?: return this
        return "$this ($location)"
    }

    private fun String.withSanitizedThrowable(throwable: Throwable?): String {
        if (throwable == null) {
            return this
        }

        return "$this\n${LogSanitizer.sanitize(throwable.stackTraceToString())}"
    }

    private fun callerLocation(): String? {
        return Throwable().stackTrace.firstOrNull { element ->
            !element.className.startsWith(LOGGING_PACKAGE_NAME)
        }?.let { element ->
            "${element.fileName}:${element.lineNumber}"
        }
    }

    companion object {
        private const val DEFAULT_TAG = "Dashcam"
        private const val LOGGING_PACKAGE_NAME = "cn.anc.dashcam.core.logging"
    }
}
