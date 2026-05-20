package cn.anc.dashcam.settings

import android.content.Context
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
import cn.anc.dashcam.core.common.AppLocaleManager
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.AppThemeMode
import cn.anc.dashcam.core.logging.AppLog

class ThemeSettingsActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLocaleManager.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.i("theme settings created", tag = "Settings")

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = AppSettingsColors.current().pageBackground,
            ) {
                ThemeSettingsPage(
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
}

@Composable
private fun ThemeSettingsPage(
    selectedThemeMode: AppThemeMode,
    onBackClick: () -> Unit,
    onThemeModeSelected: (AppThemeMode) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_theme_mode_title),
        onBackClick = onBackClick,
    ) {
        OptionRow(
            title = stringResource(AppThemeMode.LIGHT.labelRes),
            selected = selectedThemeMode == AppThemeMode.LIGHT,
            onClick = { onThemeModeSelected(AppThemeMode.LIGHT) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OptionRow(
            title = stringResource(AppThemeMode.DARK.labelRes),
            selected = selectedThemeMode == AppThemeMode.DARK,
            onClick = { onThemeModeSelected(AppThemeMode.DARK) },
        )
    }
}
