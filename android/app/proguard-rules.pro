# Add project specific ProGuard rules here.
-keep class com.nsguruji.app.data.model.** { *; }
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Retrofit & OkHttp
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

# Google Mobile Ads
-keep class com.google.android.gms.ads.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
