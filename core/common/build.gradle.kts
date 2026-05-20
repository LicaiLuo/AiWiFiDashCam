plugins {
    id("dashcam.android.core")
}

android {
    namespace = "cn.anc.dashcam.core.common"
}

dependencies {
    api(project(":core:logging"))
    implementation(libs.tencent.mmkv)
}
