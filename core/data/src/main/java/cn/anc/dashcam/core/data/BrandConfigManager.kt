package cn.anc.dashcam.core.data

import cn.anc.dashcam.core.common.BrandType

/**
 * 全局配置化品牌数据与视觉属性总管家 (Brand Visual & OSD Configuration Manager)
 * 作用：统一持有和向下发散当前打包的马甲客户端专属的主题配色与 OSD 物理配置。
 * 允许并在启动最先期利用马甲包 Application 的 override 功能完成独立特异化的覆写。
 */
object BrandConfigManager {

    // 1. 默认内置的基础 PRIDO 风格默认值
    private val DEFAULT_PRIDO_COLORS = BrandColorConfig(
        primaryColorLight = 0xFF26495C,
        primaryColorDark = 0xFF8DC6E8,
        accentColorLight = 0xFF2E7D32,
        accentColorDark = 0xFF81C784,
        pageBgLight = 0xFFF4F4F4,
        pageBgDark = 0xFF101418,
        cardBgLight = 0xFFFFFFFF,
        cardBgDark = 0xFF1C2228
    )
    private val DEFAULT_PRIDO_OSD = BrandOsdConfig(
        showBrandWatermark = true,
        watermarkText = "PRIDO SMART WIFI",
        showSpeed = true,
        showGps = false,
        showTimestamp = true,
        logoAssetPath = "logos/prido.png"
    )

    // 2. 默认内置的基础 UNIDEN 风格默认值
    private val DEFAULT_UNIDEN_COLORS = BrandColorConfig(
        primaryColorLight = 0xFF1A365D, // 极简典雅深邃暗海蓝
        primaryColorDark = 0xFF63B3ED,  // 朝阳璀璨天空亮蓝
        accentColorLight = 0xFFDD6B20,  // 日暮温和暖橘色
        accentColorDark = 0xFFFBD38D,   // 暗暮星光浅金
        pageBgLight = 0xFFF7FAFC,
        pageBgDark = 0xFF0D1117,
        cardBgLight = 0xFFFFFFFF,
        cardBgDark = 0xFF161B22
    )
    private val DEFAULT_UNIDEN_OSD = BrandOsdConfig(
        showBrandWatermark = true,
        watermarkText = "UNIDEN MOTO PRO",
        showSpeed = true,
        showGps = true,
        showTimestamp = true,
        logoAssetPath = "logos/uniden.png"
    )

    // 3. 默认内置的基础 COOAU 风格默认值
    private val DEFAULT_COOAU_COLORS = BrandColorConfig(
        primaryColorLight = 0xFF20A4F3, // 皇家天空蓝
        primaryColorDark = 0xFF2EC4B6,  // 极客薄荷绿/青绿色
        accentColorLight = 0xFF2A9D8F,  // 深翠绿配色
        accentColorDark = 0xFFE9C46A,   // 高对比流沙金
        pageBgLight = 0xFFF0F4F8,
        pageBgDark = 0xFF0B0F19,
        cardBgLight = 0xFFFFFFFF,
        cardBgDark = 0xFF151926
    )
    private val DEFAULT_COOAU_OSD = BrandOsdConfig(
        showBrandWatermark = true,
        watermarkText = "COOAU ULTRA UHD 4K",
        showSpeed = false,
        showGps = true,
        showTimestamp = true,
        logoAssetPath = "logos/cooau.png"
    )

    // 4. 默认内置的 RoadDrive 寰宇旗舰高亮配色与特色 OSD 设置 (User specifications)
    private val DEFAULT_ROADDRIVE_COLORS = BrandColorConfig(
        primaryColorLight = 0xFF8800FF, // 尊贵幻耀紫色：#8800FF
        primaryColorDark = 0xFFAA44FF,  // 极客绚丽浅紫：#AA44FF
        accentColorLight = 0xFFD4601A,
        accentColorDark = 0xFFFCD080,
        pageBgLight = 0xFFF6F8FA,
        pageBgDark = 0xFF0B0E14,
        cardBgLight = 0xFFFFFFFF,
        cardBgDark = 0xFF141820
    )
    private val DEFAULT_ROADDRIVE_OSD = BrandOsdConfig(
        showBrandWatermark = true,
        watermarkText = "ROAD_DRIVE UHD DUAL SENSOR",
        showSpeed = true,
        showGps = true,
        showTimestamp = true,
        logoAssetPath = "logos/roaddrive.png"
    )

    // 当前策略性持有的高亮配置（主线程高可见、即时并发防抖安全）
    @Volatile
    var colorConfig: BrandColorConfig = DEFAULT_PRIDO_COLORS
        private set

    @Volatile
    var osdConfig: BrandOsdConfig = DEFAULT_PRIDO_OSD
        private set

    /**
     * 策略自适应：在冷启动最先一刻根据传入的 BrandType 加载对应的底层默认配置。
     */
    fun initWithBrand(brandType: BrandType) {
        when (brandType) {
            BrandType.PRIDO -> {
                colorConfig = DEFAULT_PRIDO_COLORS
                osdConfig = DEFAULT_PRIDO_OSD
            }
            BrandType.UNIDEN -> {
                colorConfig = DEFAULT_UNIDEN_COLORS
                osdConfig = DEFAULT_UNIDEN_OSD
            }
            BrandType.COOAU -> {
                colorConfig = DEFAULT_COOAU_COLORS
                osdConfig = DEFAULT_COOAU_OSD
            }
            BrandType.ROADDRIVE -> {
                colorConfig = DEFAULT_ROADDRIVE_COLORS
                osdConfig = DEFAULT_ROADDRIVE_OSD
            }
        }
    }

    /**
     * 覆盖模式：马甲壳 Application 彻底自定义，不修改 core 模块代码，即能全权覆盖和彻底改写主色调与 OSD！
     */
    fun overrideBrandConfig(color: BrandColorConfig, osd: BrandOsdConfig) {
        colorConfig = color
        osdConfig = osd
    }
}
