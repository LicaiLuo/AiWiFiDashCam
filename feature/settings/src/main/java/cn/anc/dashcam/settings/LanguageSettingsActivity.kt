package cn.anc.dashcam.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import cn.anc.dashcam.core.common.AppLocaleManager
import cn.anc.dashcam.core.common.BaseActivity
import cn.anc.dashcam.core.logging.AppLog

class LanguageSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.i("language settings created", tag = "Settings")

        setContent {
            LanguageSettingsScreen(
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
