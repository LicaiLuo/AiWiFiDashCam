# Android项目约定

本文件记录本项目 Android/Kotlin/Compose 代码的具体约束和反模式。优先使用本项目已有结构和工具，不为了套模板引入新架构。

## 命名规范

- 类、接口、枚举、sealed 类型使用 `PascalCase`，如 `BrandRepository`、`NavigationResult`
- 函数、变量、属性使用 `lowerCamelCase`，如 `navigate()`、`routeId`
- 常量使用 `UPPER_SNAKE_CASE`，优先声明为 `private const val`
- 文件名与主要 public 类型或主要 composable 保持一致，如 `HomeActivity.kt`、`DashCamTitle.kt`
- Activity 以 `Activity` 结尾，ViewModel 以 `ViewModel` 结尾，Repository 以 `Repository` 结尾
- Composable 函数使用名词或 UI 组件名，首字母大写，如 `HomeScreen()`、`DashCamTitle()`
- 事件回调用 `onXxx` 命名，如 `onBackClick`、`onRouteSelected`
- 资源文件、string、color、dimen、style 使用小写加下划线，按模块职责命名
- 禁止使用拼音、无意义缩写、过短名称、通配符 import 和非 ASCII 标识符

## 模块边界

- `prido/`、`uniden/` 只放品牌入口、品牌资源、Manifest 和差异化配置
- `feature/<name>/` 放具体页面、流程和 feature 内部逻辑
- `core/common/` 放通用结果类型、扩展函数和工具函数
- `core/model/` 放跨模块共享模型
- `core/data/` 放数据源、仓库、持久化、网络或设备数据访问
- `core/navigation/` 放跨 feature 导航协议、路由注册和导航实现
- `core/ui/` 放跨模块复用的 Compose 组件和主题相关能力
- `build-logic/` 放 Gradle convention plugin 和构建约定

禁止让 `core` 依赖具体 app、品牌或 feature。新增共享能力前先确认至少两个模块会复用，否则优先留在 feature 内。

## Kotlin代码风格

- 优先 `val`，只有确实需要重新赋值时才用 `var`
- 显式表达可空性，避免 `!!`
- 公共 API、跨模块模型、非显而易见的返回类型应显式声明类型
- 函数保持单一职责，复杂条件拆成具名私有函数
- 控制语句即使只有单行也使用花括号
- 不使用 `println()`、`printStackTrace()` 做调试输出
- 不吞掉异常；捕获异常后返回明确结果或记录日志
- 不把 Android `Context`、`Activity`、`View` 长期保存在单例、object 或 ViewModel 中

## Activity / Fragment

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

## ViewModel / UiState

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

## Repository / Data

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

## Compose

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

## 资源与Manifest

- 品牌差异化资源放在对应 App 壳模块中，共用资源放在 `core/ui` 或具体 feature 模块
- Manifest 只声明必要组件和权限；新增权限必须说明用途和用户影响
- Activity、service、receiver 默认不导出；必须导出时显式设置 `android:exported` 并解释原因
- 不在 Manifest 中写无用 metadata、过期权限或临时调试组件
- XML 保持缩进一致，删除未使用资源和无效引用

## 禁止模式

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
```
