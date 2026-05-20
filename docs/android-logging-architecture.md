# Android 日志架构实现示例

本文档提供 Android 日志架构的完整实现示例。

## 1. Logger 接口实现

### AppLogger.kt

```kotlin
package com.dashcam.core.logging

import android.content.Context
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 统一日志接口
 */
interface AppLogger {
    /**
     * 调试日志
     */
    fun d(message: String, vararg args: Any?, tag: String? = null)
    
    /**
     * 信息日志
     */
    fun i(message: String, vararg args: Any?, tag: String? = null)
    
    /**
     * 警告日志
     */
    fun w(message: String, vararg args: Any?, tag: String? = null, throwable: Throwable? = null)
    
    /**
     * 错误日志
     */
    fun e(message: String, vararg args: Any?, tag: String? = null, throwable: Throwable? = null)
    
    /**
     * 性能监控
     */
    fun <T> measureTime(tag: String, block: () -> T): T
    
    /**
     * 网络请求日志
     */
    fun logNetworkRequest(
        url: String,
        method: String,
        statusCode: Int,
        duration: Long,
        success: Boolean
    )
    
    /**
     * 崩溃日志
     */
    fun logCrash(throwable: Throwable, context: Context? = null)
    
    /**
     * 用户行为日志
     */
    fun logUserAction(action: String, params: Map<String, Any>? = null)
}
```

### TimberLogger.kt

```kotlin
package com.dashcam.core.logging

import android.os.Build
import timber.log.Timber

/**
 * 基于 Timber 的日志实现
 */
class TimberLogger : AppLogger {
    
    override fun d(message: String, vararg args: Any?, tag: String?) {
        if (BuildConfig.DEBUG) {
            Timber.tag(tag).d(message, *args.orEmpty())
        }
    }
    
    override fun i(message: String, vararg args: Any?, tag: String?) {
        Timber.tag(tag).i(message, *args.orEmpty())
    }
    
    override fun w(message: String, vararg args: Any?, tag: String?, throwable: Throwable?) {
        Timber.tag(tag).w(throwable, message, *args.orEmpty())
    }
    
    override fun e(message: String, vararg args: Any?, tag: String?, throwable: Throwable?) {
        Timber.tag(tag).e(throwable, message, *args.orEmpty())
    }
    
    override fun <T> measureTime(tag: String, block: () -> T): T {
        val startTime = System.currentTimeMillis()
        val result = block()
        val duration = System.currentTimeMillis() - startTime
        Timber.d("[$tag] Execution time: ${duration}ms")
        return result
    }
    
    override fun logNetworkRequest(
        url: String,
        method: String,
        statusCode: Int,
        duration: Long,
        success: Boolean
    ) {
        val status = if (success) "SUCCESS" else "FAILED"
        Timber.i("Network: $method $url -> $statusCode (${duration}ms) [$status]")
    }
    
    override fun logCrash(throwable: Throwable, context: Context?) {
        Timber.e(throwable, "Application crash detected")
        // 通知崩溃收集服务
        CrashCollector.collectCrash(throwable, context)
    }
    
    override fun logUserAction(action: String, params: Map<String, Any>?) {
        val paramsStr = params?.toString() ?: ""
        Timber.i("User Action: $action $paramsStr")
    }
}
```

## 2. Timber 树实现

### DebugTree.kt

```kotlin
package com.dashcam.core.logging

import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Debug 模式的日志树
 */
class DebugTree : Timber.DebugTree() {
    
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // 添加时间戳
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val formattedMessage = "[$timestamp] $tag: $message"
        
        super.log(priority, tag, formattedMessage, t)
        
        // 同时写入文件
        FileLogger.write(formattedMessage, priority)
    }
}
```

### ReleaseTree.kt

```kotlin
package com.dashcam.core.logging

import android.os.Build
import timber.log.Timber
import com.dashcam.core.network.UploadService

/**
 * Release 模式的日志树
 */
class ReleaseTree : Timber.Tree() {
    
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // 只记录 WARNING 和 ERROR 级别
        if (priority >= Log.WARN) {
            super.log(priority, tag, message, t)
            
            // 上传到服务器
            if (priority >= Log.ERROR) {
                UploadService.uploadLog(tag, message, t)
            }
        }
    }
}
```

### FileLogger.kt

```kotlin
package com.dashcam.core.logging

import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 文件日志工具
 */
object FileLogger {
    
    private const val LOG_DIR = "logs"
    private const val LOG_FILE = "app.log"
    private const val MAX_FILE_SIZE = 1024 * 1024 // 1MB
    
    fun write(message: String, priority: Int) {
        try {
            val logDir = File(getExternalStorageDirectory(), LOG_DIR)
            if (!logDir.exists()) {
                logDir.mkdirs()
            }
            
            val logFile = File(logDir, LOG_FILE)
            
            // 检查文件大小，超过则清空
            if (logFile.exists() && logFile.length() > MAX_FILE_SIZE) {
                logFile.writeText("")
            }
            
            // 追加日志
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
            val priorityStr = when (priority) {
                Log.VERBOSE -> "V"
                Log.DEBUG -> "D"
                Log.INFO -> "I"
                Log.WARN -> "W"
                Log.ERROR -> "E"
                else -> "?"
            }
            
            FileWriter(logFile, true).use { writer ->
                writer.append("$timestamp [$priorityStr] $message\n")
            }
        } catch (e: IOException) {
            Log.e("FileLogger", "Failed to write log", e)
        }
    }
    
    private fun getExternalStorageDirectory(): File? {
        return android.os.Environment.getExternalStorageDirectory()
    }
}
```

