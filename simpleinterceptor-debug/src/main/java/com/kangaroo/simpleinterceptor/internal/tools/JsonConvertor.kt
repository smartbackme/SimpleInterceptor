package com.kangaroo.simpleinterceptor.internal.tools

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import java.util.*

internal object JsonConvertor {

    // 调用setPrettyPrinting方法，改变gson对象的默认行为
    @JvmStatic
    val instance: Gson = GsonBuilder().setPrettyPrinting().registerTypeAdapter(Date::class.java, DateTypeAdapter()).create()
}