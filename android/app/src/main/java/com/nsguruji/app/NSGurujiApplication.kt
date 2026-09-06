package com.nsguruji.app

import android.app.Application
import com.nsguruji.app.ads.AdMobManager
import com.nsguruji.app.notifications.NotificationUtils

class NSGurujiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize AdMob Mobile Ads SDK
        AdMobManager.initialize(this)

        // 2. Create Notification Channel for Firebase / Local Updates
        NotificationUtils.createNotificationChannel(this)
    }
}
