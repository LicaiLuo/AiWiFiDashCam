---
name: java-coding-standards
description: Java 代码规范和最佳实践。使用此技能编写、审查或修改 Java 代码时，确保符合 Java 语言规范、命名约定、错误处理、并发编程和代码组织原则。
---

# Java 代码规范 (Java Coding Standards)

本 skill 定义了 Java 代码的编写规范，AI 助手在生成或修改 Java 代码时必须遵循以下规则。

## 适用场景

- 编写新的 Java 代码
- 审查现有 Java 代码
- 重构或修改 Java 代码
- Java 并发、线程、集合相关代码
- Java 异常处理、资源管理

## 命名规范

- **类、接口、枚举、注解**：使用 `PascalCase`，如 `UserRepository`、`NavigationResult`
- **接口**：可以使用形容词形式，如 `Runnable`、`Serializable`
- **方法、变量**：使用 `lowerCamelCase`，如 `navigate()`、`userId`
- **常量**：使用 `UPPER_SNAKE_CASE`，声明为 `static final`
- **包名**：全小写，使用点分隔，如 `com.example.project`
- **文件名**：与公共类名完全一致，包括大小写
- **禁止**：使用拼音、无意义缩写、过短名称、非 ASCII 标识符

## 代码风格

### 基本原则

- 优先使用 `final`，只有确实需要重新赋值时才不用
- 显式处理空值，避免 `NullPointerException`
- 公共 API、非显而易见的返回类型应显式声明类型
- 方法保持单一职责，复杂条件拆成具名私有方法
- 控即使只有单行也使用花括号
- 不使用 `System.out.println()`、`printStackTrace()` 做调试输出
- 不吞掉异常；捕获异常后返回明确结果或记录日志

### 空安全

- 使用 `Objects.requireNonNull()` 验证非空参数
- 使用 `Optional` 表示可能缺失的值
- 使用 `@Nullable`、`@NonNull` 注解标记可空性
- 优先使用 `String.valueOf()` 而非 `toString()` 避免 NPE

### 集合操作

- 优先使用接口类型声明集合（`List`、`Set`、`Map`）
- 使用不可变集合（`Collections.unmodifiableList()`）
- 使用泛型避免类型转换
- 使用 `Stream` API 进行集合操作
- 避免在循环中修改集合

## 类与接口设计

### 类设计

- 优先使用不可变类
- 使用 `final` 类防止继承，除非设计为基类
- 使用 `private` 字段，通过 getter/setter 访问
- 使用 `Builder` 模式构建复杂对象
- 重写 `equals()`、`hashCode()`、`toString()` 时保持一致性

### 接口设计

- 接口保持简洁，只定义契约
- 使用默认方法谨慎提供实现
- 使用函数式接口（`@FunctionalInterface`）支持 Lambda

## 异常处理

### 异常处理原则

- 使用特定异常而非通用 `Exception`
- 使用 `try/catch/finally` 确保资源释放
- 使用 `try-with-resources` 自动管理资源
- 不吞掉异常，至少记录日志
- 提供有意义的错误消息

### 自定义异常

- 继承适当的异常基类
- 提供详细的错误信息
- 包含原始异常作为原因（`cause`）

## 并发编程

### 线程安全

- 使用 `synchronized` 或 `Lock` 保护共享状态
- 使用 `volatile` 保证可见性
- 使用 `Atomic` 类进行原子操作
- 使用线程安全集合（`ConcurrentHashMap`、`CopyOnWriteArrayList`）
- 避免死锁，按固定顺序获取锁

### 线程池

- 使用 `ExecutorService` 管理线程池
- 使用 `ForkJoinPool` 进行并行计算
- 正确关闭线程池，避免资源泄漏
- 使用 `CompletableFuture` 进行异步编程

## 资源管理

### 资源释放

- 使用 `try-with-resources` 语句
- 实现 `AutoCloseable` 接口
- 在 `finally` 块中释放资源
- 避免资源泄漏

### IO 操作

- 使用缓冲流提高性能
- 使用 `NIO` 进行高性能 IO
- 正确处理字符编码
- 关闭流和通道

## 代码组织

### 包结构

- 按功能或层次组织包
- 避免循环依赖
- 使用合理的包深度

### 导入

- 不使用通配符导入（`import package.*`）
- 按标准顺序组织导入：标准库、第三方库、项目内部
- 移除未使用的导入

## 泛型使用

- 使用泛型提高类型安全
- 使用通配符（`?`、`? extends`、`? super`）提高灵活性
- 避免原始类型（`raw types`）
- 使用类型推断减少冗余

## 注解使用

- 使用标准注解（`@Override`、`@Deprecated`、`@SuppressWarnings`）
- 使用自定义注解提供元数据
- 使用注解处理器生成代码

## 禁止模式

```java
// 禁止：使用原始类型
List list = new ArrayList();

// 替代：使用泛型
List<String> list = new ArrayList<>();

// 禁止：吞掉异常
try {
    riskyOperation();
} catch (Exception e) {
    // 什么都不做
}

// 替代：处理异常或记录日志
try {
    riskyOperation();
} catch (Exception e) {
    logger.error("Operation failed", e);
    throw e;
}

// 禁止：使用 System.out.println 调试
System.out.println("Debug: " + value);

// 替代：使用适当的日志框架
logger.debug("Value: {}", value);

// 禁止：忘记关闭资源
FileInputStream fis = new FileInputStream("file.txt");
// 使用 fis...

// 替代：使用 try-with-resources
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // 使用 fis...
}

// 禁止：在循环中创建对象
for (int i = 0; i < 1000; i++) {
    List<String> list = new ArrayList<>();
    // 使用 list...
}

// 替代：在循环外创建对象
List<String> list = new ArrayList<>();
for (int i = 0; i < 1000; i++) {
    // 使用 list...
}
```

## 最佳实践

### 性能优化

- 使用 `StringBuilder` 而非字符串连接
- 缓存频繁使用的对象
- 避免不必要的对象创建
- 使用延迟初始化

### 可读性

- 使用有意义的变量和方法名
- 保持方法简短，单一职责
- 添加必要的注释解释复杂逻辑
- 使用空行分隔逻辑块

### 安全性

- 验证输入参数
- 使用不可变对象
- 避免敏感信息泄露
- 使用安全的随机数生成器

## 审查清单

- [ ] 命名符合 Java 规范
- [ ] 使用泛型，避免原始类型
- [ ] 正确处理异常，不吞掉错误
- [ ] 使用 try-with-resources 管理资源
- [ ] 线程安全，避免竞态条件
- [ ] 代码组织清晰，职责单一
- [ ] 不使用通配符导入
- [ ] 不使用 `System.out.println()` 调试
- [ ] 重写 `equals()` 时同时重写 `hashCode()`
- [ ] 使用不可变类和对象
