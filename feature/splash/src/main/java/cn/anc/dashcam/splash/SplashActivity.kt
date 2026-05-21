package cn.anc.dashcam.splash

import android.os.Bundle
import androidx.activity.compose.setContent
import cn.anc.dashcam.core.common.BaseActivity
import cn.anc.dashcam.core.logging.AppLog
import cn.anc.dashcam.core.navigation.ActivityFeatureNavigator

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.i("splash created", tag = "Splash")

        setContent {
            SplashRoute(
                appName = getString(R.string.app_name),
                startRoute = getString(R.string.dashcam_start_route),
                navigateToStartRoute = { route ->
                    ActivityFeatureNavigator(this@SplashActivity).navigate(route)
                },
                onNavigationSuccess = ::finish,
            )
        }
    }
}
