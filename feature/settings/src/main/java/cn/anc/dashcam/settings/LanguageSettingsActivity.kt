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
import cn.anc.dashcam.core.common.AppLanguage
import cn.anc.dashcam.core.common.AppLocaleManager
import cn.anc.dashcam.core.logging.AppLog

class LanguageSettingsActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLocaleManager.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.i("language settings created", tag = "Settings")

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = AppSettingsColors.current().pageBackground,
            ) {
                LanguageSettingsPage(
                    selectedLanguage = AppLocaleManager.currentLanguage(this),
                    onBackClick = ::finish,
                    onLanguageSelected = { language ->
                        AppLog.i("language selected tag=${language.tag}", tag = "Settings")
                        AppLocaleManager.saveLanguage(this, language)
                        recreate()
                    },
                )
            }
        }
    }
}

@Composable
private fun LanguageSettingsPage(
    selectedLanguage: AppLanguage,
    onBackClick: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_language_title),
        onBackClick = onBackClick,
    ) {
        OptionRow(
            title = stringResource(AppLanguage.ZH_CN.labelRes),
            selected = selectedLanguage == AppLanguage.ZH_CN,
            onClick = { onLanguageSelected(AppLanguage.ZH_CN) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OptionRow(
            title = stringResource(AppLanguage.EN.labelRes),
            selected = selectedLanguage == AppLanguage.EN,
            onClick = { onLanguageSelected(AppLanguage.EN) },
        )
    }
}
