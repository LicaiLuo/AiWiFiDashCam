package cn.anc.dashcam.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import cn.anc.dashcam.core.common.AppLocaleManager
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.BaseActivity
import cn.anc.dashcam.core.logging.AppLog

class SettingsActivity : BaseActivity() {

    private var activeLanguageTag: String? = null
    private var activeThemeModeTag: String? = null
    private var activeThemeColorTag: String? = null
    private var activeStatusBarModeTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        snapshotSettingsTags()
        AppLog.i("settings created", tag = "Settings")

        setContent {
            AppSettingsScreen(
                currentLanguage = AppLocaleManager.currentLanguage(this),
                currentThemeMode = AppThemeManager.currentThemeMode(this),
                currentThemeColor = AppThemeManager.currentThemeColor(this),
                currentStatusBarTextMode = AppThemeManager.currentStatusBarTextMode(this),
                onBackClick = ::finish,
                onLanguageClick = { startActivity(Intent(this, LanguageSettingsActivity::class.java)) },
                onThemeClick = { startActivity(Intent(this, ThemeSettingsActivity::class.java)) },
                onThemeColorClick = { startActivity(Intent(this, ThemeColorSettingsActivity::class.java)) },
                onStatusBarClick = { startActivity(Intent(this, StatusBarSettingsActivity::class.java)) },
            )
        }
    }

    override fun onResume() {
        super.onResume()

        val currentLanguageTag = AppLocaleManager.currentLanguageTag(this)
        val currentThemeModeTag = AppThemeManager.currentThemeModeTag(this)
        val currentThemeColorTag = AppThemeManager.currentThemeColorTag(this)
        val currentStatusBarModeTag = AppThemeManager.currentStatusBarTextMode(this).tag

        if (activeLanguageTag != currentLanguageTag ||
            activeThemeModeTag != currentThemeModeTag ||
            activeThemeColorTag != currentThemeColorTag ||
            activeStatusBarModeTag != currentStatusBarModeTag
        ) {
            snapshotSettingsTags()
            recreate()
        }
    }

    private fun snapshotSettingsTags() {
        activeLanguageTag = AppLocaleManager.currentLanguageTag(this)
        activeThemeModeTag = AppThemeManager.currentThemeModeTag(this)
        activeThemeColorTag = AppThemeManager.currentThemeColorTag(this)
        activeStatusBarModeTag = AppThemeManager.currentStatusBarTextMode(this).tag
    }
}
