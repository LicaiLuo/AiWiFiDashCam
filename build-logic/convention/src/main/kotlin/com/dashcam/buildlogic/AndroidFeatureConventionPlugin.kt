/*
 * Dashcam Convention Plugins
 * 
 * Copyright 2024 ANC
 * 
 * Feature 模块插件 - 为功能模块提供一站式配置
 */
package com.dashcam.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Feature 功能模块插件
 * 
 * 应用此插件会自动配置：
 * - Android 库模块基础配置（AndroidLibraryConventionPlugin）
 * - Hilt 依赖注入支持（AndroidHiltConventionPlugin）
 * - Compose 依赖
 * 
 * 使用方式：
 * plugins {
 *     id("dashcam.android.feature")
 * }
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    
    override fun apply(target: Project) {
        val standaloneFeature = target.providers.gradleProperty("dashcam.standaloneFeature").orNull
        val isStandalone = standaloneFeature == target.path

        if (isStandalone) {
            target.plugins.apply("com.android.application")
            target.extensions.configure<ApplicationExtension> {
                compileSdk = KotlinAndroid.COMPILE_SDK

                defaultConfig {
                    applicationId = "cn.anc.dashcam.debug.${target.name.replace("-", "")}"
                    minSdk = KotlinAndroid.MIN_SDK
                    targetSdk = KotlinAndroid.TARGET_SDK
                    versionCode = 1
                    versionName = "1.0.0-dev"
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    manifestPlaceholders["dashcamStandaloneLauncherEnabled"] = "true"
                }

                compileOptions {
                    sourceCompatibility = KotlinAndroid.JAVA_VERSION
                    targetCompatibility = KotlinAndroid.JAVA_VERSION
                }
            }
        } else {
            target.plugins.apply(AndroidLibraryConventionPlugin::class.java)
            target.extensions.configure<LibraryExtension> {
                defaultConfig {
                    consumerProguardFiles("consumer-rules.pro")
                    manifestPlaceholders["dashcamStandaloneLauncherEnabled"] = "false"
                }
            }
        }

        target.plugins.apply(AndroidComposeConventionPlugin::class.java)
        target.configureKotlinCompiler()
    }
}
