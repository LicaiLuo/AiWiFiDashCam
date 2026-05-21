package cn.anc.dashcam.cooau // 声明所在的马甲包专属包路径

import cn.anc.dashcam.core.common.AppConfig // 导入全局应用配置对象
import cn.anc.dashcam.core.common.BrandType // 导入品牌类别枚举
import cn.anc.dashcam.core.common.DashcamApplication // 导入公共 Application 基类
import cn.anc.dashcam.core.data.BrandConfigManager // 导入品牌配置管理器
import cn.anc.dashcam.core.data.BrandColorConfig // 导入品牌色彩属性配置
import cn.anc.dashcam.core.data.BrandOsdConfig // 导入品牌 OSD 属性配置

/**
 * COOAU 自研马甲客户端主 Entry 启动类 (Cooau App main entry)
 * 启动时，由壳包自主注入专属的 BrandColor 与 BrandOsd 进行物理覆盖。
 */
class CooauApp : DashcamApplication() { // CooauApp 类定义

    override val logDirectoryName: String = "cooau_logs" // 覆写日志落盘沙箱路径名称为 cooau_logs

    override val startupLogMessage: String = "cooau app started" // 部署冷启动成功宣言词

    override fun onCreate() { // 重写系统 onCreate 活动入口
        AppConfig.brandType = BrandType.COOAU // 劫持注入 COOAU 品牌，适配多态 UI 树
        super.onCreate() // 调用基类进行全局初始化
        
        // 首先初始化默认配置
        BrandConfigManager.initWithBrand(BrandType.COOAU)
        
        // 马甲包自己有一份专属配置去覆盖 (Define custom color and OSD variables at shell module level)
        val cooauCustomColors = BrandColorConfig(
            primaryColorLight = 0xFF20A4F3, // 皇家天空蓝
            primaryColorDark = 0xFF2EC4B6,  // 极客薄荷绿
            accentColorLight = 0xFF1475A7,  // 暗调点缀蓝
            accentColorDark = 0xFF4FD1C5,   // 亮调点缀绿
            pageBgLight = 0xFFECF3FA,       // 独特的亮冰蓝灰底色
            pageBgDark = 0xFF080D18,        // 深静夜空蓝底色
            cardBgLight = 0xFFFFFFFF,
            cardBgDark = 0xFF12192A
        )
        val cooauCustomOsd = BrandOsdConfig(
            showBrandWatermark = true,
            watermarkText = "COOAU EXTREME UHD 4K",
            showSpeed = true,
            showGps = true,
            showTimestamp = true,
            logoAssetPath = "logos/cooau_premium.png"
        )
        
        // 覆盖写入核心：颜色和 OSD 结构均在此层完美覆盖
        BrandConfigManager.overrideBrandConfig(cooauCustomColors, cooauCustomOsd)
    } // onCreate 结束
} // CooauApp 完结
