package cn.anc.dashcam.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.BaseActivity
import cn.anc.dashcam.core.logging.AppLog

class ThemeSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.i("theme settings created", tag = "Settings")

        setContent {
            ThemeSettingsScreen(
                selectedThemeMode = AppThemeManager.currentThemeMode(this),
                onBackClick = ::finish,
                onThemeModeSelected = { themeMode ->
                    AppLog.i("theme mode selected tag=${themeMode.tag}", tag = "Settings")
                    AppThemeManager.saveThemeMode(this, themeMode)
                    recreate()
                },
            )
        }
    }
}
