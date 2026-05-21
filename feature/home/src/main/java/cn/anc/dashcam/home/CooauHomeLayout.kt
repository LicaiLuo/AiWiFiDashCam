package cn.anc.dashcam.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.anc.dashcam.album.AlbumPage
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.logging.AppLog
import cn.anc.dashcam.core.navigation.ActivityFeatureNavigator
import cn.anc.dashcam.device.DevicePage
import cn.anc.dashcam.mine.MinePage
import kotlinx.coroutines.launch

/**
 * COOAU 新客户端马甲包分支 (Modal Drawer Sliding settings layout style)
 */
@Composable
fun CooauLayoutFrame() { // 抽屉开始
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Drawer 记忆可见度，默认收拢
    val scope = rememberCoroutineScope() // 取协程域，弹出需要异步动画驱动
    var selectedTab by remember { mutableStateOf(MainTab.MINE) } // 内容页仍采用经典的 Mine 页作为底层焦点
    val colors = HomeColors.current() // 色组
    val context = LocalContext.current // 获取上下文

    // A. 嵌套 Material 3 最新的滑动抽屉大总管
    ModalNavigationDrawer( // 多层滑
        drawerState = drawerState, // 绑定状态
        drawerContent = { // 抽屉向右滑出时呈现的视觉卡片内容 (Drawer Sheet Layout)
            ModalDrawerSheet( // 侧滑卡片底座
                drawerContainerColor = if (AppThemeManager.isDarkTheme(context)) Color(0xFF1E2329) else Color.White, // 颜色同步
                modifier = Modifier.width(280.dp) // 高级优雅限宽为 280dp 绝不过饱
            ) { // 卡片内垂直排
                Column( // 盒一垂直排布
                    modifier = Modifier // 装饰
                        .fillMaxSize() //
                        .statusBarsPadding() // 让开状态栏
                        .padding(20.dp) //
                ) { // 内容
                    // 1. 模拟 Cooau 豪华 Pilot 用户玻璃卡片头部
                    Row( // 横排
                        modifier = Modifier // 修饰
                            .fillMaxWidth() //
                            .clip(RoundedCornerShape(16.dp)) //
                            .background(colors.selectedText.copy(alpha = 0.15f)) // 使用带有 15% 品牌高光色的半透明微渐变磨砂层
                            .clickable {} //
                            .padding(16.dp), //
                        verticalAlignment = Alignment.CenterVertically // 垂直中心平水
                    ) { // 会员列
                        Box( // 头像圈
                            modifier = Modifier //
                                .size(48.dp) //
                                .clip(CircleShape) // 纯圆
                                .background(colors.selectedText), // 底色
                            contentAlignment = Alignment.Center //
                        ) { // 个人符
                            Icon( //
                                imageVector = Icons.Default.AccountCircle, // Me
                                contentDescription = "Me", //
                                tint = Color.White, //
                                modifier = Modifier.size(28.dp) //
                            ) // 完毕
                        } // 结束头像
                        
                        Spacer(modifier = Modifier.width(12.dp)) //
                        
                        Column { // 字符
                            Text( // 主标题
                                text = "Cooau Pilot", // 客户端专属用户名高亮
                                color = if (AppThemeManager.isDarkTheme(context)) Color.White else Color(0xFF1F2937), //
                                fontSize = 16.sp, //
                                fontWeight = FontWeight.Bold //
                            ) // 文字完
                            Spacer(modifier = Modifier.height(2.dp)) //
                            Text( // 辅助签名描述
                                text = "Smart Drive Linker", //
                                color = colors.unselectedText, //
                                fontSize = 11.sp //
                            ) // 辅助完
                        } // 宿主完了
                    } // 头部 Row 结束

                    Spacer(modifier = Modifier.height(32.dp)) // 留白隔离

                    // 2. 项目 1: 个人中心 (Mine)
                    CooauDrawerMenuItem(
                        label = stringResource(R.string.cooau_drawer_profile), // "个人中心"
                        icon = Icons.Default.AccountCircle, // 经典个人图标
                        onClick = { // 快速关闭并切换
                            scope.launch { drawerState.close() } // 关上抽屉
                            selectedTab = MainTab.MINE // 并直接转换到 Mine 页页签作为主焦点屏幕！
                        } //
                    ) // 项目完
                    
                    Spacer(modifier = Modifier.height(12.dp)) //

                    // 3. 项目 2: 设置中心 (Settings Activity launch)
                    CooauDrawerMenuItem(
                        label = stringResource(R.string.cooau_drawer_settings), // "应用设置管理"
                        icon = Icons.Default.Settings, // 标准机械齿轮
                        onClick = { //
                            scope.launch { drawerState.close() } // 闭合抽屉
                            AppLog.i("cooau drawer click settings", tag = "Home") // 日志持久化
                            ActivityFeatureNavigator(context).navigate("settings") // 寻找 route 执行跳转二级语言首选项
                        } //
                    ) // 项目完

                    Spacer(modifier = Modifier.height(12.dp)) //

                    // 4. 项目 3: 意见反馈 (Feedback Screen tab launch)
                    CooauDrawerMenuItem(
                        label = stringResource(R.string.cooau_drawer_feedback), // "意见与反馈"
                        icon = Icons.Default.Email, // 信件/电子邮件
                        onClick = { //
                            scope.launch { drawerState.close() } // 手势闭合
                            AppLog.i("cooau drawer click feedback", tag = "Home") //
                            ActivityFeatureNavigator(context).navigate("feedback") // 跳转独立反馈页，实现多马甲并行不悖
                        } //
                    ) // 项目完

                    Spacer(modifier = Modifier.weight(1f)) // 将后续修持版权字样压迫到底端展示

                    Text( // 版权尾字
                        text = "COOAU v1.0.0 Pro", //
                        color = colors.unselectedText.copy(alpha = 0.5f), //
                        fontSize = 11.sp, //
                        fontWeight = FontWeight.Medium, //
                        modifier = Modifier.align(Alignment.CenterHorizontally) // 底端水平居中
                    ) //
                } // 盒一完
            } // 卡片底座完
        } // 侧滑展开块完
    ) { // B. 抽屉内的底层主页首屏大骨架
        Surface( //
            modifier = Modifier.fillMaxSize(), //
            color = colors.pageBackground //
        ) { //
            Column(modifier = Modifier.fillMaxSize()) { //
                
                // 1. 顶部动作栏 (Cooau Top Menu bar containing hamburger toggle)
                Row( // 顶条
                    modifier = Modifier //
                        .fillMaxWidth() //
                        .statusBarsPadding() // 避开电量及开孔屏
                        .height(56.dp) //
                        .padding(horizontal = 16.dp), //
                    verticalAlignment = Alignment.CenterVertically // 垂直居中线平齐
                ) { // 内容
                    
                    // 左上角汉堡三横线菜单 trigger (Left menu button click opens Drawer)
                    Box( // 40dp 极宽触觉圆形点按座
                        modifier = Modifier //
                            .size(40.dp) //
                            .clip(CircleShape) //
                            .clickable { // 快速触发左滑出
                                AppLog.i("cooau trigger menu clicked: toggle drawer", tag = "Home") //
                                scope.launch { drawerState.open() } // 开启抽屉
                            }, //
                        contentAlignment = Alignment.Center //
                    ) { // 向量
                        Icon( // menu
                            imageVector = Icons.Default.Menu, // 三横线 Hamburger
                            contentDescription = "DrawerMenu", //
                            tint = colors.selectedText, // 自动高亮马甲高亮蓝
                            modifier = Modifier.size(24.dp) // 24dp 黄金高
                        ) // 向量完
                    } // 触托完
                    
                    Spacer(modifier = Modifier.width(16.dp)) //

                    Text( // 标题“COOAU记录仪”
                        text = "COOAU Smart Cam", //
                        color = if (AppThemeManager.isDarkTheme(context)) Color.White else Color(0xFF1F2937), //
                        fontSize = 18.sp, //
                        fontWeight = FontWeight.Bold //
                    ) // 标题完
                } // 顶栏 Row 完

                // 2. 首页面展示区
                Box( //
                    modifier = Modifier //
                        .weight(1f) //
                        .fillMaxWidth() //
                ) { // 内容
                    when (selectedTab) { // 分流
                        MainTab.DEVICE -> DevicePage(
                            onNavigateToAlbum = { selectedTab = MainTab.LOCAL_ALBUM },
                            onOpenShare = { ActivityFeatureNavigator(context).navigate("settings") },
                            onNavigateToMine = { selectedTab = MainTab.MINE }
                        ) //
                        MainTab.LOCAL_ALBUM -> AlbumPage() //
                        MainTab.MINE -> MinePage() // Mine 页面
                        else -> MinePage() // 默认兜底
                    } //
                } //

                // 3. 极简 COOAU 双 Tab 底栏切换 (记录仪 + 本地相册)
                Row( // 采用对称双 Tab
                    modifier = Modifier //
                        .fillMaxWidth() //
                        .background(colors.bottomBarBackground) //
                        .navigationBarsPadding() // 安全三大键
                        .height(80.dp) //
                        .padding(horizontal = 24.dp), //
                    horizontalArrangement = Arrangement.SpaceBetween, // 左右完美空余
                    verticalAlignment = Alignment.CenterVertically // 垂直中心线齐美
                ) { // 双按键
                    // 页签一: 记录仪 (DEVICE)
                    UnidenBottomTab(
                        label = "记录仪", //
                        iconRes = MainTab.DEVICE.normalIconRes, //
                        selectedIconRes = MainTab.DEVICE.selectedIconRes, //
                        selected = selectedTab == MainTab.DEVICE, //
                        onClick = { selectedTab = MainTab.DEVICE } //
                    ) // 完
                    
                    // 页签二: 本地相册 (ALBUM)
                    UnidenBottomTab(
                        label = "相册", //
                        iconRes = MainTab.LOCAL_ALBUM.normalIconRes, //
                        selectedIconRes = MainTab.LOCAL_ALBUM.selectedIconRes, //
                        selected = selectedTab == MainTab.LOCAL_ALBUM, //
                        onClick = { selectedTab = MainTab.LOCAL_ALBUM } //
                    ) // 完
                } // 导航栏 Row 结束
            } // 垂直排架大框完
        } // Surface面体完
    } // Drawer 完
} // COOAU 布局结束

