# 依赖与构建策略

依赖版本会随时间变化。本文件不固定“最新版本”，只记录本项目选择依赖和生成构建配置时必须遵守的策略。

## 基本原则

- 使用 Gradle Kotlin DSL，不新增 Groovy Gradle 脚本
- 优先使用 `build-logic` 中的 convention plugin
- 通用 Android/Kotlin/Compose 配置集中在 convention plugin，不在业务模块重复硬编码
- 版本号、插件版本、依赖别名集中在 version catalog 或 build-logic
- 新增依赖前确认模块边界，不让低层 `core` 依赖高层 feature 或 app
- 不为了单个模块临时绕过 convention plugin；确需特殊配置时只加最小局部配置并说明原因

## Gradle运行限制

未经用户明确同意，禁止运行：

- `gradle`
- `gradlew`
- `gradlew.bat`
- 任何会访问或修改 Gradle 用户目录、缓存、锁文件、daemon、wrapper 下载、依赖缓存的命令

需要 Gradle 验证时，先说明原因、风险、预期影响，并等待用户明确授权。未授权前只能做静态分析、文件阅读和不依赖 Gradle 的检查。

## 版本建议规则

- 需要精确版本时，必须基于当前官方来源核对，不使用历史记忆或旧文章直接给结论
- Android Gradle Plugin、Kotlin、Compose Compiler、Compose BOM、Hilt、Room、Navigation、Retrofit、OkHttp、Coil、Lifecycle、JUnit 等版本都视为时间敏感
- 如果无法联网核对，明确说明无法确认当前最新版本，并给出“保持现有项目版本”或“等待用户授权/联网核对”的保守方案
- 更新版本时先检查兼容矩阵：AGP 与 Gradle、Kotlin 与 Compose Compiler、Hilt 与 KSP/KAPT、Room 与 KSP/KAPT

## 本项目构建约定

- App 模块优先使用 `dashcam.android.application`
- Library/core 模块优先使用 `dashcam.android.library` 或已有 core convention plugin
- Feature 模块优先使用 `dashcam.android.feature`
- Compose 模块优先通过 Compose convention plugin 启用 Compose，而不是在每个模块重复配置
- Hilt、Room 等能力优先通过对应 convention plugin 接入
- 不在业务模块重复配置 `compileSdk`、`minSdk`、`targetSdk`、Java/Kotlin 版本

## 新增依赖检查

新增依赖前确认：

- 依赖是否属于当前模块职责
- 是否已有同类依赖可以复用
- 是否应放在 version catalog
- 是否需要在 build-logic 中统一暴露
- 是否会增加 app 壳模块、feature、core 之间的反向依赖
- 是否需要 ProGuard/R8 keep rules、Manifest 权限或初始化逻辑
- 是否影响启动、包体、内存或离线可用性

## 输出构建片段时

- 只输出最小必要片段
- 优先使用已有 `libs.xxx` alias 和 convention plugin
- 不擅自引入新 plugin 或变更全局版本
- 如果片段需要 Gradle 验证，明确说明未运行 Gradle
- 如果建议版本升级，说明版本来源和兼容风险
