package cn.anc.dashcam.mine

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.AppThemeMode
import cn.anc.dashcam.core.data.BrandConfigManager
import cn.anc.dashcam.core.data.getLightHex
import cn.anc.dashcam.core.data.getDarkHex
import cn.anc.dashcam.core.logging.AppLog
import cn.anc.dashcam.core.navigation.ActivityFeatureNavigator
import cn.anc.dashcam.core.navigation.NavigationResult

/**
 * 个人中心页面 (Mine Feature Tab Page)
 * 
 * 作用: 提供用户注册登录以及跳转至 APP 核心设置的视觉导入口。 (Home profile visual page)
 * 设计: (优化需求2) 采用高品质圆角内嵌卡片设计、支持跟随系统变色、支持主题强调色高亮以及动态渐变。
 */
@Composable
fun MinePage(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val colors = MineColors.current()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.pageBackground)
            // 自动预留顶部状态栏高度，防止界面被镜头挖孔遮挡 
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        // 顶部预留留白 (Spacious spacing consistent with Material 3 guidelines)
        Spacer(modifier = Modifier.height(40.dp))
        
        // 1. (深度优化) 会员/登录卡片，渲染出高逼格圆形渐变头像及多行详细信息引导
        LoginRow()
        
        Spacer(modifier = Modifier.height(36.dp))
        
        // 2. 消息通知卡片
        MineActionRow(
            iconRes = R.drawable.ic_account_message,
            title = stringResource(R.string.mine_messages),
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        // 3. APP 功能设置卡片 (点击跳转二级设置)
        MineActionRow(
            iconRes = R.drawable.ic_account_setup,
            title = stringResource(R.string.mine_app_settings),
            onClick = {
                AppLog.i("open settings from mine", tag = "Mine")
                // 借助内置的动态 Activity 导航框架寻找 route "settings" 宿主
                val result = ActivityFeatureNavigator(context).navigate("settings")
                when (result) {
                    NavigationResult.Success -> AppLog.i("settings navigation success", tag = "Mine")
                    is NavigationResult.RouteNotFound -> {
                        AppLog.w("settings route not found route=${result.routeId}", tag = "Mine")
                    }
                    is NavigationResult.NavigationFailed -> {
                        AppLog.e(
                            message = "settings navigation failed route=${result.routeId}",
                            throwable = result.reason,
                            tag = "Mine",
                        )
                    }
                }
            },
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        // 4. 常见问题与帮助卡片
        MineActionRow(
            iconRes = R.drawable.ic_account_help,
            title = stringResource(R.string.mine_help),
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        // 5. 更多信息与意见反馈
        MineActionRow(
            iconRes = R.drawable.ic_account_more,
            title = stringResource(R.string.mine_more),
            onClick = {
                AppLog.i("open feedback from mine", tag = "Mine")
                // 跳转动态 route "feedback" 对应 Activity
                val result = ActivityFeatureNavigator(context).navigate("feedback")
                when (result) {
                    NavigationResult.Success -> AppLog.i("feedback navigation success", tag = "Mine")
                    is NavigationResult.RouteNotFound -> {
                        AppLog.w("feedback route not found route=${result.routeId}", tag = "Mine")
                    }
                    is NavigationResult.NavigationFailed -> {
                        AppLog.e(
                            message = "feedback navigation failed route=${result.routeId}",
                            throwable = result.reason,
                            tag = "Mine",
                        )
                    }
                }
            },
        )
    }
}

/**
 * 优化后的登录提示卡片组件 (Premium Profile visual Row)
 * 
 * 放弃了单行字符加大于号的平铺做法，升级为高档圆角微投影背景 + 复合文本引导。
 */
@Composable
private fun LoginRow() {
    val colors = MineColors.current()
    val context = LocalContext.current
    
    // 获取当前用户设置的主题强调色 (Accent color)
    val accent = AppThemeManager.currentThemeColor(context)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colors.cardBackground)
            .clickable { /* 触发登录事件 */ }
            .padding(vertical = 24.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 圆形精美渐变头像占位 (Circular high-end gradient avatar)
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    // 动态自适应左右渐变色组，科技感十足
                    Brush.linearGradient(
                        colors = listOf(
                            Color(accent.getLightHex()),
                            Color(accent.getDarkHex())
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // 头像中心的默认符号文字，高亮白度
            Text(
                text = "Me",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(18.dp))
        
        // 文字说明详情区域
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.mine_register_login),
                color = colors.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Unlock smart car driving logs",
                color = colors.textSecondary,
                fontSize = 14.sp
            )
        }
        
        // 终点标识指示箭头符
        Text(
            text = "＞",
            color = colors.textSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 4.dp)
        )
    }
}

/**
 * 单行设置项功能卡片 (Optimized Action Menu card row component)
 */
@Composable
private fun MineActionRow(
    iconRes: Int,
    title: String,
    onClick: (() -> Unit)? = null,
) {
    val colors = MineColors.current()
    val context = LocalContext.current
    
    // 渐变激活强调色 (Fetch theme color accent for active icon tints)
    val accent = AppThemeManager.currentThemeColor(context)
    val isDark = AppThemeManager.isDarkTheme(context)
    val tintHex = if (isDark) accent.getDarkHex() else accent.getLightHex()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            // 如果事件不为空则绑定点击监听
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 图标：应用选定的动态颜色来做着色过滤器，一呼百应 
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            colorFilter = ColorFilter.tint(Color(tintHex))
        )
        
        // 核心菜单标题文字
        Text(
            text = title,
            color = colors.textPrimary,
            fontSize = 18.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
        )
        
        // 终点指向性箭头
        Text(
            text = "＞",
            color = colors.textSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

/**
 * 仅用于本包内的配色描述数据结构 (Thematic Colors wrapper)
 */
private data class MineColorScheme(
    val pageBackground: Color,
    val cardBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
)

/**
 * 个人重心配色构建宿主，支持即时探测主题。
 */
private object MineColors {

    @Composable
    fun current(): MineColorScheme {
        val context = LocalContext.current
        val brandColor = BrandConfigManager.colorConfig
        // 支持智能判断当前显示状态是不是夜暮黑夜模式 (Support dynamic Light/Dark/System theme)
        return if (AppThemeManager.isDarkTheme(context)) {
            MineColorScheme(
                pageBackground = Color(brandColor.pageBgDark),
                cardBackground = Color(brandColor.cardBgDark),
                textPrimary = Color(0xFFE8EDF2),
                textSecondary = Color(0xFF8B96A1),
            )
        } else {
            MineColorScheme(
                pageBackground = Color(brandColor.pageBgLight),
                cardBackground = Color(brandColor.cardBgLight),
                textPrimary = Color(0xFF292929),
                textSecondary = Color(0xFF888888),
            )
        }
    }
}
