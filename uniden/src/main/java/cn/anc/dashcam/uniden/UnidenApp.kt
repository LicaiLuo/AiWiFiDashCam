package cn.anc.dashcam.uniden // 声明包路径为 uniden

import cn.anc.dashcam.core.common.AppConfig // 导入全局配置类
import cn.anc.dashcam.core.common.BrandType // 导入品牌分类枚举
import cn.anc.dashcam.core.common.DashcamApplication // 导入公共 Application 基类
import cn.anc.dashcam.core.data.BrandConfigManager // 导入品牌配置管家
import cn.anc.dashcam.core.data.BrandColorConfig // 导入品牌色谱配方
import cn.anc.dashcam.core.data.BrandOsdConfig // 导入品牌 OSD 配方

class UnidenApp : DashcamApplication() { // UnidenApp 类体开始

    override val logDirectoryName: String = "uniden_logs" // 覆写专属库文件夹为 uniden_logs

    override val startupLogMessage: String = "uniden app started" // 覆写启动宣告字样

    override fun onCreate() { // 覆写入口 onCreate 方法
        AppConfig.brandType = BrandType.UNIDEN // 关键：启动最先注入品牌分支为 UNIDEN 模式
        super.onCreate() // 调用 base 初始化多语言
        
        // 初始化默认配置
        BrandConfigManager.initWithBrand(BrandType.UNIDEN)
        
        // 覆盖为 Uniden 专属配色与 OSD (Define custom values for Uniden shell app)
        val unidenCustomColors = BrandColorConfig(
            primaryColorLight = 0xFF1B315A, // 高贵典雅深遂蓝
            primaryColorDark = 0xFF63B3ED,  // 清新浅海亮蓝
            accentColorLight = 0xFFD4601A,  // 日落暖橘色
            accentColorDark = 0xFFFCD080,   // 暗暮金色点缀
            pageBgLight = 0xFFF6F8FA,       // 冷灰色底色
            pageBgDark = 0xFF0B0E14,        // 暗墨黑色底色
            cardBgLight = 0xFFFFFFFF,
            cardBgDark = 0xFF141820
        )
        val unidenCustomOsd = BrandOsdConfig(
            showBrandWatermark = true,
            watermarkText = "UNIDEN SPECIAL MOTO 4K",
            showSpeed = true,
            showGps = true,
            showTimestamp = true,
            logoAssetPath = "logos/uniden_custom.png"
        )
        
        // 传递覆盖写入 data 模块，完成马甲壳包对 core 层资源的接管与颠覆。
        BrandConfigManager.overrideBrandConfig(unidenCustomColors, unidenCustomOsd)
    } // 结束 onCreate
} // 类结束

