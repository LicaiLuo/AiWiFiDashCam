---
name: android-coding-standards
description: Android 特定开发规范和项目特定指导。使用此技能编写、审查或修改 Android 应用代码、ViewModel、Repository、UiState、StateFlow/Flow、Jetpack Compose UI、Activity/Fragment 生命周期代码、Room/Retrofit/数据代码、Android XML 资源、Manifest、Gradle Kotlin DSL、build-logic convention 插件、app/core/feature 模块、Android 测试，或分析 Android Crash/ANR/OOM 日志和生命周期问题时使用。同时应配合 kotlin-coding-standards 和 java-coding-standards 使用。
---

# Android 代码规范 (Android Coding Standards)

本 skill 是本项目 Android 开发的入口规范，专注于 Android 特定的开发规范和最佳实践。通用的 Kotlin/Java 代码规范请参考 `kotlin-coding-standards` 和 `java-coding-standards`。

正文保持简洁，具体模板、反模式、Crash/ANR 分析和依赖策略放在 `references/` 中，避免每次触发时加载过多通用内容。

## 使用原则

- 优先写项目约束、团队约定和历史踩坑，不重复 Android/Kotlin 通用常识
- 生成代码前先判断模块职责，保持 `app` / `feature` / `core` / `build-logic` 边界清晰
- 审查代码时优先查反模式：生命周期泄漏、主线程阻塞、错误状态表达、依赖边界破坏
- 涉及版本号、AGP、Compose BOM、Hilt、Room、Navigation 等会随时间变化的内容时，先看 `references/dependencies.md`
- 未经用户明确同意，不运行 `gradle`、`gradlew`、`gradlew.bat` 或任何会访问 Gradle 缓存、wrapper、daemon 的命令

## 项目结构

- `prido/`、`uniden/`：品牌 App 壳模块，只放品牌入口、品牌资源、Manifest 和差异化配置
- `feature/<name>/`：独立功能模块，负责具体页面、流程和 feature 内部逻辑
- `core/common/`：通用基础能力、通用结果类型、扩展函数、工具函数
- `core/model/`：跨模块共享的数据模型
- `core/data/`：数据源、仓库、持久化、网络或设备数据访问
- `core/navigation/`：跨 feature 导航协议、路由注册和导航实现
- `core/ui/`：跨模块复用的 Compose 组件、主题相关 UI 能力
- `build-logic/`：Gradle convention plugin，集中管理 Android/Kotlin/Compose/Hilt/Room 等构建约定

新增代码时先放到最窄职责模块，不要把 feature 逻辑放进 App 壳模块，不要让 `core` 依赖具体品牌或具体 feature。

## Android 特定代码规范

### Activity / Fragment

- Activity 保持轻量，只负责生命周期入口、依赖装配、`setContent` 和系统交互
- Fragment 使用 ViewBinding 时，必须在 `onDestroyView()` 清空 `_binding`
- 不在 `onCreate()`、`onResume()`、`onViewCreated()` 中直接写大量业务流程，优先交给 ViewModel 或 feature 内部类
- 不在生命周期方法中执行主线程 I/O、数据库、文件、网络或耗时解码
- 启动 Activity、发送 Intent、申请权限和访问外部组件时必须处理失败路径

Fragment ViewBinding 模板：

```kotlin
private var _binding: FragmentExampleBinding? = null
private val binding: FragmentExampleBinding
    get() = requireNotNull(_binding)

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
```

### ViewModel / UiState

- ViewModel 不持有 Activity、Fragment、View 或普通 Context
- ViewModel 中使用 `viewModelScope`，禁止 `GlobalScope`
- 页面状态用 sealed interface/class 表达，暴露只读 `StateFlow`
- 私有状态使用 `MutableStateFlow`，对外使用 `asStateFlow()`
- 一次性事件需要明确建模，避免滥用普通状态字段反复触发
- 错误状态保留可诊断信息，但 UI 文案不要直接依赖原始异常 message

推荐模板：

