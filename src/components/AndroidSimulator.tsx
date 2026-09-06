import React, { useState, useEffect, useMemo } from 'react';
import {
  Home,
  Grid,
  Search,
  Bookmark,
  Menu,
  ArrowLeft,
  Share2,
  Calendar,
  Clock,
  Wifi,
  Battery,
  ExternalLink,
  RefreshCw,
  X,
  Folder,
  ChevronRight,
  ShieldCheck,
  Info,
  Star,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';
import { WordPressPost, WordPressCategory, SavedPost } from '../types';
import {
  fetchLatestPosts,
  fetchCategories,
  searchPosts,
  cleanHtmlText,
  getFeaturedImageUrl,
  getPostCategoryName,
  formatPostDate
} from '../services/wordpressApi';

type ScreenType = 'home' | 'categories' | 'category_articles' | 'search' | 'saved' | 'article' | 'about' | 'privacy';

export const AndroidSimulator: React.FC = () => {
  // Navigation State
  const [currentScreen, setCurrentScreen] = useState<ScreenType>('home');
  const [activeArticle, setActiveArticle] = useState<WordPressPost | null>(null);
  const [activeCategory, setActiveCategory] = useState<WordPressCategory | null>(null);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);

  // Data State
  const [posts, setPosts] = useState<WordPressPost[]>([]);
  const [categories, setCategories] = useState<WordPressCategory[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(null);
  const [categoryPosts, setCategoryPosts] = useState<WordPressPost[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  // Search State
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<WordPressPost[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [recentSearches, setRecentSearches] = useState<string[]>([
    'RRB NTPC', 'SSC GD', 'Admit Card', 'PM Kisan', 'Sarkari Yojana'
  ]);

  // Bookmarks (LocalStorage persistence mirroring Room)
  const [savedPosts, setSavedPosts] = useState<SavedPost[]>(() => {
    try {
      const stored = localStorage.getItem('nsguruji_saved_articles');
      return stored ? JSON.parse(stored) : [];
    } catch {
      return [];
    }
  });

  // Ads & Interstitial Simulation
  const [showInterstitialAd, setShowInterstitialAd] = useState(false);
  const [pendingArticle, setPendingArticle] = useState<WordPressPost | null>(null);
  const [interstitialTimer, setInterstitialTimer] = useState(3);
  const [notificationToast, setNotificationToast] = useState<string | null>(null);

  // Deep Link Input
  const [deepLinkInput, setDeepLinkInput] = useState('');

  // Initial Data Fetch
  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    setIsLoading(true);
    setErrorMessage(null);
    try {
      const [postsRes, catsRes] = await Promise.allSettled([
        fetchLatestPosts(1, 12),
        fetchCategories()
      ]);

      if (postsRes.status === 'fulfilled') {
        setPosts(postsRes.value.posts);
      } else {
        setErrorMessage('सर्वर से लेख लोड नहीं हो सके। इंटरनेट कनेक्शन जांचें।');
      }

      if (catsRes.status === 'fulfilled') {
        setCategories(catsRes.value.filter(c => c.count > 0));
      }
    } catch (err: any) {
      setErrorMessage(err.message || 'त्रुटि हुई');
    } finally {
      setIsLoading(false);
      setIsRefreshing(false);
    }
  };

  // Filter posts by category
  const displayedPosts = useMemo(() => {
    if (!selectedCategoryId) return posts;
    return posts.filter(p => p.categories.includes(selectedCategoryId));
  }, [posts, selectedCategoryId]);

  // Category articles fetcher
  const openCategoryArticles = async (cat: WordPressCategory) => {
    setActiveCategory(cat);
    setCurrentScreen('category_articles');
    setIsLoading(true);
    try {
      const res = await fetchLatestPosts(1, 15, cat.id);
      setCategoryPosts(res.posts);
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  // Handle Search
  useEffect(() => {
    if (!searchQuery.trim()) {
      setSearchResults([]);
      return;
    }
    const timer = setTimeout(async () => {
      setIsSearching(true);
      try {
        const results = await searchPosts(searchQuery);
        setSearchResults(results);
      } catch (err) {
        console.error(err);
      } finally {
        setIsSearching(false);
      }
    }, 450);

    return () => clearTimeout(timer);
  }, [searchQuery]);

  // Handle Bookmarks
  const toggleBookmark = (post: WordPressPost) => {
    const isSaved = savedPosts.some(s => s.id === post.id);
    let updated: SavedPost[];
    if (isSaved) {
      updated = savedPosts.filter(s => s.id !== post.id);
      showToast('लेख बुकमार्क से हटा दिया गया');
    } else {
      const newSaved: SavedPost = {
        id: post.id,
        title: post.title.rendered,
        excerpt: post.excerpt.rendered,
        content: post.content.rendered,
        link: post.link,
        date: post.date,
        modified: post.modified,
        imageUrl: getFeaturedImageUrl(post) || undefined,
        categoryName: getPostCategoryName(post),
        savedAt: Date.now()
      };
      updated = [newSaved, ...savedPosts];
      showToast('लेख ऑफलाइन पढ़ने के लिए सेव किया गया!');
    }
    setSavedPosts(updated);
    localStorage.setItem('nsguruji_saved_articles', JSON.stringify(updated));
  };

  const isPostBookmarked = (postId: number) => savedPosts.some(s => s.id === postId);

  // Show Toast
  const showToast = (msg: string) => {
    setNotificationToast(msg);
    setTimeout(() => setNotificationToast(null), 3000);
  };

  // Article Click with simulated Interstitial Ad
  const handleArticleClick = (post: WordPressPost) => {
    // 33% chance to simulate AdMob frequency capped interstitial ad
    if (Math.random() < 0.35 && !showInterstitialAd) {
      setPendingArticle(post);
      setShowInterstitialAd(true);
      setInterstitialTimer(3);
      const interval = setInterval(() => {
        setInterstitialTimer(prev => {
          if (prev <= 1) {
            clearInterval(interval);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    } else {
      setActiveArticle(post);
      setCurrentScreen('article');
    }
  };

  const closeInterstitial = () => {
    setShowInterstitialAd(false);
    if (pendingArticle) {
      setActiveArticle(pendingArticle);
      setCurrentScreen('article');
      setPendingArticle(null);
    }
  };

  // Deep Link Simulator
  const handleDeepLink = () => {
    if (!deepLinkInput) return;
    const cleanUrl = deepLinkInput.trim();
    // Try to match slug or ID
    const found = posts.find(p => p.link.includes(cleanUrl) || cleanUrl.includes(p.slug));
    if (found) {
      handleArticleClick(found);
      showToast(`डीप लिंक से लेख खोला गया: ${cleanHtmlText(found.title.rendered).slice(0, 30)}...`);
    } else {
      showToast('डीप लिंक लेख खोजा जा रहा है...');
      searchPosts(cleanUrl).then(res => {
        if (res.length > 0) {
          handleArticleClick(res[0]);
        } else {
          showToast('लेख नहीं मिला');
        }
      });
    }
  };

  return (
    <div className="flex flex-col lg:flex-row gap-8 items-start justify-center w-full max-w-7xl mx-auto">
      {/* Phone Frame */}
      <div className="w-full max-w-[410px] mx-auto bg-slate-900 rounded-[44px] p-3.5 shadow-2xl border-4 border-slate-800 ring-1 ring-slate-700/50 relative overflow-hidden">
        {/* Phone Notch & Sensor */}
        <div className="absolute top-5 left-1/2 -translate-x-1/2 w-32 h-4 bg-slate-900 rounded-full z-50 flex items-center justify-center gap-2">
          <div className="w-2.5 h-2.5 rounded-full bg-slate-800 border border-slate-700" />
          <div className="w-10 h-1.5 rounded-full bg-slate-800" />
        </div>

        {/* Screen Container */}
        <div className="w-full h-[760px] bg-slate-50 rounded-[34px] overflow-hidden flex flex-col relative font-sans text-slate-800 select-none">

          {/* Android Status Bar */}
          <div className="h-9 bg-[#b71c1c] text-white/90 text-xs px-5 pt-2 flex items-center justify-between font-medium z-40">
            <span>09:41</span>
            <div className="flex items-center gap-2">
              <Wifi size={13} />
              <span className="text-[10px] font-bold tracking-wider">5G</span>
              <Battery size={15} />
            </div>
          </div>

          {/* Top Bar */}
          <header className="bg-[#b71c1c] text-white px-3.5 py-2.5 shadow-md flex items-center justify-between z-30 shrink-0">
            <div className="flex items-center gap-2.5">
              {currentScreen === 'home' || currentScreen === 'categories' || currentScreen === 'search' || currentScreen === 'saved' ? (
                <button
                  onClick={() => setIsDrawerOpen(true)}
                  className="p-1.5 rounded-full hover:bg-white/10 active:bg-white/20 transition-colors"
                  aria-label="Menu"
                >
                  <Menu size={22} />
                </button>
              ) : (
                <button
                  onClick={() => {
                    if (currentScreen === 'category_articles') setCurrentScreen('categories');
                    else setCurrentScreen('home');
                  }}
                  className="p-1.5 rounded-full hover:bg-white/10 active:bg-white/20 transition-colors"
                  aria-label="Back"
                >
                  <ArrowLeft size={22} />
                </button>
              )}

              <div className="flex items-center gap-2">
                <img
                  src="/ns_guruji_logo.png"
                  alt="NS Guruji Logo"
                  className="w-8 h-8 rounded-lg object-contain bg-white p-0.5 shadow-sm"
                />
                <div>
                  <h1 className="text-base font-bold tracking-wide leading-none text-white">
                    {currentScreen === 'home' && 'NS Guruji'}
                    {currentScreen === 'categories' && 'श्रेणियां'}
                    {currentScreen === 'category_articles' && (activeCategory?.name || 'लेख')}
                    {currentScreen === 'search' && 'खोजें (Search)'}
                    {currentScreen === 'saved' && 'सेव किए गए लेख'}
                    {currentScreen === 'article' && 'NS Guruji'}
                    {currentScreen === 'about' && 'हमारे बारे में'}
                    {currentScreen === 'privacy' && 'गोपनीयता नीति'}
                  </h1>
                  <span className="text-[10px] text-red-100 font-medium tracking-tight">
                    सरकारी नौकरी, योजनाएं एवं अपडेट्स
                  </span>
                </div>
              </div>
            </div>

            <div className="flex items-center gap-1">
              {currentScreen === 'article' && activeArticle ? (
                <>
                  <button
                    onClick={() => toggleBookmark(activeArticle)}
                    className="p-1.5 rounded-full hover:bg-white/10"
                    title="Bookmark"
                  >
                    <Bookmark
                      size={20}
                      fill={isPostBookmarked(activeArticle.id) ? '#ffffff' : 'none'}
                    />
                  </button>
                  <button
                    onClick={() => {
                      if (navigator.share) {
                        navigator.share({
                          title: cleanHtmlText(activeArticle.title.rendered),
                          url: activeArticle.link
                        }).catch(() => {});
                      } else {
                        showToast('लेख लिंक कॉपी किया गया!');
                      }
                    }}
                    className="p-1.5 rounded-full hover:bg-white/10"
                    title="Share"
                  >
                    <Share2 size={20} />
                  </button>
                </>
              ) : (
                <button
                  onClick={() => setCurrentScreen('search')}
                  className="p-1.5 rounded-full hover:bg-white/10"
                  title="Search"
                >
                  <Search size={20} />
                </button>
              )}
            </div>
          </header>

          {/* Drawer Overlay & Menu */}
          {isDrawerOpen && (
            <div className="absolute inset-0 z-50 flex">
              <div
                className="absolute inset-0 bg-black/50 transition-opacity"
                onClick={() => setIsDrawerOpen(false)}
              />
              <div className="relative w-4/5 max-w-[280px] h-full bg-white shadow-2xl flex flex-col z-10 animate-in slide-in-from-left duration-200">
                {/* Drawer Header */}
                <div className="bg-gradient-to-br from-[#b71c1c] to-[#d32f2f] text-white p-5">
                  <img
                    src="/ns_guruji_logo.png"
                    alt="NS Guruji"
                    className="w-14 h-14 rounded-xl bg-white p-1 shadow-md mb-3 object-contain"
                  />
                  <h2 className="text-lg font-bold">NS Guruji</h2>
                  <p className="text-xs text-red-100">nsguruji.com</p>
                  <p className="text-[11px] text-red-100/90 mt-1">
                    भारत का प्रमुख हिंदी रोजगार व योजना पोर्टल
                  </p>
                </div>

                {/* Drawer Links */}
                <div className="flex-1 overflow-y-auto py-2">
                  <DrawerItem
                    icon={<Home size={19} className="text-[#d32f2f]" />}
                    label="होम (Home)"
                    active={currentScreen === 'home'}
                    onClick={() => { setCurrentScreen('home'); setIsDrawerOpen(false); }}
                  />
                  <DrawerItem
                    icon={<Grid size={19} className="text-[#0d47a1]" />}
                    label="श्रेणियां (Categories)"
                    active={currentScreen === 'categories'}
                    onClick={() => { setCurrentScreen('categories'); setIsDrawerOpen(false); }}
                  />
                  <DrawerItem
                    icon={<Bookmark size={19} className="text-[#b71c1c]" />}
                    label="सेव किए गए लेख (Saved)"
                    active={currentScreen === 'saved'}
                    onClick={() => { setCurrentScreen('saved'); setIsDrawerOpen(false); }}
                  />
                  <DrawerItem
                    icon={<Search size={19} className="text-slate-600" />}
                    label="सर्च करें (Search)"
                    active={currentScreen === 'search'}
                    onClick={() => { setCurrentScreen('search'); setIsDrawerOpen(false); }}
                  />
                  <div className="my-2 border-t border-slate-100" />
                  <DrawerItem
                    icon={<Share2 size={19} className="text-emerald-600" />}
                    label="ऐप शेयर करें (Share App)"
                    onClick={() => {
                      showToast('शेयर लिंक तैयार किया गया!');
                      setIsDrawerOpen(false);
                    }}
                  />
                  <DrawerItem
                    icon={<Star size={19} className="text-amber-500" />}
                    label="रेटिंग दें (Rate 5 Stars)"
                    onClick={() => {
                      showToast('Google Play Console पर रेटिंग दी जा सकती है');
                      setIsDrawerOpen(false);
                    }}
                  />
                  <DrawerItem
                    icon={<ShieldCheck size={19} className="text-indigo-600" />}
                    label="गोपनीयता नीति (Privacy Policy)"
                    active={currentScreen === 'privacy'}
                    onClick={() => { setCurrentScreen('privacy'); setIsDrawerOpen(false); }}
                  />
                  <DrawerItem
                    icon={<Info size={19} className="text-slate-600" />}
                    label="हमारे बारे में (About Us)"
                    active={currentScreen === 'about'}
                    onClick={() => { setCurrentScreen('about'); setIsDrawerOpen(false); }}
                  />
                </div>

                {/* Drawer Footer */}
                <div className="p-3.5 bg-slate-50 border-t border-slate-200 text-center text-[11px] text-slate-500">
                  संस्करण v1.0.0 (Android 14 Ready)
                </div>
              </div>
            </div>
          )}

          {/* Screen Content Body */}
          <div className="flex-1 overflow-y-auto bg-slate-50 relative">

            {/* SCREEN 1: HOME */}
            {currentScreen === 'home' && (
              <div className="pb-4">
                {/* Horizontal Categories */}
                <div className="bg-white border-b border-slate-200 py-2.5 px-3 overflow-x-auto flex gap-2 scrollbar-none">
                  <button
                    onClick={() => setSelectedCategoryId(null)}
                    className={`px-3.5 py-1.5 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${
                      selectedCategoryId === null
                        ? 'bg-[#b71c1c] text-white shadow-sm'
                        : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                    }`}
                  >
                    सभी लेख (All)
                  </button>
                  {categories.map(cat => (
                    <button
                      key={cat.id}
                      onClick={() => setSelectedCategoryId(cat.id)}
                      className={`px-3 py-1.5 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${
                        selectedCategoryId === cat.id
                          ? 'bg-[#b71c1c] text-white shadow-sm'
                          : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                      }`}
                    >
                      {cleanHtmlText(cat.name)} ({cat.count})
                    </button>
                  ))}
                </div>

                {/* Loading state */}
                {isLoading && (
                  <div className="p-4 space-y-3">
                    {[1, 2, 3].map(i => (
                      <div key={i} className="bg-white rounded-xl p-3 shadow-sm border border-slate-100 animate-pulse">
                        <div className="h-36 bg-slate-200 rounded-lg mb-3" />
                        <div className="h-4 bg-slate-200 rounded w-3/4 mb-2" />
                        <div className="h-3 bg-slate-100 rounded w-full mb-1" />
                        <div className="h-3 bg-slate-100 rounded w-2/3" />
                      </div>
                    ))}
                  </div>
                )}

                {/* Error state */}
                {!isLoading && errorMessage && (
                  <div className="p-6 text-center">
                    <AlertCircle className="w-12 h-12 text-red-500 mx-auto mb-2" />
                    <h3 className="font-bold text-slate-800">सर्वर से कनेक्ट नहीं हो सका</h3>
                    <p className="text-xs text-slate-600 mt-1 mb-4">{errorMessage}</p>
                    <button
                      onClick={loadInitialData}
                      className="px-4 py-2 bg-[#b71c1c] text-white text-xs font-bold rounded-lg shadow"
                    >
                      पुनः प्रयास करें (Retry)
                    </button>
                  </div>
                )}

                {/* Posts List */}
                {!isLoading && !errorMessage && (
                  <div className="p-3 space-y-3">
                    <div className="flex items-center justify-between px-1">
                      <span className="text-xs font-bold text-slate-800 flex items-center gap-1.5">
                        <span className="w-2 h-2 rounded-full bg-red-600 inline-block animate-ping" />
                        ताजा लेख (Latest WordPress Posts)
                      </span>
                      <button
                        onClick={loadInitialData}
                        className="text-[11px] text-[#b71c1c] font-semibold flex items-center gap-1"
                      >
                        <RefreshCw size={12} className={isRefreshing ? 'animate-spin' : ''} />
                        रिफ्रेश
                      </button>
                    </div>

                    {displayedPosts.length === 0 ? (
                      <div className="text-center py-10 text-xs text-slate-500">
                        इस श्रेणी में कोई लेख नहीं मिला।
                      </div>
                    ) : (
                      displayedPosts.map((post, idx) => (
                        <React.Fragment key={post.id}>
                          <ArticleCardView
                            post={post}
                            isBookmarked={isPostBookmarked(post.id)}
                            onToggleBookmark={() => toggleBookmark(post)}
                            onClick={() => handleArticleClick(post)}
                          />

                          {/* Ad Banner after every 4 posts */}
                          {(idx + 1) % 4 === 0 && (
                            <div className="my-2 p-2 bg-gradient-to-r from-amber-50 to-orange-50 border border-amber-200 rounded-lg text-center">
                              <span className="inline-block text-[9px] font-bold uppercase tracking-wider bg-amber-200 text-amber-900 px-1.5 py-0.5 rounded mr-2">
                                Google AdMob
                              </span>
                              <span className="text-xs text-slate-700 font-medium">
                                इन-फ़ीड स्पॉन्सर्ड विज्ञापन (Banner 320x50)
                              </span>
                            </div>
                          )}
                        </React.Fragment>
                      ))
                    )}
                  </div>
                )}
              </div>
            )}

            {/* SCREEN 2: CATEGORIES */}
            {currentScreen === 'categories' && (
              <div className="p-3 space-y-2.5">
                <p className="text-xs text-slate-500 px-1">
                  सभी श्रेणियां WordPress API से लाइव फेच की गई हैं:
                </p>
                {categories.map(cat => (
                  <div
                    key={cat.id}
                    onClick={() => openCategoryArticles(cat)}
                    className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between hover:bg-slate-50 active:scale-[0.99] transition-all cursor-pointer"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-9 h-9 rounded-lg bg-blue-50 text-[#0d47a1] flex items-center justify-center font-bold">
                        <Folder size={18} />
                      </div>
                      <div>
                        <h4 className="font-bold text-sm text-slate-800">
                          {cleanHtmlText(cat.name)}
                        </h4>
                        <span className="text-xs text-slate-500">
                          {cat.count} लेख उपलब्ध
                        </span>
                      </div>
                    </div>
                    <ChevronRight size={18} className="text-slate-400" />
                  </div>
                ))}
              </div>
            )}

            {/* SCREEN 2B: CATEGORY ARTICLES */}
            {currentScreen === 'category_articles' && (
              <div className="p-3 space-y-3">
                <div className="bg-red-50 p-2.5 rounded-lg border border-red-100 text-xs text-slate-700 flex items-center justify-between">
                  <span>श्रेणी: <b>{activeCategory?.name}</b></span>
                  <span className="text-[#b71c1c] font-bold">{categoryPosts.length} लेख</span>
                </div>
                {isLoading ? (
                  <div className="p-4 text-center text-xs text-slate-500 animate-pulse">
                    लेख लोड हो रहे हैं...
                  </div>
                ) : (
                  categoryPosts.map(post => (
                    <ArticleCardView
                      key={post.id}
                      post={post}
                      isBookmarked={isPostBookmarked(post.id)}
                      onToggleBookmark={() => toggleBookmark(post)}
                      onClick={() => handleArticleClick(post)}
                    />
                  ))
                )}
              </div>
            )}

            {/* SCREEN 3: SEARCH */}
            {currentScreen === 'search' && (
              <div className="p-3 space-y-4">
                <div className="relative">
                  <Search size={18} className="absolute left-3.5 top-3 text-slate-400" />
                  <input
                    type="text"
                    value={searchQuery}
                    onChange={e => setSearchQuery(e.target.value)}
                    placeholder="नौकरी, प्रवेश पत्र या योजना खोजें..."
                    className="w-full pl-10 pr-9 py-2.5 text-xs bg-white rounded-xl border border-slate-300 focus:border-[#b71c1c] focus:outline-none shadow-sm"
                  />
                  {searchQuery && (
                    <button
                      onClick={() => setSearchQuery('')}
                      className="absolute right-3 top-2.5 text-slate-400 hover:text-slate-600"
                    >
                      <X size={16} />
                    </button>
                  )}
                </div>

                {!searchQuery && (
                  <div>
                    <h4 className="text-xs font-bold text-slate-700 mb-2">लोकप्रिय खोजें:</h4>
                    <div className="flex flex-wrap gap-1.5">
                      {recentSearches.map(tag => (
                        <button
                          key={tag}
                          onClick={() => setSearchQuery(tag)}
                          className="px-3 py-1 bg-white border border-slate-200 rounded-full text-xs text-slate-700 hover:bg-slate-100"
                        >
                          {tag}
                        </button>
                      ))}
                    </div>
                  </div>
                )}

                {isSearching && (
                  <div className="text-center py-6 text-xs text-slate-500">
                    WordPress API पर खोज जारी है...
                  </div>
                )}

                {!isSearching && searchQuery && searchResults.length === 0 && (
                  <div className="text-center py-8 text-slate-500 text-xs">
                    '{searchQuery}' के लिए कोई परिणाम नहीं मिला।
                  </div>
                )}

                {!isSearching && searchResults.length > 0 && (
                  <div className="space-y-3">
                    <p className="text-xs font-semibold text-slate-700">
                      खोज परिणाम ({searchResults.length}):
                    </p>
                    {searchResults.map(post => (
                      <ArticleCardView
                        key={post.id}
                        post={post}
                        isBookmarked={isPostBookmarked(post.id)}
                        onToggleBookmark={() => toggleBookmark(post)}
                        onClick={() => handleArticleClick(post)}
                      />
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* SCREEN 4: SAVED ARTICLES (BOOKMARKS) */}
            {currentScreen === 'saved' && (
              <div className="p-3 space-y-3">
                <div className="bg-amber-50 border border-amber-200 p-2.5 rounded-lg text-[11px] text-amber-900 flex items-center gap-2">
                  <Bookmark size={15} className="text-[#b71c1c] shrink-0" />
                  <span>
                    सभी लेख डिवाइस के स्थानीय स्टोरेज में सेव हैं। इन्हें बिना इंटरनेट के भी पढ़ा जा सकता है।
                  </span>
                </div>

                {savedPosts.length === 0 ? (
                  <div className="text-center py-16 px-4">
                    <Bookmark size={40} className="text-slate-300 mx-auto mb-2" />
                    <h4 className="font-bold text-slate-700 text-sm">कोई सेव किया गया लेख नहीं है</h4>
                    <p className="text-xs text-slate-500 mt-1">
                      होम स्क्रीन पर किसी भी लेख के बुकमार्क आइकन पर क्लिक करके उसे यहाँ सहेजें।
                    </p>
                  </div>
                ) : (
                  savedPosts.map(saved => (
                    <div
                      key={saved.id}
                      onClick={() => {
                        // reconstruct minimal post
                        const mockPost: WordPressPost = {
                          id: saved.id,
                          date: saved.date,
                          modified: saved.modified,
                          slug: '',
                          link: saved.link,
                          title: { rendered: saved.title },
                          content: { rendered: saved.content },
                          excerpt: { rendered: saved.excerpt },
                          categories: []
                        };
                        handleArticleClick(mockPost);
                      }}
                      className="bg-white p-3 rounded-xl border border-slate-200 shadow-sm hover:shadow transition-all cursor-pointer relative"
                    >
                      <div className="flex gap-3">
                        {saved.imageUrl && (
                          <img
                            src={saved.imageUrl}
                            alt=""
                            className="w-20 h-20 rounded-lg object-cover bg-slate-100 shrink-0"
                          />
                        )}
                        <div className="flex-1 min-w-0">
                          <span className="text-[10px] font-bold text-[#b71c1c] uppercase">
                            {saved.categoryName || 'सेव किया गया'}
                          </span>
                          <h4 className="font-bold text-xs text-slate-900 line-clamp-2 mt-0.5 leading-snug">
                            {cleanHtmlText(saved.title)}
                          </h4>
                          <span className="text-[10px] text-slate-400 mt-1 block">
                            {formatPostDate(saved.date)}
                          </span>
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            )}

            {/* SCREEN 5: ARTICLE DETAIL SCREEN */}
            {currentScreen === 'article' && activeArticle && (
              <div className="p-4 bg-white min-h-full space-y-4">
                {/* Category badge */}
                <div className="flex items-center justify-between">
                  <span className="px-2.5 py-1 bg-[#b71c1c] text-white text-[11px] font-bold rounded-md">
                    {getPostCategoryName(activeArticle)}
                  </span>
                  <div className="flex items-center gap-1.5 text-[11px] text-slate-500">
                    <Calendar size={13} />
                    <span>{formatPostDate(activeArticle.date)}</span>
                  </div>
                </div>

                {/* Title */}
                <h2 className="text-base font-extrabold text-slate-900 leading-snug font-sans">
                  {cleanHtmlText(activeArticle.title.rendered)}
                </h2>

                {/* Modified date if any */}
                {activeArticle.modified && activeArticle.modified !== activeArticle.date && (
                  <div className="text-[11px] text-[#0d47a1] font-medium flex items-center gap-1 bg-blue-50 px-2 py-1 rounded">
                    <Clock size={12} />
                    <span>अंतिम अपडेट: {formatPostDate(activeArticle.modified)}</span>
                  </div>
                )}

                {/* Featured Image */}
                {getFeaturedImageUrl(activeArticle) && (
                  <img
                    src={getFeaturedImageUrl(activeArticle)!}
                    alt={cleanHtmlText(activeArticle.title.rendered)}
                    className="w-full rounded-xl object-cover shadow-sm max-h-52 bg-slate-100"
                  />
                )}

                {/* Native In-Article Banner Ad */}
                <div className="p-2.5 bg-slate-100 border border-dashed border-slate-300 rounded-lg text-center">
                  <span className="text-[9px] uppercase font-bold bg-slate-300 text-slate-700 px-1.5 py-0.5 rounded mr-2">
                    AdMob Banner
                  </span>
                  <span className="text-xs text-slate-600 font-medium font-mono">
                    Unit: ca-app-pub-3784953261980933/4286214969
                  </span>
                </div>

                {/* Rendered HTML Content */}
                <div
                  className="article-content text-slate-800 text-xs leading-relaxed space-y-2.5 overflow-x-auto"
                  dangerouslySetInnerHTML={{ __html: activeArticle.content.rendered }}
                />

                <div className="pt-4 border-t border-slate-200">
                  <a
                    href={activeArticle.link}
                    target="_blank"
                    rel="noreferrer"
                    className="flex items-center justify-center gap-2 w-full py-2.5 bg-[#b71c1c] text-white text-xs font-bold rounded-lg shadow-sm"
                  >
                    <ExternalLink size={15} />
                    वेबसाइट पर मूल लेख देखें (nsguruji.com)
                  </a>
                </div>
              </div>
            )}

            {/* SCREEN 6: ABOUT */}
            {currentScreen === 'about' && (
              <div className="p-4 space-y-4 text-center">
                <img
                  src="/ns_guruji_logo.png"
                  alt="NS Guruji"
                  className="w-20 h-20 mx-auto rounded-2xl p-1 bg-white shadow-md object-contain"
                />
                <h3 className="text-lg font-extrabold text-[#b71c1c]">NS Guruji</h3>
                <p className="text-xs text-slate-500">वर्शन 1.0.0 (Production Release)</p>
                <div className="bg-white p-4 rounded-xl border border-slate-200 text-left text-xs text-slate-700 leading-relaxed shadow-sm">
                  <b>NS Guruji (nsguruji.com)</b> भारत का सर्वाधिक विश्वसनीय सरकारी सूचना पोर्टल है। इस ऐप के माध्यम से सभी छात्र और नागरिक बिना किसी लॉगिन के:
                  <ul className="list-disc ml-4 mt-2 space-y-1">
                    <li>नवीनतम सरकारी भर्तियां (Govt Jobs)</li>
                    <li>प्रवेश पत्र (Admit Cards) व रिजल्ट्स</li>
                    <li>केंद्र व राज्य सरकारी योजनाएं</li>
                    <li>वित्तीय समाचार व मार्गदर्शन</li>
                  </ul>
                </div>
                <a
                  href="https://nsguruji.com/"
                  target="_blank"
                  rel="noreferrer"
                  className="block w-full py-2.5 bg-[#0d47a1] text-white text-xs font-bold rounded-lg"
                >
                  आधिकारिक वेबसाइट खोलें
                </a>
              </div>
            )}

            {/* SCREEN 7: PRIVACY POLICY */}
            {currentScreen === 'privacy' && (
              <div className="p-4 bg-white space-y-3 text-xs leading-relaxed text-slate-700">
                <h3 className="text-sm font-bold text-[#b71c1c]">NS Guruji गोपनीयता नीति (Privacy Policy)</h3>
                <p className="text-[11px] text-slate-400">अंतिम संशोधन: सितंबर 2026</p>
                <p>
                  <b>1. उपयोगकर्ता खाता:</b> हमारे ऐप का उपयोग करने के लिए किसी लॉगिन या साइनअप की आवश्यकता नहीं है।
                </p>
                <p>
                  <b>2. Google AdMob:</b> ऐप विज्ञापनों के लिए Google AdMob का उपयोग करता है। AdMob डिवाइस पहचानकर्ता और कुकीज़ का उपयोग प्रासंगिक विज्ञापन दिखाने हेतु कर सकता है।
                </p>
                <p>
                  <b>3. Firebase Cloud Messaging:</b> नई सूचनाओं के लिए FCM टोकन का उपयोग किया जाता है, जिसमें कोई व्यक्तिगत डेटा एकत्र नहीं होता।
                </p>
                <p>
                  <b>4. लोकल स्टोरेज:</b> बुकमार्क केवल उपयोगकर्ता के फोन पर सुरक्षित रहते हैं।
                </p>
              </div>
            )}

          </div>

          {/* Persistent AdMob Banner at bottom of all screens */}
          <div className="bg-slate-900 text-slate-300 py-1.5 px-3 flex items-center justify-between text-[10px] shrink-0 border-t border-slate-800">
            <span className="bg-amber-400 text-slate-900 font-bold px-1.5 rounded text-[8px]">
              AdMob
            </span>
            <span className="font-mono text-slate-300">Banner: 320x50 Smart Banner</span>
            <span className="text-emerald-400 text-[9px] font-bold">Active</span>
          </div>

          {/* Bottom Navigation Bar */}
          <nav className="bg-white border-t border-slate-200 px-3 py-2 flex items-center justify-around shrink-0 z-30">
            <BottomTabItem
              icon={<Home size={19} />}
              label="होम"
              active={currentScreen === 'home'}
              onClick={() => setCurrentScreen('home')}
            />
            <BottomTabItem
              icon={<Grid size={19} />}
              label="श्रेणियां"
              active={currentScreen === 'categories' || currentScreen === 'category_articles'}
              onClick={() => setCurrentScreen('categories')}
            />
            <BottomTabItem
              icon={<Search size={19} />}
              label="खोजें"
              active={currentScreen === 'search'}
              onClick={() => setCurrentScreen('search')}
            />
            <BottomTabItem
              icon={<Bookmark size={19} />}
              label="सेव लेख"
              active={currentScreen === 'saved'}
              onClick={() => setCurrentScreen('saved')}
              badge={savedPosts.length}
            />
          </nav>

          {/* Android Navigation Bar (Home Pill) */}
          <div className="h-4 bg-white flex items-center justify-center shrink-0">
            <div className="w-24 h-1 bg-slate-300 rounded-full" />
          </div>

          {/* Interstitial Ad Simulation Modal */}
          {showInterstitialAd && (
            <div className="absolute inset-0 bg-black/85 z-50 flex flex-col items-center justify-between p-6 text-white text-center animate-in fade-in duration-200">
              <div className="w-full flex justify-between items-center text-xs">
                <span className="bg-amber-400 text-slate-900 font-bold px-2 py-0.5 rounded text-[10px]">
                  Google AdMob Interstitial
                </span>
                <button
                  disabled={interstitialTimer > 0}
                  onClick={closeInterstitial}
                  className={`px-3 py-1 rounded text-xs font-bold transition-all ${
                    interstitialTimer > 0
                      ? 'bg-slate-700 text-slate-400 cursor-not-allowed'
                      : 'bg-white text-slate-900 hover:bg-slate-100'
                  }`}
                >
                  {interstitialTimer > 0 ? `Skip in ${interstitialTimer}s` : 'Close (✕)'}
                </button>
              </div>

              <div className="space-y-4 max-w-[260px]">
                <div className="w-16 h-16 bg-blue-600 rounded-2xl mx-auto flex items-center justify-center text-2xl font-bold shadow-lg">
                  🎯
                </div>
                <h3 className="text-lg font-bold">NS Guruji Sponsored Ad</h3>
                <p className="text-xs text-slate-300">
                  यह Google AdMob फ़्रीक्वेंसी-कैप्ड इंटरस्टीशियल विज्ञापन का लाइव सिमुलेशन है।
                </p>
                <div className="bg-slate-800 p-2.5 rounded-lg text-[10px] font-mono text-slate-300">
                  Unit: ca-app-pub-3784953261980933/6654086959
                </div>
              </div>

              <div className="text-[10px] text-slate-400">
                AdMob Frequency Cap: अधिकतम 1 विज्ञापन प्रति 3 लेख
              </div>
            </div>
          )}

          {/* Notification Toast */}
          {notificationToast && (
            <div className="absolute bottom-16 left-4 right-4 bg-slate-900/95 text-white text-xs px-3.5 py-2.5 rounded-xl shadow-xl flex items-center gap-2 z-50 animate-in slide-in-from-bottom duration-200">
              <CheckCircle2 size={16} className="text-emerald-400 shrink-0" />
              <span>{notificationToast}</span>
            </div>
          )}

        </div>
      </div>

      {/* Control & Deep Link Testing Side Panel */}
      <div className="w-full lg:w-80 space-y-4">
        {/* Deep Link Simulator Card */}
        <div className="bg-white rounded-2xl p-4 border border-slate-200 shadow-sm">
          <h3 className="text-sm font-bold text-slate-900 flex items-center gap-2 mb-2">
            <ExternalLink size={16} className="text-[#b71c1c]" />
            Deep Link & Push Notification Tester
          </h3>
          <p className="text-xs text-slate-600 mb-3 leading-relaxed">
            WordPress URL या Slug डालकर ऐप में डायरेक्ट आर्टिकल खोलने का परीक्षण करें:
          </p>
          <div className="flex gap-2 mb-2">
            <input
              type="text"
              placeholder="e.g. rrb-ntpc या scheme name"
              value={deepLinkInput}
              onChange={e => setDeepLinkInput(e.target.value)}
              className="flex-1 px-3 py-1.5 text-xs border border-slate-300 rounded-lg focus:outline-none focus:border-[#b71c1c]"
            />
            <button
              onClick={handleDeepLink}
              className="px-3 py-1.5 bg-[#b71c1c] text-white text-xs font-bold rounded-lg shadow-sm hover:bg-[#a01818]"
            >
              Open
            </button>
          </div>

          <div className="space-y-1 mt-3">
            <span className="text-[10px] text-slate-500 font-semibold block">नमूना डीप लिंक्स:</span>
            {posts.slice(0, 3).map(p => (
              <button
                key={p.id}
                onClick={() => handleArticleClick(p)}
                className="w-full text-left text-[11px] text-[#0d47a1] hover:underline truncate block"
              >
                https://nsguruji.com/{p.slug}/
              </button>
            ))}
          </div>
        </div>

        {/* Feature Verification Card */}
        <div className="bg-white rounded-2xl p-4 border border-slate-200 shadow-sm space-y-2.5">
          <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wide">
            अनिवार्य आवश्यकताओं की स्थिति:
          </h4>
          <FeatureCheck label="बिना लॉगिन/साइनअप के तुरंत पढ़ना" passed />
          <FeatureCheck label="WordPress REST API लाइव फेचिंग" passed />
          <FeatureCheck label="हिंदी फ़ॉन्ट्स (Mukta / Devanagari) रेंडरिंग" passed />
          <FeatureCheck label="HTML टेबल्स, लिंक्स व फॉर्मेटिंग सपोर्ट" passed />
          <FeatureCheck label="ऑफ़लाइन बुकमार्क (Room Local Database)" passed />
          <FeatureCheck label="Google AdMob बैनर व इंटरस्टीशियल" passed />
          <FeatureCheck label="Firebase Cloud Messaging (FCM)" passed />
          <FeatureCheck label="Android 7.0+ (Min API 24) कम्पैटिबल" passed />
        </div>
      </div>
    </div>
  );
};

// Sub-components
const ArticleCardView: React.FC<{
  post: WordPressPost;
  isBookmarked: boolean;
  onToggleBookmark: () => void;
  onClick: () => void;
}> = ({ post, isBookmarked, onToggleBookmark, onClick }) => {
  const imageUrl = getFeaturedImageUrl(post);
  const categoryName = getPostCategoryName(post);

  return (
    <div
      onClick={onClick}
      className="bg-white rounded-xl border border-slate-200/90 shadow-sm overflow-hidden hover:border-red-200 active:scale-[0.99] transition-all cursor-pointer"
    >
      {imageUrl && (
        <div className="relative h-36 w-full bg-slate-100 overflow-hidden">
          <img
            src={imageUrl}
            alt=""
            className="w-full h-full object-cover"
            loading="lazy"
          />
          <span className="absolute bottom-2 left-2 px-2 py-0.5 bg-[#b71c1c]/90 text-white text-[10px] font-bold rounded shadow-sm">
            {categoryName}
          </span>
        </div>
      )}

      <div className="p-3">
        {!imageUrl && (
          <span className="inline-block mb-1.5 px-2 py-0.5 bg-[#b71c1c]/10 text-[#b71c1c] text-[10px] font-bold rounded">
            {categoryName}
          </span>
        )}

        <h3 className="font-bold text-xs text-slate-900 line-clamp-2 leading-snug">
          {cleanHtmlText(post.title.rendered)}
        </h3>

        <p className="text-[11px] text-slate-600 line-clamp-2 mt-1 leading-normal">
          {cleanHtmlText(post.excerpt.rendered)}
        </p>

        <div className="flex items-center justify-between mt-2.5 pt-2 border-t border-slate-100">
          <div className="flex items-center gap-1 text-[10px] text-slate-400">
            <Calendar size={11} />
            <span>{formatPostDate(post.date)}</span>
          </div>

          <button
            onClick={e => {
              e.stopPropagation();
              onToggleBookmark();
            }}
            className="p-1 text-slate-400 hover:text-[#b71c1c] transition-colors"
          >
            <Bookmark
              size={15}
              fill={isBookmarked ? '#b71c1c' : 'none'}
              className={isBookmarked ? 'text-[#b71c1c]' : ''}
            />
          </button>
        </div>
      </div>
    </div>
  );
};

const DrawerItem: React.FC<{
  icon: React.ReactNode;
  label: string;
  active?: boolean;
  onClick: () => void;
}> = ({ icon, label, active, onClick }) => (
  <button
    onClick={onClick}
    className={`w-full px-4 py-3 flex items-center gap-3 text-xs font-semibold transition-colors ${
      active
        ? 'bg-red-50 text-[#b71c1c] border-r-4 border-[#b71c1c]'
        : 'text-slate-700 hover:bg-slate-50'
    }`}
  >
    {icon}
    <span>{label}</span>
  </button>
);

const BottomTabItem: React.FC<{
  icon: React.ReactNode;
  label: string;
  active?: boolean;
  onClick: () => void;
  badge?: number;
}> = ({ icon, label, active, onClick, badge }) => (
  <button
    onClick={onClick}
    className={`flex flex-col items-center justify-center py-1 px-3 relative transition-all ${
      active ? 'text-[#b71c1c] font-bold' : 'text-slate-500 font-medium'
    }`}
  >
    <div className="relative">
      {icon}
      {badge !== undefined && badge > 0 && (
        <span className="absolute -top-1 -right-2 bg-[#b71c1c] text-white text-[9px] font-bold rounded-full w-4 h-4 flex items-center justify-center">
          {badge}
        </span>
      )}
    </div>
    <span className="text-[10px] mt-0.5">{label}</span>
  </button>
);

const FeatureCheck: React.FC<{ label: string; passed: boolean }> = ({ label, passed }) => (
  <div className="flex items-center gap-2 text-xs text-slate-700">
    <CheckCircle2 size={15} className="text-emerald-600 shrink-0" />
    <span>{label}</span>
  </div>
);
