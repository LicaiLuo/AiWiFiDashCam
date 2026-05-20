# 日志功能需求文档

## 功能总结

日志功能提供统一的 `AppLog` 入口，负责控制台输出、沙盒文件落盘、日志级别过滤、敏感信息脱敏、文件轮转和崩溃日志写入。默认日志目录为应用沙盒 `Android/data/<packageName>/files/logs/`，各品牌包按包名天然隔离。

反馈功能提供本地反馈包生成能力，负责收集用户描述、App 信息、设备信息、最近日志和最近崩溃日志，脱敏后压缩为 zip 文件。默认反馈包目录为应用沙盒 `Android/data/<packageName>/files/feedback/`，本阶段只生成本地反馈包，不做无用户确认的自动上传。

当前实现落点：

- `core:logging`：统一日志入口、日志配置、脱敏、文件写入、文件轮转、崩溃日志。
- `feature:feedback`：反馈页面、反馈包生成、日志文件收集和 zip 打包。
- `prido`、`uniden`：品牌 App 壳初始化日志和崩溃处理器。
- `feature:home`：通过现有路由系统打开 `feedback` 页面。

## 1. 背景

当前项目是多品牌 Android 应用，`prido`、`uniden` 等品牌 App 共享 core 和 feature 代码。为支持开发调试、测试排查、线上问题反馈和崩溃定位，需要建立统一日志能力。

日志系统需要满足：

- 开发阶段可快速看到完整日志。
- 测试阶段可动态调整日志级别，便于复现问题。
- 线上版本默认只保留重要日志，避免性能、隐私和存储风险。
- 问题反馈功能可以自动收集本地日志、设备信息和 App 信息。
- 多品牌 App 使用同一套日志代码，但日志配置和日志文件按包名隔离。

## 2. 目标

### 2.1 功能目标

- 提供统一日志入口，业务代码不直接使用 `Log`、`println()`、`Timber`。
- 支持日志级别过滤：`VERBOSE`、`DEBUG`、`INFO`、`WARN`、`ERROR`、`FATAL`。
- 支持控制台输出、文件写入、反馈打包三类用途。
- 支持按构建类型控制日志策略。
- 支持按应用包名隔离日志目录和配置。
- 支持日志文件轮转、数量限制、过期清理。
- 支持问题反馈时打包最近日志。
- 支持崩溃时写入最后一次崩溃日志。

### 2.2 非目标

- 本阶段不实现完整远程日志平台。
- 本阶段不做无用户确认的后台自动上传。
- 本阶段不采集用户隐私数据、音视频内容、定位轨迹、通讯录等敏感信息。
- 本阶段不替代第三方 Crash 平台；后续可接入 Crashlytics、Sentry 或自研平台。

## 3. 默认日志路径

日志默认写入应用沙盒目录：

```text
/storage/emulated/0/Android/data/<packageName>/files/logs/
```

代码中应通过 Android API 获取，不允许手写绝对路径：

```kotlin
context.getExternalFilesDir("logs")
```

如果外部应用专属目录不可用，则降级到内部沙盒目录：

```kotlin
File(context.filesDir, "logs")
```

路径要求：

- 每个应用包名天然隔离，例如 `cn.anc.dashcam.prido` 和 `cn.anc.dashcam.uniden` 使用各自目录。
- 不使用 `Environment.getExternalStorageDirectory()`。
- 不申请 `READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE` 或 `MANAGE_EXTERNAL_STORAGE` 来写日志。
- 问题反馈只读取本应用自己的日志目录。

## 4. 模块规划

建议新增独立日志模块，避免 `core/common` 变成重模块：

```text
core/
├── logging/
│   ├── AppLog.kt
│   ├── AppLogger.kt
│   ├── LogLevel.kt
│   ├── LogConfig.kt
│   ├── LogPolicy.kt
│   ├── LogFileManager.kt
│   ├── LogSanitizer.kt
│   └── CrashLogWriter.kt
├── common/
├── model/
├── data/
├── navigation/
└── ui/

feature/
└── feedback/
    ├── FeedbackManager.kt
    ├── FeedbackReport.kt
    ├── FeedbackScreen.kt
    ├── FeedbackFileCollector.kt
    └── FeedbackApi.kt
```

依赖方向：

