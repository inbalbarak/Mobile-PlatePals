package com.example.platepals.networking

import com.example.platepals.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ChatgptInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.CHATGPT_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}