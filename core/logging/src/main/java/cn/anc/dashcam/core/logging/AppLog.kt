package cn.anc.dashcam.core.logging

import android.content.Context
import android.os.Process
import java.io.File
import kotlin.system.exitProcess

object AppLog {

    @Volatile
    private var logger: AppLogger = NoOpLogger

    private var appContext: Context? = null
    private var config: LogConfig? = null
    private var fileManager: LogFileManager? = null
    private var crashHandlerInstalled = false

    @Synchronized
    fun initialize(context: Context, customConfig: LogConfig? = null) {
        val applicationContext = context.applicationContext
        val nextConfig = customConfig ?: LogConfig.createDefault(applicationContext)
        val nextFileManager = fileManager ?: LogFileManager(applicationContext, nextConfig)

        nextFileManager.updateConfig(nextConfig)
        appContext = applicationContext
        config = nextConfig
        fileManager = nextFileManager
        logger = AndroidAppLogger(nextConfig, nextFileManager)

        i("logging initialized, package=${nextConfig.packageName}", tag = "AppLog")
    }

    @Synchronized
    fun setEnabled(enabled: Boolean) {
        val context = appContext ?: return
        val currentConfig = config ?: LogConfig.createDefault(context)

        if (enabled) {
            initialize(context, currentConfig)
        } else {
            logger = NoOpLogger
        }
    }

    fun isEnabled(): Boolean {
        return logger !== NoOpLogger
    }

    fun v(message: String, tag: String? = null) {
        logger.log(LogLevel.VERBOSE, message, tag = tag)
    }

    fun d(message: String, tag: String? = null) {
        logger.log(LogLevel.DEBUG, message, tag = tag)
    }

    fun i(message: String, tag: String? = null) {
        logger.log(LogLevel.INFO, message, tag = tag)
    }

    fun w(message: String, throwable: Throwable? = null, tag: String? = null) {
        logger.log(LogLevel.WARN, message, throwable = throwable, tag = tag)
    }

    fun e(message: String, throwable: Throwable? = null, tag: String? = null) {
        logger.log(LogLevel.ERROR, message, throwable = throwable, tag = tag)
    }

    fun f(message: String, throwable: Throwable? = null, tag: String? = null) {
        logger.log(LogLevel.FATAL, message, throwable = throwable, tag = tag)
    }

    fun event(name: String, params: Map<String, String> = emptyMap()) {
        val suffix = if (params.isEmpty()) {
            ""
        } else {
            params.entries.joinToString(prefix = " ", separator = ",") { "${it.key}=${it.value}" }
        }
        i("event=$name$suffix", tag = "Event")
    }

    fun network(method: String, url: String, statusCode: Int, durationMs: Long) {
        i(
            message = "network method=$method url=$url status=$statusCode duration_ms=$durationMs",
            tag = "Network",
        )
    }

    fun <T> measure(name: String, block: () -> T): T {
        val start = System.currentTimeMillis()
        return try {
            block()
        } finally {
            val duration = System.currentTimeMillis() - start
            d("measure name=$name duration_ms=$duration", tag = "Measure")
        }
    }

    fun setLogLevel(level: LogLevel) {
        logger.setLogLevel(level)
    }

    fun getLogLevel(): LogLevel {
        return logger.getLogLevel()
    }

    fun getLogFiles(): List<File> {
        return logger.getLogFiles()
    }

    fun getLogDirectory(): File {
        return logger.getLogDirectory()
    }

    fun clearLogFiles() {
        logger.clearLogFiles()
    }

    @Synchronized
    fun installCrashHandler() {
        if (crashHandlerInstalled) {
            return
        }

        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            f("application crash", throwable = throwable, tag = "Crash")
            fileManager?.writeCrashSync(throwable)
            if (previousHandler != null) {
                previousHandler.uncaughtException(thread, throwable)
            } else {
                Process.killProcess(Process.myPid())
                exitProcess(10)
            }
        }
        crashHandlerInstalled = true
    }
}
