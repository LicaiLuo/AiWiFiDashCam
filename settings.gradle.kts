pluginManagement {

    // build-logic 插件目录
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.aliyun.com/repository/public") }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/public") }
    }
}

rootProject.name = "AoniAiDashCam"
include(":prido")
include(":uniden")
include(":core:common")
include(":core:data")
include(":core:logging")
include(":core:model")
include(":core:navigation")
include(":core:ui")
include(":feature:feedback")
include(":feature:splash")
include(":feature:home")
include(":feature:device")
include(":feature:album")
include(":feature:mine")
include(":feature:settings")
