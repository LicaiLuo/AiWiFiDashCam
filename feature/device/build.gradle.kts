plugins {
    id("dashcam.android.feature")
}

android {
    namespace = "cn.anc.dashcam.device"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:logging"))
    implementation("androidx.compose.material:material-icons-core")
}
