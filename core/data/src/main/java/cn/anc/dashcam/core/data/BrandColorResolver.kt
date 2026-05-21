package cn.anc.dashcam.core.data

import cn.anc.dashcam.core.common.AppThemeColor

/**
 * 品牌颜色动态分解适配器 (Adaptive Brand Color Resolver)
 * 使得设置中选定的高亮配比能够完美和马甲包注入的主配色相辅相成。
 * 若用户维持默认的 BLUE/蓝，本模块将直接回传马甲壳包在 Application 中覆写的最顶级特异主色！
 */

/**
 * 获取明亮模式对应的物理色 Long 数值
 */
fun AppThemeColor.getLightHex(): Long {
    return when (this) {
        AppThemeColor.BLUE -> BrandConfigManager.colorConfig.primaryColorLight // 完美覆盖：使用注册包的自定义主色
        AppThemeColor.GREEN -> BrandConfigManager.colorConfig.accentColorLight // 新能源绿选用辅佐色
        AppThemeColor.ORANGE -> 0xFFD84315 // 活力橘保留经典数值
        AppThemeColor.PURPLE -> 0xFF6A1B9A // 高科紫保留经典数值
    }
}

/**
 * 获取暗黑保真模式对应的物理色 Long 数值
 */
fun AppThemeColor.getDarkHex(): Long {
    return when (this) {
        AppThemeColor.BLUE -> BrandConfigManager.colorConfig.primaryColorDark  // 完美覆盖：使用注册包的自定义高亮主色
        AppThemeColor.GREEN -> BrandConfigManager.colorConfig.accentColorDark  // 暗月亮调绿
        AppThemeColor.ORANGE -> 0xFFFF8A65
        AppThemeColor.PURPLE -> 0xFFBA68C8
    }
}
