package com.kangaroo.simpleinterceptor.internal.ui

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kangaroo.simpleinterceptor.R
import com.kangaroo.simpleinterceptor.internal.components.providers.InterceptorContentProvider
import com.kangaroo.simpleinterceptor.internal.data.HttpTransaction
import com.kangaroo.simpleinterceptor.internal.tools.NotificationHelper

/**
 * 列表
 */
internal class TransactionListFragment : Fragment(), SearchView.OnQueryTextListener,
    LoaderManager.LoaderCallbacks<Cursor> {
    private var currentFilter: String? = null
    private var listener: OnListFragmentInteractionListener? = null
    private var adapter: TransactionAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.interceptor_fragment_transaction_list, container, false)
        if (view is RecyclerView) {
            val context = view.getContext()
            val recyclerView = view
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    getContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = TransactionAdapter(requireContext(), listener)
            recyclerView.adapter = adapter
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = if (context is OnListFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement OnListFragmentInteractionListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.interceptor_main, menu)
        val searchMenuItem = menu.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.setIconifiedByDefault(true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear -> {
                context!!.contentResolver.delete(InterceptorContentProvider.TRANSACTION_URI!!, null, null)
                NotificationHelper.clearBuffer()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * 查询 responseCode 和 path
     * @param id
     * @param args
     * @return
     */
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val loader = CursorLoader(context!!)
        loader.uri = InterceptorContentProvider.TRANSACTION_URI!!
        if (!TextUtils.isEmpty(currentFilter)) {
            if (TextUtils.isDigitsOnly(currentFilter)) {
                loader.selection = "responseCode LIKE ?"
                loader.selectionArgs = arrayOf("$currentFilter%")
            } else {
                loader.selection = "path LIKE ?"
                loader.selectionArgs = arrayOf("%$currentFilter%")
            }
        }
        loader.projection = HttpTransaction.PARTIAL_PROJECTION
        loader.sortOrder = "requestDate DESC"
        return loader
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        adapter!!.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter!!.swapCursor(null)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        currentFilter = newText
        LoaderManager.getInstance(this).restartLoader(0, null, this)
        return true
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: HttpTransaction)
    }

    companion object {
        fun newInstance(): TransactionListFragment {
            return TransactionListFragment()
        }
    }
}