package cn.anc.dashcam.device

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.AppThemeMode
import cn.anc.dashcam.core.data.BrandConfigManager
import cn.anc.dashcam.core.data.getDarkHex
import cn.anc.dashcam.core.data.getLightHex
import cn.anc.dashcam.core.logging.AppLog

/**
 * 设备主控页面核心控制台 (Device Main Control Panel Dashboard)
 * 渲染四个高亮动作按钮：设备 (Device)、本地相册 (Local Album)、分享、支持 (Mine)
 */
@Composable
fun DevicePage(
    modifier: Modifier = Modifier,
    onNavigateToAlbum: (() -> Unit)? = null,
    onOpenShare: (() -> Unit)? = null,
    onNavigateToMine: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val colors = DeviceColors.current()
    val scrollState = rememberScrollState()

    // 1. 读取品牌与外观夜间/日间强调着色器
    val isDark = AppThemeManager.isDarkTheme(context)
    val accent = AppThemeManager.currentThemeColor(context)
    val accentHex = if (isDark) accent.getDarkHex() else accent.getLightHex()
    val accentColor = Color(accentHex)

    // 2. 仿真连接断开状态模拟机
    var isDeviceConnected by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.pageBackground)
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // A. 高质量头部排版 (Header Typography block)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.device_page_title),
            color = colors.textPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Text(
            text = "Smart Dashcam Control Suite",
            color = colors.textSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 24.dp),
            textAlign = TextAlign.Start
        )

        // B. 玻璃磨砂质感连接状态标牌卡片 (State visual banner)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 呼吸状态指示灯小圆形
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            if (isDeviceConnected) accentColor else Color(0xFFEF4444)
                        )
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isDeviceConnected) "CarDVR-2222: ONLINE" else "CarDVR-2222: OFFLINE",
                        color = colors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isDeviceConnected) "Connected via super-fast WiFi (5Ghz)" else "Disconnected from car dashcam",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .clickable {
                            isDeviceConnected = !isDeviceConnected
                            val statusMsg = if (isDeviceConnected) "DVR Online Connected" else "DVR Offline Disconnected"
                            AppLog.i("Device connection status toggled: $statusMsg")
                            Toast.makeText(context, statusMsg, Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isDeviceConnected) "Disconnect" else "Connect",
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // C. 四个一等高亮动作面板 (High-contrast prominent action controls grid)
        Text(
            text = "功能控制面板 / Action Suite",
            color = colors.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Start
        )

        // 使用 2x2 网格排列
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 第一排：设备 & 本地相册
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. 设备 (Device)
                HighlightActionButton(
                    title = stringResource(R.string.button_device_title),
                    description = stringResource(R.string.button_device_desc),
                    icon = Icons.Default.PlayArrow,
                    bgColor = colors.cardBackground,
                    accentColor = accentColor,
                    textColor = colors.textPrimary,
                    descColor = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        AppLog.i("DevicePage: 'Device' action trigger")
                        Toast.makeText(context, "Navigating to Device settings...", Toast.LENGTH_SHORT).show()
                    }
                )

                // 2. 本地相册 (Local Album)
                HighlightActionButton(
                    title = stringResource(R.string.button_album_title),
                    description = stringResource(R.string.button_album_desc),
                    icon = Icons.Default.Menu,
                    bgColor = colors.cardBackground,
                    accentColor = accentColor,
                    textColor = colors.textPrimary,
                    descColor = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        AppLog.i("DevicePage: 'Local Album' action trigger")
                        if (onNavigateToAlbum != null) {
                            onNavigateToAlbum()
                        } else {
                            Toast.makeText(context, "Opening Local Album...", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // 第二排：分享 & 支持
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 3. 分享 (Share)
                HighlightActionButton(
                    title = stringResource(R.string.button_share_title),
                    description = stringResource(R.string.button_share_desc),
                    icon = Icons.Default.Add,
                    bgColor = colors.cardBackground,
                    accentColor = accentColor,
                    textColor = colors.textPrimary,
                    descColor = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        AppLog.i("DevicePage: 'Share' action trigger")
                        if (onOpenShare != null) {
                            onOpenShare()
                        } else {
                            Toast.makeText(context, "Opening Dynamic Sharing...", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // 4. 支持 (Mine)
                HighlightActionButton(
                    title = stringResource(R.string.button_mine_title),
                    description = stringResource(R.string.button_mine_desc),
                    icon = Icons.Default.AccountCircle,
                    bgColor = colors.cardBackground,
                    accentColor = accentColor,
                    textColor = colors.textPrimary,
                    descColor = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        AppLog.i("DevicePage: 'Support & Mine' action trigger")
                        if (onNavigateToMine != null) {
                            onNavigateToMine()
                        } else {
                            Toast.makeText(context, "Opening Support Center...", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * 拼装单个极简拟物高对比的高亮动作卡片 (Sleek Highlighted Action Block)
 */
@Composable
private fun HighlightActionButton(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    accentColor: Color,
    textColor: Color,
    descColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(170.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            // 顶端：带呼吸底的圆形高对比 Icon 容器 (Stylized high-contrast icon container)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.25f),
                                accentColor.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            // 底端：大容量两级标题文案 (Subtitle details)
            Column {
                Text(
                    text = title,
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = descColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 15.sp,
                    maxLines = 2
                )
            }
        }
    }
}

private data class DeviceColorScheme(
    val pageBackground: Color,
    val cardBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
)

private object DeviceColors {

    @Composable
    fun current(): DeviceColorScheme {
        val context = LocalContext.current
        val brandColor = BrandConfigManager.colorConfig
        return if (AppThemeManager.isDarkTheme(context)) {
            DeviceColorScheme(
                pageBackground = Color(brandColor.pageBgDark),
                cardBackground = Color(brandColor.cardBgDark),
                textPrimary = Color(0xFFE8EDF2),
                textSecondary = Color(0xFF9BA7B3),
            )
        } else {
            DeviceColorScheme(
                pageBackground = Color(brandColor.pageBgLight),
                cardBackground = Color(brandColor.cardBgLight),
                textPrimary = Color(0xFF292929),
                textSecondary = Color(0xFF777777),
            )
        }
    }
}