## 3. Crashlytics 实现

### CrashCollector.kt

```kotlin
package com.dashcam.core.logging

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.os.StrictMode
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 崩溃收集器
 */
object CrashCollector {
    
    private var crashHandler: Thread.UncaughtExceptionHandler? = null
    private val crashLogsDir = File(android.os.Environment.getExternalStorageDirectory(), "crash_logs")
    
    fun init(context: Context) {
        // 设置全局异常处理器
        crashHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            collectCrash(throwable, context)
        }
        
        // 注册 Activity 生命周期回调
        if (context is Application) {
            (context as Application).registerActivityLifecycleCallbacks(
                object : Application.ActivityLifecycleCallbacks() {
                    override fun onActivityDestroyed(activity: android.app.Activity) {
                        // Activity 销毁时检查是否有未处理的异常
                    }
                }
            )
        }
    }
    
    fun collectCrash(throwable: Throwable, context: Context?) {
        val crashInfo = CrashInfo(
            throwable = throwable,
            timestamp = System.currentTimeMillis(),
            deviceInfo = getDeviceInfo(),
            appState = getAppState(context),
            threadInfo = getThreadInfo()
        )
        
        // 保存到本地
        saveCrashLocally(crashInfo)
        
        // 上传到服务器
        uploadCrashToServer(crashInfo)
        
        // 可选：集成 Crashlytics
        // Crashlytics.getInstance().recordException(throwable)
    }
    
    private fun saveCrashLocally(crashInfo: CrashInfo) {
        try {
            if (!crashLogsDir.exists()) {
                crashLogsDir.mkdirs()
            }
            
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS", Locale.getDefault()).format(Date(crashInfo.timestamp))
            val crashFile = File(crashLogsDir, "crash_$timestamp.log")
            
            FileWriter(crashFile).use { writer ->
                writer.append("=== CRASH REPORT ===\n")
                writer.append("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date(crashInfo.timestamp))}\n")
                writer.append("Device: ${crashInfo.deviceInfo.manufacturer} ${crashInfo.deviceInfo.model}\n")
                writer.append("Android: ${crashInfo.deviceInfo.androidVersion} (SDK ${crashInfo.deviceInfo.sdkVersion})\n")
                writer.append("App: ${crashInfo.deviceInfo.appVersion}\n")
                writer.append("Thread: ${crashInfo.threadInfo.threadName} (ID: ${crashInfo.threadInfo.threadId})\n")
                writer.append("Exception: ${crashInfo.throwable.javaClass.name}: ${crashInfo.throwable.message}\n")
                writer.append("Stack Trace:\n")
                writer.append(crashInfo.throwable.stackTraceToString.joinToString("\n") { "    at $it" })
                writer.append("\n")
            }
        } catch (e: IOException) {
            Timber.e("CrashCollector", "Failed to save crash locally", e)
        }
    }
    
    private fun uploadCrashToServer(crashInfo: CrashInfo) {
        // TODO: 实现服务器上传逻辑
        Timber.i("CrashCollector", "Crash report ready for upload: ${crashInfo.throwable.message}")
    }
    
    private fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            androidVersion = Build.VERSION.RELEASE,
            appVersion = BuildConfig.VERSION_NAME,
            sdkVersion = Build.VERSION.SDK_INT
        )
    }
    
    private fun getAppState(context: Context?): AppState {
        return AppState(
            currentActivity = context?.getCurrentActivityName(),
            memoryUsage = getMemoryUsage(),
            diskUsage = getDiskUsage()
        )
    }
    
    private fun getThreadInfo(): ThreadInfo {
        return ThreadInfo(
            threadName = Thread.currentThread().name,
            threadId = Thread.currentThread().id,
            stackTrace = Thread.currentThread().stackTraceToString()
        )
    }
    
    private fun getCurrentActivityName(context: Context): String? {
        // TODO: 实现获取当前 Activity 名称的逻辑
        return null
    }
    
    private fun getMemoryUsage(): Long {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        return usedMemory
    }
    
    private fun getDiskUsage(): Long {
        val externalStorage = android.os.Environment.getExternalStorageDirectory()
        return if (externalStorage != null) {
            externalStorage.freeSpace
        } else {
            0L
        }
    }
}

/**
 * 崩溃信息数据类
 */
data class CrashInfo(
    val throwable: Throwable,
    val timestamp: Long,
    val deviceInfo: DeviceInfo,
    val appState: AppState,
    val threadInfo: ThreadInfo
)

/**
 * 设备信息
 */
data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val appVersion: String,
    val sdkVersion: Int
)

/**
 * 应用状态
 */
data class AppState(
    val currentActivity: String?,
    val memoryUsage: Long,
    val diskUsage: Long
)

/**
 * 线程信息
 */
data class ThreadInfo(
    val threadName: String,
    val threadId: Long,
    val stackTrace: String
)
```

