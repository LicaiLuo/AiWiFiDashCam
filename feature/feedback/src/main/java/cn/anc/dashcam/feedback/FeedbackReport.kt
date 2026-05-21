package cn.anc.dashcam.feedback // 本类所处的反馈业务目录包路径

import java.io.File // 导入 Java 的 File 物理文件控制对象

data class FeedbackReport( // 声明一个保存单次运行报错和元环境数据包的 Kotlin 实体 data class 数据模型
    val description: String, // 对应由用户亲手打字写就的问题和缺陷意见说明 (经过了防泄露脱敏)
    val packageName: String, // 该应用程序唯一的签名的 Package 属性标
    val versionName: String, // 该应用当前被外界可读的产品描述版号，如 "1.0.0"
    val versionCode: Long, // 该应用提供给设备升级对比的唯一的的长整型编译代号，如 20260521
    val brand: String, // 根据宿主包名自动解析出来的产品 brand 缩写子标签名
    val device: String, // 通过 Build 环境反射出来的宿主硬件设备型号及生厂商明细，如 "Google Pixel 8 Pro"
    val androidVersion: String, // 当前客户端硬件下的 Android 软件系统的 API 等级及 Release 编码，如 "Android 14 (34)"
    val createdAtMillis: Long, // 生成本记录上报数据时的绝对物理毫秒时间戳，方便日志前后时序穿插校准
    val logFiles: List<File>, // 对应当前待采集被一同丢入 ZIP 压缩包的各种历史运行日志物理文件引用集 (最高上限 MAX_LOG_FILE_COUNT)
) // 实体数据结构声明完结
