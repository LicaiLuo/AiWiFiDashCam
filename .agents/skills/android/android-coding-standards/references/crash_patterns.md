# Crash / ANR分析模式

分析 Android 崩溃、ANR、OOM 或生命周期问题时，按“特征识别 -> 应用帧定位 -> 根因 -> 最小修复 -> 测试建议”的顺序输出。

## 通用流程

1. 识别异常类型和关键日志：异常类名、ANR reason、进程名、线程名、包名帧、native backtrace
2. 先定位本项目包名帧：`cn.anc.dashcam`、品牌 app 模块、feature 模块、core 模块
3. 区分业务代码、Android framework、第三方库、系统限制和 native/JNI 层
4. 结合生命周期、线程、权限、Intent、资源释放、模块边界判断根因
5. 给出最小修复，不做无关重构
6. 说明需要补充的 unit test、instrumentation test 或手工验证点

## NPE / NullPointerException

特征：

- `java.lang.NullPointerException`
- `lateinit property ... has not been initialized`
- `requireActivity()`、`requireContext()`、`binding`、`intent.extras` 相关崩溃

常见根因：

- Fragment ViewBinding 在 `onDestroyView()` 后被访问
- Fragment detach 后调用 `requireActivity()` / `requireContext()`
- Intent、Bundle、Parcelable 字段没有处理 null
- 异步回调晚于页面销毁返回
- Compose 中把可空状态强转为非空

修复方向：

- Fragment 清空 `_binding`，异步回调更新 UI 前检查生命周期
- Intent/Bundle 参数入口处集中校验，失败时返回明确错误或关闭页面
- 用 sealed UI state 表达 Loading/Success/Error/Empty，不用 `!!`
- 对跨模块 public API 显式声明可空性

## ANR

特征：

- `ANR in <package>`
- `Input dispatching timed out`
- `Broadcast of Intent` timeout
- main thread stacktrace 停在 I/O、锁、Binder、数据库、Bitmap 解码或同步等待

分析步骤：

1. 先看 main thread stacktrace
2. 判断阻塞类型：文件/DB/SP I/O、锁等待、Binder 调用、网络、Bitmap 解码、同步等待协程/线程
3. 查是否发生在生命周期入口、BroadcastReceiver、启动页、Compose 首屏渲染路径
4. 给出把耗时操作移出主线程的最小改法

常见修复：

```kotlin
// SharedPreferences：不要在主线程 commit
prefs.edit().apply()

// 数据库/文件/解码：移到 IO 线程
withContext(Dispatchers.IO) {
    repository.write(value)
}
```

## OOM / OutOfMemoryError

特征：

- `OutOfMemoryError: Failed to allocate`
- 图片、视频帧、列表、缓存、native buffer、Bitmap 解码相关日志

常见根因：

- Bitmap 未采样直接加载原图
- 静态对象、单例或 ViewModel 持有 Activity/View/Context
- RecyclerView/Compose 列表中创建过多大对象
- native/JNI 层资源未释放
- Dashcam 场景中视频帧、缩略图、预览图缓存无上限

修复方向：

- 图片按目标尺寸采样，缓存设置上限
- UI 层不长期持有大对象，列表使用稳定 key 和分页/懒加载
- native 资源生命周期和 Android 生命周期对齐释放
- 对 DVR/预览/缩略图场景，优先复用 buffer，避免重复分配

## IllegalStateException

常见场景：

- `Fragment already added`
- `Can not perform this action after onSaveInstanceState`
- `ViewTreeLifecycleOwner not found`
- Compose 状态在错误阶段被读写
- RecyclerView ViewHolder attach 状态错误

修复方向：

- Fragment 事务避免重复 add，非关键场景谨慎使用 `commitAllowingStateLoss()`
- 后台状态不要执行关键 UI 事务，延迟到 `onResume`
- Compose 副作用放入正确 effect API，key 保持稳定
- 让 UI state 驱动渲染，不在渲染分支里直接修改状态

## ActivityNotFoundException / SecurityException

特征：

- `ActivityNotFoundException`
- `SecurityException`
- Intent、权限、导出组件、跨应用调用相关崩溃

常见根因：

- Manifest 未声明目标 Activity
- `android:exported`、权限、package visibility 配置不正确
- 使用隐式 Intent 但设备上无匹配组件
- Android 版本行为变更导致权限或后台启动受限

修复方向：

- 启动前检查目标路由或 resolve 结果
- 捕获具体异常并返回 `NavigationResult` 等明确结果类型
- Manifest 只开放必要组件，导出组件必须说明原因
- 按 Android 版本分支处理权限和系统限制

## Native / JNI崩溃

特征：

- `SIGSEGV`、`SIGABRT`、`Fatal signal`
- tombstone、native backtrace、JNI local reference、线程 attach/detach 相关日志

分析步骤：

1. 定位 so 名称和 native backtrace
2. 查 JNI 边界是否处理 null、异常、线程 attach/detach
3. 查 native 资源是否与 Android 生命周期对齐释放
4. 修改 native 代码时同时使用 `cpp-coding-standards`

修复方向：

- JNI 入参必须显式校验
- native 错误转换为 Kotlin 明确结果，不静默吞掉
- 释放函数幂等，重复释放安全返回
- 对跨线程回调，明确线程切换和生命周期取消
