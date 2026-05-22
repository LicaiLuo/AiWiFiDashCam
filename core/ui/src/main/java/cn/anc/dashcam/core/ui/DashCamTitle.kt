package cn.anc.dashcam.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.BarColorOption

/**
 * 历史遗留兼容占位
 */
@Composable
fun DashCamTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
    )
}

private fun isColorDark(color: Color): Boolean {
    val luminance = 0.299f * color.red + 0.587f * color.green + 0.114f * color.blue
    return luminance < 0.5f
}

@Composable
private fun resolveBarColor(option: BarColorOption, fallback: Color, isDark: Boolean, themeColor: cn.anc.dashcam.core.common.AppThemeColor): Color {
    if (isDark) {
        return Color(0xFF121212) // 设置黑色模式变成黑色
    }
    return when (option) {
        BarColorOption.DEFAULT -> Color(themeColor.lightHex) // 白天模式（恢复当前设置的主题色）
        BarColorOption.THEME_ACCENT -> Color(themeColor.lightHex)
        else -> Color(option.lightHex)
    }
}

/**
 * 对应 XML 版标题栏封装的最佳 Compose 实践
 * 完美的布局对应：
 * 1. 沉浸式状态栏 (StatusBarView -> WindowInsets.statusBars)
 * 2. 高度 actionBarSize (56.dp) 与自定义颜色/背景
 * 3. 左侧返回键 & 返回文字
 * 4. 左右两侧的多图标及多文本扩展区域
 * 5. 绝对居中的主标题 (18sp, Bold, SingleLine)
 */
@Composable
fun DashCamTitleBar(
    title: String,
    modifier: Modifier = Modifier,
    // 基础配置
    onBackClick: (() -> Unit)? = null,
    backText: String? = null,
    backIcon: ImageVector = Icons.Default.ArrowBack,
    
    // 背景与文字色自定义
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    
    // 沉浸式状态栏配置
    enableStatusBarPadding: Boolean = true,
    
    // 进阶左侧自定义区域 (对应 XML 里的 iv_title_left_image, tv_title_txt_left)
    leftExtraContent: (@Composable RowScope.() -> Unit)? = null,
    
    // 进阶右侧自定义区域 (对应 XML 的 tv_title_txt_right, iv_title_right_image)
    rightContent: (@Composable RowScope.() -> Unit)? = null
) {
    val context = LocalContext.current
    val isDark = AppThemeManager.isDarkTheme(context)
    val themeColor = AppThemeManager.currentThemeColor(context)

    val statusBarOption = AppThemeManager.currentStatusBarBgColorOption(context)
    val titleBarOption = AppThemeManager.currentTitleBarBgColorOption(context)

    val resolvedTitleBarColor = resolveBarColor(titleBarOption, containerColor, isDark, themeColor)
    val resolvedStatusBarColor = if (statusBarOption == BarColorOption.DEFAULT) {
        resolvedTitleBarColor
    } else {
        resolveBarColor(statusBarOption, resolvedTitleBarColor, isDark, themeColor)
    }

    val isTitleBarDark = isColorDark(resolvedTitleBarColor)
    val finalContentColor = if (isTitleBarDark) {
        Color.White
    } else {
        if (!isColorDark(contentColor)) {
            // contentColor is light but background is light, force highly-visible dark text!
            Color(0xFF1F2937)
        } else {
            contentColor
        }
    }

    val statusBarPadding = if (enableStatusBarPadding) {
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    } else {
        0.dp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(resolvedTitleBarColor)
    ) {
        if (enableStatusBarPadding) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(statusBarPadding)
                    .background(resolvedStatusBarColor)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // 精确对应 actionbar 的标准高度 (等同于 ?attr/actionBarSize)
                .background(resolvedTitleBarColor)
                .padding(horizontal = 4.dp)
        ) {
            
            // 1. 左侧区域 (返回键 + 扩展按钮/文本)
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 返回组件组 (对应 XML 里的 layout_title_back)
                if (onBackClick != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clickable(
                                onClick = onBackClick,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null // 提供无抖动的点击相应
                            )
                            .padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = backIcon,
                                contentDescription = "Back",
                                tint = finalContentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        // 返回文字区域 (对应 tv_title_back)
                        if (!backText.isNullOrEmpty()) {
                            Text(
                                text = backText,
                                color = finalContentColor,
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
                
                // 左侧额外扩展区 (对应 XML 的 iv_title_left_image_2, tv_title_txt_left)
                leftExtraContent?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        it()
                    }
                }
            }
            
            // 2. 居中主标题 (完美绝对居中，对应 XML 的 tv_title 在 CenterInParent 里的表现)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    // 确保标题在大屏幕或长文本下不会覆盖两端，保留安全操作区
                    .padding(horizontal = 72.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = finalContentColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // 3. 右侧区域 (对应 XML 的多按钮：tv_title_txt_right, iv_title_right_image)
            if (rightContent != null) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    rightContent()
                }
            }
        }
    }
}
