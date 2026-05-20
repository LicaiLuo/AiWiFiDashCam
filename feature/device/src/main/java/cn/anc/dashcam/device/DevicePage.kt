package cn.anc.dashcam.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.AppThemeMode

@Composable
fun DevicePage(
    modifier: Modifier = Modifier,
) {
    val colors = DeviceColors.current()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.pageBackground),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.device_page_title),
            color = colors.textPrimary,
        )
    }
}

private data class DeviceColorScheme(
    val pageBackground: Color,
    val textPrimary: Color,
)

private object DeviceColors {

    @Composable
    fun current(): DeviceColorScheme {
        val context = LocalContext.current
        return if (AppThemeManager.currentThemeMode(context) == AppThemeMode.DARK) {
            DeviceColorScheme(
                pageBackground = Color(0xFF101418),
                textPrimary = Color(0xFFE8EDF2),
            )
        } else {
            DeviceColorScheme(
                pageBackground = Color(0xFFF4F4F4),
                textPrimary = Color(0xFF292929),
            )
        }
    }
}
