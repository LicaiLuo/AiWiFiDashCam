package cn.anc.dashcam.feedback

import android.content.Context
import cn.anc.dashcam.core.logging.AppLog
import java.io.File

class FeedbackManager(
    context: Context,
) {

    private val collector = FeedbackFileCollector(context)

    fun createFeedbackPackage(description: String): Result<File> {
        return runCatching {
            collector.createFeedbackPackage(description)
        }.onFailure { error ->
            AppLog.e("create feedback package failed", throwable = error, tag = "Feedback")
        }
    }
}
