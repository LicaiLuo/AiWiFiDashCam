package cn.anc.dashcam.core.common // 归属于 core:common 模块的通用包路径

import android.content.Context // 导入系统的 Context 外部上下文传递句柄
import com.tencent.mmkv.MMKV // 导入腾讯 MMKV 高性能、多线程、高并发多进程 KeyValue 存储引擎

/**
 * 应用全局主题与视觉显示管理器 (App Global Theme, Accent Color, and Status Bar manager)
 * 
 * 借助腾讯 MMKV 高性能 key-value 框架进行设置存储与实时读取。 (Leverages high-performance Tencent MMKV framework)
 */
object AppThemeManager { // 定义全局唯一的 AppThemeManager 单例对象

    /**
     * 获取用户在设置中选择的主题模式 (LIGHT, DARK, SYSTEM) (Get stored Theme Mode selection)
     */
    fun currentThemeMode(context: Context): AppThemeMode { // 获取显示主题模式的方法
        val tag = kv(context).decodeString(KEY_THEME_MODE_TAG) // 通过 MMKV 根据 key 读取保存的显示主题 tag 字符串
        return AppThemeMode.fromTag(tag) // 将序列化字符串解码转换为对应的 AppThemeMode 枚举返回
    } // 方法执行结束

    /**
     * 获取用户选择的主题模式对应的 tag (Get current theme mode tag string)
     */
    fun currentThemeModeTag(context: Context): String { // 获取模式 tag 的快捷方法
        return currentThemeMode(context).tag // 直接转化并返回对应的字符串 tag 标
    } // 方法执行结束

    /**
     * 保存用户选择的主题模式 (Save Theme Mode selection)
     */
    fun saveThemeMode(context: Context, themeMode: AppThemeMode) { // 保存主题模式设置的方法
        kv(context).encode(KEY_THEME_MODE_TAG, themeMode.tag) // 通过 MMKV 键值接口将特定模式的 tag 进行持久化存储
    } // 方法执行结束

    /**
     * 计算并确定当前是否应当渲染为深色/夜间主题 (Determine if Dark Theme should actually be rendered)
     * Supports Light mode, Dark mode, and automatic detection based on System Light/Dark configuration.
     */
    fun isDarkTheme(context: Context): Boolean { // 计算当前是否为黑夜模式主色的布尔方法
        return when (currentThemeMode(context)) { // 通过 when 并行计算
            AppThemeMode.LIGHT -> false // 如果是明亮模式，恒定返回 false(非深色)
            AppThemeMode.DARK -> true // 如果是黑夜主题模式，恒定返回 true(深色)
            AppThemeMode.SYSTEM -> { // 如果是跟随系统自适应模式
                // 读取系统当前是否为深色模式 (Read current system Configuration)
                val uiMode = context.resources.configuration.uiMode // 从系统 context 读取当前屏幕的 runtime UI 模式
                (uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == // 在掩码作用域下
                        android.content.res.Configuration.UI_MODE_NIGHT_YES // 返回是否确实等于系统的夜间状态值
            } // 结束系统自适应判断分支
        } // 结束 when 判断组合
    } // 方法执行结束

    /**
     * 获取当前存储的主题应用主色调/强调色 (Get saved Theme Accent color)
     */
    fun currentThemeColor(context: Context): AppThemeColor { // 读取当前的高亮配色色系
        val tag = kv(context).decodeString(KEY_THEME_COLOR_TAG) // 从存储引擎根据 key 读取高亮配色对应的标签 tag
        return AppThemeColor.fromTag(tag) // 映射并转换返回高亮配色枚举
    } // 结束方法

    /**
     * 获取当前存储的主题强调色对应的序列化 Tag (Get saved Theme accent color tag string)
     */
    fun currentThemeColorTag(context: Context): String { // 获取高亮配色 tag 的快捷方法
        return currentThemeColor(context).tag // 提取并返回它的序列化 tag 标
    } // 结束

    /**
     * 写入/保存用户选择的主题色 (Save Theme Accent Color)
     */
    fun saveThemeColor(context: Context, themeColor: AppThemeColor) { // 持久化写入配色方案的方法
        kv(context).encode(KEY_THEME_COLOR_TAG, themeColor.tag) // 通过腾讯 MMKV 存储该配色对应的字符串
    } // 结束保存强调色

    /**
     * 获取状态栏字体/图标颜色显示模式 (Get status bar content mode preference)
     */
    fun currentStatusBarTextMode(context: Context): StatusBarTextMode { // 获取状态栏图标前景色设置
        val tag = kv(context).decodeString(KEY_STATUS_BAR_TEXT_MODE_TAG) // 读取状态栏模式字符标签
        return StatusBarTextMode.fromTag(tag) // 解码状态栏明暗模式枚举并进行回响
    } // 结束读取状态栏图标前景色

    /**
     * 保存选中的状态栏字体颜色模式 (Save status bar content mode preference)
     */
    fun saveStatusBarTextMode(context: Context, mode: StatusBarTextMode) { // 保存状态栏前景色配置的方法
        kv(context).encode(KEY_STATUS_BAR_TEXT_MODE_TAG, mode.tag) // 加码并持久化状态栏的字符标签 tag
    } // 结束持久化

    /**
     * 获取 MMKV 存储实例 (Initialize and retrieve MMKV instance safely)
     */
    private fun kv(context: Context): MMKV { // 内部安全的腾讯 MMKV 获取接口封装
        AppLocaleManager.initialize(context) // 在获取存储前，优先令语言管理器进行初始化，MMKV 保证在其内部 init 完毕
        return requireNotNull(MMKV.mmkvWithID(MMKV_ID)) { // 提供防 null 检测与异常文案提示
            "MMKV is not initialized. Call AppLocaleManager.initialize(context) in Application.onCreate()." // 脱轨时的崩溃信息
        } // 回传安全的 MMKV 实例
    } // 方法执行结束

    private const val MMKV_ID = "dashcam_app_settings" // 定义 MMKV 的独立专属存储空间 ID，避免与其他缓存混用
    private const val KEY_THEME_MODE_TAG = "theme_mode_tag" // 存储显示模式 (日间/夜间/跟随系统) 对应的底层 key
    private const val KEY_THEME_COLOR_TAG = "theme_color_tag" // 存储全新设计品牌底色/高亮配色方案对应的底层 key
    private const val KEY_STATUS_BAR_TEXT_MODE_TAG = "status_bar_text_mode_tag" // 存储状态栏个性化前景色对应的 key
} // 单例 object 结束

