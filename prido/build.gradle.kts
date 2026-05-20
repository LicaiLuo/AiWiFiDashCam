plugins {
    id("dashcam.android.application")
}

android {
    namespace = "cn.anc.dashcam.prido"

    defaultConfig {
        applicationId = "cn.anc.dashcam.prido"
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":feature:feedback"))
    implementation(project(":feature:splash"))
    implementation(project(":feature:home"))
    implementation(project(":feature:settings"))
}
