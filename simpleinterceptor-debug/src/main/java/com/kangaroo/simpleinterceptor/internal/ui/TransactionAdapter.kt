package com.kangaroo.simpleinterceptor.internal.ui

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.CursorAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kangaroo.simpleinterceptor.R
import com.kangaroo.simpleinterceptor.internal.data.HttpTransaction
import com.kangaroo.simpleinterceptor.internal.data.LocalCupboard
import com.kangaroo.simpleinterceptor.internal.ui.TransactionListFragment.OnListFragmentInteractionListener

internal class TransactionAdapter(
    private val context: Context,
    private val listener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    private val cursorAdapter: CursorAdapter

    private val colorDefault = ContextCompat.getColor(context, R.color.interceptor_status_default)
    private val colorRequested = ContextCompat.getColor(context, R.color.interceptor_status_requested)
    private val colorError = ContextCompat.getColor(context, R.color.interceptor_status_error)
    private val color500 = ContextCompat.getColor(context, R.color.interceptor_status_500)
    private val color400 = ContextCompat.getColor(context, R.color.interceptor_status_400)
    private val color300 = ContextCompat.getColor(context, R.color.interceptor_status_300)

    override fun getItemCount(): Int {
        return cursorAdapter.count
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursorAdapter.cursor.moveToPosition(position)
        cursorAdapter.bindView(holder.itemView, context, cursorAdapter.cursor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = cursorAdapter.newView(context, cursorAdapter.cursor, parent)
        return ViewHolder(v)
    }

    fun swapCursor(newCursor: Cursor?) {
        cursorAdapter.swapCursor(newCursor)
        notifyDataSetChanged()
    }


    init {

        cursorAdapter = object : CursorAdapter(context, null, FLAG_REGISTER_CONTENT_OBSERVER) {
            override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.interceptor_list_item_transaction, parent, false)
                val holder: ViewHolder = ViewHolder(itemView)
                itemView.tag = holder
                return itemView
            }

            override fun bindView(view: View, context: Context, cursor: Cursor) {
                val transaction = LocalCupboard.instance!!.withCursor(cursor).get(
                    HttpTransaction::class.java
                )
                val holder = view.tag as ViewHolder
                holder.path.text = transaction.method + " " + transaction.path
                holder.host.text = transaction.host
                holder.start.text = transaction.requestStartTimeString
                holder.ssl.visibility = if (transaction.isSsl) View.VISIBLE else View.GONE
                if (transaction.status == HttpTransaction.Status.Complete) {
                    holder.code.text = transaction.responseCode.toString()
                    holder.duration.text = transaction.durationString
                    holder.size.text = transaction.totalSizeString
                } else {
                    holder.code.text = null
                    holder.duration.text = null
                    holder.size.text = null
                }
                if (transaction.status == HttpTransaction.Status.Failed) {
                    holder.code.text = "!!!"
                }
                setStatusColor(holder, transaction)
                holder.transaction = transaction
                holder.view.setOnClickListener {
                    listener?.onListFragmentInteraction(holder.transaction!!)
                }
            }

            private fun setStatusColor(holder: ViewHolder, transaction: HttpTransaction) {
                val color: Int = when {
                    transaction.status == HttpTransaction.Status.Failed -> {
                        colorError
                    }
                    transaction.status == HttpTransaction.Status.Requested -> {
                        colorRequested
                    }
                    transaction.responseCode!! >= 500 -> {
                        color500
                    }
                    transaction.responseCode!! >= 400 -> {
                        color400
                    }
                    transaction.responseCode!! >= 300 -> {
                        color300
                    }
                    else -> {
                        colorDefault
                    }
                }
                holder.code.setTextColor(color)
                holder.path.setTextColor(color)
            }
        }
    }

    internal inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(
        view
    ) {
        val code: TextView = view.findViewById<TextView>(R.id.code)
        val path: TextView = view.findViewById<TextView>(R.id.path)
        val host: TextView = view.findViewById<TextView>(R.id.host)
        val start: TextView = view.findViewById<TextView>(R.id.start)
        val duration: TextView = view.findViewById<TextView>(R.id.duration)
        val size: TextView = view.findViewById<TextView>(R.id.size)
        val ssl: ImageView = view.findViewById<ImageView>(R.id.ssl)
        var transaction: HttpTransaction? = null

    }
}