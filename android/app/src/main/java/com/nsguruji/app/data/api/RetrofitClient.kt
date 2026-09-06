package com.nsguruji.app.data.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.nsguruji.app.config.AppConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var apiService: WordPressApiService? = null

    fun getService(context: Context): WordPressApiService {
        return apiService ?: synchronized(this) {
            apiService ?: buildService(context).also { apiService = it }
        }
    }

    private fun buildService(context: Context): WordPressApiService {
        val cacheDir = File(context.cacheDir, "http_cache")
        val cacheSize = 20L * 1024L * 1024L // 20 MB

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val okHttpClient = OkHttpClient.Builder()
            .cache(Cache(cacheDir, cacheSize))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.WORDPRESS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(WordPressApiService::class.java)
    }
}
