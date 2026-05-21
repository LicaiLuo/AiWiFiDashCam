package cn.anc.dashcam.home // 声明属于主页模块的包路径

import androidx.compose.animation.AnimatedVisibility // 导入可见性精美动画类
import androidx.compose.animation.fadeIn // 导入渐显动画效果
import androidx.compose.animation.fadeOut // 导入渐隐动画效果
import androidx.compose.foundation.Canvas // 导入 Canvas 绘图控件
import androidx.compose.foundation.background // 导入背景绘制修饰符
import androidx.compose.foundation.clickable // 导入通用点击事件修饰符
import androidx.compose.foundation.interaction.MutableInteractionSource // 导入防干扰无边框水波交互句柄
import androidx.compose.foundation.layout.Arrangement // 导入对齐配置
import androidx.compose.foundation.layout.Box // 导入重叠盒容器层
import androidx.compose.foundation.layout.Column // 导入竖向排布链
import androidx.compose.foundation.layout.Row // 导入水平横向布局栏
import androidx.compose.foundation.layout.Spacer // 导入辅助距离调节组件
import androidx.compose.foundation.layout.fillMaxHeight // 导入高度拉满修饰
import androidx.compose.foundation.layout.fillMaxSize // 导入画板宽高铺满修饰
import androidx.compose.foundation.layout.fillMaxWidth // 导入宽度占满修饰
import androidx.compose.foundation.layout.height // 导入高矮量词
import androidx.compose.foundation.layout.padding // 导入空暇内间距
import androidx.compose.foundation.layout.size // 导入高宽大小限定
import androidx.compose.foundation.layout.statusBarsPadding // 导入避开手机顶部状态栏遮挡
import androidx.compose.foundation.shape.CircleShape // 导入圆形边界剪切形
import androidx.compose.foundation.shape.RoundedCornerShape // 导入四向圆角矩形包络
import androidx.compose.material.icons.Icons // 导入 Material 自带规范图标
import androidx.compose.material.icons.filled.Add // 导入一等 + 号标准向量
import androidx.compose.material.icons.filled.CheckCircle // 导入一等已验证圆勾实心标志
import androidx.compose.material.icons.filled.Close // 导入一等 X 关闭徽章
import androidx.compose.material3.Icon // 导入一等轻质向量图标呈现容器
import androidx.compose.material3.Text // 导入文本绘制
import androidx.compose.runtime.Composable // 导入 Compose 挂载注解
import androidx.compose.runtime.remember // 导入运行时重组缓存优化器
import androidx.compose.ui.Alignment // 导入中线与上下左右等效对齐常数
import androidx.compose.ui.Modifier // 导入链式状态修饰控制符
import androidx.compose.ui.draw.clip // 导入强制切角图形修饰
import androidx.compose.ui.draw.shadow // 导入底层立体悬浮投影墨渍
import androidx.compose.ui.geometry.Offset // 导入画板 2D 绝对偏移量坐标
import androidx.compose.ui.geometry.Size // 导入 Canvas 单次贴图绝对规格
import androidx.compose.ui.graphics.Brush // 导入多维色彩线性/环形渐变混合刷
import androidx.compose.ui.graphics.Color // 导入色彩十六进制类
import androidx.compose.ui.graphics.StrokeCap // 导入画笔端点切角造型
import androidx.compose.ui.graphics.drawscope.Stroke // 导入画笔画笔空心描边风格
import androidx.compose.ui.res.stringResource // 导入字资源读取工具
import androidx.compose.ui.text.font.FontWeight // 导入字体配重
import androidx.compose.ui.unit.dp // 导入物理对齐单位 dp
import androidx.compose.ui.unit.sp // 导入抗缩放字体 sp

/**
 * 绘图工厂：用 Compose 高阶 Canvas 技术动态完美复刻仿真拟物 dashcam 设备
 */
