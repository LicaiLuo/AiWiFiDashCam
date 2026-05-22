package cn.anc.dashcam.settings // 声明所属的设置包路径

import androidx.annotation.StringRes // 导入字符串资源注解
import androidx.compose.foundation.background // 导入背景色修饰符
import androidx.compose.foundation.clickable // 导入可点击修饰符
import androidx.compose.foundation.layout.Column // 导入垂直列布局容器
import androidx.compose.foundation.layout.ColumnScope // 导入列布局作用域接口
import androidx.compose.foundation.layout.Row // 导入水平行布局容器
import androidx.compose.foundation.layout.Spacer // 导入辅助空位组件
import androidx.compose.foundation.layout.fillMaxSize // 导入占满最大尺寸修饰符
import androidx.compose.foundation.layout.fillMaxWidth // 导入占满最大宽度修饰符
import androidx.compose.foundation.layout.height // 导入高度修饰符
import androidx.compose.foundation.layout.padding // 导入边距填充修饰符
import androidx.compose.foundation.layout.size // 导入尺寸设定修饰符
import androidx.compose.foundation.layout.statusBarsPadding // 导入避开状态栏顶部填充修饰符
import androidx.compose.foundation.shape.RoundedCornerShape // 导入圆角矩形形状
import androidx.compose.material3.RadioButton // 导入标准单选按钮组件
import androidx.compose.material3.Text // 导入文本显示控件
import androidx.compose.runtime.Composable // 导入Composable标记注解
import androidx.compose.ui.Alignment // 导入对齐方式参数
import androidx.compose.ui.Modifier // 导入修饰算子基类
import androidx.compose.ui.draw.clip // 导入图片或背景裁切修饰符
import androidx.compose.ui.graphics.Color // 导入色彩类
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode // 导入本地上下文环境获取器
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight // 导入字体粗细设置接口
import androidx.compose.ui.unit.dp // 导入dp密度时间单位
import androidx.compose.ui.unit.sp // 导入sp缩放无关像素单位
import cn.anc.dashcam.core.common.AppLanguage // 导入定义的多语言枚举类
import cn.anc.dashcam.core.common.AppThemeManager // 导入主题模式存储管理器类
import cn.anc.dashcam.core.common.AppThemeMode // 导入定义的外观暗亮模式枚举类
import cn.anc.dashcam.core.common.AppThemeColor // 导入定义的主色强调配色枚举类
import cn.anc.dashcam.core.common.StatusBarTextMode // 导入状态栏前景字模式枚举类
import cn.anc.dashcam.core.data.BrandConfigManager // 导入品牌定制数据管理器
import cn.anc.dashcam.core.ui.DashCamTitleBar


@Composable // 声明此函数是一个可组合渲染UI函数
internal fun SettingsScaffold( // 内部通用骨架组件开始
    title: String, // 传入当前设置界面的头部标题文字
    onBackClick: () -> Unit, // 传入点击左上角回退按钮的闭包监听器
    content: @Composable ColumnScope.() -> Unit, // 传入此页面的主要列表渲染内容槽
) { // 页面级统一容器定义体
    val colors = AppSettingsColors.current() // 读出贴合亮暗氛围的最优背景/文本配色池

    Column( // 根布局采用垂直链，装载顶栏和子页面内容
        modifier = Modifier // 准备全局样式修饰符连锁
            .fillMaxSize() // 填充物理屏幕的最大长和宽
            .background(colors.pageBackground), // 应用自适应的明亮/幽暗物理底色
    ) { // 排版组织器大闭包
        DashCamTitleBar(
            title = title,
            onBackClick = onBackClick,
            containerColor = colors.pageBackground,
            contentColor = colors.textPrimary,
            enableStatusBarPadding = true
        )

        Spacer(modifier = Modifier.height(24.dp)) // 设置导航栏与下方数据列表之间的 24dp 视觉间隙
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            content = content
        ) // 就地渲染外部填充引入的列块
    } // 根排版容器 Column 结束
} // 导航页骨架架设方法退出

