package com.kangaroo.simpleinterceptor.internal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kangaroo.simpleinterceptor.R
import com.kangaroo.simpleinterceptor.internal.data.HttpTransaction

/**
 * 概述
 */
internal class TransactionOverviewFragment : Fragment(), TransactionFragment {
    var url: TextView? = null
    var method: TextView? = null
    var protocol: TextView? = null
    var status: TextView? = null
    var response: TextView? = null
    var ssl: TextView? = null
    var requestTime: TextView? = null
    var responseTime: TextView? = null
    var duration: TextView? = null
    var requestSize: TextView? = null
    var responseSize: TextView? = null
    var totalSize: TextView? = null
    private var transaction: HttpTransaction? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.interceptor_fragment_transaction_overview, container, false)
        url = view.findViewById<TextView>(R.id.url)
        method = view.findViewById<TextView>(R.id.method)
        protocol = view.findViewById<TextView>(R.id.protocol)
        status = view.findViewById<TextView>(R.id.status)
        response = view.findViewById<TextView>(R.id.response)
        ssl = view.findViewById<TextView>(R.id.ssl)
        requestTime = view.findViewById<TextView>(R.id.request_time)
        responseTime = view.findViewById<TextView>(R.id.response_time)
        duration = view.findViewById<TextView>(R.id.duration)
        requestSize = view.findViewById<TextView>(R.id.request_size)
        responseSize = view.findViewById<TextView>(R.id.response_size)
        totalSize = view.findViewById<TextView>(R.id.total_size)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateUI()
    }

    override fun transactionUpdated(transaction: HttpTransaction) {
        this.transaction = transaction
        populateUI()
    }

    private fun populateUI() {
        if (isAdded && transaction != null) {
            url!!.text = transaction!!.url
            method!!.text = transaction!!.method
            protocol!!.text = transaction!!.protocol
            status!!.text = transaction!!.status.toString()
            response!!.text = transaction!!.responseSummaryText
            ssl!!.setText(if (transaction!!.isSsl) R.string.interceptor_yes else R.string.interceptor_no)
            requestTime!!.text = transaction!!.requestDateString
            responseTime!!.text = transaction!!.responseDateString
            duration!!.text = transaction!!.durationString
            requestSize!!.text = transaction!!.requestSizeString
            responseSize!!.text = transaction!!.responseSizeString
            totalSize!!.text = transaction!!.totalSizeString
        }
    }
}