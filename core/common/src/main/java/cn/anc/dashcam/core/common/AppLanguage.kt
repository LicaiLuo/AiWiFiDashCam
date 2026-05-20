package cn.anc.dashcam.core.common

import java.util.Locale

enum class AppLanguage(
    val tag: String,
) {
    ZH_CN(
        tag = "zh-CN",
    ),
    EN(
        tag = "en",
    );

    val locale: Locale
        get() = Locale.forLanguageTag(tag)

    companion object {
        fun fromTag(tag: String?): AppLanguage? {
            return entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) }
        }

        fun fromLocale(locale: Locale): AppLanguage {
            return if (locale.language.equals("zh", ignoreCase = true)) {
                ZH_CN
            } else {
                EN
            }
        }
    }
}
