import React, { useState } from 'react';
import {
  HelpCircle,
  Play,
  Image,
  Globe,
  DollarSign,
  Key,
  Flame,
  FileBox,
  Award,
  UploadCloud,
  ChevronDown,
  ChevronUp,
  CheckCircle2,
  Copy,
  Check
} from 'lucide-react';

interface StepItem {
  id: number;
  title: string;
  subtitle: string;
  icon: React.ReactNode;
  content: React.ReactNode;
}

export const PublishingGuide: React.FC = () => {
  const [openStep, setOpenStep] = useState<number | null>(1);
  const [copiedText, setCopiedText] = useState<string | null>(null);

  const copySnippet = (text: string, key: string) => {
    navigator.clipboard.writeText(text);
    setCopiedText(key);
    setTimeout(() => setCopiedText(null), 2000);
  };

  const steps: StepItem[] = [
    {
      id: 1,
      title: '1. Android Studio में प्रोजेक्ट कैसे खोलें (Open Project)',
      subtitle: 'प्रोजेक्ट डायरेक्टरी इम्पोर्ट और Gradle सिंक करने की प्रक्रिया',
      icon: <Play className="text-red-500" size={20} />,
      content: (
        <div className="space-y-3 text-xs text-slate-700 leading-relaxed">
          <ol className="list-decimal ml-5 space-y-2">
            <li><b>Android Studio डाउनलोड और इंस्टॉल करें:</b> Android Studio Hedgehog (2023.1.1) या नया वर्शन अनुशंसित है।</li>
            <li>प्रोजेक्ट की डाउनलोड की गई <b>ns-guruji-android-project.zip</b> फ़ाइल को अपने कंप्यूटर पर एक्सट्रैक्ट (Unzip) करें।</li>
            <li>Android Studio खोलें और Welcome स्क्रीन पर <b>Open</b> (या <code>File &gt; Open</code>) पर क्लिक करें।</li>
            <li>एक्सट्रैक्ट किए गए फ़ोल्डर के अंदर स्थित <b>android</b> डायरेक्टरी को चुनें और <b>OK</b> पर क्लिक करें।</li>
            <li>Android Studio स्वचालित रूप से Gradle Dependencies को डाउनलोड और सिंक करेगा। इसमें पहली बार 1-3 मिनट का समय लग सकता है।</li>
            <li>सुनिश्चित करें कि <b>Gradle JDK</b> वर्शन <b>JDK 17</b> पर सेट है (<code>Settings &gt; Build, Execution, Deployment &gt; Build Tools &gt; Gradle &gt; Gradle JDK</code>)।</li>
          </ol>
        </div>
      )
    },
    {
      id: 2,
      title: '2. ऐप को टेस्ट डिवाइस या एमुलेटर पर कैसे चलाएं (Run App)',
      subtitle: 'लाइव वर्डप्रेस डाटा के साथ ऐप रन करना',
      icon: <Play className="text-emerald-500" size={20} />,
      content: (
        <div className="space-y-3 text-xs text-slate-700 leading-relaxed">
          <ol className="list-decimal ml-5 space-y-2">
            <li>अपने कंप्यूटर से एक Android फोन को USB केबल से जोड़ें और फोन में <b>USB Debugging</b> ऑन करें (या Android Studio का AVD Emulator शुरू करें)।</li>
            <li>Android Studio के टॉप टूलबार में <b>app</b> रन कॉन्फ़िगरेशन और अपने डिवाइस का चयन करें।</li>
            <li>हरे रंग के <b>Run (▶)</b> बटन पर क्लिक करें या <code>Shift + F10</code> दबाएं।</li>
            <li>ऐप आपके फोन में इंस्टॉल होकर खुलेगा और तुरंत <b>https://nsguruji.com/</b> से ताजा हिंदी लेख लोड करेगा।</li>
          </ol>
        </div>
      )
    },
    {
      id: 3,
      title: '3. मूल NS Guruji लोगो कहाँ और कैसे लगाएं (Logo Placement)',
      subtitle: 'आधिकारिक ब्रांडिंग लोगो और ऐप आइकन पाथ',
      icon: <Image className="text-blue-500" size={20} />,
      content: (
        <div className="space-y-3 text-xs text-slate-700 leading-relaxed">
          <p>
            मूल <b>NS Guruji</b> लोगो इस प्रोजेक्ट में पहले से ही निम्नलिखित डायरेक्टरी में सुरक्षित है:
          </p>
          <div className="bg-slate-100 p-2.5 rounded-lg font-mono text-[11px] text-slate-800">
            android/app/src/main/res/drawable/ic_nsguruji_logo.png
          </div>
          <p>
            यदि आप विभिन्न घनत्व (Mipmap HDPI, XHDPI, XXHDPI) के एडाप्टिव आइकन जनरेट करना चाहते हैं:
          </p>
          <ol className="list-decimal ml-5 space-y-1.5">
            <li>Android Studio में <code>app/src/main/res</code> पर राइट-क्लिक करें।</li>
            <li><b>New &gt; Image Asset</b> चुनें।</li>
            <li><b>Icon Type:</b> Launcher Icons (Adaptive and Legacy) चुनें।</li>
            <li><b>Source Asset &gt; Path:</b> में <code>ic_nsguruji_logo.png</code> को सिलेक्ट करें और <b>Next &gt; Finish</b> दबाएं।</li>
          </ol>
        </div>
      )
    },
    {
      id: 4,
      title: '4. WordPress API कॉन्फ़िगरेशन (API Setup)',
      subtitle: 'केंद्रीकृत AppConfig.kt में सेटिंग्स और कस्टम एंडपॉइंट्स',
      icon: <Globe className="text-indigo-500" size={20} />,
      content: (
        <div className="space-y-3 text-xs text-slate-700 leading-relaxed">
          <p>
            वर्डप्रेस से संबंधित सभी API सेटिंग्स इस फ़ाइल में सेंट्रलाइज्ड हैं:
          </p>
          <div className="bg-slate-100 p-2.5 rounded-lg font-mono text-[11px] text-slate-800">
            android/app/src/main/java/com/nsguruji/app/config/AppConfig.kt
          </div>
          <div className="bg-slate-900 text-slate-100 p-3 rounded-xl font-mono text-[11px] overflow-x-auto">
            <pre>{`object AppConfig {
    const val WORDPRESS_BASE_URL = "https://nsguruji.com/"
    const val DEFAULT_PAGE_SIZE = 10
    const val WEBSITE_URL = "https://nsguruji.com/"
}`}</pre>
          </div>
          <p>
            ऐप ऑटोमेटिकली <code>/wp-json/wp/v2/posts?_embed=true</code> और <code>/wp-json/wp/v2/categories</code> का इस्तेमाल करता है।
          </p>
        </div>
      )
    },
    {
      id: 5,
      title: '5. Google AdMob अकाउंट और Ad Units कैसे बनाएं',
      subtitle: 'बैनर और इंटरस्टीशियल विज्ञापन सेटअप',
      icon: <DollarSign className="text-amber-500" size={20} />,
      content: (
        <div className="space-y-3 text-xs text-slate-700 leading-relaxed">
          <ol className="list-decimal ml-5 space-y-2">
            <li><a href="https://admob.google.com/" target="_blank" rel="noreferrer" className="text-blue-600 underline font-semibold">admob.google.com</a> पर जाएं और अपने Google अकाउंट से लॉगिन करें।</li>
            <li><b>Apps &gt; Add App</b> पर क्लिक करें।</li>
            <li>Platform में <b>Android</b> चुनें और ऐप का नाम <b>NS Guruji</b> रखें।</li>
            <li>आपका <b>AdMob App ID</b> (उदा. <code>ca-app-pub-XXXXXXXXXXXX~XXXXXXXXXX</code>) जनरेट होगा।</li>
            <li><b>Ad Units</b> में जाकर 2 Ad Unit बनाएं:
              <ul className="list-disc ml-5 mt-1 space-y-1">
                <li><b>Banner Ad Unit:</b> नाम <code>Home_Bottom_Banner</code> रखें।</li>
                <li><b>Interstitial Ad Unit:</b> नाम <code>Article_Interstitial</code> रखें।</li>
              </ul>
            </li>
          </ol>
        </div>
      )
    },
    {
      id: 6,
      title: '6. Google AdMob IDs (सफलतापूर्वक कॉन्फ़िगर हो गए)',
      subtitle: 'आपके लाइव विज्ञापन यूनिट्स प्रोजेक्ट में सक्रिय कर दिए गए हैं',
      icon: <Key className="text-emerald-500" size={20} />,
      content: (
        <div className="space-y-3 text-xs text-slate-700 leading-relaxed">
          <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-xl text-emerald-900 font-medium">
            ✅ <b>सफलतापूर्वक अपडेटेड:</b> आपके वास्तविक AdMob IDs प्रोजेक्ट की फ़ाइलों में लगा दिए गए हैं।
          </div>

          <p>
            <b>फ़ाइल 1:</b> <code>android/app/src/main/AndroidManifest.xml</code>
          </p>
          <div className="bg-slate-900 text-slate-100 p-3 rounded-xl font-mono text-[11px] overflow-x-auto">
            <pre>{`<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-3784953261980933~6912378306" />`}</pre>
          </div>

          <p>
            <b>फ़ाइल 2:</b> <code>android/app/src/main/java/com/nsguruji/app/config/AppConfig.kt</code>
          </p>
          <div className="bg-slate-900 text-slate-100 p-3 rounded-xl font-mono text-[11px] overflow-x-auto">
            <pre>{`const val ADMOB_APP_ID = "ca-app-pub-3784953261980933~6912378306"
const val ADMOB_BANNER_ID = "ca-app-pub-3784953261980933/4286214969"
const val ADMOB_INTERSTITIAL_ID = "ca-app-pub-3784953261980933/6654086959"`}</pre>
          </div>
        </div>
      )
    },
    {
      id: 7,
      title: '7. Firebase Cloud Messaging (FCM) कैसे कॉन्फ़िगर करें',
      subtitle: 'बिना लॉगिन के सभी पाठकों को पुश नोटिफिकेशन भेजना',
      icon: <Flame className="text-orange-500" size={20} />,
      content: (
        <div className="space-y-3 text-xs text-slate-700 leading-relaxed">
          <ol className="list-decimal ml-5 space-y-2">
            <li><a href="https://console.firebase.google.com/" target="_blank" rel="noreferrer" className="text-blue-600 underline font-semibold">Firebase Console</a> पर नया प्रोजेक्ट <b>NS Guruji</b> बनाएं।</li>
            <li><b>Add App</b> में Android आइकन चुनें।</li>
            <li>Package Name में <code>com.nsguruji.app</code> दर्ज करें।</li>
            <li><b>google-services.json</b> फ़ाइल डाउनलोड करें।</li>
            <li>डाउनलोड की गई फ़ाइल को <code>android/app/google-services.json</code> के स्थान पर रिप्लेस कर दें।</li>
            <li>अब Firebase Console के <b>Cloud Messaging</b> सेक्शन से आप सीधे नई भर्ती या लेख का नोटिफिकेशन सभी उपयोगकर्ताओं को भेज सकते हैं!</li>
          </ol>
        </div>
      )
    },
    {
      id: 8,
      title: '8. रिलीज APK कैसे तैयार करें (Build Release APK)',
      subtitle: 'डायरेक्ट टेस्टिंग और इंस्टॉलेशन हेतु APK बनाना',
      icon: <FileBox className="text-teal-500" size={20} />,
      content: (
        <div className="space-y-3 text-xs text-slate-700 leading-relaxed">
          <p>Android Studio में रिलीज APK बनाने के लिए:</p>
          <ol className="list-decimal ml-5 space-y-1.5">
            <li>मेनू बार में <b>Build &gt; Build Bundle(s) / APK(s) &gt; Build APK(s)</b> पर क्लिक करें।</li>
            <li>या टर्मिनल में कमांड चलाएं: <code>./gradlew assembleRelease</code></li>
            <li>जनरेटेड APK फ़ाइल यहाँ मिलेगी:
              <div className="bg-slate-100 p-2 rounded font-mono text-[11px] mt-1">
                android/app/build/outputs/apk/release/app-release.apk
              </div>
            </li>
          </ol>
        </div>
      )
    },
    {
      id: 9,
      title: '9. हस्ताक्षरित AAB (Signed Android App Bundle) कैसे जनरेट करें',
      subtitle: 'Google Play Store पब्लिशिंग हेतु आवश्यक बंडल',
      icon: <Award className="text-purple-500" size={20} />,
      content: (
        <div className="space-y-3 text-xs text-slate-700 leading-relaxed">
          <ol className="list-decimal ml-5 space-y-2">
            <li>Android Studio में <b>Build &gt; Generate Signed Bundle / APK...</b> पर क्लिक करें।</li>
            <li><b>Android App Bundle</b> को चुनें और <b>Next</b> दबाएं।</li>
            <li><b>Key store path</b> में <b>Create new...</b> पर क्लिक करके अपनी की-स्टोर फ़ाइल (उदा. <code>nsguruji.jks</code>) और पासवर्ड बनाएं।</li>
            <li>Key alias में <code>nsguruji_key</code> और पासवर्ड दर्ज करें।</li>
            <li>Build Variant में <b>release</b> चुनें और <b>Create</b> पर क्लिक करें।</li>
            <li>आपकी हस्ताक्षरित <code>.aab</code> फ़ाइल <code>android/app/release/app-release.aab</code> में तैयार हो जाएगी।</li>
          </ol>
        </div>
      )
    },
    {
      id: 10,
      title: '10. Google Play Console पर ऐप कैसे पब्लिश करें (Play Store Publish)',
      subtitle: 'स्टोर लिस्टिंग, प्राइवेसी पालिसी और 100% अनुपालन गाइड',
      icon: <UploadCloud className="text-red-600" size={20} />,
      content: (
        <div className="space-y-3 text-xs text-slate-700 leading-relaxed">
          <ol className="list-decimal ml-5 space-y-2">
            <li><a href="https://play.google.com/console/" target="_blank" rel="noreferrer" className="text-blue-600 underline font-semibold">Google Play Console</a> पर लॉगिन करें और <b>Create app</b> पर क्लिक करें।</li>
            <li><b>App Name:</b> <code>NS Guruji: Govt Jobs &amp; Yojana</code> दर्ज करें।</li>
            <li><b>Default language:</b> Hindi (hi-IN) या English (India) चुनें।</li>
            <li><b>App access:</b> "All functionality is available without special access" (क्योंकि कोई लॉगिन नहीं है)।</li>
            <li><b>Ads:</b> "Yes, my app contains ads" चुनें।</li>
            <li><b>Privacy Policy:</b> ऐप के अंदर दी गई या वेबसाइट की प्राइवेसी पालिसी URL (<code>https://nsguruji.com/privacy-policy/</code>) दर्ज करें।</li>
            <li><b>Graphics:</b> 512x512 ऐप आइकन (मूल NS Guruji लोगो) और 1024x500 का फीचर ग्राफिक अपलोड करें।</li>
            <li><b>Production Release:</b> में जनरेटेड <code>app-release.aab</code> फ़ाइल अपलोड करें और <b>Start rollout to Production</b> दबाएं!</li>
          </ol>
        </div>
      )
    }
  ];

  return (
    <div className="w-full max-w-4xl mx-auto space-y-4">
      <div className="bg-gradient-to-r from-red-600 via-red-700 to-slate-900 text-white p-6 rounded-3xl shadow-xl mb-6 border border-red-500/30">
        <div className="inline-flex items-center gap-2 px-3 py-1 bg-white/20 backdrop-blur-sm rounded-full text-xs font-bold mb-3">
          <span>⚡ बिना Android Studio के APK बनाना</span>
        </div>
        <h2 className="text-xl md:text-2xl font-black mb-2">
          क्या आपके पास Android Studio नहीं है?
        </h2>
        <p className="text-xs md:text-sm text-red-100 leading-relaxed mb-4">
          चिंता न करें! आपको अपने कंप्यूटर में 2-3 GB का भारी Android Studio डाउनलोड करने की बिल्कुल आवश्यकता नहीं है। इस प्रोजेक्ट में <b>GitHub Actions Cloud Builder</b> पहले से कॉन्फ़िगर है।
        </p>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-xs">
          <div className="bg-white/10 backdrop-blur-md p-3.5 rounded-2xl border border-white/10">
            <h4 className="font-bold text-white mb-1.5 flex items-center gap-1.5">
              <span>🚀 तरीका 1: GitHub Cloud Build (100% Free)</span>
            </h4>
            <ol className="list-decimal ml-4 space-y-1 text-red-100 text-[11px] leading-relaxed">
              <li>डाउनलोड की गई ज़िप फ़ाइल को अपने मुफ़्त <b>GitHub</b> अकाउंट में नई रिपॉजिटरी बनाकर अपलोड करें।</li>
              <li>हमने प्रोजेक्ट में <code>.github/workflows/build_apk.yml</code> पहले से जोड़ दिया है।</li>
              <li>GitHub के सर्वर 2 मिनट में अपने आप APK बनाकर <b>Actions &gt; Artifacts</b> में डायरेक्ट डाउनलोड लिंक दे देंगे!</li>
            </ol>
          </div>

          <div className="bg-white/10 backdrop-blur-md p-3.5 rounded-2xl border border-white/10">
            <h4 className="font-bold text-white mb-1.5 flex items-center gap-1.5">
              <span>📱 तरीका 2: फोन में तुरंत ऐप की तरह इस्तेमाल करें</span>
            </h4>
            <p className="text-red-100 text-[11px] leading-relaxed">
              अपने मोबाइल के Chrome ब्राउज़र में इस वेब ऐप को खोलें और ऊपर 3-डॉट मेनू पर क्लिक करके <b>"Add to Home Screen" (होम स्क्रीन पर जोड़ें)</b> दबाएं। यह बिना APK इंस्टॉल किए आपके फोन में बिल्कुल ओरिजिनल Android ऐप आइकन की तरह चलने लगेगी!
            </p>
          </div>
        </div>
      </div>

      <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm mb-6">
        <h2 className="text-xl font-black text-slate-900 mb-1">
          Google Play Store पब्लिशिंग एवं सेटअप गाइड
        </h2>
        <p className="text-xs text-slate-600 leading-relaxed">
          यह विस्तृत चेकलिस्ट आपको NS Guruji Android ऐप को Android Studio में चलाने से लेकर Google Play Store पर लाइव पब्लिश करने के सभी 10 चरणों में मार्गदर्शन करती है।
        </p>
      </div>

      <div className="space-y-3">
        {steps.map(step => {
          const isOpen = openStep === step.id;
          return (
            <div
              key={step.id}
              className="bg-white rounded-2xl border border-slate-200/90 shadow-sm overflow-hidden transition-all"
            >
              <button
                onClick={() => setOpenStep(isOpen ? null : step.id)}
                className="w-full px-5 py-4 flex items-center justify-between text-left hover:bg-slate-50 transition-colors"
              >
                <div className="flex items-center gap-3.5">
                  <div className="p-2 rounded-xl bg-slate-50 border border-slate-100 shrink-0">
                    {step.icon}
                  </div>
                  <div>
                    <h3 className="text-sm font-bold text-slate-900">{step.title}</h3>
                    <p className="text-xs text-slate-500 mt-0.5">{step.subtitle}</p>
                  </div>
                </div>
                {isOpen ? <ChevronUp size={18} className="text-slate-400" /> : <ChevronDown size={18} className="text-slate-400" />}
              </button>

              {isOpen && (
                <div className="px-5 pb-5 pt-1 border-t border-slate-100 bg-slate-50/40">
                  {step.content}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};
