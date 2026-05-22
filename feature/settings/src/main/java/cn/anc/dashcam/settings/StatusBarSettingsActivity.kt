package cn.anc.dashcam.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.BaseActivity
import cn.anc.dashcam.core.common.StatusBarTextMode
import cn.anc.dashcam.core.logging.AppLog

class StatusBarSettingsActivity : BaseActivity() {

    override fun getStatusBarTextMode(): StatusBarTextMode {
        return AppThemeManager.currentStatusBarTextMode(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.i("status bar settings created", tag = "Settings")

        setContent {
            StatusBarSettingsScreen(
                selectedMode = AppThemeManager.currentStatusBarTextMode(this),
                onBackClick = ::finish,
                onModeSelected = { mode ->
                    AppLog.i("status bar mode selected tag=${mode.tag}", tag = "Settings")
                    AppThemeManager.saveStatusBarTextMode(this, mode)
                    recreate()
                },
            )
        }
    }
}
