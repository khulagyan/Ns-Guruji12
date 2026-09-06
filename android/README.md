# NS Guruji - Official Android Application

A modern, production-grade Android application built with **Kotlin** and **Jetpack Compose (Material 3)** for **[nsguruji.com](https://nsguruji.com/)** — India's leading Hindi portal for Sarkari Jobs, Admit Cards, Results, Govt Schemes, and Financial Updates.

---

## 🌟 Architecture & Tech Stack

- **Platform:** Android (Min SDK 24 / Android 7.0+, Target SDK 34 / Android 14)
- **Language:** 100% Kotlin with Gradle Kotlin DSL (`.gradle.kts`)
- **UI Framework:** Jetpack Compose + Material 3
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture + Repository Pattern
- **Asynchronous:** Kotlin Coroutines + StateFlow / SharedFlow
- **Networking:** Retrofit 2 + OkHttp 3 + Gson Converter
- **Image Loading:** Coil 2 (AsyncImage with caching & crossfade)
- **Local Persistence:** Room Database (Offline article caching & Bookmarks)
- **Ads SDK:** Google Mobile Ads (AdMob) with frequency-capped Interstitials and Banner Ads
- **Push Notifications:** Firebase Cloud Messaging (FCM)
- **Deep Linking:** Full support for `https://nsguruji.com/*` article URLs

---

## 🚀 Step-by-Step Setup Guide

### 1. How to Open in Android Studio
1. Launch **Android Studio** (Hedgehog 2023.1.1, Iguana 2023.2.1, Jellyfish, or Ladybug recommended).
2. On the Welcome screen, click **Open** (or `File -> Open`).
3. Browse to the `android/` directory of this project and click **OK**.
4. Allow Android Studio to sync Gradle dependencies and build the project index.
5. Ensure your Gradle JDK is set to **JDK 17** (`Settings -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JDK`).

### 2. How to Run the App
1. Connect an Android device (via USB with USB Debugging enabled) or start an Android Virtual Device (AVD Emulator running API 24 or higher).
2. Select the `app` run configuration in the top toolbar.
3. Click the green **Run (▶)** button or press `Shift + F10`.
4. The app will launch, automatically connect to `https://nsguruji.com/wp-json/wp/v2/posts?_embed`, and render the latest Hindi articles.

### 3. Where to Put the Original NS Guruji Logo
The official NS Guruji logo is already included in:
```
android/app/src/main/res/drawable/ic_nsguruji_logo.png
```
If you wish to update or supply different mipmap density resolutions:
- Open Android Studio -> Right-click `app/src/main/res` -> **New** -> **Image Asset**.
- In **Asset Type**, choose **Launcher Icons (Adaptive and Legacy)**.
- For **Source Asset**, browse and pick the original NS Guruji logo PNG file.
- Click **Next** and **Finish**.

### 4. How to Configure WordPress API
All API endpoints are centralized in:
```
android/app/src/main/java/com/nsguruji/app/config/AppConfig.kt
```
```kotlin
object AppConfig {
    const val WORDPRESS_BASE_URL = "https://nsguruji.com/"
    const val DEFAULT_PAGE_SIZE = 10
    // ...
}
```
The app automatically uses standard WordPress REST API routes:
- Posts: `/wp-json/wp/v2/posts?_embed=true&per_page=10&page=1`
- Categories: `/wp-json/wp/v2/categories`
- Search: `/wp-json/wp/v2/posts?search=query`

### 5. How to Create and Configure Google AdMob
1. Visit [Google AdMob](https://admob.google.com/) and sign in with your Google account.
2. Click **Apps** -> **Add App**.
3. Select **Android** and whether your app is already published on Google Play or not.
4. Name the app **NS Guruji**.
5. Copy your **AdMob App ID** (format: `ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX`).
6. Create the following Ad Units:
   - **Banner Ad:** Name it `Home_Banner` and copy the Unit ID (`ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX`).
   - **Interstitial Ad:** Name it `Article_Interstitial` and copy the Unit ID.
   - **Native Advanced (Optional):** Name it `Feed_Native`.

### 6. How to Replace Test AdMob IDs with Production IDs
During development, the app uses official Google test IDs to prevent account suspensions. When ready for production:

1. Open `android/app/src/main/AndroidManifest.xml` and replace the meta-data value:
```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-YOUR_ADMOB_ACCOUNT_ID~YOUR_APP_ID" />
```

2. Open `android/app/src/main/java/com/nsguruji/app/config/AppConfig.kt` and replace:
```kotlin
const val ADMOB_APP_ID = "ca-app-pub-YOUR_ADMOB_APP_ID"
const val ADMOB_BANNER_ID = "ca-app-pub-YOUR_BANNER_UNIT_ID"
const val ADMOB_INTERSTITIAL_ID = "ca-app-pub-YOUR_INTERSTITIAL_UNIT_ID"
```

### 7. How to Configure Firebase Cloud Messaging (FCM)
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create a project named **NS Guruji** (or select an existing one).
3. Click **Add App** -> select the **Android** icon.
4. Enter the package name: `com.nsguruji.app`.
5. Enter app nickname: `NS Guruji`.
6. Download the `google-services.json` file.
7. Replace the placeholder file at:
```
android/app/google-services.json
```
8. In Firebase Console -> **Cloud Messaging**, you can now broadcast notifications to all your app readers without requiring any login or signup!

### 8. How to Create the Release APK
To generate a release APK for testing or direct distribution:
1. In Android Studio, click **Build** -> **Build Bundle(s) / APK(s)** -> **Build APK(s)**.
2. Or via terminal:
```bash
./gradlew assembleRelease
```
3. The APK will be generated at:
```
android/app/build/outputs/apk/release/app-release.apk
```

### 9. How to Generate Signed AAB (Android App Bundle for Google Play)
1. In Android Studio, go to **Build** -> **Generate Signed Bundle / APK...**
2. Choose **Android App Bundle** and click **Next**.
3. Under **Key store path**, click **Create new...** if you don't have a keystore:
   - Choose a secure file location (e.g., `nsguruji-release-key.jks`).
   - Enter password, key alias (`nsguruji_key`), and validity (25+ years).
   - Fill in your Organization details and click **OK**.
4. Select **release** build variant and ensure `V1` and `V2` (Full APK Signature) are checked.
5. Click **Create**.
6. Your signed `.aab` file will be generated in `android/app/release/app-release.aab`.

### 10. How to Publish on Google Play Console
1. Go to [Google Play Console](https://play.google.com/console/) and sign in with your developer account ($25 one-time registration).
2. Click **Create app**:
   - App name: **NS Guruji: Govt Jobs & Yojana**
   - Default language: **Hindi (hi-IN)** or English (India)
   - App or game: **App**
   - Free or paid: **Free**
3. Complete the **Set up your app** tasks:
   - **Privacy Policy URL:** `https://nsguruji.com/privacy-policy/`
   - **App access:** All functionality is available without special access (no login).
   - **Ads:** Check "Yes, my app contains ads".
   - **Content rating:** Complete the questionnaire (News/Educational content).
   - **Target audience:** Ages 18 and over.
   - **News apps:** Complete the declaration for news/portal apps.
   - **Data safety:** Disclose that AdMob and Firebase collect device identifiers and diagnostics.
4. In **Store presence** -> **Main store listing**:
   - Upload app icon (512x512 PNG, using the official NS Guruji logo).
   - Upload feature graphic (1024x500 PNG).
   - Upload phone screenshots (at least 2).
5. In **Production** (or Internal Testing):
   - Click **Create new release**.
   - Upload the signed `app-release.aab`.
   - Provide release notes in Hindi and English.
   - Review and rollout the release!
