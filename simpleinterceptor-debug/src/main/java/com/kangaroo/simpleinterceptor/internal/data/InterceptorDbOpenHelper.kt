package com.kangaroo.simpleinterceptor.internal.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kangaroo.simpleinterceptor.internal.data.LocalCupboard.annotatedInstance

internal class InterceptorDbOpenHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        annotatedInstance.withDatabase(db).createTables()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        annotatedInstance.withDatabase(db).upgradeTables()
    }

    companion object {
        const val DATABASE_NAME = "interceptor.db"
        private const val VERSION = 4
    }
}