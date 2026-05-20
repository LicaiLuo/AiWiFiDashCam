package cn.anc.dashcam.core.common

enum class AppThemeMode(
    val tag: String,
) {
    LIGHT(
        tag = "light",
    ),
    DARK(
        tag = "dark",
    );

    companion object {
        fun fromTag(tag: String?): AppThemeMode {
            return entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) } ?: LIGHT
        }
    }
}
