package cn.anc.dashcam.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.anc.dashcam.core.common.AppLanguage
import cn.anc.dashcam.core.common.AppLocaleManager
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.AppThemeMode
import cn.anc.dashcam.core.logging.AppLog

class SettingsActivity : ComponentActivity() {

    private var activeLanguageTag: String? = null
    private var activeThemeModeTag: String? = null

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLocaleManager.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activeLanguageTag = AppLocaleManager.currentLanguageTag(this)
        activeThemeModeTag = AppThemeManager.currentThemeModeTag(this)
        AppLog.i("settings created", tag = "Settings")

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = AppSettingsColors.current().pageBackground,
            ) {
                AppSettingsPage(
                    currentLanguage = AppLocaleManager.currentLanguage(this),
                    currentThemeMode = AppThemeManager.currentThemeMode(this),
                    onBackClick = ::finish,
                    onLanguageClick = {
                        startActivity(Intent(this, LanguageSettingsActivity::class.java))
                    },
                    onThemeClick = {
                        startActivity(Intent(this, ThemeSettingsActivity::class.java))
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentLanguageTag = AppLocaleManager.currentLanguageTag(this)
        val currentThemeModeTag = AppThemeManager.currentThemeModeTag(this)
        if (activeLanguageTag != currentLanguageTag || activeThemeModeTag != currentThemeModeTag) {
            activeLanguageTag = currentLanguageTag
            activeThemeModeTag = currentThemeModeTag
            recreate()
        }
    }
}

@Composable
private fun AppSettingsPage(
    currentLanguage: AppLanguage,
    currentThemeMode: AppThemeMode,
    onBackClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onThemeClick: () -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_title),
        onBackClick = onBackClick,
    ) {
        SettingValueRow(
            title = stringResource(R.string.settings_language),
            value = stringResource(currentLanguage.labelRes),
            onClick = onLanguageClick,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SettingValueRow(
            title = stringResource(R.string.settings_theme_mode),
            value = stringResource(currentThemeMode.labelRes),
            onClick = onThemeClick,
        )
    }
}