## 4. 应用初始化

### App.kt

```kotlin
package com.dashcam

import android.app.Application
import android.os.StrictMode
import com.dashcam.core.logging.CrashCollector
import com.dashcam.core.logging.DebugTree
import com.dashcam.core.logging.ReleaseTree
import com.dashcam.core.logging.TimberLogger
import timber.log.Timber

/**
 * 应用主类
 */
class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 启用严格模式
        enableStrictMode()
        
        // 初始化 Timber
        if (BuildConfig.DEBUG) {
            // Debug 模式：使用 DebugTree，支持多标签
            Timber.plant(DebugTree())
        } else {
            // Release 模式：使用 ReleaseTree，支持崩溃上报
            Timber.plant(ReleaseTree())
        }
        
        // 初始化自定义 Logger
        TimberLogger.init()
        
        // 初始化崩溃收集器
        CrashCollector.init(this)
    }
    
    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build())
        
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build())
    }
}
```

## 5. 使用示例

### 在 ViewModel 中使用

```kotlin
class HomeViewModel(
    private val repository: HomeRepository,
    private val logger: AppLogger
) : ViewModel() {
    
    fun loadData() {
        logger.measureTime("HomeViewModel.loadData") {
            val result = repository.fetchData()
            logger.i("Data loaded successfully", result)
            result
        }
    }
    
    fun handleError(error: Throwable) {
        logger.e("Failed to load data", error)
        logger.logCrash(error)
    }
}
```

### 在 Repository 中使用

```kotlin
class HomeRepository(
    private val remoteDataSource: RemoteDataSource,
    private val logger: AppLogger
) {
    
    suspend fun fetchData(): Result<Data> {
        return try {
            val startTime = System.currentTimeMillis()
            val result = remoteDataSource.fetchData()
            val duration = System.currentTimeMillis() - startTime
            
            logger.logNetworkRequest(
                url = "https://api.example.com/data",
                method = "GET",
                statusCode = 200,
                duration = duration,
                success = true
            )
            
            Result.success(result)
        } catch (e: Exception) {
            logger.e("Network request failed", e)
            Result.failure(e)
        }
    }
}
```

### 在 Composable 中使用

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    logger: AppLogger
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        logger.logUserAction("screen_view", mapOf("screen" to "home"))
        viewModel.loadData()
    }
    
    when (state) {
        is HomeUiState.Loading -> CircularProgressIndicator()
        is HomeUiState.Success -> ContentScreen(state.data)
        is HomeUiState.Error -> ErrorScreen(state.error)
    }
}
```

## 6. 依赖配置

### `build.gradle.kts`

```kotlin
dependencies {
    // Timber 日志库
    implementation(libs.timber)
    
    // WorkManager 用于后台上传
    implementation(libs.androidx.work.runtime.ktx)
    
    // Crashlytics (可选，用于崩溃收集)
    // implementation(libs.crashlytics)
}
```

### `libs.versions.toml`

```toml
[versions]
timber = "5.0.1"
work-runtime-ktx = "2.9.1"
# crashlytics = "2.18.0"

[libraries]
timber = { group = "com.jakewharton.timber", name = "timber", version.ref = "timber" }
androidx-work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "work-runtime-ktx" }
# crashlytics = { group = "com.crashlytics.android", name = "crashlytics", version.ref = "crashlytics" }
```

## 7. 权限配置

### `AndroidManifest.xml`

```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    
    <application
        android:name=".App"
        android:networkSecurityConfig="@xml/network_security_config">
        
        <!-- WorkManager 初始化 -->
        <provider
            android:name="androidx.startup.InitializationProvider"
            android:authorities="${applicationId}"
            android:exported="false" />
    </application>
</manifest>
```

### `network_security_config.xml`

```xml
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="@raw/certificates"/>
        </trust-anchors>
    </base-config>
</network-security-config>
```

## 8. 最佳实践

### 日志级别使用

- **DEBUG**: 开发调试信息，只在 Debug 模式输出
- **INFO**: 重要信息，如网络请求、用户操作
- **WARN**: 警告信息，如降级操作、兼容性问题
- **ERROR**: 错误信息，如异常、失败操作
- **VERBOSE**: 详细调试信息，只在 Debug 模式输出

### 性能监控

- 使用 `measureTime` 监控关键方法的执行时间
- 记录网络请求的耗时和状态
- 监控内存和磁盘使用情况

### 崩溃处理

- 自动捕获未处理异常
- 收集完整的崩溃堆栈信息
- 记录设备信息和应用状态
- 及时上传崩溃报告

### 隐私保护

- Release 模式不输出敏感信息
- 日志文件大小限制，避免占用过多存储
