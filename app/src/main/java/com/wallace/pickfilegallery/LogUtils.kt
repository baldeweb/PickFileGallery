package com.wallace.pickfilegallery

import android.util.Log

object LogUtils {
    const val TAG_LOG = "LOG"
    fun logD(text: String) {
        Log.d(TAG_LOG, "[${this.javaClass.simpleName}] - $text")
    }
}