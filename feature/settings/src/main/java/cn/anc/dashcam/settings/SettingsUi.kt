package cn.anc.dashcam.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.anc.dashcam.core.common.AppLanguage
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.AppThemeMode

@Composable
internal fun SettingsScaffold(
    title: String,
    onBackClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = AppSettingsColors.current()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.pageBackground)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "<",
                color = colors.textPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .size(width = 48.dp, height = 48.dp)
                    .clickable(onClick = onBackClick),
            )
            Text(
                text = title,
                color = colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Column(content = content)
    }
}

@Composable
internal fun SettingValueRow(
    title: String,
    value: String,
    onClick: () -> Unit,
) {
    val colors = AppSettingsColors.current()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = colors.textPrimary,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = colors.textSecondary,
            fontSize = 16.sp,
        )
        Text(
            text = ">",
            color = colors.textSecondary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
internal fun OptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = AppSettingsColors.current()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .clickable(onClick = onClick)
            .padding(start = 20.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = colors.textPrimary,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f),
        )
        RadioButton(
            selected = selected,
            onClick = onClick,
        )
    }
}

@get:StringRes
internal val AppLanguage.labelRes: Int
    get() = when (this) {
        AppLanguage.ZH_CN -> R.string.settings_language_chinese
        AppLanguage.EN -> R.string.settings_language_english
    }

@get:StringRes
internal val AppThemeMode.labelRes: Int
    get() = when (this) {
        AppThemeMode.LIGHT -> R.string.settings_theme_light
        AppThemeMode.DARK -> R.string.settings_theme_dark
    }

internal data class SettingsColors(
    val pageBackground: Color,
    val cardBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
)

internal object AppSettingsColors {

    @Composable
    fun current(): SettingsColors {
        val context = LocalContext.current
        return if (AppThemeManager.currentThemeMode(context) == AppThemeMode.DARK) {
            SettingsColors(
                pageBackground = Color(0xFF101418),
                cardBackground = Color(0xFF1C2228),
                textPrimary = Color(0xFFE8EDF2),
                textSecondary = Color(0xFF9BA7B3),
            )
        } else {
            SettingsColors(
                pageBackground = Color(0xFFF4F4F4),
                cardBackground = Color.White,
                textPrimary = Color(0xFF292929),
                textSecondary = Color(0xFF777777),
            )
        }
    }
}
