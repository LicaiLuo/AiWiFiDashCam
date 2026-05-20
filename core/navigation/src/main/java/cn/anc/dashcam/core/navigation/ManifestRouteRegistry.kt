package cn.anc.dashcam.core.navigation

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

class ManifestRouteRegistry(
    private val context: Context,
) : RouteRegistry {

    private val routes: Map<String, String> by lazy(::loadRoutes)

    override fun activityClassName(routeId: String): String? {
        return routes[routeId]
    }

    private fun loadRoutes(): Map<String, String> {
        val metadata = applicationMetadata()
        return metadata.keySet()
            .asSequence()
            .filter { it.startsWith(ROUTE_PREFIX) }
            .mapNotNull { key ->
                val routeId = key.removePrefix(ROUTE_PREFIX)
                val activityClassName = metadata.getString(key).orEmpty()
                if (routeId.isBlank() || activityClassName.isBlank()) {
                    null
                } else {
                    routeId to activityClassName
                }
            }
            .toMap()
    }

    private fun applicationMetadata(): Bundle {
        val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA,
            )
        }

        return appInfo.metaData ?: Bundle.EMPTY
    }

    companion object {
        const val ROUTE_PREFIX = "cn.anc.dashcam.route."
    }
}
