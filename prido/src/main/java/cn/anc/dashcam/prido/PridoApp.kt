package cn.anc.dashcam.prido

import cn.anc.dashcam.core.common.AppConfig
import cn.anc.dashcam.core.common.BrandType
import cn.anc.dashcam.core.common.DashcamApplication

class PridoApp : DashcamApplication() { // PridoApp 实例化类


    override val logDirectoryName: String = "prido_logs" // 覆写专属库文件夹为 prido_logs

    override val startupLogMessage: String = "prido app started" // 覆写启动宣告字样

    override fun onCreate() { // 覆写入口 onCreate 方法
        AppConfig.brandType = BrandType.PRIDO // 关键：启动最先注入品牌分支为 prido 模式
        super.onCreate() // 调用 base 初始化多语言

        // 初始化默认配置
        BrandConfigManager.initWithBrand(BrandType.PRIDO)

        // 覆盖为 prido 专属配色与 OSD (Define custom values for prido shell app)
        val pridoCustomColors = BrandColorConfig(
            primaryColorLight = 0xFF1B315A, // 高贵典雅深遂蓝
            primaryColorDark = 0xFF63B3ED,  // 清新浅海亮蓝
            accentColorLight = 0xFFD4601A,  // 日落暖橘色
            accentColorDark = 0xFFFCD080,   // 暗暮金色点缀
            pageBgLight = 0xFFF6F8FA,       // 冷灰色底色
            pageBgDark = 0xFF0B0E14,        // 暗墨黑色底色
            cardBgLight = 0xFFFFFFFF,
            cardBgDark = 0xFF141820
        )
        val pridoCustomOsd = BrandOsdConfig(
            showBrandWatermark = true,
            watermarkText = "prido SPECIAL MOTO 4K",
            showSpeed = true,
            showGps = true,
            showTimestamp = true,
            logoAssetPath = "logos/prido_custom.png"
        )

        // 传递覆盖写入 data 模块，完成马甲壳包对 core 层资源的接管与颠覆。
        BrandConfigManager.overrideBrandConfig(pridoCustomColors, pridoCustomOsd)
    } // 结束 onCreate
} // 类结束
