/*
 * Dashcam Convention Plugins
 * 
 * Copyright 2024 ANC
 * 
 * 库模块插件 - 自动配置 Android 库模块的基础构建参数
 */
package com.dashcam.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * 库模块插件
 * 
 * 应用此插件会自动配置：
 * - Android 库模块基础配置
 * - Kotlin Android 编译配置
 * - Compose 编译器插件
 * 
 * 使用方式：
 * plugins {
 *     id("dashcam.android.library")
 * }
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    
    override fun apply(target: Project) {
        target.plugins.apply("com.android.library")
        
        target.extensions.configure<LibraryExtension> {
            compileSdk = KotlinAndroid.COMPILE_SDK
            
            defaultConfig {
                minSdk = KotlinAndroid.MIN_SDK
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
