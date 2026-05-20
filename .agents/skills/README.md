# Skills 目录结构说明

本目录包含项目的所有 AI 技能（Skills），按类别组织以便于管理和使用。

## 目录结构

```
.agents/skills/
├── android/                          # Android 相关技能
│   ├── android-coding-standards/     # Android 特定开发规范（配合 kotlin/java-coding-standards 使用）
│   ├── agp-9-upgrade/               # Android Gradle Plugin 9 升级指南
│   ├── edge-to-edge/                 # Jetpack Compose 边缘到边界面迁移
│   ├── jetpack-compose-audit/         # Jetpack Compose 代码审计
│   ├── migrate-xml-views-to-jetpack-compose/  # XML 视图迁移到 Compose
│   ├── navigation-3/                 # Jetpack Navigation 3 指南
│   ├── play-billing-library-version-upgrade/  # Play Billing 库升级
│   └── r8-analyzer/                 # R8/Proguard 配置分析
├── cpp-coding-standards/             # C/C++ 代码规范
├── java-coding-standards/            # Java 代码规范
├── kotlin-coding-standards/          # Kotlin 代码规范
├── find-skills/                      # 技能查找工具
├── skill-creator/                    # 技能创建指南
└── user-preferences/                 # 用户自定义个性化配置
```

## 技能分类说明

### Android 相关技能 (`android/`)
包含所有与 Android 开发相关的技能，包括：
- **android-coding-standards** - Android 特定开发规范，专注于 Android 框架、生命周期、组件等
- **agp-9-upgrade** - Android Gradle Plugin 9 升级指南
- **edge-to-edge** - Jetpack Compose 边缘到边界面迁移
- **jetpack-compose-audit** - Jetpack Compose 代码审计
- **migrate-xml-views-to-jetpack-compose** - XML 视图迁移到 Compose
- **navigation-3** - Jetpack Navigation 3 指南
- **play-billing-library-version-upgrade** - Play Billing 库升级
- **r8-analyzer** - R8/Proguard 配置分析

### 代码规范技能
- **kotlin-coding-standards** - Kotlin 语言规范和最佳实践
- **java-coding-standards** - Java 语言规范和最佳实践
- **cpp-coding-standards** - C/C++ 语言规范和最佳实践

### 通用技能
- **find-skills** - 帮助发现和安装新技能
- **skill-creator** - 创建新技能的指南
- **user-preferences** - 用户自定义的全局配置和约束

## 技能使用关系

### Android 开发推荐技能组合
当进行 Android 开发时，建议按以下顺序使用技能：

1. **user-preferences** - 用户自定义配置（始终激活）
2. **kotlin-coding-standards** 或 **java-coding-standards** - 语言规范
3. **android-coding-standards** - Android 特定规范
4. 其他 Android 专项技能（如 jetpack-compose-audit、navigation-3 等）

### 技能依赖关系
- `android-coding-standards` 依赖 `kotlin-coding-standards` 和 `java-coding-standards`
- `android-coding-standards` 可与 `cpp-coding-standards` 配合使用（JNI/NDK 开发）

## 使用说明

每个技能都是一个独立的模块，包含：
- `SKILL.md` - 技能的主要定义文件
- `_meta.json` - 技能的元数据（可选）
- `references/` - 参考文档和详细指南（可选）
- `scripts/` - 可执行脚本（可选）
- `assets/` - 资源文件（可选）

技能会根据其描述和元数据自动触发，为 AI 提供特定领域的专业知识和工作流程。

## 注意事项

- 所有 Android 相关技能已移动到 `android/` 子目录
- `android-coding-standards` 现在专注于 Android 特定内容，通用 Kotlin/Java 规范已分离
- 用户自定义配置（`user-preferences/`）具有最高优先级，始终激活
- Google 官方技能保持原有英文内容，以确保与官方文档的一致性
