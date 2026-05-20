package cn.anc.dashcam.core.navigation

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

class ActivityFeatureNavigator(
    private val context: Context,
    private val routeRegistry: RouteRegistry = ManifestRouteRegistry(context),
) : FeatureNavigator {

    override fun navigate(routeId: String): NavigationResult {
        val activityClassName = routeRegistry.activityClassName(routeId)
            ?: return NavigationResult.RouteNotFound(routeId)

        return try {
            val intent = Intent().setClassName(context.packageName, activityClassName)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            NavigationResult.Success
        } catch (error: ActivityNotFoundException) {
            NavigationResult.NavigationFailed(routeId, error)
        } catch (error: SecurityException) {
            NavigationResult.NavigationFailed(routeId, error)
        }
    }
}
