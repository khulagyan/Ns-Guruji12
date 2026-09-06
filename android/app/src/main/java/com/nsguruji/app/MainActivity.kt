package com.nsguruji.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.nsguruji.app.data.api.NetworkResult
import com.nsguruji.app.data.repository.WordPressRepository
import com.nsguruji.app.navigation.NSGurujiNavGraph
import com.nsguruji.app.ui.theme.NSGurujiTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var initialPostId by mutableStateOf<Long?>(null)
    private val wordPressRepository by lazy { WordPressRepository(applicationContext) }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Notification permission callback for Android 13+
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission on Android 13+ (API 33)
        askNotificationPermission()

        // Handle incoming intent (deep link or push notification)
        handleIncomingIntent(intent)

        setContent {
            NSGurujiTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NSGurujiNavGraph(initialPostId = initialPostId)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        // Check if intent contains direct postId from notification
        if (intent.hasExtra("extra_post_id")) {
            val id = intent.getLongExtra("extra_post_id", 0L)
            if (id > 0) {
                initialPostId = id
                return
            }
        }

        // Check if intent contains a Web Deep Link (e.g. https://nsguruji.com/slug-here/)
        val data: Uri? = intent.data
        if (data != null && data.host?.contains("nsguruji.com") == true) {
            val path = data.path?.trim('/')
            if (!path.isNullOrBlank()) {
                lifecycleScope.launch {
                    when (val result = wordPressRepository.getPostBySlug(path)) {
                        is NetworkResult.Success -> {
                            initialPostId = result.data.id
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
