package com.nsguruji.app.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NSGurujiMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New registration token generated: $token")
        // Can be synchronized with backend if required; no login required for user
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val title = remoteMessage.notification?.title 
            ?: remoteMessage.data["title"] 
            ?: "NS Guruji पर नया अपडेट 📰"
            
        val message = remoteMessage.notification?.body 
            ?: remoteMessage.data["body"] 
            ?: "नई सरकारी भर्ती/योजना की पूरी जानकारी पढ़ें."
            
        val articleUrl = remoteMessage.data["article_url"]
        val postId = remoteMessage.data["post_id"]?.toLongOrNull()

        NotificationUtils.showNotification(
            context = applicationContext,
            title = title,
            message = message,
            articleUrl = articleUrl,
            postId = postId
        )
    }
}
