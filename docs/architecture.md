# 当前项目架构文档

## 项目定位

`AoniAiDashCam` 是一个面向行车记录仪 App 的 Android 多模块项目，主要使用 Kotlin、Jetpack Compose 和 Gradle Kotlin DSL 构建。当前架构的核心目标是用一套公共能力支撑多个品牌壳包，让 PRIDO、UNIDEN、COOAU 等品牌可以复用主功能，同时在入口、配色、OSD 水印、首页结构和资源上做差异化。

> 说明：仓库中保留了 `app/` 目录，但当前 `settings.gradle.kts` 未 include `:app`。当前被 Gradle settings 启用的品牌壳模块是 `:prido`、`:uniden`、`:cooau`。

## 架构说明

项目整体可以分为四层：品牌壳层、公共核心层、功能层和构建约定层。

### 品牌壳层

品牌壳层负责最终应用包的身份和品牌差异。当前启用的品牌壳包括：

- `:prido`
- `:uniden`
- `:cooau`

每个品牌壳模块通过自己的 `applicationId`、`AndroidManifest.xml` 和 `Application` 类声明独立应用入口。启动时，品牌 `Application` 会设置 `AppConfig.brandType`，并通过 `BrandConfigManager` 初始化或覆盖品牌颜色、OSD 水印等配置。

`app/` 目录中也存在一个 PRIDO 包名的入口实现，但它没有被当前 `settings.gradle.kts` 纳入构建模块，建议视为保留入口或历史/实验壳目录。

### 公共核心层

公共核心层位于 `core/` 下，提供跨品牌、跨功能复用的基础能力：

- `:core:common`：公共配置、`DashcamApplication`、`BaseActivity`、多语言、主题、状态栏外观、Activity 转场等基础能力。
- `:core:data`：品牌配置、品牌配色、OSD 配置、品牌数据仓库等数据侧能力。
- `:core:logging`：应用日志、日志脱敏、文件日志、崩溃日志、日志级别和日志文件管理。
- `:core:model`：轻量领域模型，例如 `BrandInfo`、`FeatureRoute`。
- `:core:navigation`：功能导航抽象，基于 Manifest meta-data 注册 route，并通过 Activity 启动目标功能。
- `:core:ui`：可复用 UI 组件，例如标题组件。

核心层的职责是沉淀通用能力，避免品牌壳和业务功能重复实现基础设施。

### 功能层

功能层位于 `feature/` 下，按业务页面或业务域拆分：

- `:feature:splash`：启动页，负责冷启动过渡并跳转到首页 route。
- `:feature:home`：主页面容器，根据品牌选择 PRIDO、UNIDEN、COOAU 的首页布局，并聚合设备、相册、我的、反馈等入口。
- `:feature:device`：设备首页/设备操作相关 UI。
- `:feature:album`：本地相册页面。
- `:feature:mine`：个人中心、账号入口、设置和反馈入口。
- `:feature:settings`：设置中心，包括语言、主题模式、主题强调色、状态栏文字颜色等设置页。
- `:feature:feedback`：反馈页面、日志收集、脱敏和反馈包生成。

功能层依赖公共核心层。`feature:home` 是当前功能聚合点，它直接依赖 `feature:device`、`feature:album`、`feature:mine`、`feature:feedback`，并根据品牌配置组织不同首页交互。

### 构建约定层

`build-logic` 是项目的 Gradle convention plugin 层，用于统一模块构建配置。当前提供的约定插件包括：

- `dashcam.android.application`
- `dashcam.android.library`
- `dashcam.android.feature`
- `dashcam.android.core`
- `dashcam.android.compose`
- `dashcam.android.hilt`
- `dashcam.android.room`

这些插件集中配置 Android Application/Library、Kotlin、Compose、测试依赖、独立 feature 调试入口等构建约定，减少各模块 `build.gradle.kts` 中的重复配置。

## 组织结构说明

当前项目的主要目录职责如下：

```text
AoniAiDashCam/
├── build-logic/          # Gradle convention plugins，统一构建配置
├── core/                 # 公共核心能力
│   ├── common/           # Application/Activity 基类、主题、多语言、状态栏等
│   ├── data/             # 品牌配置、配色、OSD、Repository
│   ├── logging/          # 日志、脱敏、文件日志、崩溃日志
│   ├── model/            # 轻量领域模型
│   ├── navigation/       # route 注册与 Activity 导航
│   └── ui/               # 通用 UI 组件
├── feature/              # 按功能拆分的业务模块
│   ├── splash/           # 启动页
│   ├── home/             # 首页聚合与品牌首页布局
│   ├── device/           # 设备页
│   ├── album/            # 相册页
│   ├── mine/             # 我的页
│   ├── settings/         # 设置页
│   └── feedback/         # 反馈与日志打包
├── prido/                # PRIDO 品牌壳模块
├── uniden/               # UNIDEN 品牌壳模块
├── cooau/                # COOAU 品牌壳模块
├── app/                  # 保留的 PRIDO 入口目录，当前未被 settings.gradle.kts include
├── gradle/               # version catalog 与 wrapper 配置
└── settings.gradle.kts   # 当前启用模块列表
```

模块依赖方向整体如下：

- 品牌壳模块依赖 `core` 和需要打包进应用的 `feature` 模块。
- `feature` 模块依赖 `core` 模块，避免直接依赖品牌壳模块。
- `feature:home` 聚合多个页面级 feature，是当前首页和底部导航/抽屉导航的组合中心。
- 跨功能跳转通过 `core:navigation` 的 `FeatureNavigator` 抽象完成。
- 功能模块通过 Manifest `meta-data` 暴露 route，例如 `cn.anc.dashcam.route.home`、`cn.anc.dashcam.route.settings`、`cn.anc.dashcam.route.feedback`。

