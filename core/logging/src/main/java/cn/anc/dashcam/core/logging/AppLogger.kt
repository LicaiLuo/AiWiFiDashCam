package cn.anc.dashcam.core.logging

import java.io.File

interface AppLogger {
    fun log(level: LogLevel, message: String, throwable: Throwable? = null, tag: String? = null)
    fun setLogLevel(level: LogLevel)
    fun getLogLevel(): LogLevel
    fun getLogFiles(): List<File>
    fun getLogDirectory(): File
    fun clearLogFiles()
}
