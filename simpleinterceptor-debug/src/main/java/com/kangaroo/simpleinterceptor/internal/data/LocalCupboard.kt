package com.kangaroo.simpleinterceptor.internal.data

import nl.qbusict.cupboard.Cupboard
import nl.qbusict.cupboard.CupboardBuilder

internal object LocalCupboard {
    private var cupboard: Cupboard? = null
    @JvmStatic
    val instance: Cupboard?
        get() {
            if (cupboard == null) {
                cupboard = CupboardBuilder().build()
            }
            return cupboard
        }
    @JvmStatic
    val annotatedInstance: Cupboard
        get() = CupboardBuilder(instance)
            .useAnnotations()
            .build()

    init {
        instance!!.register(HttpTransaction::class.java)
    }
}