package cn.anc.dashcam.uniden

import cn.anc.dashcam.core.common.DashcamApplication

class UnidenApp : DashcamApplication() {

    override val logDirectoryName: String = "uniden_logs"

    override val startupLogMessage: String = "uniden app started"
}
