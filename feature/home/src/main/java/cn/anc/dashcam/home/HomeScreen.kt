package cn.anc.dashcam.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cn.anc.dashcam.core.common.AppConfig
import cn.anc.dashcam.core.common.BrandType

@Composable
fun HomeScreen() {
    when (AppConfig.brandType) {
        BrandType.UNIDEN -> UnidenLayoutFrame()
        BrandType.COOAU -> CooauLayoutFrame()
        BrandType.PRIDO -> PridoLayoutFrame()
        BrandType.ROADDRIVE -> PridoLayoutFrame() // RoadDrive uses the classic 4-Tab standard frame
    }
}

@Preview(name = "PRIDO Home", showBackground = true)
@Composable
private fun PridoLayoutFramePreview() {
    PridoLayoutFrame()
}

@Preview(name = "UNIDEN Home", showBackground = true)
@Composable
private fun UnidenLayoutFramePreview() {
    UnidenLayoutFrame()
}

@Preview(name = "COOAU Home", showBackground = true)
@Composable
private fun CooauLayoutFramePreview() {
    CooauLayoutFrame()
}
