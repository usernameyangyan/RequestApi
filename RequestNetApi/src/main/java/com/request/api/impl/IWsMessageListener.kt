package com.request.api.impl

import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

/**
Create by yangyan
Create time:2023/11/13 17:18
Describe:
 */
interface IWsMessageListener {
    fun onMessage(message: String)
    fun onConnectSuccess()
    fun onFailure(t: Throwable)
    fun onClosed(code: Int, reason: String)
    fun onClosing(code: Int, reason: String){

    }
    fun onMessage( bytes: ByteString){

    }
}