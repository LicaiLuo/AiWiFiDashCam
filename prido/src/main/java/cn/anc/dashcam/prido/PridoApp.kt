package cn.anc.dashcam.prido

import cn.anc.dashcam.core.common.DashcamApplication
import cn.anc.dashcam.core.logging.LogLevel

class PridoApp : DashcamApplication() {

    override val fileMinLevel: LogLevel = LogLevel.VERBOSE

    override val startupLogMessage: String = "prido app started"
}
