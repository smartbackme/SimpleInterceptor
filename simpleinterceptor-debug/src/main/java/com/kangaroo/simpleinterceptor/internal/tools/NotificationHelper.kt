package com.kangaroo.simpleinterceptor.internal.tools

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.LongSparseArray
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.kangaroo.simpleinterceptor.SimpleLaunch
import com.kangaroo.simpleinterceptor.R
import com.kangaroo.simpleinterceptor.internal.components.services.ClearTransactionsService
import com.kangaroo.simpleinterceptor.internal.data.HttpTransaction
import com.kangaroo.simpleinterceptor.internal.ui.BaseInterceptorActivity.Companion.isInForeground

internal class NotificationHelper(private val context: Context) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_category),
                    NotificationManager.IMPORTANCE_LOW
                )
            )
            //            try {
//                setChannelId = NotificationCompat.Builder.class.getMethod("setChannelId", String.class);
//            } catch (Exception ignored) {}
        }
    }
    @Synchronized
    fun show(transaction: HttpTransaction) {
        addToBuffer(transaction)
        if (!isInForeground) {
            val builder = NotificationCompat.Builder(
                context, CHANNEL_ID
            )
                .setContentIntent(
                    PendingIntent.getActivity(
                        context, 0, SimpleLaunch.getLaunchIntent(
                            context
                        ), 0
                    )
                )
                .setLocalOnly(true)
                .setSmallIcon(R.drawable.interceptor_ic_notification_white_24dp)
                .setColor(ContextCompat.getColor(context, R.color.interceptor_colorPrimary))
                .setContentTitle(context.getString(R.string.interceptor_notification_title))
            val inboxStyle = NotificationCompat.InboxStyle()
            //            if (setChannelId != null) {
//                try { setChannelId.invoke(builder, CHANNEL_ID); } catch (Exception ignored) {}
//            }
            for ((count, i) in (transactionBuffer.size() - 1 downTo 0).withIndex()) {
                if (count < BUFFER_SIZE) {
                    if (count == 0) {
                        builder.setContentText(transactionBuffer.valueAt(i).notificationText)
                    }
                    inboxStyle.addLine(transactionBuffer.valueAt(i).notificationText)
                }
            }
            builder.setAutoCancel(true)
            builder.setStyle(inboxStyle)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setSubText(transactionCount.toString())
            } else {
                builder.setNumber(transactionCount)
            }
            builder.addAction(clearAction)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private val clearAction: NotificationCompat.Action
        private get() {
            val clearTitle: CharSequence = context.getString(R.string.interceptor_clear)
            val deleteIntent = Intent(context, ClearTransactionsService::class.java)
            val intent =
                PendingIntent.getService(context, 11, deleteIntent, PendingIntent.FLAG_ONE_SHOT)
            return NotificationCompat.Action(
                R.drawable.interceptor_ic_delete_white_24dp,
                clearTitle, intent
            )
        }

    fun dismiss() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    companion object {
        private const val CHANNEL_ID = "interceptor"
        private const val NOTIFICATION_ID = 1138
        private const val BUFFER_SIZE = 10
        private val transactionBuffer = LongSparseArray<HttpTransaction>()
        private var transactionCount = 0

        //    private Method setChannelId;
        @Synchronized
        fun clearBuffer() {
            transactionBuffer.clear()
            transactionCount = 0
        }

        /**
         * 给缓存加值，超过尺寸则删除第一个
         * @param transaction
         */
        @Synchronized
        private fun addToBuffer(transaction: HttpTransaction) {
            if (transaction.status == HttpTransaction.Status.Requested) {
                transactionCount++
            }
            transactionBuffer.put(transaction.id!!, transaction)
            if (transactionBuffer.size() > BUFFER_SIZE) {
                transactionBuffer.removeAt(0)
            }
        }
    }


}