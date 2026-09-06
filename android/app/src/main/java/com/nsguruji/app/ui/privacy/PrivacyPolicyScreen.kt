package com.nsguruji.app.ui.privacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nsguruji.app.ui.components.AppTopBar
import com.nsguruji.app.ui.theme.BackgroundLight
import com.nsguruji.app.ui.theme.PrimaryBlue
import com.nsguruji.app.ui.theme.PrimaryDarkRed
import com.nsguruji.app.ui.theme.SurfaceCard
import com.nsguruji.app.ui.theme.TextPrimary
import com.nsguruji.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "गोपनीयता नीति (Privacy Policy)",
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
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "NS Guruji गोपनीयता नीति (Privacy Policy)",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkRed,
                            fontSize = 19.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "अंतिम अपडेट: सितंबर 2026",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PolicySection(
                        title = "1. परिचय एवं उपयोगकर्ता खाता",
                        content = "NS Guruji (nsguruji.com) आपकी गोपनीयता का पूर्ण सम्मान करता है। हमारी एप्लिकेशन का उपयोग करने के लिए आपको किसी भी प्रकार का व्यक्तिगत खाता (Login/Signup) बनाने की आवश्यकता नहीं है। आप बिना कोई व्यक्तिगत जानकारी दिए सभी लेखों को स्वतंत्र रूप से पढ़ सकते हैं।"
                    )

                    PolicySection(
                        title = "2. तृतीय-पक्ष विज्ञापन (Google AdMob)",
                        content = "हमारा ऐप विकास और रखरखाव लागतों के समर्थन के लिए Google AdMob विज्ञापन सेवा का उपयोग करता है। AdMob प्रासंगिक विज्ञापन प्रदर्शित करने के लिए उपयोगकर्ता के डिवाइस आइडेंटिफायर (जैसे Google Advertising ID), सामान्य स्थान और ऐप उपयोग से संबंधित गैर-व्यक्तिगत डेटा का उपयोग कर सकता है। Google की विज्ञापन नीतियों के बारे में अधिक जानने के लिए कृपया Google Privacy & Terms देखें।"
                    )

                    PolicySection(
                        title = "3. सूचनाएं (Firebase Cloud Messaging)",
                        content = "हम आपको नई सरकारी भर्ती, प्रवेश पत्र और रिजल्ट्स के तत्काल अपडेट्स भेजने के लिए Google Firebase Cloud Messaging (FCM) का उपयोग करते हैं। इसके लिए ऐप एक गुमनाम इंस्टॉलेशन टोकन उत्पन्न करता है, जिसमें कोई भी व्यक्तिगत पहचान योग्य जानकारी (PII) शामिल नहीं होती है।"
                    )

                    PolicySection(
                        title = "4. स्थानीय डेटा संग्रहण (Room / Local Storage)",
                        content = "आपके द्वारा 'सेव' (Bookmark) किए गए लेख केवल आपके मोबाइल डिवाइस के स्थानीय स्टोरेज में सुरक्षित रहते हैं। यह डेटा हमारे सर्वर पर अपलोड नहीं किया जाता है और ऐप अनइंस्टॉल करने पर डिवाइस से स्वतः हट जाता है।"
                    )

                    PolicySection(
                        title = "5. बाहरी लिंक (External Links)",
                        content = "हमारे लेखों में आधिकारिक सरकारी वेबसाइटों, रिजल्ट पोर्टलों और भर्ती बोर्डों के लिंक शामिल हो सकते हैं। एक बार जब आप बाहरी लिंक पर क्लिक करते हैं, तो आप उन वेबसाइटों की संबंधित गोपनीयता नीतियों के अधीन होते हैं।"
                    )

                    PolicySection(
                        title = "6. संपर्क करें",
                        content = "यदि आपके पास इस गोपनीयता नीति के संबंध में कोई प्रश्न या सुझाव हैं, तो आप हमसे contact@nsguruji.com पर संपर्क कर सकते हैं या हमारी आधिकारिक वेबसाइट nsguruji.com पर विजिट कर सकते हैं।"
                    )
                }
            }
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                fontSize = 15.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimary,
                lineHeight = 22.sp
            )
        )
    }
}
