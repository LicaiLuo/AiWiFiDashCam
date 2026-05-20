plugins {
    id("dashcam.android.feature")
}

android {
    namespace = "cn.anc.dashcam.home"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:logging"))
    implementation(project(":feature:device"))
    implementation(project(":feature:album"))
    implementation(project(":feature:mine"))
}
