import React, { useState } from 'react';
import { Smartphone, Code, BookOpen, ExternalLink, ShieldCheck, Download } from 'lucide-react';
import { AndroidSimulator } from './components/AndroidSimulator';
import { ProjectExporter } from './components/ProjectExporter';
import { PublishingGuide } from './components/PublishingGuide';

export default function App() {
  const [activeTab, setActiveTab] = useState<'simulator' | 'code' | 'guide'>('simulator');
  const [showApkHelpModal, setShowApkHelpModal] = useState(false);

  return (
    <div className="min-h-screen bg-slate-100 text-slate-900 flex flex-col font-sans">
      {/* Global Application Header */}
      <header className="bg-white border-b border-slate-200 sticky top-0 z-40 shadow-xs">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-3 flex flex-col md:flex-row items-center justify-between gap-3">
          {/* Logo & Branding */}
          <div className="flex items-center gap-3">
            <img
              src="/ns_guruji_logo.png"
              alt="NS Guruji Official Logo"
              className="w-10 h-10 rounded-xl object-contain shadow-xs border border-slate-200 p-0.5 bg-white"
            />
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-lg font-black tracking-tight text-slate-900">
                  NS Guruji
                </h1>
                <span className="px-2 py-0.5 text-[10px] font-extrabold uppercase bg-red-100 text-red-700 rounded-md border border-red-200">
                  Android App
                </span>
                <button
                  onClick={() => setShowApkHelpModal(true)}
                  className="px-2 py-0.5 text-[10px] font-extrabold uppercase bg-amber-100 hover:bg-amber-200 text-amber-800 rounded-md border border-amber-300 transition-colors cursor-pointer flex items-center gap-1"
                >
                  <span>📲 APK डाउनलोड गाइड</span>
                </button>
              </div>
              <p className="text-xs text-slate-500 font-medium">
                Production-Ready Kotlin & Jetpack Compose Android Client for <a href="https://nsguruji.com/" target="_blank" rel="noreferrer" className="text-blue-600 underline font-semibold">nsguruji.com</a>
              </p>
            </div>
          </div>

          {/* Navigation Tabs */}
          <div className="flex items-center gap-1.5 bg-slate-100 p-1.5 rounded-2xl border border-slate-200">
            <button
              onClick={() => setActiveTab('simulator')}
              className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                activeTab === 'simulator'
                  ? 'bg-white text-red-700 shadow-xs'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <Smartphone size={16} />
              <span>लाइव Android सिमुलेटर</span>
            </button>

            <button
              onClick={() => setActiveTab('code')}
              className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                activeTab === 'code'
                  ? 'bg-white text-red-700 shadow-xs'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <Code size={16} />
              <span>Android Studio Code &amp; ZIP</span>
            </button>

            <button
              onClick={() => setActiveTab('guide')}
              className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                activeTab === 'guide'
                  ? 'bg-white text-red-700 shadow-xs'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <BookOpen size={16} />
              <span>पब्लिशिंग गाइड (10 स्टेप्स)</span>
            </button>
          </div>
        </div>
      </header>

      {/* APK Direct Help Modal */}
      {showApkHelpModal && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 relative animate-in fade-in duration-200">
            <button
              onClick={() => setShowApkHelpModal(false)}
              className="absolute top-4 right-4 text-slate-400 hover:text-slate-700 p-1 rounded-full text-lg font-bold cursor-pointer"
            >
              ✕
            </button>

            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 rounded-xl bg-red-100 text-red-700 flex items-center justify-center font-bold text-lg">
                📲
              </div>
              <div>
                <h3 className="text-base font-black text-slate-900">
                  सीधे APK क्यों नहीं मिल रही और इसका हल क्या है?
                </h3>
                <p className="text-xs text-slate-500">
                  आसान शब्दों में समझें
                </p>
              </div>
            </div>

            <div className="space-y-3 text-xs text-slate-700 leading-relaxed">
              <p className="bg-slate-50 p-3 rounded-xl border border-slate-200">
                <b>कारण:</b> AI Studio का यह चैटबॉट केवल वेबसाइट और कोड लिख सकता है। <b>.apk फ़ाइल</b> बनाने के लिए Google के 10 GB वाले भारी Java/Android SDK कंपाइलर की ज़रूरत होती है जो केवल Android Studio या क्लाउड बिल्ड सर्वर पर ही चलता है।
              </p>

              <h4 className="font-bold text-slate-900 pt-1">
                सबसे आसान समाधान (आपके पास 2 विकल्प हैं):
              </h4>

              <div className="space-y-2">
                <div className="p-3 bg-red-50 border border-red-200 rounded-xl">
                  <span className="font-bold text-red-800 block mb-0.5">
                    1. फोन में तुरंत असली ऐप की तरह चलाएं (0 सेकंड):
                  </span>
                  <p className="text-[11px] text-red-900">
                    अपने मोबाइल के Chrome में इस लिंक को खोलें और <b>⋮ (3 डॉट्स)</b> दबाकर <b>"Add to Home screen" / "Install App"</b> दबाएं। यह बिना APK के सीधे आपके फोन में ओरिजिनल ऐप बन जाएगी!
                  </p>
                </div>

                <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-xl">
                  <span className="font-bold text-emerald-800 block mb-0.5">
                    2. मुफ़्त ऑनलाइन APK डाउनलोड करें (GitHub Actions):
                  </span>
                  <p className="text-[11px] text-emerald-900">
                    प्रोजेक्ट की <b>ZIP डाउनलोड</b> करें और GitHub पर नई रिपॉजिटरी में डाल दें। उसमें हमने ऑटो-बिल्डर फ़ाइल जोड़ दी है जो 2 मिनट में अपने आप APK बनाकर आपको डायरेक्ट डाउनलोड लिंक दे देगी!
                  </p>
                </div>
              </div>
            </div>

            <div className="mt-5 flex gap-2 justify-end">
              <button
                onClick={() => setShowApkHelpModal(false)}
                className="px-4 py-2 bg-slate-900 text-white rounded-xl text-xs font-bold hover:bg-slate-800 cursor-pointer"
              >
                समझ गया, धन्यवाद!
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Main Tabbed View */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 py-6">
        {activeTab === 'simulator' && <AndroidSimulator />}
        {activeTab === 'code' && <ProjectExporter />}
        {activeTab === 'guide' && <PublishingGuide />}
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 py-4 text-center text-xs text-slate-500">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-2">
          <span>
            © {new Date().getFullYear()} NS Guruji Official Android Application • Architecture: MVVM + Jetpack Compose + Room + Retrofit + FCM + AdMob
          </span>
          <div className="flex items-center gap-4">
            <a
              href="https://nsguruji.com/"
              target="_blank"
              rel="noreferrer"
              className="hover:text-red-700 font-medium inline-flex items-center gap-1"
            >
              nsguruji.com <ExternalLink size={12} />
            </a>
          </div>
        </div>
      </footer>
    </div>
  );
}
