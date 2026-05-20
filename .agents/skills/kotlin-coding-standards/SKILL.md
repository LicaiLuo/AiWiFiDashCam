---
name: kotlin-coding-standards
description: Kotlin 代码规范和最佳实践。使用此技能编写、审查或修改 Kotlin 代码时，确保符合 Kotlin 语言规范、命名约定、错误处理、协程使用和代码组织原则。
---

# Kotlin 代码规范 (Kotlin Coding Standards)

本 skill 定义了 Kotlin 代码的编写规范，AI 助手在生成或修改 Kotlin 代码时必须遵循以下规则。

## 适用场景

- 编写新的 Kotlin 代码
- 审查现有 Kotlin 代码
- 重构或修改 Kotlin 代码
- Kotlin 协程、Flow、StateFlow 相关代码
- Kotlin 数据类、密封类、扩展函数等特性使用

## 命名规范

- **类、接口、枚举、密封类型**：使用 `PascalCase`，如 `UserRepository`、`NavigationResult`
- **函数、变量、属性**：使用 `lowerCamelCase`，如 `navigate()`、`userId`
- **常量**：使用 `UPPER_SNAKE_CASE`，优先声明为 `private const val`
- **文件名**：与主要 public 类型保持一致，如 `UserRepository.kt`、`Utils.kt`
- **扩展函数**：接收者类型在前，功能描述在后，如 `String.isValidEmail()`
- **Composable 函数**：使用名词或 UI 组件名，首字母大写，如 `HomeScreen()`、`UserProfile()`
- **事件回调**：使用 `onXxx` 命名，如 `onBackClick`、`onRouteSelected`
- **禁止**：使用拼音、无意义缩写、过短名称、通非 ASCII 标识符

## 代码风格

### 基本原则

- 优先使用 `val`，只有确实需要重新赋值时才用 `var`
- 显式表达可空性，避免使用 `!!` 非空断言
- 公共 API、跨模块模型、非显而易见的返回类型应显式声明类型
- 函数保持单一职责，复杂条件拆成具名私有函数
- 控制语句即使只有单行也使用花括号
- 不使用 `println()`、`printStackTrace()` 做调试输出
- 不吞掉异常；捕获异常后返回明确结果或记录日志

### 空安全

- 优先使用 `?.` 安全调用操作符而非 `!!`
- 使用 `?:` 提供默认值而非 `!!`
- 使用 `let`、`also`、`apply`、`run`、`with` 等作用域函数简化代码
- 使用 `require`、`check` 进行参数和状态验证

### 集合操作

- 优先使用不可变集合（`listOf`、`mapOf`、`setOf`）
- 使用序列（`asSequence()`）处理大型集合链式操作
- 使用 `map`、`filter`、`flatMap` 等函数式操作
- 避免在循环中修改集合

## 函数与类

### 函数设计

- 函数参数不超过 5 个，超过时考虑使用数据类
- 使用默认参数减少重载
- 使用命名参数提高可读性
- 高阶函数使用 `inline` 优化性能

### 类设计

- 优先使用 `data class` 表示数据载体
- 使用 `sealed class/interface` 表示受限的类层次结构
- 单例使用 `object` 而非类
- 优先使用 `companion object` 而非 Java 静态成员

## 协程与 Flow

### 协程使用

- 使用结构化并发，避免 `GlobalScope`
- 使用适当的 `CoroutineScope`：`viewModelScope`、`lifecycleScope`、`coroutineScope`
- 使用 `suspend` 函数而非阻塞调用
- 使用 `withContext(Dispatchers.IO)` 切换到 IO 线程
- 使用 `try/catch` 或 `CoroutineExceptionHandler` 处理协程异常

### Flow 使用

- 使用 `StateFlow` 表示状态，`SharedFlow` 表示事件
- 使用 `asStateFlow()` 暴露只读 `StateFlow`
- 使用 `collectAsStateWithLifecycle()` 在 Compose 中收集 Flow
- 使用 `flow` 构建器创建冷流
- 使用 `shareIn`、`stateIn` 转换为热流

## 错误处理

### 异常处理

- 使用 `try/catch` 捕获可恢复异常
- 使用 `Result` 类型或自定义结果类型表示操作结果
- 不吞掉异常，至少记录日志
- 提供有意义的错误消息

### 资源管理

- 使用 `use` 块管理 `Closeable` 资源
- 使用 `DisposableEffect` 管理需要释放的资源
- 避免资源泄漏

## 代码组织

### 文件结构

- 一个文件只包含一个主要的公共类/接口
- 相关的私有类可以放在同一文件中
- 使用包声明组织代码

### 导入

- 不使用通配符导入（`import package.*`）
- 按字母顺序组织导入
- 移除未使用的导入

## 禁止模式

```kotlin
// 禁止：使用 !! 非空断言
val name = user.name!!

// 替代：使用安全调用或提供默认值
val name = user.name ?: "Unknown"

// 禁止：使用 GlobalScope
GlobalScope.launch { }

// 替代：使用适当的 CoroutineScope
viewModelScope.launch { }

// 禁止：在主线程执行阻塞操作
val data = readFileBlocking()

// 替代：使用协程
val data = withContext(Dispatchers.IO) { readFileBlocking() }

// 禁止：吞掉异常
try {
    riskyOperation()
} catch (e: Exception) {
    // 什么都不做
}

// 替代：处理异常或记录日志
try {
    riskyOperation()
} catch (e: Exception) {
    logger.error("Operation failed", e)
    return Result.Error(e)
}

// 禁止：使用 println 调试
println("Debug: $value")

// 替代：使用适当的日志框架
logger.debug("Value: $value")
```

## 最佳实践

### 性能优化

- 使用 `inline` 函数减少高阶函数开销
- 使用 `const val` 编译时常量
- 避免不必要的对象创建
- 使用 `lazy` 延迟初始化

### 可读性

- 使用有意义的变量和函数名
- 保持函数简短，单一职责
- 添加必要的注释解释复杂逻辑
- 使用空行分隔逻辑块

### 测试可测试性

- 依赖注入而非硬编码依赖
- 使用接口而非具体类
- 避免静态状态
- 使函数纯函数化，便于测试

## 审查清单

- [ ] 命名符合 Kotlin 规范
- [ ] 优先使用 `val` 而非 `var`
- [ ] 避免使用 `!!` 非空断言
- [ ] 使用结构化并发，避免 `GlobalScope`
- [ ] 正确处理异常，不吞掉错误
- [ ] 使用适当的协程调度器
- [ ] 资源正确管理，无泄漏
- [ ] 代码组织清晰，职责单一
- [ ] 不使用通配符导入
- [ ] 不使用 `println()`、`printStackTrace()` 调试
