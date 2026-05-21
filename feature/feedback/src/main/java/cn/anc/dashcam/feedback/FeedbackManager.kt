package cn.anc.dashcam.feedback // 反包下的功能目录路径

import android.content.Context // 导入系统 Context 句柄类
import cn.anc.dashcam.core.logging.AppLog // 导入自建全局系统写日志记录器目类
import java.io.File // 导入 Java IO 的物理文件对象类

class FeedbackManager( // 问题反馈总管理员代理类开始定义
    context: Context, // 构造函数：注入所需的系统上下文环境
) { // 类体大框架

    private val collector = FeedbackFileCollector(context) // 在内部实例化专用的反馈沙箱物理文件收集组包引擎 collector

    fun createFeedbackPackage(description: String): Result<File> { // 外部入口：执行抓取打包，返还 Result 包装的 File 对象
        return runCatching { // 通过安全沙盒捕获可能会发生的任何磁盘异常
            collector.createFeedbackPackage(description) // 让文件组包 collector 执行核心日志抽取与元文件打包，返还生成的 Zip 物理文件
        }.onFailure { error -> // 倘若在此沙盒内触发了不可收拾的磁盘权限、容量满溢、文件损坏等
            AppLog.e("create feedback package failed", throwable = error, tag = "Feedback") // 向日志框架打印一个严重报错描述、及附带的堆栈指针，tag 置为 Feedback
        } // 链式处理和捕获返回 Result 容器
    } // 打包反馈入口方法退出
} // 主代理类定义结束