@Composable
fun RealisticCameraIllustration( // 绘制高度还原截图中的记录仪硬件图
    modifier: Modifier = Modifier // 样式扩展
) { // 绘图开始
    Box( // 用一个带优雅阴影和特定长宽比的外部盒，限定其显示边界
        modifier = modifier // 带上外层修饰锁
            .size(width = 240.dp, height = 150.dp) // 固定黄金比例 240x150 dp
            .shadow(12.dp, shape = RoundedCornerShape(20.dp)) // 给予 12dp 立体感爆棚的透视光阴影，切出 20dp 的圆角
            .clip(RoundedCornerShape(20.dp)) // 再在圆角边界内强制限位切割裁剪
            .background( // 精心重绘机身为具有极客哑光金属质感的渐变拉丝灰黑色
                brush = Brush.verticalGradient( // 青睐于垂直方向的双色调缓和渐变
                    colors = listOf( // 灰黑色度
                        Color(0xFF2E3134), // 顶边缘浅反光：0xFF2E3134
                        Color(0xFF141617)  // 底阴霾煤黑色：0xFF141617
                    ) // 双色表
                ) // 渐变刷
            ), // 底色完
        contentAlignment = Alignment.Center // 令内部的核心大镜头在水平及垂直上始终绝对中位对齐
    ) { // 盒体内容渲染
        Canvas( // 利用强大的底层绝对坐标 Canvas 画笔进行精准光效与细节雕琢
            modifier = Modifier.fillMaxSize() // 铺平整个 ILLUSTRATION
        ) { // 渲染回调上下文
            val w = size.width // 读取当前真实绘制画板的高度 w 像素
            val h = size.height // 读取高度 h 像素
            
            // 描绘底部磨砂防滑质感的镜头座子反光底圈
            drawRoundRect( // 画一个带有微弱色差深黑色背垫，加强镜头镜筒立起时的突兀立体感
                brush = Brush.radialGradient( // 径向散射光源
                    colors = listOf(Color(0xFF232527), Color(0xFF0F1011)), // 银灰至深空黑
                    center = Offset(w * 0.70f, h * 0.50f), // 锁定镜头偏右侧在 70% 黄金十字交汇处
                    radius = h * 0.45f // 外扩半径
                ), // 径向刷
                topLeft = Offset(w * 0.45f, h * 0.15f), // 限定左上角起点，形成极佳不对称式镜头透视
                size = Size(w * 0.48f, h * 0.70f), // 占机身的 48%x70%
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f) // 圆润切角
            ) // 座子写毕

            // 描绘镜筒的极富折射感的镜片与反光孔径
            drawCircle( // 外围不锈钢合金电镀防护套圈
                color = Color(0xFF3E4347), // 亮质防锈钢灰色
                radius = h * 0.32f, // 外侧大套圈
                center = Offset(w * 0.69f, h * 0.50f) // 中线
            ) // 钢圈完

            drawCircle( // 中层光学镜片密封阻光槽
                color = Color(0xFF0B0C0C), // 绝对墨黑色，避免漏光失真
                radius = h * 0.28f, // 稍稍缩窄一圈
                center = Offset(w * 0.69f, h * 0.50f) // 绝对对齐
            ) // 隔绝完

            // 高清折射蓝/青色超广角大光圈镜头组核心
            drawCircle( // 镀了深蓝色氟化镁增透膜的光学主体透镜
                brush = Brush.radialGradient( // 光源汇聚
                    colors = listOf(
                        Color(0xFF3B82F6), // 中心折射晶亮湛天蓝
                        Color(0xFF1E3A8A), // 过渡到科幻墨海水蓝
                        Color(0xFF0A0F1D)  // 边缘反射出高密镜面玻璃黑
                    ), // 色彩组
                    center = Offset(w * 0.71f, h * 0.48f), // 稍微朝左上方偏移 2% 像素，模仿左前方自然界环境强光线折射
                    radius = h * 0.22f // 半径 22%
                ), // 透光刷
                radius = h * 0.24f, // 真实镜头半径
                center = Offset(w * 0.69f, h * 0.50f) // 同轴
            ) // 主眼完

            // 点缀超真实高反光玻璃极光斑点
            drawCircle( // 添加主斑点
                brush = Brush.radialGradient( // 漫反射闪烁斑点
                    colors = listOf(Color.White.copy(alpha = 0.8f), Color.Transparent), // 80% 耀眼纯白至无
                    center = Offset(w * 0.61f, h * 0.42f), // 倾斜漫射角
                    radius = h * 0.08f // 小白圆孔
                ), // 反光斑点刷
                radius = h * 0.08f, // 直径
                center = Offset(w * 0.61f, h * 0.42f) // 汇聚
            ) // 光斑完

            // 为镜头下方绘制一排极具汽车科技风格的小透气排音孔群（完全还原截图中的九孔矩阵）
            val startX = w * 0.60f // 排音孔起点
            val startY = h * 0.82f // 位于镜头下方的 82% 轴线高度
            for (i in 0 until 4) { // 横向排布四孔
                drawCircle( // 第一排第一孔
                    color = Color(0xFF333537), // 比漆黑机身稍微淡一点的孔洞阴影
                    radius = 4f, // 微孔半径 4f 像素，刚好呈现出磨砂蜂窝阻尼
                    center = Offset(startX + (i * 12f), startY) // 12f 的呼吸间距错开
                ) // 一孔完
                drawCircle( // 叠绘第二排
                    color = Color(0xFF333537), // 同色
                    radius = 4f, // 细致孔
                    center = Offset(startX + (i * 12f), startY + 8f) // 垂直位移 8f 像素错开
                ) // 二孔完
            } // 蜂窝阵完成
        } // Canvas 大闭包退出
    } // 机箱盒结束
} // 仿生画板结束