```kotlin
class ExampleViewModel(
    private val repository: ExampleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ExampleUiState>(ExampleUiState.Loading)
    val state: StateFlow<ExampleUiState> = _state.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            _state.value = ExampleUiState.Loading
            _state.value = when (val result = repository.load(id)) {
                is AppResult.Success -> ExampleUiState.Success(result.value)
                is AppResult.Error -> ExampleUiState.Error(result.throwable)
            }
        }
    }
}

sealed interface ExampleUiState {
    data object Loading : ExampleUiState
    data class Success(val value: ExampleModel) : ExampleUiState
    data class Error(val cause: Throwable) : ExampleUiState
}
```

### Repository / Data

- Repository 隔离数据源，不把 Retrofit、Room、SharedPreferences、文件系统细节泄漏给 UI 层
- Repository 优先暴露 `suspend fun`、`Flow<T>` 或项目现有结果类型
- 不从 Repository 直接返回 `LiveData`
- 数据源失败要转换为明确结果，不在底层静默吞掉异常
- 主线程禁止执行文件、数据库、网络、Bitmap 解码和 `SharedPreferences.commit()`

推荐方向：

```kotlin
class ExampleRepository(
    private val remote: ExampleRemoteDataSource,
    private val local: ExampleLocalDataSource,
) {

    suspend fun load(id: String): AppResult<ExampleModel> {
        return try {
            val cached = local.find(id)
            AppResult.Success(cached ?: remote.fetch(id).also { local.save(it) })
        } catch (error: IOException) {
            AppResult.Error(error)
        }
    }
}
```

### Jetpack Compose

- 页面级 composable 使用 `modifier: Modifier = Modifier`，并传给最外层布局
- 组件尽量无状态；需要状态时优先 state hoisting
- 不在 composable body 中直接执行导航、启动 Activity、I/O、网络、数据库或耗时操作
- 副作用使用 `LaunchedEffect`、`DisposableEffect`、`rememberUpdatedState` 等 API，并选择稳定 key
- `remember` 只保存 UI 状态或轻量对象，不保存需要生命周期释放的资源
- 收集 Flow 时优先使用生命周期感知 API，如 `collectAsStateWithLifecycle`
- 列表使用稳定 key，避免在 recomposition 中创建大量临时对象
- UI 文案优先使用 string resource；临时样例或测试界面可以硬编码，正式功能必须资源化
- 不把复杂业务逻辑写进 UI 分支中，先转换为 UI state

推荐结构：

```kotlin
@Composable
fun ExampleScreen(
    state: ExampleUiState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (state) {
            ExampleUiState.Loading -> CircularProgressIndicator()
            is ExampleUiState.Success -> ExampleContent(value = state.value)
            is ExampleUiState.Error -> ExampleError(onRetryClick = onRetryClick)
        }
    }
}
```

### 资源与 Manifest

- 品牌差异化资源放在对应 App 壳模块中，共用资源放在 `core/ui` 或具体 feature 模块
- Manifest 只声明必要组件和权限；新增权限必须说明用途和用户影响
- Activity、service、receiver 默认不导出；必须导出时显式设置 `android:exported` 并解释原因
- 不在 Manifest 中写无用 metadata、过期权限或临时调试组件
- XML 保持缩进一致，删除未使用资源和无效引用

## Crash/ANR 分析流程

详见 `references/crash_patterns.md`：

1. 识别异常类型：NPE、ANR、OOM、IllegalStateException、ActivityNotFoundException、SecurityException 等
2. 定位应用包名帧，区分业务代码、Android framework、第三方库和 native/JNI 层
3. 结合生命周期、线程、权限、Intent、资源释放和模块边界判断根因
4. 给出最小复现路径、最小修复和需要补充的测试

## 依赖与构建

详见 `references/dependencies.md`：

- 使用 Gradle Kotlin DSL 和 `build-logic` convention plugin
- 通用 Android/Kotlin/Compose 配置集中在 convention plugin，不在业务模块重复硬编码
- 版本号和依赖别名集中在 version catalog 或 build-logic
- 需要精确版本建议时必须基于当前官方来源核对，不使用过期文章或历史记忆直接下结论

### libs.versions.toml 管理规范

**唯一性原则：**
- 所有依赖版本必须统一在 `gradle/libs.versions.toml` 中管理
- 禁止在各个模块的 `build.gradle.kts` 中硬编码版本号
- 禁止在 `build-logic` 中硬编码版本号
- 所有版本引用必须使用 version catalog 的别名

