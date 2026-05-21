package cn.anc.dashcam.core.common // 本类所属的基类公共包路径

import android.content.Context // 导入系统的 Context 句柄类
import android.content.Intent // 导入系统的 Intent 意图跳转类
import android.os.Bundle // 导入系统的 Bundle 跨进程信息载体类
import androidx.activity.ComponentActivity // 导入 ComponentActivity 作为所有页面的承载基础
import androidx.core.view.WindowCompat // 导入 WindowCompat 窗口界面样式设置兼容处理辅助类

/**
 * 业务基础 Activity 基类 (Base Activity Class)
 * 
 * 所有的界面 Activity 都需要继承自本类。它提供和封装了以下核心系统功能: (Core base features for all activities:)
 * 
 * 1. 语言本地化包裹: 运行时保证多语言跟随用户的 App 设置。 (Locale wrapping)
 * 2. 状态栏字体图标颜色控制: 允许全局配置、或强制白色/黑色图标，解决浅色背景看不清状态栏的 Bug。(Status bar icon styles)
 * 3. 左右滑动跳转动画: 当打开新 Activity 或是返回/销毁 Page 时，自动附带丝滑的物理阻尼滑动过渡。 (Slide slide animations)
 */
open class BaseActivity : ComponentActivity() { // 业务中所有 Activity 页面的通用父类

    override fun attachBaseContext(newBase: Context) { // 在组件启动附着上下文时，动态加载并包装本地化 Context
        val wrappedContext = AppLocaleManager.wrapContext(newBase) // 调用语言管理器包方法生成带有语言环境包裹的 context
        super.attachBaseContext(wrappedContext) // 将带有多语言包环境的 context 回调传递给父类进行承载
    } // 结束上下文拦截与附着机制的配置

    override fun onCreate(savedInstanceState: Bundle?) { // 当 Activity 正式实例化创建生命周期触发时
        super.onCreate(savedInstanceState) // 加载父类的原生构建资源
        syncStatusBarIconsStyle() // 页面新建时立刻刷新并计算最匹配当前设置的状态栏前景色 (亮/暗)
    } // 结束 onCreate 初始化生命周期配置

    override fun onResume() { // 当 Activity 从后台返回或完全在前台获取用户点击焦点焦点时
        super.onResume() // 调用父类的原生 Resume 特征
        syncStatusBarIconsStyle() // 确保在从下级页面退回本页或从后台重返时，状态栏外观仍能精准刷新同步
    } // 结束 onResume 生命周期配置

    private var isStartingActivity = false // 增加一个重入标记阀，杜绝 startActivity 无限回流或死循环带来的 input-dispatcher 奔溃

    override fun startActivity(intent: Intent?) { // 重写系统默认的 Activity 跳转意图方法以插入右滑动画
        if (isStartingActivity) { // 若检测到上一个跳转指令正在底层事务还未完全完成，即当前处于已标记的跳转锁定态
            super.startActivity(intent) // 本次直接调用系统原生跳转处理，绕过可能存在的重复切面拦截
            return // 即行结束整个方法的递归执行链，安全返还
        } // 结束防重入跳转态的快速预切校验
        isStartingActivity = true // 正式入轨跳转，将跳转锁定标记设为 true 拦截下次任何连击
        try { // 进入 startActivity 的异常隔离保护沙盒
            super.startActivity(intent) // 调用基础父类的启动跳转指令
            overrideTransition(isEntering = true) // 启动完毕后立即强制通过 pending 接口应用从右侧平滑推入的视觉转场
        } finally { // 无论 startActivity 运行成功、或抛出因 Activity 未注册等任何错误异常
            isStartingActivity = false // 在最终态无条件重置跳转标记为可用状态 false
        } // 结束 try-finally 沙盒结构
    } // 结束 startActivity 跳转方法

    override fun startActivity(intent: Intent?, options: Bundle?) { // 重写系统带参数配置 options 的 Activity 启动方法
        if (isStartingActivity) { // 防重入检验
            super.startActivity(intent, options) // 在标记已被占用时，直接调用系统自带路由
            return // 出栈返回
        } // 校验逻辑处理块结束
        isStartingActivity = true // 标记在跳转行，将 activity 跳转设为阻断状态
        try { // 依旧通过隔离沙盒执行带参数跳转
            super.startActivity(intent, options) // 交付底层系统处理实际的 activity 打开请求
            overrideTransition(isEntering = true) // 转场：将新界面从屏幕右边平移推入屏幕
        } finally { // 执行完毕收尾态
            isStartingActivity = false // 重设重入保护位恢复为 false 保证按钮下一次还能被用户正常响应
        } // 保护沙盒结束
    } // 结束带 options 的 startActivity 启动方法

