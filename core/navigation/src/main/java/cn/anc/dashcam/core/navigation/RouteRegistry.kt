package cn.anc.dashcam.core.navigation

interface RouteRegistry {
    fun activityClassName(routeId: String): String?
}
