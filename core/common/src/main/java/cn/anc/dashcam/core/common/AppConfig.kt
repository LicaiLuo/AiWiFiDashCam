package cn.anc.dashcam.core.common // 归属于 core:common 模块的通用包路径

/**
 * 品牌分类枚举定义 (Brand Type Enumeration)
 * - PRIDO: 4 个底部 Tab 按钮风格
 * - UNIDEN: 根据截图还原的 3 底部 Tab，且右上角有设置和记录图标，中间有拟物主页和连接大圆按钮，底部中间是圆形大 + 分享
 * - COOAU: 前端左上角抽屉按钮风格，点击拉出侧滑菜单，支持设置与特色功能
 */
enum class BrandType {
    PRIDO,   // 经典 4-Tab 品牌分支
    UNIDEN,  // 极简 3-Tab (设备、大+号分享、相册) 品牌分支
    COOAU,   // 左上角设置侧滑抽屉品牌分支
    ROADDRIVE // 极地顶级 4-Tab 旗舰品牌分支 (RoadDrive 4 navigation keys)
}

/**
 * 全局应用配置管家 (Global App Configuration Manager)
 * 提供简单的静态变量供给其他模块实时调取，控制各界面外观
 */
object AppConfig {
    // 全局当前在 Application.onCreate() 中注入的当前品牌分支，默认采用 PRIDO 风格
    var brandType: BrandType = BrandType.PRIDO
}
