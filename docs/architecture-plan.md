# Android 多品牌架构方案

## Summary

本方案用于当前项目的多品牌 Android 应用开发：通过同级 App 模块承载不同品牌包名和资源，通过可裁剪 Feature 模块复用功能，通过 Core 模块沉淀通用能力，并将 Gradle 配置统一收敛到 `build-logic` convention plugin。

目标是让 `:prido`、`:uniden` 等品牌 App 功能一致但包名、应用名、图标和 UI 资源可独立配置，同时支持不同品牌在编译期选择是否打包某些功能。

## 多品牌同级 App 模块

采用同级 application module 方案，而不是 product flavors。

规划结构：

```text
AoniAiDashCam/
├── prido/
├── uniden/
├── feature/
│   ├── splash/
│   └── home/
├── core/
│   ├── common/
│   ├── ui/
│   ├── model/
│   ├── data/
│   └── navigation/
└── build-logic/
```

品牌模块示例：

```text
:prido
:uniden
```

每个品牌模块都可以在 Android Studio 中独立选择运行和打包。

统一包名前缀：

```text
cn.anc.dashcam.*
```

品牌模块可以分别配置：

```text
cn.anc.dashcam.prido
cn.anc.dashcam.uniden
```

## 品牌模块职责

`:prido`、`:uniden` 等品牌模块只作为打包壳。

品牌模块只保留：

- `applicationId`
- `namespace`
- `app_name`
- launcher icon
- 品牌颜色
- 品牌主题
- 品牌资源覆盖
- 必要的 Manifest 配置
- 对需要打包的 feature 模块的依赖

品牌模块不放：

- `MainActivity`
- 通用业务代码
- 通用 Compose 页面
- 通用导航逻辑
- 可复用的数据层或工具层逻辑

正式依赖方向：

```text
brand app -> feature:* -> core:*
```

禁止依赖方向：

```text
core:* -> feature:*
core:* -> brand app
feature:* -> brand app
```

## Feature 模块职责

Feature 模块承载具体业务功能。

`:feature:splash` 是统一启动入口：

- 提供启动 Activity。
- 在 Manifest 中声明 `MAIN` + `LAUNCHER`。
- 启动后根据路由跳转到默认功能，例如 `home`。
- 正式品牌包中只有 `:feature:splash` 的 launcher 入口启用；其他 feature 如需单独调试，可以通过 standalone 开关启用自己的调试 launcher alias。

其他功能模块示例：

```text
:feature:home
:feature:settings
:feature:device
:feature:album
```

每个 feature 可以提供：

- 自己的 Activity。
- 自己的 Compose 页面。
- 自己的 ViewModel。
- 自己的路由注册。
- 自己所需的最小依赖。

Feature 默认是 Android library module，由品牌 App 依赖后合并进最终 APK。

## Core 模块规划

Core 模块用于沉淀跨 feature 复用的能力。

规划模块：

```text
:core:common
:core:ui
:core:model
:core:data
:core:navigation
```

### core:model

职责：

- 放置通用数据模型。
- 不依赖 Android UI。
- 不依赖 feature。

最小参照功能：

```kotlin
package cn.anc.dashcam.core.model

data class BrandInfo(
    val code: String,
    val displayName: String,
)

data class FeatureRoute(
    val id: String,
)
```

### core:common

职责：

- 放置通用工具类、扩展函数、结果封装。
- 尽量保持轻量。

最小参照功能：

```kotlin
package cn.anc.dashcam.core.common

sealed interface AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>
    data class Error(val throwable: Throwable) : AppResult<Nothing>
}

fun normalizeBrandCode(value: String): String {
    return value.trim().lowercase()
}
```

### core:data

职责：

- 放置通用数据仓库接口和默认实现。
- 负责组合 model、common、存储或网络数据。

最小参照功能：

```kotlin
package cn.anc.dashcam.core.data

import cn.anc.dashcam.core.model.BrandInfo

interface BrandRepository {
    fun currentBrand(): BrandInfo
}

class InMemoryBrandRepository(
    private val brandInfo: BrandInfo,
) : BrandRepository {
    override fun currentBrand(): BrandInfo = brandInfo
}
```

### core:ui

职责：

- 放置通用 Compose 组件。
- 放置通用主题、设计系统、基础控件。
- 不放具体业务页面。

最小参照功能：

```kotlin
package cn.anc.dashcam.core.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DashCamTitle(text: String) {
    Text(text = text)
}
```

### core:navigation

职责：

- 放置跨 feature 路由契约。
- 屏蔽具体 Activity 跳转细节。
- 支持功能未打包时的失败兜底。

最小参照功能：

```kotlin
package cn.anc.dashcam.core.navigation

interface FeatureNavigator {
    fun navigate(routeId: String): NavigationResult
}

sealed interface NavigationResult {
    data object Success : NavigationResult
    data class RouteNotFound(val routeId: String) : NavigationResult
}
```

## 功能裁剪方案

不同品牌是否包含某个功能，通过品牌模块依赖控制。

