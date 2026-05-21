plugins {
    id("dashcam.android.application") // 导入公共的 android 自适应应用插件规则
}

android {
    namespace = "cn.anc.dashcam.cooau" // 声明 cooau 唯一的类库命名空间

    defaultConfig {
        applicationId = "cn.anc.dashcam.cooau" // 声明应用 ID 为 cn.anc.dashcam.cooau
        versionName = "1.0" // 定义基本版本号 1.0
    }
}

dependencies {
    implementation(project(":core:common")) // 引入底层通用层，取得 AppConfig 支配权
    implementation(project(":core:data")) // 引入核心数据层，取得颜色和 OSD 配置
    implementation(project(":feature:feedback")) // 引入反馈模块，支持侧滑弹出反馈
    implementation(project(":feature:splash")) // 引入闪屏启动过渡页面
    implementation(project(":feature:home")) // 引入整合的主页面功能模块
    implementation(project(":feature:settings")) // 引入设置以及多语言外观设定模块
}
