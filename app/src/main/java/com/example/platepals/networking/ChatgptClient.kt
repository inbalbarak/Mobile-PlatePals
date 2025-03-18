package com.example.platepals.networking

import com.example.platepals.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ChatgptClient {
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(ChatgptInterceptor())
            .build()
    }

    val chatgptApiClient: ChatgptAPI by lazy {
        val retrofitClient = Retrofit.Builder()
            .baseUrl(BuildConfig.CHATGPT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitClient.create(ChatgptAPI::class.java)
    }
}