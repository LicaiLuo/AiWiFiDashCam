plugins {
    id("dashcam.android.feature")
}

android {
    namespace = "cn.anc.dashcam.splash"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:logging"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
}
