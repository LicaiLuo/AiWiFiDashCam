package cn.anc.dashcam.core.logging

import java.io.File

object NoOpLogger : AppLogger {

    override fun log(level: LogLevel, message: String, throwable: Throwable?, tag: String?) = Unit

    override fun setLogLevel(level: LogLevel) = Unit

    override fun getLogLevel(): LogLevel {
        return LogLevel.WARN
    }

    override fun getLogFiles(): List<File> {
        return emptyList()
    }

    override fun getLogDirectory(): File {
        return File("")
    }

    override fun clearLogFiles() = Unit
}
