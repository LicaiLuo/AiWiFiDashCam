package cn.anc.dashcam.splash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.anc.dashcam.core.logging.AppLog
import cn.anc.dashcam.core.navigation.ActivityFeatureNavigator
import cn.anc.dashcam.core.navigation.NavigationResult
import cn.anc.dashcam.core.ui.DashCamTitle

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.i("splash created", tag = "Splash")

        setContent {
            var navigationResult by remember { mutableStateOf<NavigationResult?>(null) }

            LaunchedEffect(Unit) {
                val startRoute = getString(R.string.dashcam_start_route)
                AppLog.i("navigate from splash route=$startRoute", tag = "Splash")
                navigationResult = ActivityFeatureNavigator(this@SplashActivity).navigate(startRoute)

                when (val result = navigationResult) {
                    NavigationResult.Success -> {
                        AppLog.i("splash navigation success route=$startRoute", tag = "Splash")
                        finish()
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
                    null -> Unit
                }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    DashCamTitle(text = getString(R.string.app_name))

                    when (val result = navigationResult) {
                        null -> Text(text = "Loading")
                        NavigationResult.Success -> Text(text = "Opening")
                        is NavigationResult.RouteNotFound -> Text(text = "Route not found: ${result.routeId}")
                        is NavigationResult.NavigationFailed -> Text(text = "Navigation failed: ${result.routeId}")
                    }
                }
            }
        }
    }
}
