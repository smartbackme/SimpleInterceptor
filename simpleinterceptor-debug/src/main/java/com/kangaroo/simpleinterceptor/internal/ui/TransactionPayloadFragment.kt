package com.kangaroo.simpleinterceptor.internal.ui

import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kangaroo.simpleinterceptor.R
import com.kangaroo.simpleinterceptor.internal.data.HttpTransaction

internal class TransactionPayloadFragment : Fragment(), TransactionFragment {
    var headers: TextView? = null
    var body: TextView? = null
    private var type = 0
    private var transaction: HttpTransaction? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments!!.getInt(ARG_TYPE)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.interceptor_fragment_transaction_payload, container, false)
        headers = view.findViewById<TextView>(R.id.headers)
        body = view.findViewById<TextView>(R.id.body)
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
            when (type) {
                TYPE_REQUEST -> setText(
                    transaction!!.getRequestHeadersString(true),
                    transaction!!.formattedRequestBody, transaction!!.requestBodyIsPlainText()
                )
                TYPE_RESPONSE -> setText(
                    transaction!!.getResponseHeadersString(true),
                    transaction!!.formattedResponseBody, transaction!!.responseBodyIsPlainText()
                )
            }
        }
    }

    private fun setText(headersString: String, bodyString: String?, isPlainText: Boolean) {
        headers!!.visibility =
            if (TextUtils.isEmpty(headersString)) View.GONE else View.VISIBLE
        headers!!.text = Html.fromHtml(headersString)
        if (!isPlainText) {
            body!!.text = getString(R.string.interceptor_body_omitted)
        } else {
            body!!.text = bodyString
        }
    }

    companion object {
        //请求
        const val TYPE_REQUEST = 0
        //相应
        const val TYPE_RESPONSE = 1
        private const val ARG_TYPE = "type"
        fun newInstance(type: Int): TransactionPayloadFragment {
            val fragment = TransactionPayloadFragment()
            val b = Bundle()
            b.putInt(ARG_TYPE, type)
            fragment.arguments = b
            return fragment
        }
    }
}