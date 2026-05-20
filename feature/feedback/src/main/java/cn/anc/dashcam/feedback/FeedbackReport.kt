package cn.anc.dashcam.feedback

import java.io.File

data class FeedbackReport(
    val description: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val brand: String,
    val device: String,
    val androidVersion: String,
    val createdAtMillis: Long,
    val logFiles: List<File>,
)
