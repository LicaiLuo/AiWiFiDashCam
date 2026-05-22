package cn.anc.dashcam.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import cn.anc.dashcam.core.common.AppThemeManager
import cn.anc.dashcam.core.common.BaseActivity
import cn.anc.dashcam.core.logging.AppLog
import cn.anc.dashcam.core.ui.DashCamTitleBar

class LiveStreamPreviewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.i("Live stream preview activity created", tag = "Settings")

        setContent {
            var isFullScreen by remember { mutableStateOf(false) }

            // Sync full screen mode using side effect
            LaunchedEffect(isFullScreen) {
                val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
                if (isFullScreen) {
                    // Hide BOTH status bars and navigation bars (Immersive)
                    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                    windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    // Restore them
                    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                }
            }

            LiveStreamPreviewScreen(
                isFullScreen = isFullScreen,
                onFullScreenToggle = { isFullScreen = it },
                onBackClick = {
                    if (isFullScreen) {
                        isFullScreen = false
                    } else {
                        finish()
                    }
                }
            )
        }
    }
}

@Composable
fun LiveStreamPreviewScreen(
    isFullScreen: Boolean,
    onFullScreenToggle: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    val themeColor = AppThemeManager.currentThemeColor(androidx.compose.ui.platform.LocalContext.current)
    val appColors = AppSettingsColors.current()

    if (isFullScreen) {
        // Fully Immersive Full Screen Mode
        var showOverlay by remember { mutableStateOf(true) }

        // Automatically hide overlay controls after 3 seconds of inactiveness
        LaunchedEffect(showOverlay) {
            if (showOverlay) {
                kotlinx.coroutines.delay(3500)
                showOverlay = false
            }
        }

         Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = cn.anc.dashcam.core.common.R.color.settings_pure_black))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showOverlay = !showOverlay
                },
            contentAlignment = Alignment.Center
        ) {
            // Simulated 16:9 Real-time Stream Canvas (Gray Panel)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(colorResource(id = cn.anc.dashcam.core.common.R.color.live_stream_shading_full)) // Dark slate-gray representing the raw video stream layer
                    .clip(RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "16 : 9",
                        color = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_white_alpha_80),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.settings_live_stream_mode_full),
                        color = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_white_alpha_60),
                        fontSize = 12.sp,
                        textAlign = Alignment.CenterHorizontally.let { TextAlign.Center }
                    )
                }
            }

             // Overlay UI Controls (HUD)
            AnimatedVisibility(
                visible = showOverlay,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Dark translucent status bar overlay and exit back at top left
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(colorResource(id = cn.anc.dashcam.core.common.R.color.settings_overlay_bg))
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Exit Fullscreen",
                                tint = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_pure_white)
                            )
                        }
                        Text(
                            text = stringResource(R.string.settings_live_stream_demo),
                            color = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_pure_white),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    // Floating description indicator and toggle controls at Bottom Center
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(colorResource(id = cn.anc.dashcam.core.common.R.color.settings_overlay_bg))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.settings_live_stream_preview_exit_hint),
                            color = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_white_alpha_80),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onFullScreenToggle(false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(themeColor.lightHex)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.settings_live_stream_preview_btn),
                                color = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_pure_white),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Standard non-fullscreen setup with title bar and details
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(appColors.pageBackground)
        ) {
            DashCamTitleBar(
                title = stringResource(R.string.settings_live_stream_preview_title),
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Section Title header
                Text(
                    text = stringResource(R.string.settings_live_stream_mode_normal),
                    color = appColors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 16:9 simulated stream block
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorResource(id = cn.anc.dashcam.core.common.R.color.live_stream_shading_normal)), // Balanced neutral gray representing standard screen state
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "16 : 9",
                            color = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_pure_white),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.settings_live_stream_mode_normal),
                            color = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_white_alpha_80),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Explanatory Info Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(appColors.cardBackground)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_live_stream_desc),
                        color = appColors.textSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Primary CTA button to engage full screen mode
                Button(
                    onClick = { onFullScreenToggle(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(themeColor.lightHex)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_live_stream_preview_btn),
                        color = colorResource(id = cn.anc.dashcam.core.common.R.color.settings_pure_white),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
