package com.wallace.pickfilegallery

import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment

object LogUtils {
    const val TAG_LOG = "LOG"
    fun Activity.logD(text: String) {
        Log.d(TAG_LOG, "[${this.javaClass.simpleName}] - $text")
    }

    fun Fragment.logD(text: String) {
        Log.d(TAG_LOG, "[${this.javaClass.simpleName}] - $text")
    }

    fun logD(text: String) {
        Log.d(TAG_LOG, "[${this.javaClass.simpleName}] - $text")
    }

    fun logE(text: String) {
        Log.e(TAG_LOG, text)
    }

    fun logV(text: String) {
        Log.v(TAG_LOG, text)
    }
}