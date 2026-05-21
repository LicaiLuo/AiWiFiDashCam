package cn.anc.dashcam.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.anc.dashcam.core.logging.AppLog
import cn.anc.dashcam.core.navigation.NavigationResult
import cn.anc.dashcam.core.ui.DashCamTitle

@Composable
internal fun SplashRoute(
    appName: String,
    startRoute: String,
    navigateToStartRoute: (String) -> NavigationResult,
    onNavigationSuccess: () -> Unit,
) {
    var navigationResult by remember { mutableStateOf<NavigationResult?>(null) }

    LaunchedEffect(startRoute) {
        AppLog.i("navigate from splash route=$startRoute", tag = "Splash")
        val result = navigateToStartRoute(startRoute)
        navigationResult = result

        when (result) {
            NavigationResult.Success -> {
                AppLog.i("splash navigation success route=$startRoute", tag = "Splash")
                onNavigationSuccess()
            }
            is NavigationResult.RouteNotFound -> {
                AppLog.w("splash route not found route=${result.routeId}", tag = "Splash")
            }
            is NavigationResult.NavigationFailed -> {
                AppLog.e(
                    message = "splash navigation failed route=${result.routeId}",
                    throwable = result.reason,
                    tag = "Splash",
                )
            }
        }
    }

    SplashScreen(
        appName = appName,
        navigationResult = navigationResult,
    )
}

@Composable
internal fun SplashScreen(
    appName: String,
    navigationResult: NavigationResult?,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            DashCamTitle(text = appName)
            Text(text = navigationResult.statusText())
        }
    }
}

private fun NavigationResult?.statusText(): String {
    return when (this) {
        null -> "Loading"
        NavigationResult.Success -> "Opening"
        is NavigationResult.RouteNotFound -> "Route not found: $routeId"
        is NavigationResult.NavigationFailed -> "Navigation failed: $routeId"
    }
}

@Preview(name = "Splash Loading", showBackground = true)
@Composable
private fun SplashScreenLoadingPreview() {
    SplashScreen(
        appName = "Aoni DashCam",
        navigationResult = null,
    )
}

@Preview(name = "Splash Route Missing", showBackground = true)
@Composable
private fun SplashScreenRouteMissingPreview() {
    SplashScreen(
        appName = "Aoni DashCam",
        navigationResult = NavigationResult.RouteNotFound("home"),
    )
}
