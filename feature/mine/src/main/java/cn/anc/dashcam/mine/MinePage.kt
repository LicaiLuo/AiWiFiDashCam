package cn.anc.dashcam.mine

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.AppThemeMode
import cn.anc.dashcam.core.logging.AppLog
import cn.anc.dashcam.core.navigation.ActivityFeatureNavigator
import cn.anc.dashcam.core.navigation.NavigationResult

@Composable
fun MinePage(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val colors = MineColors.current()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.pageBackground)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        LoginRow()
        Spacer(modifier = Modifier.height(56.dp))
        MineActionRow(
            iconRes = R.drawable.ic_account_message,
            title = stringResource(R.string.mine_messages),
        )
        Spacer(modifier = Modifier.height(12.dp))
        MineActionRow(
            iconRes = R.drawable.ic_account_setup,
            title = stringResource(R.string.mine_app_settings),
            onClick = {
                AppLog.i("open settings from mine", tag = "Mine")
                val result = ActivityFeatureNavigator(context).navigate("settings")
                when (result) {
                    NavigationResult.Success -> AppLog.i("settings navigation success", tag = "Mine")
                    is NavigationResult.RouteNotFound -> {
                        AppLog.w("settings route not found route=${result.routeId}", tag = "Mine")
                    }
                    is NavigationResult.NavigationFailed -> {
                        AppLog.e(
                            message = "settings navigation failed route=${result.routeId}",
                            throwable = result.reason,
                            tag = "Mine",
                        )
                    }
                }
            },
        )
        Spacer(modifier = Modifier.height(12.dp))
        MineActionRow(
            iconRes = R.drawable.ic_account_help,
            title = stringResource(R.string.mine_help),
        )
        Spacer(modifier = Modifier.height(12.dp))
        MineActionRow(
            iconRes = R.drawable.ic_account_more,
            title = stringResource(R.string.mine_more),
            onClick = {
                AppLog.i("open feedback from mine", tag = "Mine")
                val result = ActivityFeatureNavigator(context).navigate("feedback")
                when (result) {
                    NavigationResult.Success -> AppLog.i("feedback navigation success", tag = "Mine")
                    is NavigationResult.RouteNotFound -> {
                        AppLog.w("feedback route not found route=${result.routeId}", tag = "Mine")
                    }
                    is NavigationResult.NavigationFailed -> {
                        AppLog.e(
                            message = "feedback navigation failed route=${result.routeId}",
                            throwable = result.reason,
                            tag = "Mine",
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun LoginRow() {
    val colors = MineColors.current()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.mine_register_login),
            color = colors.textPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = ">",
            color = colors.textPrimary,
            fontSize = 34.sp,
            fontWeight = FontWeight.Light,
        )
    }
}

@Composable
private fun MineActionRow(
    iconRes: Int,
    title: String,
    onClick: (() -> Unit)? = null,
) {
    val colors = MineColors.current()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(colors.cardBackground)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(start = 20.dp, end = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
        )
        Text(
            text = title,
            color = colors.textPrimary,
            fontSize = 20.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
        )
        Text(
            text = ">",
            color = colors.textPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
        )
    }
}

private data class MineColorScheme(
    val pageBackground: Color,
    val cardBackground: Color,
    val textPrimary: Color,
)

private object MineColors {

    @Composable
    fun current(): MineColorScheme {
        val context = LocalContext.current
        return if (AppThemeManager.currentThemeMode(context) == AppThemeMode.DARK) {
            MineColorScheme(
                pageBackground = Color(0xFF101418),
                cardBackground = Color(0xFF1C2228),
                textPrimary = Color(0xFFE8EDF2),
            )
        } else {
            MineColorScheme(
                pageBackground = Color(0xFFF4F4F4),
                cardBackground = Color.White,
                textPrimary = Color(0xFF292929),
            )
        }
    }
}
