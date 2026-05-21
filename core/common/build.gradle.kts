plugins {
    id("dashcam.android.core")
}

android {
    namespace = "cn.anc.dashcam.core.common"
}

dependencies {
    api(project(":core:logging"))
    implementation(libs.tencent.mmkv)
    api(libs.androidx.activity.compose)
    api(libs.androidx.core.ktx)
}
