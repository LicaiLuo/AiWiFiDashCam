plugins {
    id("dashcam.android.feature")
}

android {
    namespace = "cn.anc.dashcam.mine"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:logging"))
    implementation(project(":core:navigation"))
}