示例：

```kotlin
dependencies {
    implementation(project(":feature:splash"))
    implementation(project(":feature:home"))
}
```

如果某品牌不需要 `settings` 功能，则不添加：

```kotlin
implementation(project(":feature:settings"))
```

这样可以做到：

- 编译期不打包无关 feature。
- APK 更干净。
- 权限、资源、代码更容易裁剪。
- 不需要的 feature 不参与路由注册。

要求：

- feature 之间不要直接强依赖可选功能。
- 跳转必须通过 `:core:navigation` 的路由契约。
- 目标功能不存在时必须有兜底行为。

## 路由方案

不优先使用 ARouter。

当前推荐自建轻量路由契约，原因：

- 项目路由需求主要是跨 feature 解耦和功能裁剪。
- 自建方案可控，不依赖第三方注解处理。
- 更容易适配 Kotlin、KSP、Compose 和多品牌裁剪。
- 避免全局字符串、初始化、混淆和维护成本失控。

推荐机制：

- `:core:navigation` 定义 `FeatureNavigator`、`RouteRegistry`、`NavigationResult`。
- 每个 feature 只注册自己的 route。
- 品牌 App 依赖了哪个 feature，哪个 feature 才会进入最终 APK。
- `:feature:splash` 启动后通过 route id 跳转到默认功能。

示例 route：

```text
splash
home
settings
device
album
```

示例启动配置：

```xml
<string name="dashcam_start_route">home</string>
```

当目标 route 未注册时：

- 返回 `RouteNotFound`。
- Splash 显示统一错误态。
- 或跳转到默认兜底页面。

业务模块不直接写显式 Intent。显式 Intent 可以作为内部实现细节封装在 navigation 层或 feature 自己的路由注册中。

## Gradle 收敛方案

Gradle 配置统一收敛到 `build-logic` convention plugin。

规划插件：

```text
dashcam.android.application
dashcam.android.library
dashcam.android.feature
dashcam.android.core
dashcam.android.compose
dashcam.android.hilt
dashcam.android.room
```

要求：

- 品牌 App 统一使用 `dashcam.android.application`。
- Feature 模块统一使用 `dashcam.android.feature`。
- Core 模块统一使用 `dashcam.android.core` 或更细分的 library convention。
- Compose 能力通过 convention plugin 统一配置。
- Hilt、Room 等能力按需通过 convention plugin 添加。

注意事项：

- `dashcam.android.application` 必须直接应用 `com.android.application`。
- `dashcam.android.application` 不能复用会应用 `com.android.library` 的插件实现。
- 所有模块的 `compileSdk`、`minSdk`、Java/Kotlin 版本应集中配置。
- 依赖版本应优先统一放在 version catalog。

## Feature 本地独立调试

Feature 正式形态保持 Android library。

为了本地开发便利，可以在 `gradle.properties` 中增加调试开关。

示例：

```properties
dashcam.standaloneFeature=:feature:home
```

规则：

- 开关只用于本地开发调试。
- 正式构建不能依赖 standalone 模式。
- 默认情况下所有 feature 都是 library。
- 被选中的 feature 可以临时按 application 方式运行。
- standalone 模式必须使用临时 `applicationId`。
- standalone 模式不能依赖任何品牌 App 模块。

如果后续发现 application/library 动态切换导致 Android Studio Sync 不稳定，应改为独立 demo 壳模块。

更稳定的备用方案：

```text
:debug:home-app -> :feature:home
:debug:splash-app -> :feature:splash
```

## 测试与验证注意事项

静态检查项：

- 所有源码包名前缀统一为 `cn.anc.dashcam.*`。
- `:prido`、`:uniden` 不包含 `MainActivity`。
- `:prido`、`:uniden` 不包含业务 UI 代码。
- 正式品牌包中 `:feature:splash` 是唯一启用的启动 Activity 来源。
- 品牌模块只依赖需要打包的 feature。
- 未被品牌模块依赖的 feature 不参与最终 App。
- Feature 不反向依赖品牌模块。
- Core 不依赖 feature 和品牌模块。
- Gradle 配置逐步统一收敛到 convention plugin。

构建验证限制：

- 未经明确授权，不运行 `gradle`。
- 未经明确授权，不运行 `gradlew`。
- 未经明确授权，不运行 `gradlew.bat`。
- 未经明确授权，不执行任何会触发 Gradle wrapper 下载、依赖解析、缓存、锁文件或守护进程的命令。

如需 Gradle sync、build、test、lint 验证，需要先说明原因、风险和预期影响，并获得明确同意。

## Assumptions

- 采用同级多 App 模块，不采用 product flavors。
- `:prido`、`:uniden` 等品牌模块只作为打包壳。
- 启动入口放在 `:feature:splash`。
- 不同品牌通过依赖不同 feature 实现编译期功能裁剪。
- 不采用 ARouter，优先自建轻量路由契约。
- 后续真正落地改造时，再分步骤修改模块、Gradle 和源码。