**版本升级限制：**
- AI 不能擅自修改 `libs.versions.toml` 中的版本号
- 版本升级必须由用户手动确认和修改
- AI 只能提供版本升级建议和风险评估
- 如果需要升级版本，必须明确告知用户：
  1. 当前版本和目标版本
  2. 升级的原因和好处
  3. 可能的风险和兼容性问题
  4. 需要用户手动修改的具体位置

**版本建议流程：**
1. 分析当前依赖版本和最新稳定版本
2. 识别潜在的兼容性问题和破坏性变更
3. 提供详细的升级建议和风险评估
4. 明确告知用户需要手动修改 `libs.versions.toml`
5. 等待用户确认后再进行相关代码调整

**示例：**
```toml
# gradle/libs.versions.toml
[versions]
agp = "8.7.2"
kotlin = "2.0.21"
compose-bom = "2024.10.01"
hilt = "2.52"

[libraries]
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "agp" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
google-dagger-hilt = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
```

**禁止模式：**
```kotlin
// 禁止：在 build.gradle.kts 中硬编码版本号
implementation("androidx.compose.ui:ui:1.7.0")

// 替代：使用 version catalog 别名
implementation(libs.androidx.compose.ui)

// 禁止：AI 擅自修改 libs.versions.toml 版本号
// 必须由用户手动修改
```

## 与 C/C++ 规范的关系

- 修改 `.c`、`.cpp`、`.h`、`.hpp`、JNI native 层或 NDK 代码时，同时使用 `cpp-coding-standards`
- Android 层调用 native 能力时，Kotlin API 保持 Android 命名风格，native 边界内部遵循 C/C++ 命名和日志规范
- JNI 边界必须显式处理空指针、异常、线程和资源释放，不把 native 错误静默吞掉

## Android 特定禁止模式

```kotlin
// 禁止：生命周期泄漏
GlobalScope.launch { }

// 替代：绑定到 ViewModel 生命周期
viewModelScope.launch { }

// 禁止：主线程阻塞，容易造成 ANR
prefs.edit().commit()

// 替代：异步提交
prefs.edit().apply()

// 禁止：ViewModel 长期持有 Context
class BadViewModel(private val context: Context) : ViewModel()

// 替代：传入必要数据，或使用 SavedStateHandle / AndroidViewModel 的明确场景
class GoodViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel()

// 禁止：Repository 向 UI 层返回 LiveData
fun user(): LiveData<User>

// 替代：使用 suspend / Flow / 项目结果类型
suspend fun user(): AppResult<User>
fun userFlow(): Flow<User>

// 禁止：在 Composable body 中执行副作用
@Composable
fun BadScreen() {
    val navController = rememberNavController()
    navigateToNext() // 直接导航
}

// 替代：使用 LaunchedEffect
@Composable
fun GoodScreen() {
    val navController = rememberNavController()
    LaunchedEffect(Unit) {
        navigateToNext()
    }
}
```

## 审查清单

- [ ] 代码放在正确模块，没有破坏 app/feature/core/build-logic 边界
- [ ] Kotlin/Java 代码符合通用规范（参考 kotlin-coding-standards/java-coding-standards）
- [ ] Activity/Fragment 足够轻量，业务逻辑没有堆在生命周期方法里
- [ ] Compose 没有在 body 中执行副作用、导航、I/O 或耗时操作
- [ ] ViewModel、Repository、UiState、Flow/StateFlow 的职责清晰
- [ ] 可空、异常、权限、Intent、导航、资源释放和失败路径已处理
- [ ] Crash/ANR/OOM 问题按 `references/crash_patterns.md` 给出根因和最小修复
- [ ] Gradle 配置复用 convention plugin，没有重复硬编码通用配置
- [ ] 所有依赖版本使用 libs.versions.toml 中的别名，没有硬编码版本号
- [ ] 没有擅自修改 libs.versions.toml 中的版本号
- [ ] 没有未经授权运行 Gradle 命令
- [ ] 新增复杂逻辑有合适测试或说明无法验证的原因
