package com.kangaroo.simpleinterceptor.internal.components.providers

import android.content.*
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import com.kangaroo.simpleinterceptor.internal.data.InterceptorDbOpenHelper
import com.kangaroo.simpleinterceptor.internal.data.HttpTransaction
import com.kangaroo.simpleinterceptor.internal.data.LocalCupboard

internal class InterceptorContentProvider : ContentProvider() {
    private var databaseHelper: InterceptorDbOpenHelper? = null
    override fun attachInfo(context: Context, info: ProviderInfo) {
        super.attachInfo(context, info)
        TRANSACTION_URI = Uri.parse("content://" + info.authority + "/transaction")
        matcher.addURI(info.authority, "transaction/#", TRANSACTION)
        matcher.addURI(info.authority, "transaction", TRANSACTIONS)
    }

    override fun onCreate(): Boolean {
        databaseHelper =
            InterceptorDbOpenHelper(
                context
            )
        return true
    }

    /**
     * 查询多条与查询单条
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    override fun query(
        uri: Uri, projection: Array<String>?,
        selection: String?, selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val db = databaseHelper!!.writableDatabase
        var cursor: Cursor? = null
        when (matcher.match(uri)) {
            TRANSACTIONS -> {
                var builder = LocalCupboard.instance!!.withDatabase(db).query(HttpTransaction::class.java)
                if(projection!=null){
                    builder.withProjection(*projection)
                }
                if(selectionArgs!=null){
                    builder.withSelection(selection, *selectionArgs)
                }
                cursor = builder.orderBy(sortOrder).cursor
            }
            TRANSACTION -> cursor = LocalCupboard.instance!!.withDatabase(db).query(
                HttpTransaction::class.java
            ).byId(ContentUris.parseId(uri)).cursor
        }
        cursor?.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val db = databaseHelper!!.writableDatabase
        when (matcher.match(uri)) {
            TRANSACTIONS -> {
                val id = db.insert(
                    LocalCupboard.instance!!.getTable(
                        HttpTransaction::class.java
                    ), null, contentValues
                )
                if (id > 0) {
                    context!!.contentResolver.notifyChange(uri, null)
                    return ContentUris.withAppendedId(TRANSACTION_URI!!, id)
                }
            }
        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = databaseHelper!!.writableDatabase
        var result = 0
        when (matcher.match(uri)) {
            TRANSACTIONS -> result = db.delete(
                LocalCupboard.instance!!.getTable(
                    HttpTransaction::class.java
                ), selection, selectionArgs
            )
            TRANSACTION -> result = db.delete(
                LocalCupboard.instance!!.getTable(
                    HttpTransaction::class.java
                ),
                "_id = ?", arrayOf(uri.pathSegments[1])
            )
        }
        if (result > 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return result
    }

    override fun update(
        uri: Uri, contentValues: ContentValues?,
        selection: String?, selectionArgs: Array<String>?
    ): Int {
        val db = databaseHelper!!.writableDatabase
        var result = 0
        when (matcher.match(uri)) {
            TRANSACTIONS -> result = db.update(
                LocalCupboard.instance!!.getTable(
                    HttpTransaction::class.java
                ), contentValues, selection, selectionArgs
            )
            TRANSACTION -> result = db.update(
                LocalCupboard.instance!!.getTable(
                    HttpTransaction::class.java
                ), contentValues,
                "_id = ?", arrayOf(uri.pathSegments[1])
            )
        }
        if (result > 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return result
    }

    companion object {
        @JvmField
        var TRANSACTION_URI: Uri? = null
        private const val TRANSACTION = 0
        private const val TRANSACTIONS = 1
        private val matcher = UriMatcher(UriMatcher.NO_MATCH)
    }
}