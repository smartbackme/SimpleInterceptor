package com.kangaroo.simpleinterceptor

import android.content.Context
import android.content.Intent
import com.kangaroo.simpleinterceptor.internal.ui.MainActivity


object SimpleLaunch {

    @JvmStatic
    fun getLaunchIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}