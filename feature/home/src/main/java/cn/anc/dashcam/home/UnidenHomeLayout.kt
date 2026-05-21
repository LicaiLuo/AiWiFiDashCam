package cn.anc.dashcam.home

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.anc.dashcam.album.AlbumPage
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.logging.AppLog
import cn.anc.dashcam.core.navigation.ActivityFeatureNavigator
import cn.anc.dashcam.device.DevicePage

/**
 * UNIDEN 品牌分支布局方案 (完美还原截图的 3-Tab 奢华拟物主页布局)
 */
@Composable
fun UnidenLayoutFrame() { // 方法开始
    var selectedTab by remember { mutableStateOf(MainTab.DEVICE) } // 主页面页签，记录仪 DEVICE 或本地相册 LOCAL_ALBUM
    var showAddDialog by remember { mutableStateOf(false) } // 共享/添加 "+" 号大弹板控制触发变量
    var isConnected by remember { mutableStateOf(false) } // 设备模拟物理连接真状态，动态切换按键描述与打勾
    val colors = HomeColors.current() // 读取大色彩盘
    val context = LocalContext.current // 读取原生 Context 用于日志气泡提醒

    Surface( // 面层容器
        modifier = Modifier.fillMaxSize(), // 全屏
        color = colors.pageBackground, // 色泽
    ) { // 容器内部
        Column(modifier = Modifier.fillMaxSize()) { // 垂直大排架
            // A. UNIDEN 尊享豪华顶部动作栏 (AppBar Layout based on Screenshot)
            Row( // 顶部条
                modifier = Modifier // 修饰
                    .fillMaxWidth() // 平铺
                    .statusBarsPadding() // 自动贴合沉浸式电池电量状态栏高度
                    .height(56.dp) // 精致的 56dp 一级导航高
                    .padding(horizontal = 16.dp), // 留出 16dp 自适应
                horizontalArrangement = Arrangement.SpaceBetween, // 左右完美排列
                verticalAlignment = Alignment.CenterVertically // 垂直居中线平齐
            ) { // 内容
                // 1. 左侧：经典的返回箭头 (Left side back arrow)
                Box( // 自包裹可点触圆形底座
                    modifier = Modifier // 修饰
                        .size(40.dp) // 40dp 黄金触角大小
                        .clip(CircleShape) // 切圆
                        .clickable { // 点击退出或隐藏 Activity
                            AppLog.i("uniden navigation back clicked", tag = "Home") // 日志持久化
                            (context as? Activity)?.onBackPressed() // 触发原生实体 Activity 物理退出，提高与硬件底座统一体验
                        }, // 结束 clickable
                    contentAlignment = Alignment.Center // 居中
                ) { // 放置文字
                    Text( // 简练优雅高对比返回符号
                        text = "＜", // 仿真截图左方向键
                        color = colors.selectedText, // 采用主题亮光蓝配色
                        fontSize = 20.sp, // 特设 20sp
                        fontWeight = FontWeight.Bold // 强调粗
                    ) // 字符完
                } // 盒完

                // 2. 中间：记录仪多语言大标题 (Center adaptive title)
                Text( // 标题组件
                    text = stringResource(R.string.home_tab_device), // 绑定多语言：“记录仪” / “Device”
                    color = if (AppThemeManager.isDarkTheme(context)) Color.White else Color(0xFF1F2937), // 支持日间高雅亮灰与暗夜钛白自感知
                    fontSize = 18.sp, // 标准一档 18sp
                    fontWeight = FontWeight.Bold // 强调大黑粗
                ) // 标题完

                // 3. 右侧：截图原版动作区：带有设置⚙️ 与 日志/文件📄 的双通道图标
                Row( // 侧排横组
                    verticalAlignment = Alignment.CenterVertically // 垂直中心线齐美
                ) { // 双动作孔
                    Box( // 📄 日志快捷按键底托
                        modifier = Modifier // 装饰
                            .size(40.dp) // 40dp 完美可点
                            .clip(CircleShape) // 四角全圆圆润
                            .clickable { // 点开
                                AppLog.i("doc logs clicked", tag = "Home") // 写硬日志
                                Toast.makeText(context, "Log & Document Open Successfully", Toast.LENGTH_SHORT).show() // 弹出防呆成功反馈小气泡
                            }, //
                        contentAlignment = Alignment.Center // 中间对齐
                    ) { // 容器里
                        Icon( // 日志图标容器
                            imageVector = Icons.Default.PlayArrow, // 极简播放/文件指示
                            contentDescription = "Logs", // 文字辅说
                            tint = Color(0xFF6B7280), // 优美复古金属哑灰色：0xFF6B7280
                            modifier = Modifier.size(22.dp) // 22dp 矢量微宽
                        ) // 向量完
                    } // 盒完
                    
                    Spacer(modifier = Modifier.width(8.dp)) // 重组 8dp 呼吸距离，隔离设置按键

                    Box( // ⚙️ 设置按钮快捷底盘
                        modifier = Modifier // 装饰
                            .size(40.dp) // 40dp
                            .clip(CircleShape) // 纯圆
                            .clickable { // 劫持并极速导航到 settings
                                AppLog.i("open settings shortcut from uniden top bar", tag = "Home") // 日志
                                ActivityFeatureNavigator(context).navigate("settings") // 重载核心 settings 模块
                            }, // 点击完
                        contentAlignment = Alignment.Center //
                    ) { // 图标
                        Icon( // 齿轮
                            imageVector = Icons.Default.Settings, // M3 纯正机械⚙️齿轮矢量
                            contentDescription = "Settings", //
                            tint = Color(0xFF6B7280), // 奢华磨砂暗灰色
                            modifier = Modifier.size(22.dp) // 22dp
                        ) // 图标完
                    } // 盒体完
                } // 右端组完
            } // 顶部导航 Row 结束

            // B. 主内容工作区自适应动态挂载 (Middle Body Switch Layout)
            Box( // 用 weight(1f) 自适应分配除顶栏和底栏外所有的剩余屏幕面积
                modifier = Modifier // 样式
                    .weight(1f) //
                    .fillMaxWidth() // 平铺铺宽
            ) { // 内容
                if (selectedTab == MainTab.DEVICE) { // 如果在 记录仪 栏
                    Column( // 垂直排列排版
                        modifier = Modifier // 修饰锁
                            .fillMaxSize() //
                            .padding(horizontal = 24.dp), // 留出经典的 24dp 左右页边距
                        horizontalAlignment = Alignment.CenterHorizontally, // 全员水平居中对齐，符合拟物海报风格
                        verticalArrangement = Arrangement.Center // 垂直亦居中，形成极其舒适之握持重心
                    ) { // 容器里
                        
                        // 1. 高精度真·行车记录仪外形精绘渲染 (High fidelity mock illustrative card)
                        RealisticCameraIllustration() // 加载我们精制手绘的不锈钢超广角记录仪组件！
                        
                        Spacer(modifier = Modifier.height(28.dp)) // 饱满的 28dp 高度大留空

                        // 2. 连接标识牌及高亮成功勾徽章 (Connection details row)
                        Row( // 水平横排
                            verticalAlignment = Alignment.CenterVertically, // 垂直对齐
                            horizontalArrangement = Arrangement.Center //
                        ) { // 组内容
                            Text( // 配备 CarDVR-2222
                                text = stringResource(R.string.device_connect_status), // “CarDVR-2222” 的高精度字符载入入口
                                color = if (AppThemeManager.isDarkTheme(context)) Color.White else Color(0xFF1F2937), // 日间黑色夜间高亮钛白
                                fontSize = 18.sp, // 一档精美大字
                                fontWeight = FontWeight.Bold // 强调黑粗
                            ) // 文字完
                            
                            Spacer(modifier = Modifier.width(6.dp)) // 6dp 指示符空暇

                            Icon( // 已验证勾标志
                                imageVector = Icons.Default.CheckCircle, // 一等实心勾圆标
                                contentDescription = "Active Check", // 释词
                                tint = colors.selectedText, // 自动取用户选择的橙色、绿光、或经典湛空科技蓝配色
                                modifier = Modifier.size(18.dp) // 18dp 精美高
                            ) // 向量勾完
                        } // 验证排完

                        Spacer(modifier = Modifier.height(36.dp)) // 36dp 大间隙

                        // 3. 豪华连接控制大按钮 (Huge Round Core Connection Button with rings)
                        UnidenConnectionRing { // 完美还原截图的旋转双圆弧环圈组件
                            Box( // 真正的拟物圆形连接按钮实体
                                modifier = Modifier // 装饰
                                    .size(140.dp) // 140dp 扎实大按键，完全契合截图大视觉冲击点
                                    .clip(CircleShape) // 切为完美无暇大圆形
                                    .shadow(elevation = 8.dp, shape = CircleShape) // 注入 8dp 特色微反光拟物灰阶投影
                                    .background( // 双色线性荧光高亮渐变，触碰有极光闪烁感
                                        brush = Brush.verticalGradient( // 纵向闪烁拉丝
                                            colors = listOf(
                                                Color(0xFF1E293B), // 外缘青石黑
                                                Color(0xFF0F172A)  // 深层夜空煤黑
                                            ) // 色组
                                        ) // 刷
                                    ) // 色完
                                    .clickable { // 点击行为
                                        AppLog.event("uniden_connect_btn_clicked") // 日志行为埋点记录
                                        isConnected = !isConnected // 开关状态动态互逆反转！
                                    }, // 手动点击结束
                                contentAlignment = Alignment.Center // 文字中线居中
                            ) { // 内容
                                Column( // 文字和状态灯垂直
                                    horizontalAlignment = Alignment.CenterHorizontally //
                                ) { // 列内
                                    Text( // 连接提示汉字
                                        text = if (isConnected) "已连接" else stringResource(R.string.device_connect_btn), // “连接记录仪” 或 “已连接” 自动切控
                                        color = if (isConnected) colors.selectedText else Color.White, // 依据物理信号，动态转变为品牌高光蓝（或自定义强调色）
                                        fontSize = 14.sp, // 14sp 黄金比例说明字
                                        fontWeight = FontWeight.Bold, // 最硬粗体，提高文字能见度
                                        modifier = Modifier.padding(horizontal = 12.0.dp), // 避免溢出
                                    ) // 文字完
                                    Spacer(modifier = Modifier.height(4.dp)) // 极窄 4dp 空瑕
                                    // 状态呼吸小跳点
                                    Box( // 跳点底座
                                        modifier = Modifier // 修饰
                                            .size(8.dp) // 8dp
                                            .clip(CircleShape) // 纯圆
                                            .background(if (isConnected) colors.selectedText else Color(0xFFEF4444)) // 开启是酷蓝色，未载入是提示醒目红
                                    ) // 点完次
                                } // 列结束
                            } // 圆盘按钮结束
                        } // 描边环完
                    } // DEVICE 布局结束
                } else { // 否则是在 ALbumPage 本地相册
                    AlbumPage() // 挂载相册特色主屏，支持秒切换
                } // 页面切空完毕
            } // 内容工作区 Box 结束

            // C. 经典截图同款 “记录仪、中央大加号、本地相册” 3-Tab 底部自适应导航栏 (Custom UNIDEN Tab Bar)
            // 采用 3-Tab Box 叠层悬浮结构，使得大加号完美向上凸出 20dp+，并环抱精细极亮白钢环
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.Transparent),
                contentAlignment = Alignment.BottomCenter
            ) {
                // 底栏高饱和度卡片背景
                Row( // 创建底部横盘
                    modifier = Modifier // 修饰链
                        .fillMaxWidth() // 铺满
                        .background(colors.bottomBarBackground) // 自动跟随系统的冷黑暗色底
                        .navigationBarsPadding() // 施加高安全 navigationBarsPadding 占位，保障大虚拟键开启下不挤压
                        .height(80.dp) // 保障最舒适 80dp 高度，杜绝文字或大图标被裁变
                        .padding(horizontal = 24.dp), // 边缘留 24dp 呼吸
                    horizontalArrangement = Arrangement.SpaceBetween, // 左右分散排布
                    verticalAlignment = Alignment.CenterVertically // 垂直居中线平齐
                ) { // 内容
                    
                    // 页签 1: 记录仪 (DEVICE)
                    UnidenBottomTab(
                        label = "记录仪", // 中文截图文字
                        iconRes = MainTab.DEVICE.normalIconRes, // 通用设备 icon
                        selectedIconRes = MainTab.DEVICE.selectedIconRes, //
                        selected = selectedTab == MainTab.DEVICE, // 指示
                        onClick = { // 跳转
                            AppLog.i("uniden tab selected: Device", tag = "Home") // 日志
                            selectedTab = MainTab.DEVICE // 切换
                        } //
                    ) // 页签 1 完

                    // 中央预留出 66dp 的大虚景段，专门承接上方叠放的呼吸加号
                    Spacer(modifier = Modifier.width(66.dp))

                    // 页签 3: 本地相册 (ALBUM)
                    UnidenBottomTab(
                        label = "相册", // 截图简中自研文字“相册”
                        iconRes = MainTab.LOCAL_ALBUM.normalIconRes, // 相册常态灰 icon
                        selectedIconRes = MainTab.LOCAL_ALBUM.selectedIconRes, //
                        selected = selectedTab == MainTab.LOCAL_ALBUM, //
                        onClick = { // 跳转
                            AppLog.i("uniden tab selected: Local Album", tag = "Home") //
                            selectedTab = MainTab.LOCAL_ALBUM // 切换
                        } //
                    ) // 页签 3 完
                } // 3-Tab 底部 bar 行大修饰完

                // 圆心 2: 完美拟物浮空的 “+” 号加号大按钮动作，伴随高品质纯白/亮灰质地圆圈框架 (Central Floating Action +)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding() // 同样施加虚拟键内嵌高度，保证完全平齐合一
                        .offset(y = (-30).dp) // 向外上浮空高悬 30dp，穿过 Row 顶栏上沿
                        .size(66.dp) // 外侧白钢圈整体口径设定为 66dp
                        .shadow(8.dp, shape = CircleShape) // 浮突微光灰度立体阴影
                        .clip(CircleShape)
                        .background(Color.White) // 极致高雅圆润的纯白钢外圈
                        .padding(4.dp), // 4dp 圆润钛白保护缝隙，让加号富有层次
                    contentAlignment = Alignment.Center
                ) {
                    Box( // 轴承核心暗黑面
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF2D3748), Color(0xFF111827)) // 仿真截图硬黑金属底
                                )
                            )
                            .clickable {
                                AppLog.i("central plus '+' clicked: show dialog", tag = "Home")
                                showAddDialog = true // 完美在主屏幕中央弹出隔离的全新分享大蒙板
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon( // 标准向量加号
                            imageVector = Icons.Default.Add, // +
                            contentDescription = "Add & Share", //
                            tint = Color.White, // 纯耀眼高白，凸出主要动作
                            modifier = Modifier.size(28.dp) // 28dp 超大加号图标
                        )
                    }
                }
            }
        } // 垂直排架大框完
        
        // D. 挂载我们完全解耦、高颜值的全屏弹版对话框，用户关闭自退
        UnidenShareDialog( // 唤起大面板
            visible = showAddDialog, // 传入控制信号
            onClose = { showAddDialog = false } // 向上响应关闭信号回位为 false，彻底解耦
        ) // 弹框结束
    } // Surface 大根结束
} // UNIDEN 布局结束

