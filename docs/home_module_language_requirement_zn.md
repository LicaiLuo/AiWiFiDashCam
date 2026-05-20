# Home 主界面模块拆分与语言切换需求

## 1. 背景与目标

当前 `HomeActivity` 同时承担主界面容器、底部导航、Device 页面、Local Album 页面、Mine 页面、设置入口和跨功能跳转等职责，Activity 代码会随着功能增加持续膨胀，不利于多品牌裁剪和 feature 独立维护。

本需求将 Home 主界面按底部 Tab 拆分为独立功能模块，并新增 APP 设置模块。用户可以在 Mine 页面进入 APP 设置，查看当前语言并在中文和英语之间切换，也可以查看当前显示模式并在明亮模式和黑夜模式之间切换。

目标如下：

- `HomeActivity` 只负责主界面容器和底部 Tab 切换。
- Device、Local Album、Mine 三个主界面分别放入独立 feature 模块。
- Mine 页面提供 APP Setting 入口，通过路由进入 Settings 模块。
- Settings 模块提供当前语言展示和语言切换。
- Settings 模块提供当前显示模式展示和明亮/黑夜模式切换。
- 首次未选择语言时跟随系统语言；用户选择语言后持久化保存，并立即生效。

## 2. 功能范围

本次实现范围：

- 新增 `:feature:device` 模块，承载 Device 主界面。
- 新增 `:feature:album` 模块，承载 Local Album 主界面。
- 新增 `:feature:mine` 模块，承载 Mine 主界面和 APP Setting 入口。
- 新增 `:feature:settings` 模块，承载 APP 设置页、语言设置页和显示模式设置页；语言选择和显示模式选择分别使用独立 Activity。
- 在 `:core:common` 中新增语言模型、主题模式模型、语言管理能力和主题管理能力，语言选择和显示模式使用腾讯 MMKV 持久化。
- 为 Home、Mine、Settings、Device、Album 的正式 UI 文案增加英文和简体中文资源。

不包含范围：

- 不新增“跟随系统”设置选项；跟随系统只作为首次默认策略。
- 不新增更多语言；本期只支持中文和英语。
- 不新增跟随系统主题；本期只支持明亮模式和黑夜模式。
- 不调整品牌名称、启动页、反馈页等已有业务功能。

## 3. 用户流程

1. 用户启动 App，Splash 按 `home` 路由进入 Home 主界面。
2. Home 底部展示三个 Tab：Device、Local Album、Mine。
3. 用户点击底部 Tab，可以在三个主界面之间切换。
4. 用户进入 Mine 页面，点击 APP Setting。
5. App 通过 `settings` route 打开 APP 设置页。
6. APP 设置页展示 Language 行，右侧显示当前有效语言。
7. 用户点击 Language 行，进入 `LanguageSettingsActivity` 语言设置页。
8. 语言设置页展示中文和 English 两个选项，并标记当前选中的语言。
9. 用户选择中文或 English 后，语言选择立即保存，当前设置界面刷新为新语言。
10. APP 设置页展示显示模式行，右侧显示当前显示模式。
11. 用户点击显示模式行，进入 `ThemeSettingsActivity` 显示模式设置页。
12. 显示模式设置页展示明亮模式和黑夜模式两个选项，并标记当前选中的模式。
13. 用户选择明亮模式或黑夜模式后，显示模式立即保存，当前设置界面刷新为新配色。
14. 用户返回 Home/Mine 后，Home 检测语言或显示模式变化并刷新底部 Tab、Mine 页面文案和页面配色。

## 4. 模块设计

模块职责：

- `:feature:home`：主界面容器，只负责底部导航和承载三个 Tab 页面。
- `:feature:device`：提供 Device 页面入口 Composable。
- `:feature:album`：提供 Local Album 页面入口 Composable。
- `:feature:mine`：提供 Mine 页面入口 Composable，APP Setting 和 Feedback 入口均通过 `ActivityFeatureNavigator` 跳转。
- `:feature:settings`：提供 `SettingsActivity`、`LanguageSettingsActivity`、`ThemeSettingsActivity`；`SettingsActivity` 作为 APP 设置入口并注册 `settings` route，语言和显示模式二级页通过模块内显式 Intent 打开。
- `:core:common`：提供 `AppLanguage`、`AppLocaleManager`、`AppThemeMode` 和 `AppThemeManager`，负责语言解析、主题模式解析、MMKV 保存/读取和 Context 本地化包装。

