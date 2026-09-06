import React, { useState } from 'react';
import JSZip from 'jszip';
import {
  Download,
  FileCode,
  Folder,
  Copy,
  Check,
  Search,
  ExternalLink,
  Smartphone,
  Layers,
  Sparkles
} from 'lucide-react';
import { ANDROID_FILES } from '../data/androidFilesData';

export const ProjectExporter: React.FC = () => {
  const [selectedFilePath, setSelectedFilePath] = useState<string>(
    'android/app/src/main/java/com/nsguruji/app/MainActivity.kt'
  );
  const [isCopied, setIsCopied] = useState(false);
  const [fileFilter, setFileFilter] = useState('');
  const [isZipping, setIsZipping] = useState(false);

  const filePaths = Object.keys(ANDROID_FILES);

  const filteredPaths = filePaths.filter(p =>
    p.toLowerCase().includes(fileFilter.toLowerCase())
  );

  const selectedFileContent = ANDROID_FILES[selectedFilePath] || '// फ़ाइल लोड नहीं हो सकी';

  const handleCopyCode = () => {
    navigator.clipboard.writeText(selectedFileContent);
    setIsCopied(true);
    setTimeout(() => setIsCopied(false), 2000);
  };

  const handleDownloadZip = async () => {
    setIsZipping(true);
    try {
      const zip = new JSZip();

      // Loop through all files and add them to zip
      for (const [path, content] of Object.entries(ANDROID_FILES)) {
        if (content === '[Binary Asset - included in ZIP]' || content.startsWith('[Binary')) {
          try {
            if (path.endsWith('.jar')) {
              const jarRes = await fetch('/gradle-wrapper.jar');
              const jarBlob = await jarRes.blob();
              zip.file(path, jarBlob);
            } else {
              const logoRes = await fetch('/ns_guruji_logo.png');
              const logoBlob = await logoRes.blob();
              zip.file(path, logoBlob);
            }
          } catch (e) {
            console.error('Failed to fetch binary asset for ' + path, e);
            zip.file(path, '');
          }
        } else {
          zip.file(path, content);
        }
      }

      // Generate the zip
      const blob = await zip.generateAsync({ type: 'blob' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'ns-guruji-android-project.zip';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Error creating ZIP:', err);
      alert('ज़िप फ़ाइल तैयार करने में त्रुटि हुई।');
    } finally {
      setIsZipping(false);
    }
  };

  return (
    <div className="w-full max-w-7xl mx-auto space-y-6">
      {/* Header with Download Action */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-red-950 text-white p-6 rounded-3xl shadow-xl flex flex-col md:flex-row md:items-center justify-between gap-4 border border-slate-700/50">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <span className="px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-red-600/30 text-red-300 border border-red-500/40">
              Android Studio Ready
            </span>
            <span className="px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-emerald-600/30 text-emerald-300 border border-emerald-500/40">
              AdMob Live IDs Active
            </span>
            <span className="text-xs text-slate-400">Min SDK 24 • Target SDK 34 • Kotlin 2.0</span>
          </div>
          <h2 className="text-xl md:text-2xl font-black">
            NS Guruji Android Source Code
          </h2>
          <p className="text-xs md:text-sm text-slate-300 max-w-2xl mt-1">
            कंप्लीट प्रोडक्शन-ग्रेड Android कोडबेस (66 फ़ाइलें): MVVM, Room Database, Retrofit, Jetpack Compose UI, AdMob SDK और Firebase Cloud Messaging।
          </p>
        </div>

        <button
          onClick={handleDownloadZip}
          disabled={isZipping}
          className="flex items-center justify-center gap-2.5 px-6 py-3.5 bg-gradient-to-r from-red-600 to-red-700 hover:from-red-500 hover:to-red-600 active:scale-95 text-white font-bold text-sm rounded-2xl shadow-lg shadow-red-950/40 transition-all shrink-0 cursor-pointer disabled:opacity-75"
        >
          <Download size={18} className={isZipping ? 'animate-bounce' : ''} />
          <span>{isZipping ? 'ज़िप फ़ाइल बन रही है...' : 'Download Android Project (.zip)'}</span>
        </button>
      </div>

      {/* Code Inspector & File Explorer */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden min-h-[600px]">
        {/* Left Sidebar: File Tree */}
        <div className="lg:col-span-4 border-r border-slate-200 flex flex-col h-[650px] bg-slate-50/50">
          <div className="p-3.5 border-b border-slate-200">
            <div className="relative">
              <Search size={15} className="absolute left-3 top-2.5 text-slate-400" />
              <input
                type="text"
                value={fileFilter}
                onChange={e => setFileFilter(e.target.value)}
                placeholder="फ़ाइल खोजें (e.g. MainActivity, AppConfig)..."
                className="w-full pl-9 pr-3 py-1.5 text-xs bg-white border border-slate-200 rounded-xl focus:outline-none focus:border-red-600"
              />
            </div>
            <div className="flex items-center justify-between text-[11px] text-slate-500 mt-2 px-1">
              <span>कुल फ़ाइलें: <b>{filePaths.length}</b></span>
              <span>फ़िल्टर: {filteredPaths.length}</span>
            </div>
          </div>

          <div className="flex-1 overflow-y-auto p-2 space-y-0.5 font-mono text-xs">
            {filteredPaths.map(path => {
              const fileName = path.split('/').pop() || path;
              const isSelected = selectedFilePath === path;
              const isKt = path.endsWith('.kt');
              const isXml = path.endsWith('.xml');
              const isGradle = path.endsWith('.kts');

              return (
                <button
                  key={path}
                  onClick={() => setSelectedFilePath(path)}
                  className={`w-full text-left px-2.5 py-1.5 rounded-lg flex items-center gap-2 transition-colors ${
                    isSelected
                      ? 'bg-red-600 text-white font-semibold shadow-sm'
                      : 'text-slate-700 hover:bg-slate-100'
                  }`}
                  title={path}
                >
                  <FileCode
                    size={14}
                    className={
                      isSelected
                        ? 'text-white'
                        : isKt
                        ? 'text-purple-600'
                        : isXml
                        ? 'text-amber-600'
                        : isGradle
                        ? 'text-emerald-600'
                        : 'text-slate-400'
                    }
                  />
                  <div className="truncate flex-1">
                    <span className="text-xs">{fileName}</span>
                    <span
                      className={`block text-[10px] truncate ${
                        isSelected ? 'text-red-100' : 'text-slate-400'
                      }`}
                    >
                      {path.replace('android/', '')}
                    </span>
                  </div>
                </button>
              );
            })}
          </div>
        </div>

        {/* Right Area: Code Display */}
        <div className="lg:col-span-8 flex flex-col h-[650px] bg-slate-900 text-slate-100">
          <div className="px-4 py-2.5 bg-slate-950 border-b border-slate-800 flex items-center justify-between">
            <div className="flex items-center gap-2 truncate text-xs font-mono text-slate-300">
              <FileCode size={16} className="text-red-400 shrink-0" />
              <span className="truncate">{selectedFilePath}</span>
            </div>

            <button
              onClick={handleCopyCode}
              className="flex items-center gap-1.5 px-3 py-1 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-medium rounded-lg transition-colors shrink-0"
            >
              {isCopied ? (
                <>
                  <Check size={14} className="text-emerald-400" />
                  <span className="text-emerald-400">कॉपी हो गया!</span>
                </>
              ) : (
                <>
                  <Copy size={14} />
                  <span>कोड कॉपी करें</span>
                </>
              )}
            </button>
          </div>

          <div className="flex-1 overflow-auto p-4 font-mono text-xs leading-relaxed text-slate-200">
            <pre className="whitespace-pre">{selectedFileContent}</pre>
          </div>
        </div>
      </div>
    </div>
  );
};
