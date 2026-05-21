package cn.anc.dashcam.core.common // 归属于设置与多语言通用子架构项目包路径

/**
 * 主题颜色选项枚举类 (App Accent Theme Color Enum Class)
 * 
 * - BLUE: 经典科技蓝 (Classic Tech Blue / Sky Blue)
 * - GREEN: 新能源环保绿 (Fresh Green / ECO Green)
 * - ORANGE: 活力阳光橙 (Warm Orange / Active Orange)
 * - PURPLE: 优雅梦幻紫 (Dreamy Purple / Elegant Purple)
 */
enum class AppThemeColor( // AppThemeColor 主颜色配方枚举类开始
    val tag: String, // 该主色对应的唯一的、可做底层轻量 MMKV 序列化的标记 tag 字符串
    val lightHex: Long, // 浅色明亮白昼模式环境下，需要应用的主打高亮主色调 16 进制 Long 精度色码
    val darkHex: Long,  // 在深黑色护眼或黑夜环境下，需要应用的高比重对比主高亮 16 进制 Long 精度色码
) { // 枚举项
    BLUE("blue", 0xFF26495C, 0xFF8DC6E8), // 科技蓝色 BLUE 及其亮色调和暗色调设置
    GREEN("green", 0xFF2E7D32, 0xFF81C784), // 环保新能源绿色 GREEN 及其色度设置
    ORANGE("orange", 0xFFD84315, 0xFFFF8A65), // 朝阳活力暖系橙色 ORANGE 及其属性
    PURPLE("purple", 0xFF6A1B9A, 0xFFBA68C8); // 皇家科幻紫系 PURPLE 配色

    companion object { // 静态函数伴生区
        /**
         * 根据标记字符串获取主题颜色 (Get theme accent color by tag)
         * 默认返回 BLUE (Classic Blue is default)
         */
        fun fromTag(tag: String?): AppThemeColor { // 根据存入的高斯 tag 精确转换为对应的颜色枚举
            return entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) } ?: BLUE // 轮询遍历所有 entries 排查名称等价项，如果未查询到或者传入值为 null 则重定向保底采用经典的 BLUE 科技蓝
        } // 转换方法退口
    } // companion static 块结束
} // 颜色枚举类定义结束
