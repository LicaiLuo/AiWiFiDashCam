package cn.anc.dashcam.core.common // 归属于设置及核心 common 的模块全局应用报名

import android.app.Application // 导入标准系统的 Application 全局上下文生命周期类
import cn.anc.dashcam.core.logging.AppLog // 导入自建安全且高性能的外部全局日志管理器。
import cn.anc.dashcam.core.logging.LogConfig // 导入自建日志相关属性与路径的配置文件对象
import cn.anc.dashcam.core.logging.LogLevel // 导入日志级别枚举，代表 Info，Error，Debug 等。

abstract class DashcamApplication : Application() { // 声明在业务中所有 Application 的公共虚拟抽象基类，继承自系统的 Application

    protected open val logDirectoryName: String = LogConfig.DEFAULT_LOG_DIRECTORY_NAME // 受保护可被子应用重写的日志在沙箱中的文件夹分类名称，默认引用 DEFAULT_LOG_DIRECTORY_NAME 为 "logs"

    protected open val fileMinLevel: LogLevel? = null // 受保护可被子类覆写的落盘物理日志文件的最低敏感级别，默认置位 null 代表走底层配置

    protected open val startupLogMessage: String // 声明一个受保护可重写的产品启动告白首词
        get() = "app started" // 默认返回字符串 "app started" 宣告生命开启

    override fun onCreate() { // 重写系统初始化 onCreate，它是在整个进程诞生拉起后，最早触发的第一个应用级逻辑
        super.onCreate() // 优先令父类执行常规的 context 附着装载
        AppLocaleManager.initialize(this) // 手动驱动并初始化 AppLocaleManager 语言配置管理器，此时腾讯其内部 MMKV 也确保初始化落盘
        AppLog.initialize(this, createLogConfig()) // 通过工厂生成的自定义配置，令安全日志 AppLog 启动运行
        AppLog.installCrashHandler() // 全面将进程的 Crash 未捕获异常处理器接替为 AppLog 的处理闭包，防止奔溃直接成废墟，并转储为脱敏物理文件
        AppLog.i(startupLogMessage, tag = "App") // 向物理磁盘中和控制台烙印一条带有 "App" 标签的启动通知日志
    } // onCreate 生命周期方法退出

    private fun createLogConfig(): LogConfig { // 内部用于动态装载生成更适合该宿主的日志属性元组的方法
        return LogConfig.createDefault( // 高等工厂模式创建默认的标准沙箱配置
            context = this, // 传递 Application 自身句柄
            logDirectoryName = logDirectoryName, // 绑上面受保护的文件夹名
        ).let { config -> // 使用作用域函数 let 进行链式修饰配置
            fileMinLevel?.let { config.copy(fileMinLevel = it) } ?: config // 如果用户指定了非空 fileMinLevel 则将其通过 config.copy 进行覆盖，否则无摩擦返回默认 config
        } // 离开 let 并返回生成的 LogConfig 实体
    } // createLogConfig 方法退栈
} // 车辆监控 DashcamApplication 基础类结束
