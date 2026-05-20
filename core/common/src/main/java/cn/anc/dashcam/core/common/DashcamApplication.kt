package cn.anc.dashcam.core.common

import android.app.Application
import cn.anc.dashcam.core.logging.AppLog
import cn.anc.dashcam.core.logging.LogConfig
import cn.anc.dashcam.core.logging.LogLevel

abstract class DashcamApplication : Application() {

    protected open val logDirectoryName: String = LogConfig.DEFAULT_LOG_DIRECTORY_NAME

    protected open val fileMinLevel: LogLevel? = null

    protected open val startupLogMessage: String
        get() = "app started"

    override fun onCreate() {
        super.onCreate()
        AppLocaleManager.initialize(this)
        AppLog.initialize(this, createLogConfig())
        AppLog.installCrashHandler()
        AppLog.i(startupLogMessage, tag = "App")
    }

    private fun createLogConfig(): LogConfig {
        return LogConfig.createDefault(
            context = this,
            logDirectoryName = logDirectoryName,
        ).let { config ->
            fileMinLevel?.let { config.copy(fileMinLevel = it) } ?: config
        }
    }
}