```text
brand app -> feature:feedback -> core:logging
brand app -> feature:* -> core:*
core:logging -> core:common，可选
core:* 不依赖 feature:* 或 brand app
```

如果当前阶段不想新增 `core:logging`，可以临时放在 `core:common`，但文档和代码应保留后续迁移到 `core:logging` 的边界。

## 5. 构建类型策略

日志策略由“构建类型 + 运行时配置 + 日志级别”共同决定。

| 场景 | 控制台输出 | 文件写入 | 可动态调级 | 反馈收集 |
| --- | --- | --- | --- | --- |
| Debug | 全部日志 | DEBUG 及以上，或全量可配置 | 是 | 是 |
| 测试包 | INFO 及以上，支持开关 | WARN 及以上，支持临时 DEBUG | 是 | 是 |
| Release | 默认关闭 DEBUG/INFO | WARN 及以上 | 受控开关 | 是 |

注意：

- 不建议在 `core` 模块直接依赖自身 `BuildConfig.DEBUG` 判断 App 类型。
- App 壳模块应在初始化日志时传入 `LogConfig`，或通过 manifest metadata / assets 配置声明当前日志策略。
- 线上包打开 DEBUG 日志必须是临时、可关闭、可过期的行为。

## 6. 日志级别

| 级别 | 用途 | Debug | 测试包 | Release | 默认写文件 |
| --- | --- | --- | --- | --- | --- |
| VERBOSE | 极细粒度流程 | 可输出 | 默认不输出 | 不输出 | 否 |
| DEBUG | 开发调试 | 输出 | 可配置 | 不输出 | 否 |
| INFO | 关键业务流程 | 输出 | 输出 | 默认不输出 | 否 |
| WARN | 可恢复异常或风险 | 输出 | 输出 | 输出 | 是 |
| ERROR | 功能失败 | 输出 | 输出 | 输出 | 是 |
| FATAL | 崩溃或不可恢复错误 | 输出 | 输出 | 输出 | 是 |

日志内容要求：

- 日志必须能表达“发生了什么、在哪个模块、关键上下文是什么”。
- 禁止记录密码、token、完整手机号、完整车牌号、完整设备 SN、GPS 精确坐标、Wi-Fi 密码、接口请求 body。
- URL 默认去掉 query，确需保留时必须先脱敏。
- 错误日志应携带异常对象，不能只写字符串。

## 7. 日志文件规则

### 7.1 文件命名

建议格式：

```text
app-YYYY-MM-DD.log
crash-YYYY-MM-DD-HH-mm-ss.log
feedback-YYYY-MM-DD-HH-mm-ss.zip
```

### 7.2 文件轮转

默认策略：

- 单个普通日志文件最大 `2MB`。
- 最多保留 `5` 个普通日志文件。
- 默认保留 `7` 天。
- Debug 包可放宽到单文件 `5MB`、最多 `10` 个文件、保留 `3` 天。
- 超过大小时新建滚动文件，不允许直接清空当前文件。

### 7.3 写入策略

- 文件写入必须在后台线程执行。
- 日志写入失败不能影响业务流程。
- 崩溃场景可以同步 flush 最后一段日志，但要设置超时，避免二次卡死。
- 高频日志需要采样或降级，避免影响首屏、录像、预览等性能敏感路径。

## 8. 统一日志 API

业务侧只允许使用统一入口：

```kotlin
AppLog.d("message")
AppLog.i("message")
AppLog.w("message", throwable = error)
AppLog.e("message", throwable = error)
AppLog.f("message", throwable = error)
```

建议扩展能力：

```kotlin
AppLog.event("feedback_open")
AppLog.network(method = "GET", url = url, statusCode = code, durationMs = duration)
AppLog.measure("home_load") {
    // block
}
```

调用要求：

- 不在业务代码中直接 `Timber.plant()`。
- 不直接使用 `println()`、`printStackTrace()`。
- 不在日志 message 中拼接未脱敏的敏感字段。
- 捕获异常后用 `throwable = error` 参数传入。

## 9. 隐私与脱敏

必须提供 `LogSanitizer`，所有落盘和反馈上传前日志都经过脱敏。

默认脱敏字段：

- password、pwd、token、access_token、refresh_token、authorization
- phone、mobile、email
- ssid、wifiPassword
- vin、sn、deviceId、imei、mac
- plate、licensePlate
- latitude、longitude、gps

