/*
 * Dashcam Convention Plugins
 * 
 * Copyright 2024 ANC
 * 
 * Room 数据库插件 - 为模块添加本地数据库支持
 */
package com.dashcam.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Room 数据库插件
 * 
 * 应用此插件会自动配置：
 * - KSP 插件（用于 Room 注解处理）
 * - Room 依赖库
 * - Room Kotlin 代码生成
 * 
 * 使用方式：
 * plugins {
 *     id("dashcam.android.room")
 * }
 */
class AndroidRoomConventionPlugin : Plugin<Project> {
    
    override fun apply(target: Project) {
        // 应用 KSP 插件
        target.plugins.apply("com.google.devtools.ksp")
        
        target.dependencies.add("implementation", "androidx.room:room-runtime:2.7.7")
        target.dependencies.add("implementation", "androidx.room:room-ktx:2.7.7")
        target.dependencies.add("ksp", "androidx.room:room-compiler:2.7.7")
    }
}
