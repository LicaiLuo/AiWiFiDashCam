package cn.anc.dashcam.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DashCamTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
    )
}
