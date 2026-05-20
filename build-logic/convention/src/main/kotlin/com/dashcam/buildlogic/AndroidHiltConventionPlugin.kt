/*
 * Dashcam Convention Plugins
 * 
 * Copyright 2024 ANC
 * 
 * Hilt DI 插件 - 为模块添加依赖注入支持
 */
package com.dashcam.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Hilt 依赖注入插件
 * 
 * 应用此插件会自动配置：
 * - Hilt Android 插件
 * - KSP 插件
 * - Hilt 依赖库
 * 
 * 使用方式：
 * plugins {
 *     id("dashcam.android.hilt")
 * }
 */
class AndroidHiltConventionPlugin : Plugin<Project> {
    
    override fun apply(target: Project) {
        // 应用 KSP 插件（用于注解处理）
        target.plugins.apply("com.google.devtools.ksp")
        
        // 添加 Hilt 依赖
        target.dependencies.add("implementation", "com.google.dagger:hilt-android:2.56")
        target.dependencies.add("ksp", "com.google.dagger:hilt-android-compiler:2.56")
    }
}
