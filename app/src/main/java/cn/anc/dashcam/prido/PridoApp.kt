package cn.anc.dashcam.prido // 声明所属包路径为 prido

import cn.anc.dashcam.core.common.AppConfig // 导入全局应用配置对象
import cn.anc.dashcam.core.common.BrandType // 导入品牌类别枚举
import cn.anc.dashcam.core.common.DashcamApplication // 导入公共 Application 基类
import cn.anc.dashcam.core.logging.LogLevel // 导入日志级别枚举

class PridoApp : DashcamApplication() { // PridoApp 实例化类

    override val fileMinLevel: LogLevel = LogLevel.VERBOSE // 覆写最低日志等级为 VERBOSE

    override val startupLogMessage: String = "prido app started" // 覆写冷启动通知日志内容

    override fun onCreate() { // 覆写 onCreate 重中之重基础入口
        val selectedBrand = BrandType.UNIDEN // 选择当前打包/运行的品牌（此处可灵活配制为 UNIDEN 或 COOAU 等）
        AppConfig.brandType = selectedBrand
        super.onCreate() // 调用基类执行 AppLocale 以及 AppLog 系统加载
        
        // 初始化 core 数据模块对应的默认设置
        cn.anc.dashcam.core.data.BrandConfigManager.initWithBrand(selectedBrand)
        
        // 马甲包自己备有一份配置用来强行覆盖
        val pridoCustomColors = cn.anc.dashcam.core.data.BrandColorConfig(
            primaryColorLight = 0xFF26495C, // Prido 经典蓝
            primaryColorDark = 0xFF8DC6E8,  // Prido 璀璨天蓝
            accentColorLight = 0xFF2E7D32,
            accentColorDark = 0xFF81C784,
            pageBgLight = 0xFFF4F4F4,
            pageBgDark = 0xFF101418,
            cardBgLight = 0xFFFFFFFF,
            cardBgDark = 0xFF1C2228
        )
        val pridoCustomOsd = cn.anc.dashcam.core.data.BrandOsdConfig(
            showBrandWatermark = true,
            watermarkText = "PRIDO PREMIUM GPS 4K",
            showSpeed = true,
            showGps = false,
            showTimestamp = true,
            logoAssetPath = "logos/prido_custom.png"
        )
        
        // 进行数据层的覆盖接管
        cn.anc.dashcam.core.data.BrandConfigManager.overrideBrandConfig(pridoCustomColors, pridoCustomOsd)
    } // 结束 onCreate
} // PridoApp 结束

