package com.nsguruji.app.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.nsguruji.app.config.AppConfig

object AdMobManager {

    private const val TAG = "AdMobManager"
    private var isInitialized = false
    private var interstitialAd: InterstitialAd? = null
    private var articleClickCount = 0

    fun initialize(context: Context) {
        if (isInitialized) return
        MobileAds.initialize(context) {
            isInitialized = true
            Log.d(TAG, "Google Mobile Ads SDK Initialized")
            loadInterstitial(context)
        }
    }

    fun loadInterstitial(context: Context) {
        if (interstitialAd != null) return

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            AppConfig.ADMOB_INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d(TAG, "Interstitial Ad Loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    Log.w(TAG, "Interstitial Ad Failed to load: ${error.message}")
                }
            }
        )
    }

    /**
     * Checks frequency capping and shows interstitial ad at natural transition points.
     * Never interrupts the user while reading or immediately upon article open.
     */
    fun showInterstitialWithFrequencyCap(activity: Activity, onAdDismissed: () -> Unit = {}) {
        articleClickCount++
        if (articleClickCount % AppConfig.INTERSTITIAL_CLICKS_INTERVAL != 0) {
            onAdDismissed()
            return
        }

        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    loadInterstitial(activity)
                    onAdDismissed()
                }
            }
            interstitialAd?.show(activity)
        } else {
            loadInterstitial(activity)
            onAdDismissed()
        }
    }
}
