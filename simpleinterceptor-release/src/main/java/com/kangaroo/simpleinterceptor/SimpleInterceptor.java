package com.kangaroo.simpleinterceptor;

import android.content.Context;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp拦截器
 */
public final class SimpleInterceptor implements Interceptor {

    public SimpleInterceptor(Context context) {
    }


    @Override public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        return chain.proceed(request);
    }
}
