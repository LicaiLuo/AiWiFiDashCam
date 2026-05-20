package cn.anc.dashcam.core.logging

import android.util.Log

enum class LogLevel(val priority: Int) {
    VERBOSE(Log.VERBOSE),
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR),
    FATAL(Log.ASSERT);

    fun allows(minLevel: LogLevel): Boolean {
        return ordinal >= minLevel.ordinal
    }
}
