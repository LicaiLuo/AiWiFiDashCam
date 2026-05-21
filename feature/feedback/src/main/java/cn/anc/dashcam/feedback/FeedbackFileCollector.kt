package cn.anc.dashcam.feedback // 本类所处的反馈逻辑包路径

import android.content.Context // 导入系统的 Context 上下文引用
import android.content.pm.PackageInfo // 导入系统的 PackageInfo 安装包信息类
import android.content.pm.PackageManager // 导入系统的 PackageManager 安装包管理器类
import android.os.Build // 导入系统的 Build 硬件和版本信息类
import cn.anc.dashcam.core.logging.AppLog // 导入自建日志管理类
import cn.anc.dashcam.core.logging.LogSanitizer // 导入自建日志脱敏和内容过滤处理器
import java.io.File // 导入 Java IO 的 File 文件夹/文件对象
import java.text.SimpleDateFormat // 导入 SimpleDateFormat 格式化时间辅助类
import java.util.Date // 导入 Java 标的 Date 时间源类
import java.util.Locale // 导入 Locale 地区环境语言句柄
import java.util.zip.ZipEntry // 导入 ZipEntry 压缩单元实例类
import java.util.zip.ZipOutputStream // 导入 ZipOutputStream 压缩打包写出流

class FeedbackFileCollector( // 定义反馈日志收集收集器类开始
    context: Context, // 接收 Context 上下文参数以访问应用沙盒和包管理器
) { // 类体开始

    private val appContext = context.applicationContext // 将 context 安全包装，预防其在 activity 终结时引发内存泄漏
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US) // 创建用于保存生成的 zip 包时命名的时间格式化工具
    private val displayTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US) // 创建用于在文档中美化时间毫秒戳展示的格式化工具

    fun createFeedbackPackage(description: String): File { // 核心方法：构建整个反馈日志打包任务
        val report = createReport(description) // 创建包含了具体脱敏用户描述及环境数据的报表对象
        val feedbackDir = feedbackDirectory() // 解析获得当前的反馈打包储存沙盒分区目录
        feedbackDir.mkdirs() // 如果该反馈本地文件夹不存在，则立即递归创建所有父目录级目录
 
        // 构造专场反馈 ZIP 类文件存储地址
        val feedbackFile = File(feedbackDir, "feedback-${fileNameFormat.format(Date(report.createdAtMillis))}.zip") // 绑定新 Zip 本地指针
        ZipOutputStream(feedbackFile.outputStream().buffered()).use { zip -> // 用带高性能缓冲区的流开启一个 ZIP 压缩写出器在 use 流作用域中
            zip.writestr("metadata.txt", buildMetadata(report)) // 自定义封装流方法：将构造好的元属性文件写入 ZIP 内部 metadata 主干
            report.logFiles.forEach { file -> // 轮询遍历拉取到的 MAX_LOG_FILE_COUNT 个日志源文件
                if (file.exists() && file.isFile) { // 如果判断日志文件在沙箱中真正物理存在、且为常规单体文件
                    val sanitizedText = LogSanitizer.sanitize(file.readText(Charsets.UTF_8)) // 精确脱敏：读出日志字符串后进行字符隐秘脱敏
                    zip.writestr("logs/${file.name}", sanitizedText) // 将经脱敏修正后的文本打包写入 logs 子路径
                } // 结束校验
            } // 轮询结束
        } // 离开 closeable 流作用域自动关闭包裹
 
        AppLog.i("feedback package created path=${feedbackFile.absolutePath}", tag = "Feedback") // 控制台输出反馈包压缩创建详情日志
        return feedbackFile // 返还已经精美处理的打包物理文件以备上传或呈现
    } // 结束打包流程

    fun feedbackDirectory(): File { // 获取反馈模块工作沙箱区域
        return appContext.getExternalFilesDir("feedback") ?: File(appContext.filesDir, "feedback") // 智能返回外置SD卡专属目录或保底沙盒 files 内部反馈目录
    } // 方法退出

    private fun createReport(description: String): FeedbackReport { // 内部封装数据环境实体报表的方法
        val packageInfo = appContext.packageInfo() // 工具扩展方法：安全获取当前 APP 对应版本的 package 元信息
        val packageName = appContext.packageName // 获取包签名包全名路径

        return FeedbackReport( // 返回构造出的完整数据模型
            description = LogSanitizer.sanitize(description), // 将用户可能含有关键路径、IP密码或隐私行为的输入进行彻底脱敏清理
            packageName = packageName, // 注入宿主包名
            versionName = packageInfo.versionName.orEmpty(), // 注入宿主当前的对外可读版本号字符
            versionCode = packageInfo.versionCodeCompat(), // 注入高版本与低版本兼容处理之后的长整型 versionCode
            brand = packageName.substringAfterLast('.', missingDelimiterValue = packageName), // 从包名切除其后段子名作为产品 brand 软标签
            device = "${Build.MANUFACTURER} ${Build.MODEL}", // 反射采集用户当前的硬件厂牌与精确模组型号
            androidVersion = "${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})", // 读取宿主所在的 Android OS 对外版号和 API 常量等级
            createdAtMillis = System.currentTimeMillis(), // 捕获当前的时钟绝对物理世界时间戳
            logFiles = AppLog.getLogFiles().take(MAX_LOG_FILE_COUNT), // 切割式采集底层的最新数个物理文件加入轮询
        ) // 承载实体构造结束
    } // 结束报表构造

    private fun buildMetadata(report: FeedbackReport): String { // 内部辅助：将报表数据格式整齐输出为元属性配置文件
        return buildString { // 用 StringBuilder 的包裹块
            appendLine("created=${displayTimeFormat.format(Date(report.createdAtMillis))}") // 追写格式化创建时间
            appendLine("package=${report.packageName}") // 追写包全名
            appendLine("brand=${report.brand}") // 追写提取出的产品 brand 软标签
            appendLine("versionName=${report.versionName}") // 追写当前版本号
            appendLine("versionCode=${report.versionCode}") // 追写当前版本标识码
            appendLine("device=${report.device}") // 追写硬件终端明细
            appendLine("android=${report.androidVersion}") // 追写宿主系统的 API 版本信息
            appendLine("logDirectory=${AppLog.getLogDirectory().absolutePath}") // 追写日志归档文件沙盒
            appendLine("description=${report.description}") // 最终追加用户的说明内容文本
        } // 结束构造串并回传
    } // 方法退出

    private fun Context.packageInfo(): PackageInfo { // 扩展辅助：为 Context 提供安全的 package 获取方法 (针对各阶层 API 兼容)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // 比对当前系统是否大于等于 Android 13
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L)) // 通过 13+ 加强版 Flag 配置检索 package
        } else { // 针对 13 以下历史经典旧系统
            @Suppress("DEPRECATION") // 压制废弃接口警告
            packageManager.getPackageInfo(packageName, 0) // 进行基础的数字 Flag 实例化 package 读取
        } // 结束分支
    } // 回传 package 结束

    private fun PackageInfo.versionCodeCompat(): Long { // 扩展辅助：为 PackageInfo 提供新旧版本代码兼容的长整型 versionCode 换算
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // 判断运行环境平台是否大于等 Android 9 (Pie)
            longVersionCode // 取新式的长整型 longVersionCode
        } else { // 针对 Android 9 以下旧版
            @Suppress("DEPRECATION") // 遮盖陈旧接口调警告
            versionCode.toLong() // 从旧整型 versionCode 取得其值并向上安全转换为长整型 (Long) 回传
        } // 结束分支
    } // 回传 code

    private fun ZipOutputStream.writestr(name: String, value: String) { // 为 ZipOutputStream 扩展一个快捷的字符直接写入 entry 二进制管道方法
        putNextEntry(ZipEntry(name)) // 将指定文件名称压入 Zip 流管道作为一个新的 ZipEntry 文件目录头
        write(value.toByteArray(Charsets.UTF_8)) // 把待写 String 强制以 UTF-8 二进制数组灌入压缩压缩管道中
        closeEntry() // 优雅关闭当前压入的 ZipEntry
    } // 方法返回

    companion object { // 安全的静态区
        private const val MAX_LOG_FILE_COUNT = 8 // 定义反馈包所包含的最深限制日志文件数量为 8 个
    } // 结束静态定义
} // 类终点

