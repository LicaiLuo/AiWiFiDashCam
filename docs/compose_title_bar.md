# Jetpack Compose 标题栏封装设计说明书 (`DashCamTitleBar`)

用于替代和完美还原 DashCam 应用中传统 XML 视图的通用标题栏组件。该组件已成功部署至 `:core:ui` 基础公共 UI 模块中，路径为：
`cn.anc.dashcam.core.ui.DashCamTitleBar`

---

## 1. 核心设计思路：从 XML 到 Jetpack Compose 的完美映射

传统的 XML 布局采用的是嵌套的 `LinearLayout` + `RelativeLayout` 来实现：
*   **状态栏占位 (`StatusBarView`)**：XML 以前使用一个定制高度的 `StatusBarView` 用于防止内容被刘海屏或系统状态栏遮挡。
*   **左侧返回及额外内容**：`layout_title_back` 配合多个 `ImageView`（如 `iv_title_left_image_2`）和 `TextView`。
*   **绝对居中的核心标题 (`tv_title`)**：使用 `RelativeLayout` 的 `layout_centerInParent="true"`，无论左/右侧内容有多少，都需要将其绝对居中，单行展示，缩进处理。
*   **右侧操作按钮组**：右侧包含多个隐藏的按钮在水平容器内，包括图标按钮和文本按钮。

在 **Jetpack Compose 现代化组件封装**中，我们采用了以下的现代设计原则来实现高度自适应、沉浸式的高聚合组件：

1.  **安全区域感知 (Insets Integration)**：
    *   利用 Compose 原生的 `WindowInsets.statusBars` 获取每个 Android 设备运行时的顶层物理高度，动态转换为顶层 padding。无需在页面里手动计算，即可原生支持“沉浸式状态栏”。
2.  **绝对居中对齐模型 (`Box` 机制)**：
    *   为确保标题绝对位于屏幕中央，我们摒弃了纯 `Row` 模式（因为 `Row` 里的权重布局会导致标题被左右不平衡的动作按钮挤偏）。
    *   我们使用 `Box` 作为底层容器，将左侧区、主标题、右侧功能区放入其中。
    *   左侧、右侧、居中分别通过 `Modifier.align(Alignment.CenterStart/Center/CenterEnd)` 精确对应，并使用了 `horizontalPadding(72.dp)` 的安全限定区，确保长标题能自动省略并完美居中，不遮挡四周操作。
3.  **高度声明与配置组合化**：
    *   用标准 `RowScope` 重置左侧副工具栏和右侧主要动作区，避免了 XML 里大量无用 `visibility="gone"` 元素的堆积。

---

## 2. 封装函数源码说明

我们在 `:core:ui` 中成功注册并编译通过了如下组件：

```kotlin
@Composable
fun DashCamTitleBar(
    title: String,
    modifier: Modifier = Modifier,
    // 基础配置
    onBackClick: (() -> Unit)? = null,             // 传递非空表示支持返回
    backText: String? = null,                       // 返回对应的自定义文案 (例如 首页 或者是 "Back")
    backIcon: ImageVector = Icons.Default.ArrowBack,// 返回键图标覆盖
    
    // 背景与文字色自定义
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    
    // 沉浸式状态栏配置
    enableStatusBarPadding: Boolean = true,         // 是否自动启用状态栏安全感知高度
    
    // 进阶左侧自定义区域 (对应 原 XML 里的 left_image / txt_left)
    leftExtraContent: (@Composable RowScope.() -> Unit)? = null,
    
    // 进阶右侧自定义区域 (对应 原 XML 里的 txt_right / right_image)
    rightContent: (@Composable RowScope.() -> Unit)? = null
)
```

### XML 资源属性到 Compose 接口的映射表：

| XML ID / 功能 | Compose 参数名称 & 映射逻辑 | 备注 |
| :--- | :--- | :--- |
| `StatusBarView` | `enableStatusBarPadding = true` | 自动计算沉浸式状态栏高度顶出 |
| `layout_title_back` | `onBackClick: (() -> Unit)?` | 传入 `onClick` Lambda 代表需要展示返回功能键 |
| `tv_title_back` | `backText: String?` | 传入非空字符串（如 “返回”）则左侧额外贴附文本 |
| `iv_title_back` | `backIcon: ImageVector` | 默认是 Material 默认的 `ArrowBack` 矢量图标 |
| `tv_title` | `title: String` | 绝对单行居中主标题，字号 18sp，字体加粗 (Bold) |
| `left Extra Image/Text` | `leftExtraContent: @Composable RowScope.() -> Unit` | **高扩展性**：支持以插槽形式传入多个图标或文案 |
| `right Action Layout` | `rightContent: @Composable RowScope.() -> Unit` | **高扩展性**：支持以插槽形式传入任何操作图标或文本 |

---

## 3. 使用场景示例代码

### 场景 A：极简单标题（没有返回键，例如仪表盘首页）
```kotlin
DashCamTitleBar(title = "WiFi 记录仪")
```

### 场景 B：标准二级页面（有返回键带默认图标）
```kotlin
DashCamTitleBar(
    title = "照片相册",
    onBackClick = { 
        navController.popBackStack() 
    }
)
```

### 场景 C：高级配置（带右侧点击动作和自定义背景）
```kotlin
DashCamTitleBar(
    title = "记录仪设置",
    containerColor = Color(0xFF1E2022), // 经典记录仪深邃灰色背景
    contentColor = Color.White,         // 文字调白
    onBackClick = { 
        navController.popBackStack() 
    },
    rightContent = {
        // 这里的 Scope 为 RowScope，能够优雅横向排列
        Text(
            text = "保存",
            color = Color.Green,
            modifier = Modifier
                .clickable { /* 执行保存逻辑 */ }
                .padding(horizontal = 8.dp)
        )
        IconButton(onClick = { /* 更多菜单 */ }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
        }
    }
)
```

### 场景 D：支持左侧多功能扩展
```kotlin
DashCamTitleBar(
    title = "高级调试",
    onBackClick = { navController.popBackStack() },
    leftExtraContent = {
        // 返回键后面的额外小图标或文案
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp).padding(start = 4.dp)
        )
    }
)
```

---

## 4. 架构优势与设计亮点

*   **真正的零能耗占位 (Zero Overheads)**：相比在 XML 里需要一直初始化大量的隐藏 `LinearLayout`、`TextView` 和 `ImageView`，Compose 是“按需声明式执行”的。如果没有传入 `rightContent` 动作或者 `leftExtraContent` 插槽，运行时底层不会渲染任何无用布局。
*   **极致自适应**：原生适配平板、旋转屏、折叠大屏。中间 `title` 的 `padding(horizontal = 72.dp)` 完美控制文字，避免其在大屏上因为两端内容过长导致的重叠，满足 **Material 3 超宽容器** 规范。
