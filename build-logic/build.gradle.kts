/*
 * build-logic 构建脚本
 * 
 * 本项目使用 Kotlin DSL 编写 convention 插件
 */
plugins {
    // Kotlin DSL 插件，允许使用 Kotlin 编写 Gradle 插件
    `kotlin-dsl`
}

// 插件配置
group = "com.dashcam.buildlogic"

// 仓库配置
repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

// 依赖配置
dependencies {
    // 使用 Gradle 插件的默认依赖
    implementation(gradleApi())
    implementation(localGroovy())
}

// 禁止发布插件到 Gradle Plugin Portal
gradlePlugin {
    // 不需要自动发布插件
}
