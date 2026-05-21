package cn.anc.dashcam.home

import android.os.Bundle
import androidx.activity.compose.setContent
import cn.anc.dashcam.core.common.AppConfig
import cn.anc.dashcam.core.common.AppLocaleManager
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.BaseActivity
import cn.anc.dashcam.core.logging.AppLog

/**
 * 应用主屏幕主 Activity。
 *
 * Activity 只负责生命周期、配置刷新和 Compose 入口挂载；具体品牌 UI 已拆分到独立文件。
 */
class HomeActivity : BaseActivity() {

    private var activeLanguageTag: String? = null
    private var activeThemeModeTag: String? = null
    private var activeThemeColorTag: String? = null
    private var activeStatusBarModeTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        snapshotSettingsTags()
        AppLog.i("home created with brand: ${AppConfig.brandType.name}", tag = "Home")

        setContent {
            HomeScreen()
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
