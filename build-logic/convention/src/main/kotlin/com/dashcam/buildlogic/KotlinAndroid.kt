/*
 * Dashcam Convention Plugins
 * 
 * 本文件定义了 Kotlin Android 项目的基础配置
 * 所有插件共享此配置
 */
package com.dashcam.buildlogic

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * 配置 Kotlin Android 项目
 * 
 * @param configure Kotlin Android 扩展配置回调
 */
fun Project.configureKotlinAndroid(
    configure: KotlinAndroidProjectExtension.() -> Unit,
) {
    extensions.configure<KotlinAndroidProjectExtension>(configure)
}

/**
 * 配置 Kotlin JVM 选项
 * 
 * @param configure JVM 选项配置回调
 */
fun KotlinAndroidProjectExtension.configureKotlinJvmOptions() {
    jvmToolchain(11)
}

fun Project.configureKotlinCompiler() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
}

fun Project.libs(): VersionCatalog {
    return extensions.getByType<VersionCatalogsExtension>().named("libs")
}

fun Project.addImplementation(alias: String) {
    dependencies.add("implementation", libs().findLibrary(alias).get())
}

fun Project.addDebugImplementation(alias: String) {
    dependencies.add("debugImplementation", libs().findLibrary(alias).get())
}

fun Project.addAndroidTestImplementation(alias: String) {
    dependencies.add("androidTestImplementation", libs().findLibrary(alias).get())
}

fun Project.addImplementationPlatform(alias: String) {
    dependencies.add("implementation", dependencies.platform(libs().findLibrary(alias).get()))
}

fun Project.addAndroidTestImplementationPlatform(alias: String) {
    dependencies.add("androidTestImplementation", dependencies.platform(libs().findLibrary(alias).get()))
}

/**
 * Kotlin Android 配置
 */
object KotlinAndroid {
    const val COMPILE_SDK = 36
    const val MIN_SDK = 24
    const val TARGET_SDK = 36
    
    val JAVA_VERSION = JavaVersion.VERSION_11
}
