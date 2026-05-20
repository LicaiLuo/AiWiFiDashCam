plugins {
    id("dashcam.android.feature")
}

android {
    namespace = "cn.anc.dashcam.settings"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:logging"))
}
