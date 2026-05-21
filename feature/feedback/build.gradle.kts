plugins {
    id("dashcam.android.feature")
}

android {
    namespace = "cn.anc.dashcam.feedback"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:logging"))
}
