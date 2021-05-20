package com.kangaroo.simpleinterceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * OkHttp拦截器
 */
class SimpleInterceptor(context: Context?) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request)
    }
}