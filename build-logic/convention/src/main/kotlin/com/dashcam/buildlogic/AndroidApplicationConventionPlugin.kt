/*
 * Dashcam Convention Plugins
 * 
 * Copyright 2024 ANC
 * 
 * App 模块插件 - 在库模块基础上添加 App 特定配置
 */
package com.dashcam.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * App 模块插件
 * 
 * 应用此插件会自动配置：
 * - Android App 模块基础配置
 * - Kotlin Android 编译配置
 * - Compose 编译器插件
 * - Hilt 插件
 * - 版本号和版本名称
 * 
 * 使用方式：
 * plugins {
 *     id("dashcam.android.application")
 * }
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    
    override fun apply(target: Project) {
        target.plugins.apply("com.android.application")
        
        target.extensions.configure<ApplicationExtension> {
            compileSdk = KotlinAndroid.COMPILE_SDK

            defaultConfig {
                minSdk = KotlinAndroid.MIN_SDK
                targetSdk = KotlinAndroid.TARGET_SDK
                versionCode = 1
                versionName = "1.0.0"
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            compileOptions {
                sourceCompatibility = KotlinAndroid.JAVA_VERSION
                targetCompatibility = KotlinAndroid.JAVA_VERSION
            }
        }

        target.addImplementation("androidx-core-ktx")
        target.dependencies.add("testImplementation", target.libs().findLibrary("junit").get())
        target.addAndroidTestImplementation("androidx-junit")
        target.addAndroidTestImplementation("androidx-espresso-core")
        target.configureKotlinCompiler()
    }
}
