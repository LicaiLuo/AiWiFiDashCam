package cn.anc.dashcam.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.data.BrandConfigManager
import cn.anc.dashcam.core.data.getDarkHex
import cn.anc.dashcam.core.data.getLightHex

/**
 * 主页底栏可选页签参数声明
 */
internal enum class MainTab(
    val labelRes: Int,
    val selectedIconRes: Int,
    val normalIconRes: Int,
) {
    DEVICE(
        labelRes = R.string.home_tab_device,
        selectedIconRes = R.drawable.ic_main_nav_device_s,
        normalIconRes = R.drawable.ic_main_nav_device_n,
    ),
    LOCAL_ALBUM(
        labelRes = R.string.home_tab_album,
        selectedIconRes = R.drawable.ic_main_nav_album_s,
        normalIconRes = R.drawable.ic_main_nav_album_n,
    ),
    MINE(
        labelRes = R.string.home_tab_mine,
        selectedIconRes = R.drawable.ic_main_nav_mine_s,
        normalIconRes = R.drawable.ic_main_nav_mine_n,
    ),
    FEEDBACK( // PRIDO 专属的一等反馈 Tab 参数挂载点
        labelRes = R.string.home_tab_feedback, // 问题反馈描述字
        selectedIconRes = R.drawable.ic_main_nav_mine_s, // 兜底
        normalIconRes = R.drawable.ic_main_nav_mine_n, // 兜底
    ),
}

/**
 * 配色调色盘结构体 (Home Color Scheme configuration)
 */
internal data class HomeColorScheme(
    val pageBackground: Color,
    val bottomBarBackground: Color,
    val selectedText: Color,
    val unselectedText: Color,
)

/**
 * 主界面的全局色彩控制器
 */
internal object HomeColors {

    @Composable
    fun current(): HomeColorScheme {
        if (LocalInspectionMode.current) {
            return HomeColorScheme(
                pageBackground = Color(0xFFF6F8FA),
                bottomBarBackground = Color.White,
                selectedText = Color(0xFF1B315A),
                unselectedText = Color(0xFF8B96A1),
            )
        }

        val context = LocalContext.current
        
        // 1. 获取主题颜色主色调配置 (Fetch saved user accent theme configuration)
        val accent = AppThemeManager.currentThemeColor(context)
        
        // 2. 探知当前是否处于黑夜配色
        val isDark = AppThemeManager.isDarkTheme(context)
        
        // 3. 读取其马甲客户端专有的品牌调色配置
        val brandColor = BrandConfigManager.colorConfig
        
        return if (isDark) {
            HomeColorScheme(
                pageBackground = Color(brandColor.pageBgDark),
                bottomBarBackground = Color(brandColor.cardBgDark),
                selectedText = Color(accent.getDarkHex()), 
                unselectedText = Color(0xFF8B96A1),
            )
        } else {
            HomeColorScheme(
                pageBackground = Color(brandColor.pageBgLight),
                bottomBarBackground = Color(brandColor.cardBgLight),
                selectedText = Color(accent.getLightHex()), 
                unselectedText = Color(0xFF999999),
            )
        }
    }
}
