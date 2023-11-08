package com.request.api.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.request.api.config.ReqConfig
import com.request.api.exception.OtherException

/**
Create by yangyan
Create time:2023/9/6 09:35
Describe:
 */
object RequestNetUtils {
    @SuppressLint("MissingPermission")
    fun isNetworkConnected(): Boolean {
        if (ReqConfig.instance == null) {
            throw OtherException("ReqConfig is not init!")
        }
        val connectivityManager = ReqConfig.instance!!.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
            if (networkInfo.state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }
}