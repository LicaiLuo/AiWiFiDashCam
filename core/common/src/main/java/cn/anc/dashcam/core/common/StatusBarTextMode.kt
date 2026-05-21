package cn.anc.dashcam.core.common // 归属于设置和状态栏全局功能的包层目录路径

/**
 * 状态栏字体颜色模式 (Status Bar Text & Icon Style Mode Enum)
 * 
 * - AUTO: 智能模式。根据夜间模式/显示模式自动调整；亮色背景下显示深色字体，暗色背景下显示浅色字体 (Auto style: light text on dark, dark text on light)
 * - DARK: 强制深色/黑色字体和图标 (Force dark status bar icons/text)
 * - LIGHT: 强制明亮/白色字体和图标 (Force light status bar icons/text)
 */
enum class StatusBarTextMode( // StatusBarTextMode 状态栏字体颜色覆盖模式枚举类
    val tag: String, // 状态栏前景模式枚举对应的轻量化字符 tag 标，例如 "auto", "dark", "light"
) { // 枚举值列表
    AUTO("auto"), // 选项一：AUTO, 智能跟随系统亮暗决定状态栏是黑字还是白字配合
    DARK("dark"), // 选项二：DARK, 无论背景一律强制墨黑色图标和文字，便于查看
    LIGHT("light"); // 选项三：LIGHT, 无论背景一律强制素白色状态栏，精巧高雅

    companion object { // 静态函数伴生区
        /**
         * 根据标记从存储中获取对应的状态栏模式 (Parse text mode from tag)
         * 默认为 AUTO 智能跟随背景 (Defaults to AUTO)
         */
        fun fromTag(tag: String?): StatusBarTextMode { // 由持有的字符串 tag 反编译还原为状态栏枚举
            return entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) } ?: AUTO // 遍历 entries 排查，若一致则返回目标项；如果是不明符号或 null，保底重定为智能 AUTO 模式返还
        } // fromTag 方法全满完结
    } // companion static 块结束
} // 状态栏排版枚举类定义结束