/**
 * 分流解耦全页面：当点击底部大加号 "+" 按钮时，瞬间展开的高精度全屏呼出面板（带毛玻璃或半透明优雅隔离）
 */
@Composable
fun UnidenShareDialog( // 圆满响应截图，分离的添加与共享遮罩面板
    visible: Boolean, // 该全屏组件当前显隐状态变量
    onClose: () -> Unit // 用户点击关闭之后向上溢出的呼叫闭包
) { // 方法开始
    AnimatedVisibility( // 使用 Compose 本地底盘加速的可见性动效容器
        visible = visible, // 是否可见
        enter = fadeIn(), // 精美无缝淡入
        exit = fadeOut() // 顺爽的淡隐
    ) { // 动画域内部开始
        Box( // 承载整个屏幕的反光半透明护眼黑色层
            modifier = Modifier // 样式
                .fillMaxSize() // 填充最高
                .background(Color(0xE60D1117)) // 使用 90% 透明度的深空经典墨色覆盖：0xE60D1117，既看到底层页面有隐约背垫，又将当前焦点拉置核心
                .clickable( // 用户点击空白处，也贴心退去遮罩，提高系统的耐用和防呆感
                    onClick = onClose, // 联动接口
                    indication = null, // 注销系统默认的点击高亮水波纹，防止眼花
                    interactionSource = remember { MutableInteractionSource() } // 注入原生接收源以保证不冲突
                ), // 点击退出
            contentAlignment = Alignment.Center // 令主要的配置卡片牢牢悬停在中央，最舒服的观察焦点
        ) { // 盒体
            Column( // 卡片上的各个按钮元素垂直排布
                modifier = Modifier // 修饰
                    .padding(32.dp) // 外围呼吸空隙
                    .fillMaxWidth() // 卡片面宽拉平
                    .clip(RoundedCornerShape(24.dp)) // 特色切出舒雅的大 24dp 圆角
                    .background(Color(0xFF1E232A)) // 专为对话框制备的暗灰黑大底
                    .padding(24.dp) // 卡片内部控件左右四周大间隙 24dp 增加高雅气息
                    .clickable(onClick = {}), // 用户意外点击对话框实体时，截断点击响应（不发生误关）
                horizontalAlignment = Alignment.CenterHorizontally // 将内联子项在横切面上居中对齐
            ) { // 卡片内部垂直排列体
                Row( // 顶部小栏
                    modifier = Modifier.fillMaxWidth(), // 横向拉齐
                    horizontalArrangement = Arrangement.SpaceBetween, // 左右完美空余舒朗
                    verticalAlignment = Alignment.CenterVertically // 垂直中心线对齐
                ) { // 顶栏绘制
                    Text( // 添加/分享大标题
                        text = stringResource(R.string.uniden_share_add_device), // 动态载入字符
                        color = Color.White, // 精美的钛白色高亮重点
                        fontSize = 20.sp, // 20sp 标准大字号说明
                        fontWeight = FontWeight.Bold // 粗体，聚焦视线
                    ) // 标题完
                    Box( // 小 X 关闭按键高亮手操壳
                        modifier = Modifier // 修饰
                            .size(36.dp) // 极窄 36dp 大小
                            .clip(CircleShape) // 切为纯圆，完美容纳水波
                            .background(Color(0xFF2C323C)) // 灰黑小底托
                            .clickable(onClick = onClose), // 调用关闭
                        contentAlignment = Alignment.Center // X 在其中居中
                    ) { // 圆圈内
                        Icon( // 经典的 X 形指示图标容器
                            imageVector = Icons.Default.Close, // 输入 X 核心矢量
                            contentDescription = "Close", // 辅助说明关闭
                            tint = Color(0xFF9CA7B3), // 优雅的雾霾灰蓝灰
                            modifier = Modifier.size(18.dp) // 18dp 精细矢量高
                        ) // 向量结束
                    } // 壳结束
                } // 顶部返回行结束

                Spacer(modifier = Modifier.height(24.dp)) // 行间距
                
                Text( // 说明正文字段
                    text = stringResource(R.string.uniden_share_content), // 多语言文本
                    color = Color(0xFFD1D5DB), // 浅白灰色
                    fontSize = 15.sp, // 舒适字号段
                    lineHeight = 22.sp, // 增加每行的纵向间隙，大幅提升连续文字的可读与呼吸感
                    modifier = Modifier.fillMaxWidth() // 铺满可用区
                ) // 说明组件完

                Spacer(modifier = Modifier.height(32.dp)) // 正文与提交按钮间留存大空间
                
                Box( // 自适应大渐变色确认分享提交按钮，采用亮蓝绿等鲜明高亮吸引眼球
                    modifier = Modifier // 装饰
                        .fillMaxWidth() // 灌注一排
                        .height(54.dp) // M3 推荐之 54dp 饱满高度
                        .clip(RoundedCornerShape(14.dp)) // 切 14dp 四角微圆
                        .background( // 双色线性荧光高亮渐变
                            brush = Brush.horizontalGradient( // 从左到右
                                colors = listOf(Color(0xFF2563EB), Color(0xFF3B82F6)) // 从深蓝至湛蓝高能量过渡
                            ) // 渐变色
                        ) // 色完
                        .clickable(onClick = onClose), // 点击关闭自退
                    contentAlignment = Alignment.Center // 文字中线居中
                ) { // 文字内容
                    Text( // 确认文字
                        text = "OK", // 大字
                        color = Color.White, // 纯耀眼钛白字
                        fontSize = 16.sp, // 16sp
                        fontWeight = FontWeight.SemiBold // 中重量级粗
                    ) // 文本完
                } // 按钮完
            } // 垂直列结束
        } // 根蒙砂层结束
    } // 动画结束
} // 添加与共享大页面结束