依赖方向：

```text
brand app -> feature:home -> feature:device / feature:album / feature:mine
brand app -> feature:settings
feature:mine -> core:navigation
feature:settings -> core:common
core:* 不依赖 feature 或 brand app
```

路由要求：

- `:feature:settings` 在 Manifest 中注册：

```text
cn.anc.dashcam.route.settings = cn.anc.dashcam.settings.SettingsActivity
```

- Mine 页面点击 APP Setting 时只调用 `ActivityFeatureNavigator(context).navigate("settings")`。
- Mine 模块不直接依赖 `:feature:settings`，保证功能边界清晰。

## 5. 语言规则

支持语言：

- 中文：`zh-CN`
- 英语：`en`

默认规则：

- 如果用户没有保存过语言选择，则读取系统语言。
- 系统语言为中文时显示中文。
- 其他系统语言默认显示 English。

保存规则：

- 用户在语言设置页选择中文或 English 后，将语言 tag 保存到本地 MMKV。
- 后续进入 App 时优先使用用户保存的语言。

生效规则：

- `LanguageSettingsActivity` 选择语言后立即 `recreate()` 当前 Activity。
- Home 页面在 `onResume()` 检测当前语言 tag，如发现语言变化则 `recreate()`，确保返回 Mine 后文案刷新。

## 6. 显示模式规则

支持显示模式：

- 明亮模式：`light`
- 黑夜模式：`dark`

默认规则：

- 如果用户没有保存过显示模式选择，则默认使用明亮模式。

保存规则：

- 用户在显示模式设置页选择明亮模式或黑夜模式后，将主题模式 tag 保存到本地 MMKV。
- 后续进入 App 时优先使用用户保存的显示模式。

生效规则：

- `ThemeSettingsActivity` 选择显示模式后立即 `recreate()` 当前 Activity。
- Home 页面在 `onResume()` 检测当前显示模式 tag，如发现显示模式变化则 `recreate()`，确保返回 Home 后三个主页面和底部 Tab 使用最新配色。

## 7. 验收标准

模块验收：

- `HomeActivity` 不再包含 Device、Album、Mine 页面实现。
- Device、Album、Mine 分别位于独立 feature 模块。
- Settings 位于独立 feature 模块，并注册 `settings` route。
- 语言选择和显示模式选择分别位于 `LanguageSettingsActivity`、`ThemeSettingsActivity`，不再由 `SettingsActivity` 内部页面状态切换承载。
- `:prido`、`:uniden` 打包壳包含 `:feature:settings` 依赖，确保设置路由可用。

功能验收：

- 启动 App 后进入 Home，底部三个 Tab 可正常切换。
- Mine 页面显示 APP Setting 入口。
- 点击 APP Setting 可以进入 APP 设置页。
- APP 设置页显示当前有效语言。
- 点击语言行可以进入语言设置页。
- 语言设置页提供中文和 English 两个选项。
- 切换语言后当前设置界面立即刷新。
- 返回 Home/Mine 后，底部 Tab 和 Mine 文案使用最新语言。
- 退出并重新进入 App 后，仍使用上次选择的语言。
- APP 设置页显示当前显示模式。
- 点击显示模式行可以进入显示模式设置页。
- 显示模式设置页提供明亮模式和黑夜模式两个选项。
- 切换显示模式后当前设置界面立即刷新。
- 返回 Home 后，Home、Device、Album、Mine 和 Settings 页面使用最新配色。
- 退出并重新进入 App 后，仍使用上次选择的显示模式。

资源验收：

- 正式 UI 文案使用 string resource。
- 英文资源位于 `values/strings.xml`。
- 简体中文资源位于 `values-zh-rCN/strings.xml`。

验证限制：

- 未经明确授权，不运行 `gradle`、`gradlew`、`gradlew.bat`。
- 未经明确授权，不执行会触发 Gradle 缓存、wrapper 下载、依赖解析、锁文件或 daemon 的构建验证。
