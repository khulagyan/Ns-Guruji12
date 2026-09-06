package com.nsguruji.app.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.nsguruji.app.config.AppConfig

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String = AppConfig.ADMOB_BANNER_ID
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 4.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
