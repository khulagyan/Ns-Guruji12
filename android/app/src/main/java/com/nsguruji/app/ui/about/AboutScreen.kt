package com.nsguruji.app.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nsguruji.app.R
import com.nsguruji.app.config.AppConfig
import com.nsguruji.app.ui.components.AppTopBar
import com.nsguruji.app.ui.theme.BackgroundLight
import com.nsguruji.app.ui.theme.PrimaryBlue
import com.nsguruji.app.ui.theme.PrimaryDarkRed
import com.nsguruji.app.ui.theme.PrimaryRed
import com.nsguruji.app.ui.theme.SurfaceCard
import com.nsguruji.app.ui.theme.SurfaceLight
import com.nsguruji.app.ui.theme.TextPrimary
import com.nsguruji.app.ui.theme.TextSecondary
import com.nsguruji.app.utils.ShareUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppTopBar(
                title = "हमारे बारे में (About Us)",
                isRootScreen = false,
                onBackClick = onBackClick
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_nsguruji_logo),
                contentDescription = "NS Guruji Logo",
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "NS Guruji",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryDarkRed,
                    fontSize = 24.sp
                )
            )

            Text(
                text = "संस्करण: 1.0.0 (Production)",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "NS Guruji के बारे में",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "NS Guruji (nsguruji.com) भारत का एक अग्रणी और विश्वसनीय हिंदी सूचना पोर्टल है। यह एप्लीकेशन आपको केंद्र व राज्य सरकार की नवीनतम सरकारी नौकरियों (Govt Jobs), प्रवेश पत्र (Admit Card), रिजल्ट (Results), सरकारी योजनाओं (Govt Schemes), वित्तीय समाचार (Financial News) और शिक्षा से जुड़ी प्रत्येक जानकारी सबसे पहले और प्रामाणिक रूप से उपलब्ध करवाता है।",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "मुख्य विशेषताएं:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• बिना लॉगिन के सभी लेख तुरंत पढ़ें\n• दैनिक सरकारी अपडेट्स एवं नोटिफिकेशन\n• ऑफलाइन पढ़ने के लिए लेख सेव करें\n• आसान हिंदी भाषा और आधिकारिक लिंक्स",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Official links & actions
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.WEBSITE_URL))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Language, contentDescription = null, tint = SurfaceLight)
                Spacer(modifier = Modifier.width(8.dp))
                Text("वेबसाइट खोलें (nsguruji.com)", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { ShareUtils.shareApp(context) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = PrimaryBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("मित्रों के साथ ऐप शेयर करें", color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}