/**
 * 绘图核心：用 Compose 画笔动态为 UNIDEN 的“连接记录仪”巨大圆按钮绘制环绕式的微光旋转双圆弧环线
 */
@Composable
fun UnidenConnectionRing( // 自定义双圆弧指示线条组件，高契合截图中的艺术环圈
    modifier: Modifier = Modifier, // 接收扩展
    content: @Composable () -> Unit // 在环形正中央承载的实际大按钮 Composable
) { // 方法体
    Box( // 提供一个重叠布局装载中央大环
        modifier = modifier // 绑定外面转来的约束
            .size(180.dp), // 全局外层环宽度锁定在 180dp x 180dp
        contentAlignment = Alignment.Center // 让承载的内容 text 在大环中央正中中线对齐
    ) { // 绘图装配
        Canvas( // 下层绘制美丽的断点双轨圆弧
            modifier = Modifier.fillMaxSize() // Canvas 涂满整槽
        ) { // 内部绘制
            val strokeWidth = 5f // 环圈线条粗细定为标准而精致的 5 像素
            val drawSize = size.width - strokeWidth // 避免由于边缘反锯齿和切角超出，将描画空间向内微锁 5 像素
            
            // 描画圆圈上段的第一根精美渐变圆弧，在 220 角度度至 100 角度范围内分布
            drawArc( // 描画弧线一号
                color = Color(0xFF3A3D42), // 使用截图同款的铁空灰色作为底层基础弧圈
                startAngle = 220f, // 顺时钟 220 度作为起点
                sweepAngle = 100f, // 偏转扫过 100 度弧面
                useCenter = false, // false 意味仅用边界描边，不形成实心切片蛋糕
                topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f), // 精细原点偏移，避开溢出
                size = Size(drawSize, drawSize), // 圆规格
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round) // 5 像素描边，且端点使用优雅的圆切头
            ) // 一号弧圈涂完

            // 描画圆圈下段的主弧线，在 10 度到 180 角度分布，刚好与上段产生经典视觉断空隔空感
            drawArc( // 描画弧线二号
                color = Color(0xFF2A2C2F), // 稍微更幽幽暗一些的灰
                startAngle = 10f, // 弧起点 10 度
                sweepAngle = 180f, // 偏转扫过 180 度广阔表面
                useCenter = false, // 纯轮廓空心描边
                topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f), // 偏移
                size = Size(drawSize, drawSize), //
                style = Stroke(width = strokeWidth + 2f, cap = StrokeCap.Round) // 特意增强 2f 像素粗，展现截图里粗细对比设计的艺术节奏
            ) // 弧圈二完成
        } // Canvas 退出
        
        content() // 将内部包装的内容组件(例如真正的按键)绘制在环中核心
    } // 结束外壳 Box
} // 动态双弧绘制退出
