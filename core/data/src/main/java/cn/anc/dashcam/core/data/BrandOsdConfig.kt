package cn.anc.dashcam.core.data

/**
 * 品牌专属 OSD (On-Screen Display) 水印显示属性配置
 * 控制行车记录仪画面/APP端视频播放水印中的个性品牌标识。
 */
data class BrandOsdConfig(
    val showBrandWatermark: Boolean = true,     // 在实时视频/下载图片中是否常驻该品牌的特色水印标
    val watermarkText: String = "PRIDO SMART WIFI", // 水印显示的个性化自定义前缀文本字样
    val showSpeed: Boolean = true,              // 视频 / 播放 OSD 中是否显示实时 KM/H 行车速度
    val showGps: Boolean = true,                // 视频 / 播放 OSD 中是否叠加渲染 GPS 定位精度
    val showTimestamp: Boolean = true,          // 是否显示常备运行时间轴
    val logoAssetPath: String = "logos/prido.png" // 水印 logo 资产文件的资源路径
)