## 功能说明

### 启动流程

应用启动后，品牌壳模块的 `Application` 首先执行品牌初始化：

1. 设置 `AppConfig.brandType`，确定当前运行品牌。
2. 调用 `DashcamApplication.onCreate()` 初始化公共能力。
3. 初始化 `AppLocaleManager`，为多语言设置提供 MMKV 存储和 Context 包装能力。
4. 初始化 `AppLog`，启用控制台日志、文件日志和未捕获异常日志。
5. 调用 `BrandConfigManager.initWithBrand()` 或 `overrideBrandConfig()` 注入品牌配色和 OSD 水印配置。

随后 `feature:splash` 的 `SplashActivity` 作为启动页展示过渡界面，并通过 `ActivityFeatureNavigator` 跳转到 `home` route。

### 首页与品牌差异

`feature:home` 的 `HomeActivity` 是主页面容器。它读取 `AppConfig.brandType`，在 Compose 中分流到不同品牌布局：

- PRIDO：偏经典四 Tab 结构，包含设备、本地相册、反馈、我的。
- UNIDEN：偏三 Tab 和中间大按钮结构，包含设备、相册、分享/添加等入口。
- COOAU：偏侧滑抽屉结构，通过抽屉入口访问设置、反馈和其他功能。

这种设计让多个品牌共享同一套功能模块，同时在首页框架和视觉交互上保持差异化。

### 设备、相册与我的

`feature:device` 提供设备相关操作入口和设备页 UI；`feature:album` 提供本地相册页面；`feature:mine` 提供个人中心入口，并可以跳转到设置和反馈。

这些模块主要以 Compose 页面形式对外暴露，由 `feature:home` 负责组合进不同品牌首页。

### 设置能力

`feature:settings` 提供独立的设置中心和多个二级设置页，当前包括：

- 语言设置：通过 `AppLocaleManager` 保存语言偏好，并在 `BaseActivity.attachBaseContext()` 中包装 Context。
- 明暗主题设置：通过 `AppThemeManager` 保存浅色、深色或跟随系统。
- 主题强调色设置：通过 `AppThemeColor` 与品牌配色解析逻辑应用不同强调色。
- 状态栏文字颜色设置：通过 `StatusBarTextMode` 控制自动、深色、浅色状态栏前景。

设置变化后，相关 Activity 会在恢复时读取当前配置并刷新页面或状态栏表现。

### 反馈与日志

`core:logging` 提供全局日志能力，包含日志级别、日志脱敏、日志文件落盘、日志轮转和崩溃日志。`feature:feedback` 在此基础上提供反馈页面，允许用户填写问题描述，并通过 `FeedbackFileCollector` 收集最近日志、元信息和用户描述，生成脱敏后的反馈压缩包。

### 导航机制

`core:navigation` 定义了 `FeatureNavigator` 和 `RouteRegistry`。当前实现 `ManifestRouteRegistry` 从应用 Manifest meta-data 中读取 route 到 Activity class name 的映射，`ActivityFeatureNavigator` 再根据 route 启动对应 Activity。

这种机制让功能模块可以通过 Manifest 声明自己的导航入口，调用方只需要知道 route id，不需要直接依赖目标 Activity 类。

## 基于当前架构的优化建议

1. 收敛 `app/` 与 `prido/` 的重复入口  
   当前仓库中同时存在 `app/` 和 `prido/` 两套 PRIDO 相关入口，但只有 `:prido` 被 settings 启用。建议明确 `app/` 的定位：如果不再使用，应迁移必要差异后删除；如果仍要保留，应在文档或 settings 中说明它的构建场景，避免后续维护者误改错误入口。

2. 将品牌配置从可变全局单例迁移为更明确的配置提供接口  
   `AppConfig.brandType` 和 `BrandConfigManager` 当前依赖进程级可变状态，使用简单，但测试隔离和初始化顺序容易变得隐性。后续可以抽象 `BrandConfigProvider`，由品牌壳提供配置，功能层只读取明确接口。

3. 为 route id 建立集中常量或类型安全封装  
   当前 `"home"`、`"settings"`、`"feedback"` 等 route id 以字符串形式散落在调用点和 Manifest 中。建议增加集中定义，例如 `FeatureRoutes`，降低拼写错误和重命名遗漏风险。

4. 拆分过大的 `HomeActivity.kt`  
   `feature:home` 同时承载 Activity、三套品牌首页布局、Tab、抽屉、弹窗和配色逻辑。建议按品牌布局或页面区域拆分为独立文件，例如 PRIDO、UNIDEN、COOAU 各自的 layout 文件，并保留 `HomeActivity` 作为轻量入口和分发层。

5. 补齐架构级文档和模块依赖规则  
   当前已有多模块雏形，但模块边界、依赖方向、route 注册规范、品牌壳接入流程还可以继续文档化。建议补充新品牌接入指南、功能模块接入指南，以及哪些模块允许依赖哪些模块的规则。

6. 增加核心模块单元测试  
   建议优先覆盖 `core:logging`、`core:navigation`、`core:data` 和 `core:common` 中的关键逻辑，例如日志脱敏、路由注册解析、品牌配置选择、主题/语言持久化读取。这样可以在不启动完整 App 的情况下保护架构基础能力。

7. 梳理未启用或占位的构建能力  
   `build-logic` 中已有 Hilt、Room 约定插件，但当前源码中主要能力仍以手动对象和轻量数据结构为主。建议确认这些插件是预留能力还是计划中能力，并在模块接入时保持依赖和插件使用一致。
