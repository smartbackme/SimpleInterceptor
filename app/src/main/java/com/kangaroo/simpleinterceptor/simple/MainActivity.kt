package com.kangaroo.simpleinterceptor.simple

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.kangaroo.simpleinterceptor.SimpleInterceptor
import com.kangaroo.simpleinterceptor.SimpleLaunch
import com.kangaroo.simpleinterceptor.sample.R
import com.kangaroo.simpleinterceptor.simple.data.service.SampleApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.do_http).setOnClickListener(View.OnClickListener { doHttpActivity() })
        findViewById<Button>(R.id.launch_interceptor).setOnClickListener(View.OnClickListener { launchChuckDirectly() })
    }
    private fun getClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder() // Add a ChuckInterceptor instance to your OkHttp client
            .addInterceptor(SimpleInterceptor(context))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    private fun launchChuckDirectly() {
        // Optionally launch Chuck directly from your own app UI
        startActivity(SimpleLaunch.getLaunchIntent(this))
    }

    private fun doHttpActivity() {
        val api: SampleApiService.HttpbinApi = SampleApiService.getInstance(getClient(this))

        val cb = object : Callback<Void>{
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                t?.printStackTrace()
            }

        }
        api.get().enqueue(cb)
        api.post(SampleApiService.Data("posted")).enqueue(cb)
        api.patch(SampleApiService.Data("patched")).enqueue(cb)
        api.put(SampleApiService.Data("put")).enqueue(cb)
        api.delete().enqueue(cb)
        api.status(201).enqueue(cb)
        api.status(401).enqueue(cb)
        api.status(500).enqueue(cb)
        api.delay(9).enqueue(cb)
        api.delay(15).enqueue(cb)
        api.redirectTo("https://http2.akamai.com").enqueue(cb)
        api.redirect(3).enqueue(cb)
        api.redirectRelative(2).enqueue(cb)
        api.redirectAbsolute(4).enqueue(cb)
        api.stream(500).enqueue(cb)
        api.streamBytes(2048).enqueue(cb)
        api.image("image/png").enqueue(cb)
        api.gzip().enqueue(cb)
        api.xml().enqueue(cb)
        api.utf8().enqueue(cb)
        api.deflate().enqueue(cb)
        api.cookieSet("v").enqueue(cb)
        api.basicAuth("me", "pass").enqueue(cb)
        api.drip(512, 5, 1, 200).enqueue(cb)
        api.deny().enqueue(cb)
        api.cache("Mon").enqueue(cb)
        api.cache(30).enqueue(cb)
    }

}