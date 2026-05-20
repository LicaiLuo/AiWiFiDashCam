package cn.anc.dashcam.feedback

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import cn.anc.dashcam.core.logging.AppLog
import cn.anc.dashcam.core.logging.LogSanitizer
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class FeedbackFileCollector(
    context: Context,
) {

    private val appContext = context.applicationContext
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US)
    private val displayTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    fun createFeedbackPackage(description: String): File {
        val report = createReport(description)
        val feedbackDir = feedbackDirectory()
        feedbackDir.mkdirs()

        val feedbackFile = File(feedbackDir, "feedback-${fileNameFormat.format(Date(report.createdAtMillis))}.zip")
        ZipOutputStream(feedbackFile.outputStream().buffered()).use { zip ->
            zip.writestr("metadata.txt", buildMetadata(report))
            report.logFiles.forEach { file ->
                if (file.exists() && file.isFile) {
                    zip.writestr("logs/${file.name}", LogSanitizer.sanitize(file.readText(Charsets.UTF_8)))
                }
            }
        }

        AppLog.i("feedback package created path=${feedbackFile.absolutePath}", tag = "Feedback")
        return feedbackFile
    }

    fun feedbackDirectory(): File {
        return appContext.getExternalFilesDir("feedback") ?: File(appContext.filesDir, "feedback")
    }

    private fun createReport(description: String): FeedbackReport {
        val packageInfo = appContext.packageInfo()
        val packageName = appContext.packageName

        return FeedbackReport(
            description = LogSanitizer.sanitize(description),
            packageName = packageName,
            versionName = packageInfo.versionName.orEmpty(),
            versionCode = packageInfo.versionCodeCompat(),
            brand = packageName.substringAfterLast('.', missingDelimiterValue = packageName),
            device = "${Build.MANUFACTURER} ${Build.MODEL}",
            androidVersion = "${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})",
            createdAtMillis = System.currentTimeMillis(),
            logFiles = AppLog.getLogFiles().take(MAX_LOG_FILE_COUNT),
        )
    }

    private fun buildMetadata(report: FeedbackReport): String {
        return buildString {
            appendLine("created=${displayTimeFormat.format(Date(report.createdAtMillis))}")
            appendLine("package=${report.packageName}")
            appendLine("brand=${report.brand}")
            appendLine("versionName=${report.versionName}")
            appendLine("versionCode=${report.versionCode}")
            appendLine("device=${report.device}")
            appendLine("android=${report.androidVersion}")
            appendLine("logDirectory=${AppLog.getLogDirectory().absolutePath}")
            appendLine("description=${report.description}")
        }
    }

    private fun Context.packageInfo(): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
    }

    private fun PackageInfo.versionCodeCompat(): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longVersionCode
        } else {
            @Suppress("DEPRECATION")
            versionCode.toLong()
        }
    }

    private fun ZipOutputStream.writestr(name: String, value: String) {
        putNextEntry(ZipEntry(name))
        write(value.toByteArray(Charsets.UTF_8))
        closeEntry()
    }

    companion object {
        private const val MAX_LOG_FILE_COUNT = 8
    }
}
