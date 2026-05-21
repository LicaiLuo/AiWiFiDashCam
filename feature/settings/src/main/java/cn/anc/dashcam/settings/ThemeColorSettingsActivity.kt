package cn.anc.dashcam.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.BaseActivity
import cn.anc.dashcam.core.logging.AppLog

class ThemeColorSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.i("theme color settings created", tag = "Settings")

        setContent {
            ThemeColorSettingsScreen(
                selectedColor = AppThemeManager.currentThemeColor(this),
                onBackClick = ::finish,
                onColorSelected = { color ->
                    AppLog.i("theme color selected tag=${color.tag}", tag = "Settings")
                    AppThemeManager.saveThemeColor(this, color)
                    recreate()
                },
            )
        }
    }
}
