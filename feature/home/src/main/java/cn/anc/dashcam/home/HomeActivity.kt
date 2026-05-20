package cn.anc.dashcam.home

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.anc.dashcam.album.AlbumPage
import cn.anc.dashcam.core.common.AppLocaleManager
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.AppThemeMode
import cn.anc.dashcam.core.logging.AppLog
import cn.anc.dashcam.device.DevicePage
import cn.anc.dashcam.mine.MinePage

class HomeActivity : ComponentActivity() {

    private var activeLanguageTag: String? = null
    private var activeThemeModeTag: String? = null

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLocaleManager.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activeLanguageTag = AppLocaleManager.currentLanguageTag(this)
        activeThemeModeTag = AppThemeManager.currentThemeModeTag(this)
        AppLog.i("home created", tag = "Home")

        setContent {
            HomeScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        val currentLanguageTag = AppLocaleManager.currentLanguageTag(this)
        val currentThemeModeTag = AppThemeManager.currentThemeModeTag(this)
        if (activeLanguageTag != currentLanguageTag || activeThemeModeTag != currentThemeModeTag) {
            activeLanguageTag = currentLanguageTag
            activeThemeModeTag = currentThemeModeTag
            recreate()
        }
    }
}

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(MainTab.MINE) }
    val colors = HomeColors.current()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.pageBackground,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    MainTab.DEVICE -> DevicePage()
                    MainTab.LOCAL_ALBUM -> AlbumPage()
                    MainTab.MINE -> MinePage()
                }
            }
            MainBottomBar(
                selectedTab = selectedTab,
                onTabClick = {
                    AppLog.i("home tab selected tab=${it.name}", tag = "Home")
                    selectedTab = it
                },
            )
        }
    }
}

@Composable
private fun MainBottomBar(
    selectedTab: MainTab,
    onTabClick: (MainTab) -> Unit,
) {
    val colors = HomeColors.current()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(colors.bottomBarBackground)
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MainBottomTab(
            tab = MainTab.DEVICE,
            selected = selectedTab == MainTab.DEVICE,
            onClick = onTabClick,
        )
        MainBottomTab(
            tab = MainTab.LOCAL_ALBUM,
            selected = selectedTab == MainTab.LOCAL_ALBUM,
            onClick = onTabClick,
        )
        MainBottomTab(
            tab = MainTab.MINE,
            selected = selectedTab == MainTab.MINE,
            onClick = onTabClick,
        )
    }
}

@Composable
private fun MainBottomTab(
    tab: MainTab,
    selected: Boolean,
    onClick: (MainTab) -> Unit,
) {
    val label = stringResource(tab.labelRes)
    val colors = HomeColors.current()

    Column(
        modifier = Modifier
            .size(width = 98.dp, height = 58.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick(tab) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = if (selected) tab.selectedIconRes else tab.normalIconRes),
            contentDescription = label,
            modifier = Modifier.size(32.dp),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (selected) colors.selectedText else colors.unselectedText,
            fontSize = 14.sp,
        )
    }
}

private enum class MainTab(
    val labelRes: Int,
    val selectedIconRes: Int,
    val normalIconRes: Int,
) {
    DEVICE(
        labelRes = R.string.home_tab_device,
        selectedIconRes = R.drawable.ic_main_nav_device_s,
        normalIconRes = R.drawable.ic_main_nav_device_n,
    ),
    LOCAL_ALBUM(
        labelRes = R.string.home_tab_album,
        selectedIconRes = R.drawable.ic_main_nav_album_s,
        normalIconRes = R.drawable.ic_main_nav_album_n,
    ),
    MINE(
        labelRes = R.string.home_tab_mine,
        selectedIconRes = R.drawable.ic_main_nav_mine_s,
        normalIconRes = R.drawable.ic_main_nav_mine_n,
    ),
}

private data class HomeColorScheme(
    val pageBackground: Color,
    val bottomBarBackground: Color,
    val selectedText: Color,
    val unselectedText: Color,
)

private object HomeColors {

    @Composable
    fun current(): HomeColorScheme {
        val context = androidx.compose.ui.platform.LocalContext.current
        return if (AppThemeManager.currentThemeMode(context) == AppThemeMode.DARK) {
            HomeColorScheme(
                pageBackground = Color(0xFF101418),
                bottomBarBackground = Color(0xFF171D23),
                selectedText = Color(0xFF8DC6E8),
                unselectedText = Color(0xFF8B96A1),
            )
        } else {
            HomeColorScheme(
                pageBackground = Color(0xFFF4F4F4),
                bottomBarBackground = Color.White,
                selectedText = Color(0xFF26495C),
                unselectedText = Color(0xFF999999),
            )
        }
    }
}
