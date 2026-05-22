package cn.anc.dashcam.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.BaseActivity
import cn.anc.dashcam.core.logging.AppLog

class TitleBarBgSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.i("title bar bg settings created", tag = "Settings")

        setContent {
            TitleBarBgSettingsScreen(
                selectedOption = AppThemeManager.currentTitleBarBgColorOption(this),
                onBackClick = ::finish,
                onOptionSelected = { option ->
                    AppLog.i("title bar bg selected tag=${option.tag}", tag = "Settings")
                    AppThemeManager.saveTitleBarBgColorOption(this, option)
                    recreate()
                },
            )
        }
    }
}
