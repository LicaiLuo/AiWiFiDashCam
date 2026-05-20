plugins {
    id("dashcam.android.feature")
}

android {
    namespace = "cn.anc.dashcam.album"
}

dependencies {
    implementation(project(":core:common"))
}
