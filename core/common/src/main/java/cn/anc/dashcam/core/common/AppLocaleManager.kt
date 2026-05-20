package cn.anc.dashcam.core.common

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import com.tencent.mmkv.MMKV

object AppLocaleManager {

    @Volatile
    private var initialized = false

    fun initialize(context: Context) {
        if (!initialized) {
            synchronized(this) {
                if (!initialized) {
                    MMKV.initialize(context.applicationContext)
                    initialized = true
                }
            }
        }
    }

    fun wrapContext(context: Context): Context {
        val language = currentLanguage(context)
        val configuration = Configuration(context.resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocales(LocaleList(language.locale))
        } else {
            @Suppress("DEPRECATION")
            configuration.setLocale(language.locale)
        }

        return context.createConfigurationContext(configuration)
    }

    fun currentLanguage(context: Context): AppLanguage {
        return selectedLanguage(context) ?: systemLanguage(context)
    }

    fun currentLanguageTag(context: Context): String {
        return currentLanguage(context).tag
    }

    fun saveLanguage(context: Context, language: AppLanguage) {
        kv(context).encode(KEY_LANGUAGE_TAG, language.tag)
    }

    private fun selectedLanguage(context: Context): AppLanguage? {
        val tag = kv(context).decodeString(KEY_LANGUAGE_TAG)
        return AppLanguage.fromTag(tag)
    }

    private fun systemLanguage(context: Context): AppLanguage {
        val configuration = context.resources.configuration
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            configuration.locale
        }
        return AppLanguage.fromLocale(locale)
    }

    private fun kv(context: Context): MMKV {
        initialize(context)
        return requireNotNull(MMKV.mmkvWithID(MMKV_ID)) {
            "MMKV is not initialized. Call AppLocaleManager.initialize(context) in Application.onCreate()."
        }
    }

    private const val MMKV_ID = "dashcam_app_settings"
    private const val KEY_LANGUAGE_TAG = "language_tag"
}
