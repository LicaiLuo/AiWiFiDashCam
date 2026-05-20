plugins {
    id("dashcam.android.feature")
}

android {
    namespace = "cn.anc.dashcam.device"
}

dependencies {
    implementation(project(":core:common"))
}