    /**
     * 重写 finish，以便在当前界面被关闭或回退时，触发“左进右出”的滑出滑落回执动画。
     * (Slide in from left, slide out to right on finishing views)
     */
    override fun finish() { // 重载 Activity 销毁滑出退出
        super.finish() // 优先令系统完成标准销毁行为、释放大部分不必要的界面引用
        overrideTransition(isEntering = false) // 立刻调用 pending 专场触发原 Activity 向屏幕右边滑出消逝、旧 Activity 从左侧推入前台
    } // 结束整个 finish 回退执行

    /**
     * 辅助执行窗口打开/关闭时的原生 Pending Transition 动画
     */
    private fun overrideTransition(isEntering: Boolean) { // 内置根据当前页面进退动作决定调用哪套资源文件的动画引擎
        try { // 动画引擎必须用 try-catch 包裹，因为在极少数平板设备定制 Room 上 pending API 极易直接引发崩溃
            if (isEntering) { // 如果检测到当前的行为是打开/进入新界面
                // 打开界面：从右侧推入 (Slide in from right, push old out to left)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left) // 绑定从右向左覆盖动画
            } else { // 如果检测到当前行为是退出/销毁
                // 退出界面：向右侧滑出 (Slide out to right, fetch old back from left)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right) // 绑定向右移出渐出动画
            } // 结束分支选择
        } catch (e: Exception) { // 全面接纳因系统硬件接口调用发生的任何 Exception
            cn.anc.dashcam.core.logging.AppLog.e( // 往全局安全日志模块打印一条警告，辅助开发进行底层日志定位
                "Failed to override pending transition: ${e.message}", // 日志内容
                throwable = e, // 附加的异常栈抛出堆结构
                tag = "BaseActivity" // 日志归属的 tag 模块
            ) // 结束错误收集
        } // 异常框架退出
    } // 动画过渡方法退出

    /**
     * 应用并刷新状态栏的前景字体与图标颜色 (Light/Dark style on StatusBar icons)
     * 
     * 遵循 3 种用户选择模式:
     * - AUTO: (默认) 智能跟随当前 App 的显示主题。如果是黑夜模式，使用亮色(白色)字体；否则使用暗色(黑色)字体。
     * - DARK: 强制状态栏采用黑色字体与图标。无论背景如何均恒定清晰。
     * - LIGHT: 强制状态栏采用白色字体与图标。适用于特定沉浸式顶部 Banner 或品牌底图。
     */
    fun syncStatusBarIconsStyle() { // 刷新状态栏前景色字体和图案颜色的主要封装方法
        val currentWindow = window ?: return // 从当前 Activity 环境获取 Window 实例，如果为 null 则返回不作操作
        val decorView = currentWindow.decorView // 解析出最底层的 decorView 平台渲染宿主视窗
        val insetsController = WindowCompat.getInsetsController(currentWindow, decorView) // 通过兼容类获取 insets 系统状态控制器，方便设置前景色

        // 1. 获取当前的状态栏颜色模式 (Read style)
        val mode = AppThemeManager.currentStatusBarTextMode(this) // 读取 SharedPreferences/MMKV 中用户在设置里配置的当前状态栏模式
        
        // 2. 查看当前是否处于夜间模式下 (Read if dark theme is active)
        val isDarkTheme = AppThemeManager.isDarkTheme(this) // 读取主题渲染器，计算当如果是 SYSTEM 探测下的实际明暗渲染底色

        // 3. 计算是否应当显示白色图标 (Calculate if we should use light icons)
        val shouldShowLightIcons = when (mode) { // 通过 when 选择框架匹配出最贴合配置的状态
            StatusBarTextMode.LIGHT -> true // LIGHT 代表强制显示亮色(白字体)
            StatusBarTextMode.DARK -> false  // DARK 代表强制显示暗色(黑字体)
            StatusBarTextMode.AUTO -> isDarkTheme // AUTO 会智能比对：如果处于护眼夜间深色底，则采用亮色(白字体)图标；否则相反
        } // 决策得出最终明暗布尔值

        // 4. 将设置反馈给系统视窗句柄 (Apply to set status bar light text or dark text)
        // isAppearanceLightStatusBars = true 代表“深色字体图标”（用于浅色背景）
        // isAppearanceLightStatusBars = false 代表“浅色（白色）字体图标”（用于深色背景）
        insetsController.isAppearanceLightStatusBars = !shouldShowLightIcons // 倒置赋值：若显示白字，则 LightStatusBar 应设为 false
    } // 结束刷新状态栏前景色方法
} // 基类结束