@Composable // Compose 装饰标签
internal fun SettingValueRow( // 导航跳转单行设置行的可组合结构
    title: String, // 该行项目的功能汉化名称
    value: String, // 该行右侧当前生效的状态数值快照描述
    onClick: () -> Unit, // 当用户整行选中触发的跳转回调闭包
) { // 界面行渲染方法开始
    val colors = AppSettingsColors.current() // 解析捕获最新的环境配色元组

    Row( // 设置主元素水平拉伸行
        modifier = Modifier // 初始化行的修饰器链
            .fillMaxWidth() // 拉伸塞满整个容纳宽度
            .height(72.dp) // 固定高度 72dp
            .clip(RoundedCornerShape(16.dp)) // 裁切出一个精致的 16dp 四周小圆角
            .background(colors.cardBackground) // 赋以精致舒雅的卡片底色隔离
            .clickable(onClick = onClick) // 绑定跳转逻辑
            .padding(horizontal = 20.dp), // 行内部组件左右两侧呼吸留白 20dp
        verticalAlignment = Alignment.CenterVertically, // 将标题与当前状态选项保持居中对齐
    ) { // 单元行具体控件绘制开始
        Text( // 最左侧的设置标题文字组件
            text = title, // 功能项词语
            color = colors.textPrimary, // 主字色
            fontSize = 18.sp, // 18sp 一级功能标题体量
            modifier = Modifier.weight(1f), // 智能高比重占领屏幕左侧全部空旷区间
        ) // 标题组件结束
        Text( // 显示当前已经选定的设置值的灰色提示文案
            text = value, // 设置值，如：简体中文，暗沉
            color = colors.textSecondary, // 配色系统读取次级的灰色配比
            fontSize = 16.sp, // 16sp 微小二阶详情大小
        ) // 值组件结束
        Text( // 在最右侧画出的象征跳向二级子页面的单箭标识
            text = ">", // 标志
            color = colors.textSecondary, // 跟随次级灰色色
            fontSize = 28.sp, // 28sp 精准指示箭头
            fontWeight = FontWeight.Light, // 轻细，展现扁平质感
            modifier = Modifier.padding(start = 8.dp), // 向左保持 8dp 微调空暇
        ) // 结尾箭头指示控件结束
    } // 水平行结构 Row 结束
} // 设置项单行绘制结束

@Composable // 声明可组合组件
internal fun OptionRow( // 单选设置界面每行选项行的自定义布局
    title: String, // 每一个单选项的具体本地化汉语或英语名称
    selected: Boolean, // 该项在持久化中是否处于激活勾选态 (布尔状态)
    onClick: () -> Unit, // 当该行行本身或单选按钮被点按后的改变回调
) { // OptionRow 功能实现开始
    val colors = AppSettingsColors.current() // 载入当前的系统渲染配色表

    Row( // 设置水平单选题界面单元行
        modifier = Modifier // 样理组链
            .fillMaxWidth() // 灌满卡片盒
            .height(72.dp) // 保持 72dp 科技行高
            .clip(RoundedCornerShape(16.dp)) // 精确卡片盒为 16dp 四角圆润弧度
            .background(colors.cardBackground) // 将自适应的卡片亮暗着色覆盖
            .clickable(onClick = onClick) // 点击任一处，都视同选中触发
            .padding(start = 20.dp, end = 12.dp), // 左边入空 20dp，右侧由于有按钮，稍微拉长保护至 12dp
        verticalAlignment = Alignment.CenterVertically, // 中线对称排列
    ) { // 子组件大渲染块
        Text( // 用来显示选项标题文本的组件
            text = title, // 选项标题
            color = colors.textPrimary, // 一等字色
            fontSize = 18.sp, // 保持舒服可辨的 18sp 字体
            modifier = Modifier.weight(1f), // 抢夺剩余所有闲置的屏幕宽度
        ) // 描述控件结束
        RadioButton( // Android 标准 M3 圆圈单选指示控件
            selected = selected, // 直接接收外界传入的数据勾选真假
            onClick = onClick, // 点击该 RadioButton 也同样向上通知回调
        ) // 单选按钮控件闭着
    } // 水平 Row 排版完成
} // 选项选择行渲染退出

@get:StringRes // 精准声明返回值必须对应 res 文件夹下的特定 int 字符串 id
internal val AppLanguage.labelRes: Int // 为 AppLanguage 新增扩展属性 labelRes，以便在 Compose 系统直接读取语言资源
    get() = when (this) { // when 匹配不同语系下的 int
        AppLanguage.ZH_CN -> R.string.settings_language_chinese // 简体中文对应 ZH_CN 的翻译路径
        AppLanguage.EN -> R.string.settings_language_english // 英语对应 EN 的翻译路径
    } // 结束多语言映射。

@get:StringRes // 表明仅能返回 String 的资源 ID 指针
internal val AppThemeMode.labelRes: Int // 为 AppThemeMode 扩增 labelRes 资源描述属性
    get() = when (this) { // 匹配并分流
        AppThemeMode.LIGHT -> R.string.settings_theme_light // 日间模式 XML 资源指针
        AppThemeMode.DARK -> R.string.settings_theme_dark // 护眼暗夜模式 XML 的词组
        AppThemeMode.SYSTEM -> R.string.settings_theme_system // 自适应跟随系统 XML 的翻译词 id
    } // 属性返回完毕

