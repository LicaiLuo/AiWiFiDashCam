package cn.anc.dashcam.core.data

/**
 * 品牌专属主色彩配方配置类 (Brand Color Schema Recipe)
 * 允许各品牌马甲包在 Application 启动时进行差异化和重写，达到完美的覆盖效果。
 */
data class BrandColorConfig(
    val primaryColorLight: Long = 0xFF26495C,   // 亮色主打科技色
    val primaryColorDark: Long = 0xFF8DC6E8,    // 暗色首选高亮色
    val accentColorLight: Long = 0xFF2E7D32,    // 点缀级微光亮色
    val accentColorDark: Long = 0xFF81C784,     // 点缀级微光暗色
    val pageBgLight: Long = 0xFFF4F4F4,         // 珍珠亮浅灰背景底座
    val pageBgDark: Long = 0xFF101418,          // 深色钛夜天空背景底座
    val cardBgLight: Long = 0xFFFFFFFF,        // 日间物理极亮白卡片
    val cardBgDark: Long = 0xFF1C2228,         // 晚夜优雅磨砂灰卡片
)
