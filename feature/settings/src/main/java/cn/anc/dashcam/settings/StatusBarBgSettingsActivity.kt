package cn.anc.dashcam.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.BaseActivity
import cn.anc.dashcam.core.logging.AppLog

class StatusBarBgSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.i("status bar bg settings created", tag = "Settings")

        setContent {
            StatusBarBgSettingsScreen(
                selectedOption = AppThemeManager.currentStatusBarBgColorOption(this),
                onBackClick = ::finish,
                onOptionSelected = { option ->
                    AppLog.i("status bar bg selected tag=${option.tag}", tag = "Settings")
                    AppThemeManager.saveStatusBarBgColorOption(this, option)
                    recreate()
                },
            )
        }
    }
}
