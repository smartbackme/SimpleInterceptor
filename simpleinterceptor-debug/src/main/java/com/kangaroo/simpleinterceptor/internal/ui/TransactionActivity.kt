package com.kangaroo.simpleinterceptor.internal.ui

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.kangaroo.simpleinterceptor.R
import com.kangaroo.simpleinterceptor.internal.components.providers.InterceptorContentProvider
import com.kangaroo.simpleinterceptor.internal.data.HttpTransaction
import com.kangaroo.simpleinterceptor.internal.data.LocalCupboard
import com.kangaroo.simpleinterceptor.internal.listener.SimpleOnPageChangedListener
import com.kangaroo.simpleinterceptor.internal.tools.FormatUtils
import java.util.*

internal class TransactionActivity : BaseInterceptorActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    var title: TextView? = null
    var adapter: Adapter? = null
    private var transactionId: Long = 0
    private var transaction: HttpTransaction? = null

    /**
     * 初始化页面，并加载数据
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interceptor_activity_transaction)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = findViewById<TextView>(R.id.toolbar_title)
        val ab = supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(true)
        val viewPager = findViewById<ViewPager>(R.id.viewpager)
        setupViewPager(viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)
        transactionId = intent.getLongExtra(ARG_TRANSACTION_ID, 0)
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    /**
     * 重启数据
     */
    override fun onResume() {
        super.onResume()
        LoaderManager.getInstance(this).restartLoader(0, null, this)
    }

    /**
     * 菜单
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.interceptor_transaction, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * 菜单选择
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share_text -> {
                share(FormatUtils.getShareText(this, transaction!!))
                true
            }
            R.id.share_curl -> {
                share(FormatUtils.getShareCurlCommand(transaction!!))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val loader = CursorLoader(this)
        loader.uri = ContentUris.withAppendedId(
            InterceptorContentProvider.TRANSACTION_URI!!,
            transactionId
        )
        return loader
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        transaction = LocalCupboard.instance!!.withCursor(data).get(
            HttpTransaction::class.java
        )
        populateUI()
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {}

    /**
     * 数据加载到页面
     */
    private fun populateUI() {
        if (transaction != null) {
            title!!.text = transaction!!.method + " " + transaction!!.path
            for (fragment in adapter!!.fragments) {
                fragment.transactionUpdated(transaction!!)
            }
        }
    }

    /**
     * 加载viewpager页面
     */
    private fun setupViewPager(viewPager: ViewPager) {
        adapter = Adapter(
            supportFragmentManager
        )
        adapter!!.addFragment(TransactionOverviewFragment(), getString(R.string.interceptor_overview))
        adapter!!.addFragment(
            TransactionPayloadFragment.newInstance(TransactionPayloadFragment.TYPE_REQUEST),
            getString(
                R.string.interceptor_request
            )
        )
        adapter!!.addFragment(
            TransactionPayloadFragment.newInstance(TransactionPayloadFragment.TYPE_RESPONSE),
            getString(
                R.string.interceptor_response
            )
        )
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : SimpleOnPageChangedListener() {
            override fun onPageSelected(position: Int) {
                selectedTabPosition = position
            }
        })
        viewPager.currentItem = selectedTabPosition
    }

    /**
     * 分享
     */
    private fun share(content: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, null))
    }

    /**
     * 适配器
     */
    class Adapter(fm: FragmentManager?) : FragmentPagerAdapter(
        fm!!
    ) {
        val fragments: MutableList<TransactionFragment> = ArrayList()
        private val fragmentTitles: MutableList<String> = ArrayList()
        fun addFragment(fragment: TransactionFragment, title: String) {
            fragments.add(fragment)
            fragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position] as Fragment
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitles[position]
        }
    }

    companion object {
        private const val ARG_TRANSACTION_ID = "transaction_id"

        /**
         * 静态变量，为了每次进入固定展示某一tab
         */
        private var selectedTabPosition = 0
        fun start(context: Context, transactionId: Long) {
            val intent = Intent(context, TransactionActivity::class.java)
            intent.putExtra(ARG_TRANSACTION_ID, transactionId)
            context.startActivity(intent)
        }
    }
}