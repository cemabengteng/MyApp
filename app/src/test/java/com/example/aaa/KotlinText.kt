package com.example.aaa

import android.os.Looper
import com.safframework.app.annotation.Async
import org.junit.Test

/**
 * Created by chengXing on 2017/7/4.
 */

class KotlinText {

    @Test
    @Async
    fun useAsync() {
        print(" thread=" + Thread.currentThread().id)
        print("ui thread=" + Looper.getMainLooper().thread.id)
    }
}