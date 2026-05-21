package cn.anc.dashcam.core.common // 归属于 core:common 公共包层

import android.content.Context // 导入系统的 Context 上下文句柄类
import android.content.res.Configuration // 导入系统的 Configuration 屏幕与环境配置参数类
import android.os.Build // 导入系统的 Build 检测设备版本常量的工具
import android.os.LocaleList // 导入系统的 LocaleList 配置多语言串联表
import com.tencent.mmkv.MMKV // 导入高效率高吞吐的 MMKV 腾讯底层缓存库

object AppLocaleManager { // AppLocaleManager 管理器全局单例 object 开始

    @Volatile // 确保该初始化变量对于所有并发处理器核心均具有即时内存可见性，预防重入冲突
    private var initialized = false // 初始化持久化变量，默认置为未初始化 false

    fun initialize(context: Context) { // 专门提供的单例初始化底座工具方法
        if (!initialized) { // 如果变量尚为 false 说明还没有启动过在本次进程生命内
            synchronized(this) { // 抢锁获取锁：采用双重检查锁(DCL)多线程屏障防止在并发启动时造成重入事故
                if (!initialized) { // 第二重校验是否已经被另一个抢跑线程顺畅启动完毕
                    MMKV.initialize(context.applicationContext) // 初始化 MMKV 高速存储机制核心库
                    initialized = true // 置该标志位为已初始化 true
                } // 结束内校验
            } // 离开抢占锁作用域
        } // 结束外校验
    } // initialize 结束

    fun wrapContext(context: Context): Context { // 包装 Application 或 Activity 传阅的 Context 对象，赋予其用户设定的语言词集映射
        val language = currentLanguage(context) // 在高速 MMKV 内捕获或者系统预判捕获当前最符合用户的语系对象
        val configuration = Configuration(context.resources.configuration) // 根据 context 里面的当前环境复制出来一份全新的配置对象 configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 如果 Android 系统等级 >= 24 (Nougat N 7.0)
            configuration.setLocales(LocaleList(language.locale)) // 采用支持多语种联想串联的新设置方法，置入 locale
        } else { // 针对旧的 Android 7.0 以下古玩系统
            @Suppress("DEPRECATION") // 压制旧接口设置 locale 的报黄警告
            configuration.setLocale(language.locale) // 直呼老版 configuration.setLocale 置入其 locale 实体
        } // 结束分支

        return context.createConfigurationContext(configuration) // 回传一个经过创造包装了自定义语言 configuration 之后生成的全局 context，实现局部/全局劫持替换
    } // wrapContext 结束

    fun currentLanguage(context: Context): AppLanguage { // 读取用户设定的语系；如果尚无，则用当前 Android 的系统语言垫背保底
        return selectedLanguage(context) ?: systemLanguage(context) // 使用猫尾表达式，若 selectedLanguage 得到 null 则选用 systemLanguage 保底
    } // 当前语言判定方法退出

    fun currentLanguageTag(context: Context): String { // 读取当前语言枚举对应的 tag 字符串的快捷通道
        return currentLanguage(context).tag // 直接投石返还对应的 tag 串
    } // 结束

    fun saveLanguage(context: Context, language: AppLanguage) { // 持久化持久保存用户设定语系的方法
        kv(context).encode(KEY_LANGUAGE_TAG, language.tag) // 利用腾讯 MMKV 存储该语系配置唯一的字符标识 tag，例如 "zh-CN"
    } // 结束写入

    private fun selectedLanguage(context: Context): AppLanguage? { // 用户自己在设置里的手操变动在本地化持久层有没有落盘数据
        val tag = kv(context).decodeString(KEY_LANGUAGE_TAG) // 按照 KEY_LANGUAGE_TAG 作为键，去 MMKV 解密读取它的 String tag
        return AppLanguage.fromTag(tag) // 将该 tag 由 AppLanguage 的伴生解析器还原为语系枚举，或返回 null
    } // 结束获取

    private fun systemLanguage(context: Context): AppLanguage { // 用于探测底层 Android 当前所开机的原生系统偏好语言
        val configuration = context.resources.configuration // 提取主环境的 configuration 常量集
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 判断系统是否为 >= Android 7.0 
            configuration.locales[0] // 从 Nougat 提供的 Locale 堆列表队列里读取最优先首选的那个 Locale 对象
        } else { // 针对古玩系统老版本
            @Suppress("DEPRECATION") // 压制历史旧引用警告异常
            configuration.locale // 直接取底盘上承载的单一 locale
        } // 结束提取
        return AppLanguage.fromLocale(locale) // 转换为自研语系，以便上报和界面渲染
    } // 结束探测

    private fun kv(context: Context): MMKV { // 统一获取本地存储空间实例的私有封装方法
        initialize(context) // 保障首先在此会触发生存初始化，不发生 Null 指针事件
        return requireNotNull(MMKV.mmkvWithID(MMKV_ID)) { // 解包并返回 MMKV_ID 对应的空间，如果为空则报错
            "MMKV is not initialized. Call AppLocaleManager.initialize(context) in Application.onCreate()." // 在脱轨崩溃时向开发反馈此条信息
        } // 理智退出
    } // 获取 MMKV 实例结束

    private const val MMKV_ID = "dashcam_app_settings" // 整个 DashCam 系统存储设置属性的统一 MMKV 文件前缀 ID
    private const val KEY_LANGUAGE_TAG = "language_tag" // 存储多语言(ZH_CN/EN)标签 tag 的固定 key 值
} // 实体主单例结束
