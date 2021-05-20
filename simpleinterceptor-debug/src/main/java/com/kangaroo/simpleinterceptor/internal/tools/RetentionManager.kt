package com.kangaroo.simpleinterceptor.internal.tools

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.kangaroo.simpleinterceptor.SimpleInterceptor
import com.kangaroo.simpleinterceptor.internal.components.providers.InterceptorContentProvider
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 持久化管理
 */
internal class RetentionManager(private val context: Context, retentionPeriod: SimpleInterceptor.Period) {
    private val period: Long

    /**
     * 清理频率
     */
    private val cleanupFrequency: Long
    private val prefs: SharedPreferences

    /**
     * 维护清理，如果到达清理时间则清理
     */
    @Synchronized
    fun doMaintenance() {
        if (period > 0) {
            val now = Date().time
            if (isCleanupDue(now)) {
                Log.i(LOG_TAG, "Performing data retention maintenance...")
                deleteSince(getThreshold(now))
                updateLastCleanup(now)
            }
        }
    }

    /**
     * 获取最后一次清理时间
     * @param fallback
     * @return
     */
    private fun getLastCleanup(fallback: Long): Long {
        if (lastCleanup == 0L) {
            lastCleanup = prefs.getLong(KEY_LAST_CLEANUP, fallback)
        }
        return lastCleanup
    }

    /**
     * 更新清理时间
     * @param time
     */
    private fun updateLastCleanup(time: Long) {
        lastCleanup = time
        prefs.edit().putLong(KEY_LAST_CLEANUP, time).apply()
    }

    /**
     * 以某个时间段来清理
     * @param threshold
     */
    private fun deleteSince(threshold: Long) {
        val rows = context.contentResolver.delete(
            InterceptorContentProvider.TRANSACTION_URI!!,
            "requestDate <= ?", arrayOf(threshold.toString())
        )
        Log.i(LOG_TAG, "$rows transactions deleted")
    }

    /**
     * 判断是否到达清理时间
     * @param now
     * @return
     */
    private fun isCleanupDue(now: Long): Boolean {
        return now - getLastCleanup(now) > cleanupFrequency
    }

    /**
     * 计算清理数据的时间
     * @param now
     * @return
     */
    private fun getThreshold(now: Long): Long {
        return if (period == 0L) now else now - period
    }

    /**
     * 根据策略来计算保存时间
     * @param period
     * @return
     */
    private fun toMillis(period: SimpleInterceptor.Period): Long {
        return when (period) {
            SimpleInterceptor.Period.ONE_HOUR -> TimeUnit.HOURS.toMillis(1)
            SimpleInterceptor.Period.ONE_DAY -> TimeUnit.DAYS.toMillis(1)
            SimpleInterceptor.Period.ONE_WEEK -> TimeUnit.DAYS.toMillis(7)
            else -> 0
        }
    }

    companion object {
        private const val LOG_TAG = "interceptor"
        private const val PREFS_NAME = "interceptor_preferences"
        private const val KEY_LAST_CLEANUP = "last_cleanup"
        private var lastCleanup: Long = 0
    }

    init {
        period = toMillis(retentionPeriod)
        prefs = context.getSharedPreferences(PREFS_NAME, 0)
        /**
         * 如果过期时间1小时，那么清理频率为30分钟一次，其他为2小时一次
         */
        cleanupFrequency =
            if (retentionPeriod == SimpleInterceptor.Period.ONE_HOUR) TimeUnit.MINUTES.toMillis(30) else TimeUnit.HOURS.toMillis(
                2
            )
    }
}