package cn.anc.dashcam.core.logging

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.ArrayDeque
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LogFileManager(
    context: Context,
    private var config: LogConfig,
) {

    private val appContext = context.applicationContext
    private val lock = Any()
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val pendingLines = ArrayDeque<String>()
    private val logDateFormat = threadLocalDateFormat("yyyy-MM-dd")
    private val lineDateFormat = threadLocalDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    private val rotateDateFormat = threadLocalDateFormat("yyyy-MM-dd-HH-mm-ss-SSS")
    private var drainScheduled = false

    fun updateConfig(newConfig: LogConfig) {
        config = newConfig
    }

    fun logDirectory(): File {
        return appContext.getExternalFilesDir(config.logDirectoryName)
            ?: File(appContext.filesDir, config.logDirectoryName)
    }

    fun write(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        if (!config.enableFileLogging || !level.allows(config.fileMinLevel)) {
            return
        }

        val line = buildLine(level, tag, message, throwable).limitLength()
        synchronized(lock) {
            if (pendingLines.size >= MAX_PENDING_LINE_COUNT) {
                while (pendingLines.size >= MAX_PENDING_LINE_COUNT - 1) {
                    pendingLines.removeFirst()
                }
                pendingLines.addLast(buildDroppedLine())
            }
            pendingLines.addLast(line)
            if (!drainScheduled) {
                drainScheduled = true
                executor.execute {
                    drainPendingLines()
                }
            }
        }
    }

    fun writeCrashSync(throwable: Throwable) {
        val crashFile = File(logDirectory(), "crash-${format(rotateDateFormat)}.log")
        val message = buildString {
            appendLine("timestamp=${format(lineDateFormat)}")
            appendLine("package=${config.packageName}")
            appendLine("exception=${throwable.javaClass.name}")
            appendLine("message=${LogSanitizer.sanitize(throwable.message.orEmpty())}")
            appendLine("stacktrace=")
            append(LogSanitizer.sanitize(throwable.stackTraceToString()))
        }

        synchronized(lock) {
            crashFile.parentFile?.mkdirs()
            crashFile.writeText(message, Charsets.UTF_8)
            cleanupLocked()
        }
    }

    fun files(): List<File> {
        val dir = logDirectory()
        if (!dir.exists()) {
            return emptyList()
        }

        return dir.listFiles()
            ?.filter { it.isFile && (it.extension == "log" || it.extension == "txt") }
            ?.sortedByDescending { it.lastModified() }
            .orEmpty()
    }

    fun clear() {
        synchronized(lock) {
            files().forEach { file ->
                file.delete()
            }
        }
    }

    fun shutdown() {
        executor.shutdown()
        executor.awaitTermination(2L, TimeUnit.SECONDS)
    }

    private fun buildLine(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?,
    ): String {
        val sanitizedMessage = LogSanitizer.sanitize(message)
        val throwableText = throwable?.let {
            "\n${LogSanitizer.sanitize(it.stackTraceToString())}"
        }.orEmpty()

        return "${format(lineDateFormat)} ${level.name}/$tag: $sanitizedMessage$throwableText\n"
    }

    private fun drainPendingLines() {
        while (true) {
            val batch = synchronized(lock) {
                if (pendingLines.isEmpty()) {
                    drainScheduled = false
                    null
                } else {
                    ArrayList<String>(pendingLines).also {
                        pendingLines.clear()
                    }
                }
            } ?: return

            writeLines(batch)
        }
    }

    private fun writeLines(lines: List<String>) {
        if (lines.isEmpty()) {
            return
        }

        val content = buildString {
            lines.forEach { append(it) }
        }
        val contentSizeBytes = content.toByteArray(Charsets.UTF_8).size

        synchronized(lock) {
            val dir = logDirectory()
            dir.mkdirs()

            val activeFile = File(dir, "app-${format(logDateFormat)}.log")
            rotateIfNeededLocked(activeFile, contentSizeBytes)
            activeFile.appendText(content, Charsets.UTF_8)
            cleanupLocked()
        }
    }

    private fun rotateIfNeededLocked(activeFile: File, incomingBytes: Int) {
        if (!activeFile.exists() || activeFile.length() + incomingBytes <= config.maxFileSizeBytes) {
            return
        }

        val rotatedFile = nextRotatedFile(activeFile.parentFile)
        if (activeFile.renameTo(rotatedFile)) {
            return
        }

        runCatching {
            activeFile.copyTo(rotatedFile, overwrite = false)
            activeFile.delete()
        }.getOrElse {
            activeFile.delete()
        }
    }

    private fun cleanupLocked() {
        val now = System.currentTimeMillis()
        val retentionMs = TimeUnit.DAYS.toMillis(config.retentionDays.toLong())
        val logFiles = files()

        logFiles
            .filter { now - it.lastModified() > retentionMs }
            .forEach { it.delete() }

        files()
            .drop(config.maxFileCount)
            .forEach { it.delete() }
    }

    private fun nextRotatedFile(directory: File?): File {
        val parent = directory ?: logDirectory()
        val timestamp = format(rotateDateFormat)
        var index = 0
        while (true) {
            val suffix = if (index == 0) "" else "-$index"
            val candidate = File(parent, "app-$timestamp$suffix.log")
            if (!candidate.exists()) {
                return candidate
            }
            index++
        }
    }

    private fun buildDroppedLine(): String {
        return "${format(lineDateFormat)} WARN/LogFileManager: dropped oldest log line because pending queue is full\n"
    }

    private fun String.limitLength(): String {
        if (length <= MAX_LINE_LENGTH) {
            return this
        }

        return take(MAX_LINE_LENGTH) + "\n... log line truncated because it exceeded $MAX_LINE_LENGTH chars\n"
    }

    private fun format(formatter: ThreadLocal<SimpleDateFormat>): String {
        return formatter.get().format(Date())
    }

    private fun threadLocalDateFormat(pattern: String): ThreadLocal<SimpleDateFormat> {
        return object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat(pattern, Locale.US)
            }
        }
    }

    companion object {
        private const val MAX_PENDING_LINE_COUNT = 1_000
        private const val MAX_LINE_LENGTH = 16_384
    }
}