/**
 * UNIDEN 页签子项独立适配器
 */
@Composable
internal fun UnidenBottomTab( // 极简自愈页签
    label: String, //
    iconRes: Int, //
    selectedIconRes: Int, //
    selected: Boolean, //
    onClick: () -> Unit //
) { // 方法体
    val colors = HomeColors.current() // 获取配色
    
    Column( // 垂直
        modifier = Modifier // 样式
            .size(width = 86.dp, height = 64.dp) // 精确适配
            .clip(RoundedCornerShape(12.dp)) //
            .clickable(onClick = onClick), //
        horizontalAlignment = Alignment.CenterHorizontally, // 中心
        verticalArrangement = Arrangement.Center // 居中
    ) { //
        Image( // 图片
            painter = painterResource(id = if (selected) selectedIconRes else iconRes), // 指示切换亮图或暗灰图
            contentDescription = label, //
            modifier = Modifier.size(26.dp) // 26dp
        ) // 图片完
        Spacer(modifier = Modifier.height(4.dp)) // 间距
        Text( // 本地文字
            text = label, // 字
            color = if (selected) colors.selectedText else colors.unselectedText, // 支持强调高亮炫彩
            fontSize = 11.sp, //
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal // 略粗
        ) // 文字完
    } // 容器完
} // 完毕
