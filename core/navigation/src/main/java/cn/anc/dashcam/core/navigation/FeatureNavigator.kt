package cn.anc.dashcam.core.navigation

interface FeatureNavigator {
    fun navigate(routeId: String): NavigationResult
}

sealed interface NavigationResult {
    data object Success : NavigationResult
    data class RouteNotFound(val routeId: String) : NavigationResult
    data class NavigationFailed(val routeId: String, val reason: Throwable) : NavigationResult
}
