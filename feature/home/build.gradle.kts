plugins {
    id("dashcam.android.feature")
}

android {
    namespace = "cn.anc.dashcam.home"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:logging"))
    implementation(project(":core:navigation")) // 注入导航控制中心，适配多客户端间的模块跳转
    implementation(project(":feature:device"))
    implementation(project(":feature:album"))
    implementation(project(":feature:mine"))
    implementation(project(":feature:feedback")) // 增加反馈模块依赖，适配 PRIDO 的四个按钮中的反馈版块
    implementation("androidx.compose.material:material-icons-core") // 引入 Compose 标准矢量图标库，支持 Add, Settings, Close 等
}
