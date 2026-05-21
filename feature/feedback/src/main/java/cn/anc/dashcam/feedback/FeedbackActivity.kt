package cn.anc.dashcam.feedback

import android.os.Bundle
import androidx.activity.compose.setContent
import cn.anc.dashcam.core.common.BaseActivity
import cn.anc.dashcam.core.logging.AppLog

class FeedbackActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.event("feedback_open")

        setContent {
            FeedbackScreen()
        }
    }
}
