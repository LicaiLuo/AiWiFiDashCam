package cn.anc.dashcam.core.common

enum class BarColorOption(
    val tag: String,
    val lightHex: Long,
    val darkHex: Long,
) {
    DEFAULT("default", 0x00000000, 0x00000000), // Follow default theme/page
    THEME_ACCENT("theme_accent", 0x01111111, 0x01111111), // Follow active AppThemeColor!
    CHARCOAL("charcoal", 0xFF1F2937, 0xFF121212),
    WHITE("white", 0xFFFFFFFF, 0xFF2C2C2C),
    BLUE("blue", 0xFF0D47A1, 0xFF1A237E),
    GREEN("green", 0xFF1B5E20, 0xFF003300),
    ORANGE("orange", 0xFFE65100, 0xFF4E342E),
    PURPLE("purple", 0xFF4A148C, 0xFF311B92);

    companion object {
        fun fromTag(tag: String?): BarColorOption {
            return entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) } ?: DEFAULT
        }
    }
}
