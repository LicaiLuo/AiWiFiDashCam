plugins {
    id("dashcam.android.core")
}

android {
    namespace = "cn.anc.dashcam.core.data"
}

dependencies {
    implementation(project(":core:model"))
}
