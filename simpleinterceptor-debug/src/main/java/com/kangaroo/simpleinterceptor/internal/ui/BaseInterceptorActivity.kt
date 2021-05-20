package com.kangaroo.simpleinterceptor.internal.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kangaroo.simpleinterceptor.internal.tools.NotificationHelper

/**
 * 拦截Activity基类
 */
internal abstract class BaseInterceptorActivity : AppCompatActivity() {
    private var notificationHelper: NotificationHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationHelper = NotificationHelper(this)
    }

    /**
     * 前台则清除通知
     */
    override fun onResume() {
        super.onResume()
        isInForeground = true
        notificationHelper!!.dismiss()
    }

    override fun onPause() {
        super.onPause()
        isInForeground = false
    }

    companion object {
        /**
         * 前台标识
         * @return
         */
        @JvmStatic
        var isInForeground = false
            private set
    }
}