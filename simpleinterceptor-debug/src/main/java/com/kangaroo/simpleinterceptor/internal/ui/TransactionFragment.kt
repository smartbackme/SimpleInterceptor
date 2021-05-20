package com.kangaroo.simpleinterceptor.internal.ui

import com.kangaroo.simpleinterceptor.internal.data.HttpTransaction

/**
 * 外部更新数据
 */
internal interface TransactionFragment {
    fun transactionUpdated(transaction: HttpTransaction)
}