@get:StringRes // 暗示其转换出唯一的字资源 code
internal val AppThemeColor.labelRes: Int // 为新增的主题特色高亮配色的枚举扩充其本地化的文案 XML 字典地址
    get() = when (this) { // 根据枚举的方案类型展开路由
        AppThemeColor.BLUE -> R.string.settings_color_blue // BLUE 代表经典科技的海军蓝高亮配色
        AppThemeColor.GREEN -> R.string.settings_color_green // GREEN 代表清新低碳的翡翠绿
        AppThemeColor.ORANGE -> R.string.settings_color_orange // ORANGE 代表朝气蓬勃的暖系亮丽橙
        AppThemeColor.PURPLE -> R.string.settings_color_purple // PURPLE 代表科幻神秘的高雅皇家紫
    } // 结束色组转换

@get:StringRes // xml 字符注解护航
internal val StatusBarTextMode.labelRes: Int // 为新增的状态栏文字/图标颜色覆盖管理器扩充属性
    get() = when (this) { // when 分流处理
        StatusBarTextMode.AUTO -> R.string.settings_status_bar_auto // AUTO 指代动态智能比对，白昼显示黑字，深夜显示白字
        StatusBarTextMode.DARK -> R.string.settings_status_bar_dark // DARK 代表绝对状态下强制状态栏转为墨黑色字体和图标
        StatusBarTextMode.LIGHT -> R.string.settings_status_bar_light // LIGHT 为状态栏盖印一层雪白色
    } // 状态栏映射属性结束

@get:StringRes
internal val cn.anc.dashcam.core.common.BarColorOption.labelRes: Int
    get() = when (this) {
        cn.anc.dashcam.core.common.BarColorOption.DEFAULT -> R.string.settings_bar_color_default
        cn.anc.dashcam.core.common.BarColorOption.THEME_ACCENT -> R.string.settings_bar_color_theme_accent
        cn.anc.dashcam.core.common.BarColorOption.CHARCOAL -> R.string.settings_bar_color_charcoal
        cn.anc.dashcam.core.common.BarColorOption.WHITE -> R.string.settings_bar_color_white
        cn.anc.dashcam.core.common.BarColorOption.BLUE -> R.string.settings_bar_color_blue
        cn.anc.dashcam.core.common.BarColorOption.GREEN -> R.string.settings_bar_color_green
        cn.anc.dashcam.core.common.BarColorOption.ORANGE -> R.string.settings_bar_color_orange
        cn.anc.dashcam.core.common.BarColorOption.PURPLE -> R.string.settings_bar_color_purple
    }

internal data class SettingsColors( // 建立该设置页面在亮色及暗夜环境下对应的配色配置数据模型类
    val pageBackground: Color, // 物理主屏幕画布底色
    val cardBackground: Color, // 承载单行设置要素的卡片防尘白/极客灰底色
    val textPrimary: Color, // 一等文本大标签专有颜色 (高对比度)
    val textSecondary: Color, // 二等二级提示小文本颜色 (低对比度灰)
) // 配色包声明终结

internal object AppSettingsColors { // 全局的在 Compose 沙盒下根据明暗自适应转换色彩的工厂工具代理

    @Composable // 只能在 compose 生命周期堆栈里回调，因为 LocalContext 与 theme 检测均依靠状态树
    fun current(): SettingsColors { // 工厂返回函数
        if (LocalInspectionMode.current) {
            return SettingsColors(
                pageBackground = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_preview_page_bg),
                cardBackground = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_pure_white),
                textPrimary = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_text_primary),
                textSecondary = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_text_secondary),
            )
        }

        val context = LocalContext.current // 精准获取当前视口里的 Activity 上下文句柄
        val brandColor = BrandConfigManager.colorConfig
        return if (AppThemeManager.isDarkTheme(context)) { // 询问并比对：如果读取得到当前系统已进入深色护眼/夜间状态
            SettingsColors( // 返回一整套为深夜模式重绘的、护眼、抗疲劳、极客风采的高对比暗色系
                pageBackground = Color(brandColor.pageBgDark), // 极富质感的黑灰夜空背景由 BrandConfigManager 接管
                cardBackground = Color(brandColor.cardBgDark), // 繁星微光卡片深灰黑色的背景板由 BrandConfigManager 接管
                textPrimary = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_text_primary),
                textSecondary = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_text_secondary),
            ) // 模式构建结束
        } else { // 如果没有处于黑夜状态，代表用户正身处一汪清泉般明快的朝阳白昼下
            SettingsColors( // 返回一整套整洁、明朗、高洁、反光护眼的基础日间亮色配比
                pageBackground = Color(brandColor.pageBgLight), // 经典的防尘浅亮灰色背垫由 BrandConfigManager 接管
                cardBackground = Color(brandColor.cardBgLight), // 珍珠白大底的卡片背景由 BrandConfigManager 接管
                textPrimary = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_text_primary),
                textSecondary = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_text_secondary),
            ) // 日间包构建
        } // 亮暗对比结束
    } // current 方法结束
} // AppSettingsColors 结束