脱敏规则：

- token 类字段全部替换为 `***`。
- 手机号、设备号只保留前后少量字符。
- URL 去掉 query 或只保留白名单 query。
- Throwable stacktrace 可以保留，但 message 也要经过脱敏。

## 10. 问题反馈集成

问题反馈由 `feature:feedback` 提供，负责收集并打包：

- 用户填写的问题描述。
- App 包名、版本名、版本号、品牌、构建类型。
- Android 版本、设备厂商、设备型号、可用存储、内存摘要。
- 最近日志文件。
- 最近崩溃日志。
- 可选截图，必须用户确认。

反馈包要求：

- 默认从 `Android/data/<packageName>/files/logs/` 收集。
- 打包前再次执行脱敏。
- 反馈 zip 最大默认 `10MB`，超过后只保留最近和最高级别日志。
- 上传前必须用户确认。
- 上传失败应保留本地反馈包，并允许用户重试或删除。

## 11. 崩溃日志

崩溃处理要求：

- 初始化时保存原始 `UncaughtExceptionHandler`。
- 自定义 handler 写入崩溃日志后，必须继续交给原 handler。
- 避免递归调用当前 handler。
- 崩溃日志写入要尽量短路径，不做复杂网络上传。

正确流程：

```kotlin
val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
    AppLog.f("application crash", throwable = throwable)
    CrashLogWriter.write(throwable)
    previousHandler?.uncaughtException(thread, throwable)
}
```

## 12. 多品牌支持

多品牌 App 使用同一套日志代码，但配置和文件自然按包名隔离。

要求：

- `prido`、`uniden` 等 App 壳模块都要初始化日志系统。
- 初始化配置由 App 壳传入，不能在 `core` 中硬编码品牌。
- 日志文件路径必须使用当前 `context.packageName`。
- 问题反馈报告中必须包含 `packageName` 和品牌标识。

## 13. 配置来源

优先级从高到低：

1. 测试工具或设置页临时配置。
2. 远程配置，后续可选。
3. App 壳模块传入的 `LogConfig`。
4. manifest metadata 或 assets 默认配置。
5. 日志模块内置默认值。

配置项建议：

```text
enabled
consoleMinLevel
fileMinLevel
maxFileSizeBytes
maxFileCount
retentionDays
allowFeedbackUpload
sanitizeEnabled
temporaryDebugUntil
```

## 14. 测试要求

必须覆盖：

- 不同日志级别过滤是否符合预期。
- Debug、测试包、Release 默认策略是否符合预期。
- 日志路径是否位于 `Android/data/<packageName>/files/logs/`。
- 外部 files dir 不可用时是否降级到内部目录。
- 文件轮转、数量限制、过期清理是否正确。
- 反馈包是否包含必要信息。
- 反馈包是否执行脱敏。
- 崩溃 handler 是否不会递归。
- 关闭日志后是否停止控制台输出和文件写入。
- 重新开启日志后是否恢复，不重复安装 Timber Tree。

涉及 Gradle 构建、单元测试或 instrumentation test 时，必须先获得用户明确授权。

## 15. 验收标准

- App 启动后日志系统完成初始化，不影响启动流程。
- Debug 包可以看到 DEBUG 及以上日志。
- Release 包默认只落盘 WARN、ERROR、FATAL。
- 日志文件默认生成在应用沙盒 `Android/data/<packageName>/files/logs/`。
- 问题反馈可以打包最近日志并生成反馈 zip。
- 反馈 zip 不包含明文敏感信息。
- 崩溃后能生成 crash 日志。
- 多品牌包生成各自独立日志目录。
- 未授权时不执行任何 Gradle 命令。

## 16. 实施步骤

1. 新增或确认日志模块边界：优先 `core:logging`，临时可放 `core:common`。
2. 定义 `LogLevel`、`LogConfig`、`AppLogger`、`AppLog`。
3. 实现 `LogFileManager`，包含路径、写入、轮转、清理。
4. 实现 `LogSanitizer`，所有落盘和反馈打包前脱敏。
5. 在 `prido`、`uniden` App 壳初始化日志。
6. 实现 `feature:feedback` 的日志收集与 zip 打包。
7. 接入崩溃日志写入。
8. 替换业务代码中的直接日志调用。
9. 补充测试和手工验证清单。
