package com.kangaroo.simpleinterceptor.internal.components.services

import android.app.IntentService
import android.content.Intent
import com.kangaroo.simpleinterceptor.internal.components.providers.InterceptorContentProvider
import com.kangaroo.simpleinterceptor.internal.tools.NotificationHelper

/**
 * 清除数据
 */
internal class ClearTransactionsService : IntentService("SimpleInterceptor-ClearTransactionsService") {
    override fun onHandleIntent(intent: Intent?) {
        contentResolver.delete(InterceptorContentProvider.TRANSACTION_URI!!, null, null)
        NotificationHelper.clearBuffer()
        val notificationHelper = NotificationHelper(this)
        notificationHelper.dismiss()
    }
}