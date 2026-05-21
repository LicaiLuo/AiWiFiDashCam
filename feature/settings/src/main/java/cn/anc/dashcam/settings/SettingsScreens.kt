package cn.anc.dashcam.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.anc.dashcam.core.common.AppLanguage
import cn.anc.dashcam.core.common.AppThemeColor
import cn.anc.dashcam.core.common.AppThemeMode
import cn.anc.dashcam.core.common.StatusBarTextMode

@Composable
internal fun AppSettingsScreen(
    currentLanguage: AppLanguage,
    currentThemeMode: AppThemeMode,
    currentThemeColor: AppThemeColor,
    currentStatusBarTextMode: StatusBarTextMode,
    onBackClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onThemeClick: () -> Unit,
    onThemeColorClick: () -> Unit,
    onStatusBarClick: () -> Unit,
) {
    SettingsSurface {
        AppSettingsPage(
            currentLanguage = currentLanguage,
            currentThemeMode = currentThemeMode,
            currentThemeColor = currentThemeColor,
            currentStatusBarTextMode = currentStatusBarTextMode,
            onBackClick = onBackClick,
            onLanguageClick = onLanguageClick,
            onThemeClick = onThemeClick,
            onThemeColorClick = onThemeColorClick,
            onStatusBarClick = onStatusBarClick,
        )
    }
}

@Composable
internal fun LanguageSettingsScreen(
    selectedLanguage: AppLanguage,
    onBackClick: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
) {
    SettingsSurface {
        LanguageSettingsPage(
            selectedLanguage = selectedLanguage,
            onBackClick = onBackClick,
            onLanguageSelected = onLanguageSelected,
        )
    }
}

@Composable
internal fun ThemeSettingsScreen(
    selectedThemeMode: AppThemeMode,
    onBackClick: () -> Unit,
    onThemeModeSelected: (AppThemeMode) -> Unit,
) {
    SettingsSurface {
        ThemeSettingsPage(
            selectedThemeMode = selectedThemeMode,
            onBackClick = onBackClick,
            onThemeModeSelected = onThemeModeSelected,
        )
    }
}

@Composable
internal fun ThemeColorSettingsScreen(
    selectedColor: AppThemeColor,
    onBackClick: () -> Unit,
    onColorSelected: (AppThemeColor) -> Unit,
) {
    SettingsSurface {
        ThemeColorSettingsPage(
            selectedColor = selectedColor,
            onBackClick = onBackClick,
            onColorSelected = onColorSelected,
        )
    }
}

@Composable
internal fun StatusBarSettingsScreen(
    selectedMode: StatusBarTextMode,
    onBackClick: () -> Unit,
    onModeSelected: (StatusBarTextMode) -> Unit,
) {
    SettingsSurface {
        StatusBarSettingsPage(
            selectedMode = selectedMode,
            onBackClick = onBackClick,
            onModeSelected = onModeSelected,
        )
    }
}

@Composable
private fun SettingsSurface(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppSettingsColors.current().pageBackground,
        content = content,
    )
}