/**
 * COOAU 抽屉专用单行纸片菜单项
 */
@Composable
private fun CooauDrawerMenuItem( // 菜单子项件
    label: String, // 字标题
    icon: ImageVector, // 特征矢量图
    onClick: () -> Unit //
) { // 方法体
    val colors = HomeColors.current() // 载色
    
    Row( // 横排
        modifier = Modifier //
            .fillMaxWidth() //
            .height(54.dp) // M3 绝佳高
            .clip(RoundedCornerShape(12.dp)) //
            .background(Color(0xFF2E353F).copy(alpha = 0.12f)) // 柔和微白隔绝底
            .clickable(onClick = onClick) //
            .padding(horizontal = 16.dp), //
        verticalAlignment = Alignment.CenterVertically // 垂直居中线平齐
    ) { // 项内容
        Icon( // 图标
            imageVector = icon, // 传入矢量
            contentDescription = label, //
            tint = colors.selectedText, //
            modifier = Modifier.size(22.dp) // 22dp 格局
        ) // 矢量完
        
        Spacer(modifier = Modifier.width(16.dp)) //
        
        Text( // 中文标题文字
            text = label, // 字
            color = if (AppThemeManager.isDarkTheme(LocalContext.current)) Color.White else Color(0xFF1F2937), //
            fontSize = 15.sp, //
            fontWeight = FontWeight.SemiBold //
        ) // 标题完
    } // 结束项目 Row
} // Item 完
