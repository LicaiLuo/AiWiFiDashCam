plugins {
    id("dashcam.android.application")
}

android {
    namespace = "cn.anc.dashcam.roaddrive"

    defaultConfig {
        applicationId = "cn.anc.dashcam.roaddrive"
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":feature:feedback"))
    implementation(project(":feature:splash"))
    implementation(project(":feature:home"))
    implementation(project(":feature:settings"))
}
