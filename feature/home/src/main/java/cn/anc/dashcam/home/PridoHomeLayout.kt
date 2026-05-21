package cn.anc.dashcam.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.anc.dashcam.album.AlbumPage
import cn.anc.dashcam.device.DevicePage
import cn.anc.dashcam.feedback.FeedbackScreen
import cn.anc.dashcam.mine.MinePage

/**
 * PRIDO 分支首屏布局方案 (Classic 4-Tab standard frame)
 */
@Composable
fun PridoLayoutFrame() { // 方法开始
    // 默认从 MINE 个人中心着底
    var selectedTab by remember { mutableStateOf(MainTab.MINE) } // 记忆所选页签
    val colors = HomeColors.current() // 载入主辅页调色方案

    Surface( // 表层
        modifier = Modifier.fillMaxSize(), // 铺满
        color = colors.pageBackground, // 页面背景
    ) { // 容器
        Column(modifier = Modifier.fillMaxSize()) { // 大竖条
            Box(modifier = Modifier.weight(1f)) { // 主内容
                when (selectedTab) { // 分页
                    MainTab.DEVICE -> DevicePage(
                        onNavigateToAlbum = { selectedTab = MainTab.LOCAL_ALBUM },
                        onOpenShare = { selectedTab = MainTab.FEEDBACK },
                        onNavigateToMine = { selectedTab = MainTab.MINE }
                    ) // 设备
                    MainTab.LOCAL_ALBUM -> AlbumPage() // 相册
                    MainTab.FEEDBACK -> FeedbackScreen(modifier = Modifier.statusBarsPadding()) // 反馈 (整合 feature:feedback Composable 进来作 Tab 页)
                    MainTab.MINE -> MinePage() // 个人
                } // 选完页
            } // 内容完
            
            // 底栏绘制
            Row( // 底部四导航栏实体
                modifier = Modifier // 样式
                    .fillMaxWidth() // 铺宽
                    .background(colors.bottomBarBackground) // 导航栏高对比底色
                    .navigationBarsPadding() // 绕让虚拟物理三大键
                    .height(80.dp) // 高端大气 80dp
                    .padding(horizontal = 16.dp), // 两侧留 16dp
                horizontalArrangement = Arrangement.SpaceBetween, // 等距分散
                verticalAlignment = Alignment.CenterVertically // 垂直中心线齐美
            ) { // 页签行
                // 一号页签
                PridoBottomTab(
                    tab = MainTab.DEVICE,
                    selected = selectedTab == MainTab.DEVICE,
                    onClick = { selectedTab = it }
                ) // 页签结束
                // 二号页签
                PridoBottomTab(
                    tab = MainTab.LOCAL_ALBUM,
                    selected = selectedTab == MainTab.LOCAL_ALBUM,
                    onClick = { selectedTab = it }
                ) // 页签结束
                // 三号页签
                PridoBottomTab(
                    tab = MainTab.FEEDBACK,
                    labelOverride = stringResource(R.string.home_tab_feedback), // 国际化重写反馈文本
                    selected = selectedTab == MainTab.FEEDBACK,
                    onClick = { selectedTab = it }
                ) // 页签结束
                // 四号页签
                PridoBottomTab(
                    tab = MainTab.MINE,
                    selected = selectedTab == MainTab.MINE,
                    onClick = { selectedTab = it }
                ) // 页签结束
            } // 行结束
        } // 大竖条结束
    } // 面板结束
} // PRIDO 布局结束

/**
 * PRIDO 单一底部页签样式
 */
@Composable
private fun PridoBottomTab( // 页签辅助渲染
    tab: MainTab, // 主页签绑定自变量
    labelOverride: String? = null, // 重写国际化标题
    selected: Boolean, // 指导当前高亮渲染
    onClick: (MainTab) -> Unit // 页签选择器回调
) { // 方法开始
    val label = labelOverride ?: stringResource(tab.labelRes) // 获取字描述
    val colors = HomeColors.current() // 色组
    
    // 如果是反馈，借用信封 Email 矢量，若是 mine 保持原样，其它均保持
    val painter = if (tab == MainTab.FEEDBACK) { // 反馈分支图标
        painterResource(id = R.drawable.ic_account_message) // 采用信件图标
    } else { // 其它
        painterResource(id = if (selected) tab.selectedIconRes else tab.normalIconRes) // 读取通用图标
    } // 读图截止

    Column( // 垂直对齐
        modifier = Modifier // 装饰
            .size(width = 80.dp, height = 64.dp) // 极简 80x64
            .clip(RoundedCornerShape(12.dp)) // 特色切圆
            .clickable { onClick(tab) }, // 绑定选中
        horizontalAlignment = Alignment.CenterHorizontally, // 中心
        verticalArrangement = Arrangement.Center // 中心
    ) { // 内容
        Image( // 图片
            painter = painter, // 图源
            contentDescription = label, // 字释
            modifier = Modifier.size(26.dp) // 向量大尺寸
        ) // 图片完
        Spacer(modifier = Modifier.height(4.dp)) // 呼吸距离
        Text( // 正文
            text = label, // 文字
            color = if (selected) colors.selectedText else colors.unselectedText, // 动态主题着色
            fontSize = 11.sp, // 耐看 11sp 小写
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal // 略粗
        ) // 标签完
    } // 垂直列完
} // 完毕
