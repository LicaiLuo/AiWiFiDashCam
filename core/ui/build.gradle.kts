plugins {
    id("dashcam.android.core")
    id("dashcam.android.compose")
}

android {
    namespace = "cn.anc.dashcam.core.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation("androidx.compose.material:material-icons-core")
}

