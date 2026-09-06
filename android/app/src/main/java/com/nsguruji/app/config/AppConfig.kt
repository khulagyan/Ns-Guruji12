package com.nsguruji.app.config

/**
 * Centralized configuration for NS Guruji application.
 * Replace test IDs with your production AdMob / API keys when releasing.
 */
object AppConfig {
    // WordPress REST API Base URL
    const val WORDPRESS_BASE_URL = "https://nsguruji.com/"
    const val WEBSITE_URL = "https://nsguruji.com/"
    const val PRIVACY_POLICY_URL = "https://nsguruji.com/privacy-policy/"
    const val CONTACT_EMAIL = "contact@nsguruji.com"

    // Pagination
    const val DEFAULT_PAGE_SIZE = 10

    // Google AdMob Configuration (User Production Ad Units)
    const val ADMOB_APP_ID = "ca-app-pub-3784953261980933~6912378306"
    const val ADMOB_BANNER_ID = "ca-app-pub-3784953261980933/4286214969"
    const val ADMOB_INTERSTITIAL_ID = "ca-app-pub-3784953261980933/6654086959"
    const val ADMOB_NATIVE_ID = "ca-app-pub-3940256099942544/2247696110"
    const val ADMOB_APP_OPEN_ID = "ca-app-pub-3940256099942544/9257390321"

    // AdMob Interstitial Frequency Cap: Show at most once every 3 article clicks
    const val INTERSTITIAL_CLICKS_INTERVAL = 3

    // Notification Channel
    const val NOTIFICATION_CHANNEL_ID = "nsguruji_updates_channel"
    const val NOTIFICATION_CHANNEL_NAME = "NS Guruji Alerts & Updates"
}
