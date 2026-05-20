/*
 * Convention 插件模块构建脚本
 * 
 * 本模块定义了所有的 convention 插件
 */
plugins {
    // Kotlin DSL
    `kotlin-dsl`
}

// 仓库配置
repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

// 依赖 AGP API 以访问 Android 扩展类型
dependencies {
    // 使用 Gradle 插件的默认依赖
    implementation(gradleApi())
    implementation(localGroovy())
    
    // Android Gradle Plugin
    implementation("com.android.tools.build:gradle:9.1.1")
    
    // Kotlin Gradle Plugin
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.10")
}

// 插件注册
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "dashcam.android.application"
            implementationClass = "com.dashcam.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "dashcam.android.library"
            implementationClass = "com.dashcam.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "dashcam.android.feature"
            implementationClass = "com.dashcam.buildlogic.AndroidFeatureConventionPlugin"
        }
        register("androidCore") {
            id = "dashcam.android.core"
            implementationClass = "com.dashcam.buildlogic.AndroidCoreConventionPlugin"
        }
        register("androidCompose") {
            id = "dashcam.android.compose"
            implementationClass = "com.dashcam.buildlogic.AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "dashcam.android.hilt"
            implementationClass = "com.dashcam.buildlogic.AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "dashcam.android.room"
            implementationClass = "com.dashcam.buildlogic.AndroidRoomConventionPlugin"
        }
    }
}