@Composable
internal fun AppSettingsPage(
    currentLanguage: AppLanguage,
    currentThemeMode: AppThemeMode,
    currentThemeColor: AppThemeColor,
    currentStatusBarTextMode: StatusBarTextMode,
    onBackClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onThemeClick: () -> Unit,
    onThemeColorClick: () -> Unit,
    onStatusBarClick: () -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_title),
        onBackClick = onBackClick,
    ) {
        SettingValueRow(
            title = stringResource(R.string.settings_language),
            value = stringResource(currentLanguage.labelRes),
            onClick = onLanguageClick,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SettingValueRow(
            title = stringResource(R.string.settings_theme_mode),
            value = stringResource(currentThemeMode.labelRes),
            onClick = onThemeClick,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SettingValueRow(
            title = stringResource(R.string.settings_theme_color),
            value = stringResource(currentThemeColor.labelRes),
            onClick = onThemeColorClick,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SettingValueRow(
            title = stringResource(R.string.settings_status_bar),
            value = stringResource(currentStatusBarTextMode.labelRes),
            onClick = onStatusBarClick,
        )
    }
}

@Composable
internal fun LanguageSettingsPage(
    selectedLanguage: AppLanguage,
    onBackClick: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_language_title),
        onBackClick = onBackClick,
    ) {
        OptionRow(
            title = stringResource(AppLanguage.ZH_CN.labelRes),
            selected = selectedLanguage == AppLanguage.ZH_CN,
            onClick = { onLanguageSelected(AppLanguage.ZH_CN) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OptionRow(
            title = stringResource(AppLanguage.EN.labelRes),
            selected = selectedLanguage == AppLanguage.EN,
            onClick = { onLanguageSelected(AppLanguage.EN) },
        )
    }
}

@Composable
internal fun ThemeSettingsPage(
    selectedThemeMode: AppThemeMode,
    onBackClick: () -> Unit,
    onThemeModeSelected: (AppThemeMode) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_theme_title),
        onBackClick = onBackClick,
    ) {
        OptionRow(
            title = stringResource(AppThemeMode.LIGHT.labelRes),
            selected = selectedThemeMode == AppThemeMode.LIGHT,
            onClick = { onThemeModeSelected(AppThemeMode.LIGHT) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OptionRow(
            title = stringResource(AppThemeMode.DARK.labelRes),
            selected = selectedThemeMode == AppThemeMode.DARK,
            onClick = { onThemeModeSelected(AppThemeMode.DARK) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OptionRow(
            title = stringResource(AppThemeMode.SYSTEM.labelRes),
            selected = selectedThemeMode == AppThemeMode.SYSTEM,
            onClick = { onThemeModeSelected(AppThemeMode.SYSTEM) },
        )
    }
}

@Composable
internal fun ThemeColorSettingsPage(
    selectedColor: AppThemeColor,
    onBackClick: () -> Unit,
    onColorSelected: (AppThemeColor) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_theme_color_title),
        onBackClick = onBackClick,
    ) {
        OptionRow(
            title = stringResource(AppThemeColor.BLUE.labelRes),
            selected = selectedColor == AppThemeColor.BLUE,
            onClick = { onColorSelected(AppThemeColor.BLUE) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OptionRow(
            title = stringResource(AppThemeColor.GREEN.labelRes),
            selected = selectedColor == AppThemeColor.GREEN,
            onClick = { onColorSelected(AppThemeColor.GREEN) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OptionRow(
            title = stringResource(AppThemeColor.ORANGE.labelRes),
            selected = selectedColor == AppThemeColor.ORANGE,
            onClick = { onColorSelected(AppThemeColor.ORANGE) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OptionRow(
            title = stringResource(AppThemeColor.PURPLE.labelRes),
            selected = selectedColor == AppThemeColor.PURPLE,
            onClick = { onColorSelected(AppThemeColor.PURPLE) },
        )
    }
}

@Composable
internal fun StatusBarSettingsPage(
    selectedMode: StatusBarTextMode,
    onBackClick: () -> Unit,
    onModeSelected: (StatusBarTextMode) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_status_bar_title),
        onBackClick = onBackClick,
    ) {
        OptionRow(
            title = stringResource(StatusBarTextMode.AUTO.labelRes),
            selected = selectedMode == StatusBarTextMode.AUTO,
            onClick = { onModeSelected(StatusBarTextMode.AUTO) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OptionRow(
            title = stringResource(StatusBarTextMode.DARK.labelRes),
            selected = selectedMode == StatusBarTextMode.DARK,
            onClick = { onModeSelected(StatusBarTextMode.DARK) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OptionRow(
            title = stringResource(StatusBarTextMode.LIGHT.labelRes),
            selected = selectedMode == StatusBarTextMode.LIGHT,
            onClick = { onModeSelected(StatusBarTextMode.LIGHT) },
        )
    }
}

@Preview(name = "Settings", showBackground = true)
@Composable
private fun AppSettingsPagePreview() {
    AppSettingsScreen(
        currentLanguage = AppLanguage.ZH_CN,
        currentThemeMode = AppThemeMode.SYSTEM,
        currentThemeColor = AppThemeColor.BLUE,
        currentStatusBarTextMode = StatusBarTextMode.AUTO,
        onBackClick = {},
        onLanguageClick = {},
        onThemeClick = {},
        onThemeColorClick = {},
        onStatusBarClick = {},
    )
}

@Preview(name = "Language Settings", showBackground = true)
@Composable
private fun LanguageSettingsPagePreview() {
    LanguageSettingsScreen(
        selectedLanguage = AppLanguage.ZH_CN,
        onBackClick = {},
        onLanguageSelected = {},
    )
}

@Preview(name = "Theme Settings", showBackground = true)
@Composable
private fun ThemeSettingsPagePreview() {
    ThemeSettingsScreen(
        selectedThemeMode = AppThemeMode.SYSTEM,
        onBackClick = {},
        onThemeModeSelected = {},
    )
}

@Preview(name = "Theme Color Settings", showBackground = true)
@Composable
private fun ThemeColorSettingsPagePreview() {
    ThemeColorSettingsScreen(
        selectedColor = AppThemeColor.BLUE,
        onBackClick = {},
        onColorSelected = {},
    )
}

@Preview(name = "Status Bar Settings", showBackground = true)
@Composable
private fun StatusBarSettingsPagePreview() {
    StatusBarSettingsScreen(
        selectedMode = StatusBarTextMode.AUTO,
        onBackClick = {},
        onModeSelected = {},
    )
}
