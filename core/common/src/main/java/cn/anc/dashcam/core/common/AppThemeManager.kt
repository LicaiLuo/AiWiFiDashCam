package cn.anc.dashcam.core.common

import android.content.Context
import com.tencent.mmkv.MMKV

object AppThemeManager {

    fun currentThemeMode(context: Context): AppThemeMode {
        val tag = kv(context).decodeString(KEY_THEME_MODE_TAG)
        return AppThemeMode.fromTag(tag)
    }

    fun currentThemeModeTag(context: Context): String {
        return currentThemeMode(context).tag
    }

    fun saveThemeMode(context: Context, themeMode: AppThemeMode) {
        kv(context).encode(KEY_THEME_MODE_TAG, themeMode.tag)
    }

    private fun kv(context: Context): MMKV {
        AppLocaleManager.initialize(context)
        return requireNotNull(MMKV.mmkvWithID(MMKV_ID)) {
            "MMKV is not initialized. Call AppLocaleManager.initialize(context) in Application.onCreate()."
        }
    }

    private const val MMKV_ID = "dashcam_app_settings"
    private const val KEY_THEME_MODE_TAG = "theme_mode_tag"
}
