package com.kangaroo.simpleinterceptor.internal.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.kangaroo.simpleinterceptor.R
import com.kangaroo.simpleinterceptor.internal.data.HttpTransaction
import com.kangaroo.simpleinterceptor.internal.ui.TransactionListFragment.OnListFragmentInteractionListener

internal class MainActivity : BaseInterceptorActivity(), OnListFragmentInteractionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interceptor_activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.subtitle = applicationName
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, TransactionListFragment.newInstance())
                .commit()
        }
    }

    override fun onListFragmentInteraction(transaction: HttpTransaction) {
        TransactionActivity.start(this, transaction.id)
    }

    private val applicationName: String
        private get() {
            val applicationInfo = applicationInfo
            val stringId = applicationInfo.labelRes
            return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(
                stringId
            )
        }

}