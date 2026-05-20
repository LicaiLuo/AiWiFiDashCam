/*
 * build-logic 设置文件
 * 
 * 定义插件注册和仓库配置
 */
pluginManagement {
    // 插件仓库
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "build-logic"
include(":convention")
