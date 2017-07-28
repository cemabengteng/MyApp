package com.example.aaa

import org.junit.Test

/**
 * Created by chengXing on 2017/7/4.
 */

class KotlinText {

    @Test
    fun go() {
        var a:String? = ""
        a = null
//        var l = a?.length ?: -1
        var l = a!!.length
        print(l)
    }
}