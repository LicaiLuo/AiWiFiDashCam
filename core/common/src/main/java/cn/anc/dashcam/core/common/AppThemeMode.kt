package cn.anc.dashcam.core.common // 归属于设置及核心 common 的模块报名

/**
 * 应用主题模式枚举类 (App Theme Mode Enum Class)
 * 
 * - LIGHT: 强制明亮模式 (Force Light Mode)
 * - DARK: 强制黑夜模式 (Force Dark Mode)
 * - SYSTEM: 跟随系统模式 (Follow System Mode - Automatically adapt based on Android system configuration)
 */
enum class AppThemeMode( // AppThemeMode 主题亮暗跟随系统模式枚举类开始
    val tag: String, // 每一个模式枚举携带的用于序列化存储的英文短标 tag, 如 "light", "dark"
) { // 项定义
    LIGHT( // 选项一：强制纯亮色日间色
        tag = "light", // tag 为 "light"
    ), // 结束 LIGHT
    DARK( // 选项二：强制护眼极客暗色模式
        tag = "dark", // tag 为 "dark"
    ), // 结束 DARK
    SYSTEM( // 选项三：智能自适应跟随系统模式
        tag = "system", // tag 为 "system"
    ); // 项结束

    companion object { // 静态函数区域
        /**
         * 根据标记字符串获取主题模式 (Get theme mode according to tag string)
         * 默认为 SYSTEM (跟随系统) (Defaults to SYSTEM)
         */
        fun fromTag(tag: String?): AppThemeMode { // 从 tag 词根进行解码
            return entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) } ?: SYSTEM // 遍历全部枚举 entries 判定，如果一致则返还；如果不匹配或为空，则保底选用 SYSTEM 智能跟随模式返还
        } // fromTag 方法完结
    } // companion static 块结束
} // 模式枚举主类定义结束
